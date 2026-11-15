package com.securegate;

import com.sun.net.httpserver.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;

public class SecureGate {
  static class User {
    String username, salt, hash, role="USER"; int failures=0; long lockedUntil=0;
    User(String u,String s,String h){username=u;salt=s;hash=h;}
  }
  static Map<String,User> users=new ConcurrentHashMap<>();
  static Map<String,String> pendingOtp=new ConcurrentHashMap<>();
  static Map<String,String> sessions=new ConcurrentHashMap<>();
  static List<String> audit=Collections.synchronizedList(new ArrayList<>());
  static SecureRandom random=new SecureRandom();

  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
    server.createContext("/", SecureGate::handle);
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    System.out.println("SecureGate running at http://localhost:8081");
  }
  static void handle(HttpExchange ex) throws java.io.IOException {
    String path=ex.getRequestURI().getPath();
    Map<String,String> form=parse(ex);
    String msg="", page="";
    if(path.equals("/register") && ex.getRequestMethod().equalsIgnoreCase("POST")){
      String u=form.getOrDefault("username","").trim(), p=form.getOrDefault("password","");
      if(!u.matches("[A-Za-z0-9_]{3,20}") || p.length()<8) msg="Username: 3-20 letters/numbers/_; password: minimum 8 characters.";
      else if(users.containsKey(u.toLowerCase())) msg="Username already exists.";
      else {String salt=hex(randomBytes(16)); users.put(u.toLowerCase(),new User(u,salt,hash(p,salt))); log("REGISTER",u); msg="Account created. Please sign in.";}
      page=home(msg);
    } else if(path.equals("/login") && ex.getRequestMethod().equalsIgnoreCase("POST")){
      String u=form.getOrDefault("username","").trim(), p=form.getOrDefault("password","");
      User user=users.get(u.toLowerCase());
      if(user==null){log("LOGIN_FAIL",u);msg="Invalid credentials.";}
      else if(user.lockedUntil>System.currentTimeMillis()){msg="Account temporarily locked. Try later.";}
      else if(!constantEquals(user.hash,hash(p,user.salt))){
        user.failures++; log("LOGIN_FAIL",u);
        if(user.failures>=5){user.lockedUntil=System.currentTimeMillis()+60_000;user.failures=0;msg="Too many attempts. Account locked for 60 seconds.";}
        else msg="Invalid credentials. Attempts remaining: "+(5-user.failures);
      } else {
        user.failures=0; String otp=String.format("%06d",random.nextInt(1_000_000));
        pendingOtp.put(u.toLowerCase(),otp); System.out.println("[DEMO OTP for "+u+"] "+otp);
        msg="Password accepted. OTP sent in demo mode. Check server terminal.";
        page=otpPage(u,msg);
      }
      if(page.isEmpty())page=home(msg);
    } else if(path.equals("/verify") && ex.getRequestMethod().equalsIgnoreCase("POST")){
      String u=form.getOrDefault("username","").toLowerCase(), otp=form.getOrDefault("otp","");
      if(otp.equals(pendingOtp.get(u)) && users.containsKey(u)){
        pendingOtp.remove(u); String token=hex(randomBytes(24)); sessions.put(token,u);
        log("MFA_SUCCESS",u); User user=users.get(u);
        page=dashboard(user,token);
        send(ex,page,"Set-Cookie: SGSESSION="+token+"; HttpOnly; SameSite=Strict; Path=/");
        return;
      } else {log("MFA_FAIL",u);page=otpPage(u,"Incorrect OTP. Try again.");}
    } else if(path.equals("/logout")){
      String token=cookie(ex,"SGSESSION"); String u=sessions.remove(token); if(u!=null)log("LOGOUT",u);
      page=home("You have been logged out.");
    } else if(path.equals("/admin")){
      String u=sessions.get(cookie(ex,"SGSESSION"));
      if(u!=null && users.get(u).role.equals("ADMIN")) page=admin();
      else page=home("Access denied: ADMIN role required.");
    } else page=home("");
    send(ex,page,null);
  }
  static String home(String msg){return shell("SecureGate | Authentication",
  "<section class='hero'><span class='tag'>APPLICATION SECURITY • PBL</span><h1>Secure access.<br><em>Stronger trust.</em></h1><p>Identity protection with password hashing, MFA and intelligent login defense.</p></section>"+
  "<div class='grid'><form method='post' action='/login' class='panel'><h2>Sign in</h2><label>Username</label><input name='username' required maxlength='20'><label>Password</label><input name='password' type='password' required><button>Authenticate</button></form>"+
  "<form method='post' action='/register' class='panel'><h2>Create account</h2><label>Username</label><input name='username' required pattern='[A-Za-z0-9_]{3,20}'><label>Password (8+ chars)</label><input name='password' type='password' minlength='8' required><button class='secondary'>Register securely</button></form></div>"+
  "<div class='notice'>"+esc(msg)+"</div><footer>SecureGate · Educational security prototype</footer>");}
  static String otpPage(String u,String msg){return shell("MFA Verification", "<div class='panel narrow'><span class='tag'>SECOND FACTOR</span><h1>Verify identity</h1><p>"+esc(msg)+"</p><form method='post' action='/verify'><input type='hidden' name='username' value='"+esc(u)+"'><label>6-digit OTP</label><input name='otp' inputmode='numeric' pattern='[0-9]{6}' required><button>Verify & continue</button></form><a href='/'>Back</a></div>");}
  static String dashboard(User u,String token){return shell("Security Dashboard","<div class='panel'><span class='tag'>AUTHENTICATED</span><h1>Welcome, "+esc(u.username)+"</h1><p>Your second factor was verified successfully.</p><div class='grid'><div class='metric'><b>PBKDF2</b><small>Password storage</small></div><div class='metric'><b>MFA</b><small>Two-step verification</small></div><div class='metric'><b>"+esc(u.role)+"</b><small>Access role</small></div></div><p><a href='/admin'>Admin console</a> (ADMIN role only)</p><a class='button' href='/logout'>Logout</a></div>");}
  static String admin(){return shell("Admin Console","<div class='panel'><span class='tag'>RESTRICTED AREA</span><h1>Admin Console</h1><p>Authorized administrator access granted.</p><h2>Audit log (latest)</h2><pre>"+esc(String.join(" | ",audit))+"</pre><a href='/'>Home</a></div>");}
  static String shell(String title,String body){return "<!doctype html><html><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'><title>"+title+"</title><style>"+css()+"</style></head><body><nav><div class='brand'><span class='shield'>◆</span> SecureGate</div><span class='navlabel'>IDENTITY & ACCESS</span></nav><main>"+body+"</main></body></html>";}
  static String css(){return " *{box-sizing:border-box}body{margin:0;background:#08111f;color:#eaf1ff;font:16px system-ui}nav{display:flex;justify-content:space-between;padding:22px 7%;border-bottom:1px solid #203047}.brand{font-weight:800;font-size:21px}.shield{color:#5eead4}.navlabel,.tag{color:#8ba5c8;font-size:11px;letter-spacing:2px}.hero{padding:55px 7% 35px;max-width:900px}.hero h1,h1{font-size:clamp(34px,6vw,58px);line-height:1.05;margin:16px 0}.hero em{color:#5eead4;font-style:normal}.hero p{color:#a8bad4;max-width:540px}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:18px;padding:0 7%}.panel{background:#101e31;border:1px solid #263a55;border-radius:20px;padding:25px;margin:12px 0}.panel h2{margin-top:0}label{display:block;color:#9fb4d2;margin:14px 0 6px}input{width:100%;padding:13px;border:1px solid #304764;background:#08111f;color:white;border-radius:10px}button,.button{display:inline-block;margin-top:18px;width:100%;padding:13px;background:#5eead4;color:#06201d;border:0;border-radius:10px;font-weight:800;text-decoration:none;text-align:center;cursor:pointer}.secondary{background:#7aa8ff;color:#08111f}.notice{margin:22px 7%;color:#5eead4}.narrow{max-width:460px;margin:60px auto}.metric{padding:16px;background:#0a1526;border-radius:12px}.metric b{display:block;color:#5eead4;font-size:20px}.metric small{color:#9fb4d2}footer{padding:35px 7%;color:#637b9d}a{color:#5eead4}pre{white-space:pre-wrap;color:#9fb4d2}@media(max-width:600px){.navlabel{display:none}}";}
  static Map<String,String> parse(HttpExchange ex)throws java.io.IOException{
    Map<String,String> m=new HashMap<>(); if(ex.getRequestMethod().equalsIgnoreCase("POST")){
      String body=new String(ex.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
      for(String pair:body.split("&")){String[] kv=pair.split("=",2);if(kv.length==2)m.put(java.net.URLDecoder.decode(kv[0],"UTF-8"),java.net.URLDecoder.decode(kv[1],"UTF-8"));}
    }return m;
  }
  static void send(HttpExchange ex,String html,String header)throws java.io.IOException{
    byte[] b=html.getBytes(StandardCharsets.UTF_8);ex.getResponseHeaders().set("Content-Type","text/html; charset=utf-8");
    ex.getResponseHeaders().set("X-Content-Type-Options","nosniff");ex.getResponseHeaders().set("X-Frame-Options","DENY");ex.getResponseHeaders().set("Content-Security-Policy","default-src 'self'; style-src 'unsafe-inline'; form-action 'self'; frame-ancestors 'none'");
    if(header!=null){String[] x=header.split(": ",2);ex.getResponseHeaders().set(x[0],x[1]);}
    ex.sendResponseHeaders(200,b.length);ex.getResponseBody().write(b);ex.close();
  }
  static String hash(String p,String salt){try{PBEKeySpec spec=new PBEKeySpec(p.toCharArray(),salt.getBytes(StandardCharsets.UTF_8),120000,256);byte[] out=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();spec.clearPassword();return Base64.getEncoder().encodeToString(out);}catch(Exception e){throw new RuntimeException(e);}}
  static byte[] randomBytes(int n){byte[] b=new byte[n];random.nextBytes(b);return b;}
  static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x));return s.toString();}
  static boolean constantEquals(String a,String b){return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8),b.getBytes(StandardCharsets.UTF_8));}
  static String cookie(HttpExchange ex,String name){String c=ex.getRequestHeaders().getFirst("Cookie");if(c!=null)for(String part:c.split(";")){String[] kv=part.trim().split("=",2);if(kv.length==2&&kv[0].equals(name))return kv[1];}return "";}
  static void log(String event,String user){audit.add(new Date()+" | "+event+" | "+user);if(audit.size()>200)audit.remove(0);}
  static String esc(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("\'","&#39;");}
}
