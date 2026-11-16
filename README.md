🔐 SecureGate

Advanced Secure Login & Authentication System

<p align="center">
  <b>A Java 17 Application Security Project</b><br>
  Secure Authentication • OTP Verification • Password Protection
</p>---

🌐 Project Links

🚀 "LIVE DEMO WEBSITE" (https://securegate-application-security.onrender.com)

💻 "VIEW SOURCE CODE" (https://github.com/AKAnsarkhan-lang/SecureGate_Application_Security/tree/main/src/main/java/com/securegate)

📄 [View / Download Final Project Report (PDF)](Final_Project_Report.pdf)

📂 "EXPLORE PROJECT REPOSITORY" (https://github.com/AKAnsarkhan-lang/SecureGate_Application_Security)


📌 About The Project

SecureGate is an Advanced Secure Login and Authentication System developed as part of the Application Security Project-Based Learning (PBL).

The project demonstrates how application security techniques can be implemented to protect user accounts from unauthorized access.

It provides user registration, secure password hashing, OTP-based verification, role-based access control, session management, and protection against brute-force attacks.

The main purpose of SecureGate is to demonstrate practical implementation of authentication and security concepts using Java.

---

🎯 Project Objectives

- Develop a secure user authentication system.
- Protect user passwords using secure hashing.
- Implement OTP-based second-factor authentication.
- Prevent unauthorized login attempts.
- Implement role-based access control.
- Demonstrate practical Application Security concepts.

---

✨ Key Features

Feature| Description
🔑 User Registration| Create new user accounts
🔐 Secure Login| Authenticate registered users
🔒 Password Hashing| PBKDF2-HMAC-SHA256 with per-user salt
📩 OTP Verification| Demo OTP-based second-factor authentication
👥 Role-Based Access| USER and ADMIN access control
🛡️ Brute-Force Protection| Temporary lock after 5 failed attempts
🍪 Session Management| Basic session cookie and logout
📋 Security Audit Logs| Record security-related events
✅ Input Validation| Validate user inputs
📱 Responsive UI| Modern responsive interface

---

⚙️ Technologies Used

- Programming Language: Java 17
- Security: PBKDF2-HMAC-SHA256
- Authentication: Username, Password, OTP
- Frontend: HTML, CSS, Java-based application interface
- Deployment: Render
- Version Control: Git & GitHub
- Development Environment: Visual Studio Code

---

🔄 System Workflow

1. User opens the SecureGate website.
2. User creates an account through registration.
3. Password is securely hashed and stored.
4. User enters login credentials.
5. System validates the credentials.
6. A demo OTP is generated and printed in the server terminal.
7. User enters the OTP on the verification page.
8. The system verifies the OTP.
9. Successful verification grants access.
10. Security events are recorded in the audit logs.

---

🏗️ Project Architecture

                 USER
                  |
                  v
          SecureGate Website
                  |
                  v
         Registration / Login
                  |
                  v
       Password Hash Verification
                  |
                  v
          OTP Verification
                  |
          +-------+-------+
          |               |
          v               v
      OTP Valid       OTP Invalid
          |               |
          v               v
    Access Granted    Access Denied
          |
          v
   User / Admin Dashboard
          |
          v
     Security Audit Logs

---

💻 Source Code

The complete Java source code is available in the repository.

📂 "Open Java Source Code Folder" (https://github.com/AKAnsarkhan-lang/SecureGate_Application_Security/tree/main/src/main/java/com/securegate)

💻 "View SecureGate Java Source Code" (https://github.com/AKAnsarkhan-lang/SecureGate_Application_Security/blob/main/src/main/java/com/securegate/SecureGate.java

📄 "Open README Documentation" (https://github.com/AKAnsarkhan-lang/SecureGate_Application_Security/blob/main/README.md)

---

📸 Project Screenshots

Screenshots will demonstrate the actual working application.

- User Registration Page
- Secure Login Page
- OTP Verification Page
- User Dashboard
- Admin Dashboard
- Security Audit Logs

---

🚀 Live Application

🌍 "Click Here to Open SecureGate Live Website" (https://securegate-application-security.onrender.com)

Demo OTP: The OTP is generated dynamically and displayed in the server terminal during login. This is a demonstration-only OTP delivery mechanism.

---

📄 Final Project Report

The complete final project report contains:

- Project Introduction
- Problem Statement
- Objectives
- System Design
- Security Features
- Implementation
- Application Screenshots
- Testing
- Results
- Conclusion

📥 "VIEW / DOWNLOAD FINAL PROJECT REPORT" (Final_Project_Report.pdf)

---

▶️ How to Run the Project

Requirements

- Java Development Kit (JDK) 17 or newer
- Visual Studio Code
- Java Extension Pack

Compile

javac -d out src/main/java/com/securegate/SecureGate.java

Run

java -cp out com.securegate.SecureGate

Open the local website at:

http://localhost:8081

For OTP verification, check the terminal where the server is running.

---

⚠️ Security Disclaimer

SecureGate is an educational Application Security prototype.
It uses in-memory users and sessions and a demo OTP delivery channel. It is not intended for production deployment.
Production systems require persistent databases, secure OTP delivery, HTTPS, CSRF protection, rate limiting, secure cookie configuration, and independent security testing.

---

👨‍💻 Project Team

Developed as a B.Tech Computer Science and Engineering (Cyber Security) Project-Based Learning project.

Pathan Ansar khan 
Course: B.Tech CSE (Cyber Security)
Project: SecureGate - Advanced Secure Login & Authentication System

---

<p align="center">
  🔐 <b>SecureGate</b><br>
  <i>Security Begins with Authentication.</i>
</p>















