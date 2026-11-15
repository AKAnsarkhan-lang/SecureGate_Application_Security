# SecureGate - Advanced Secure Login & Authentication System

A Java 17 demonstration project for Application Security PBL.

## Features
- Registration and login
- PBKDF2-HMAC-SHA256 password hashing with per-user salt
- Demo OTP-based second factor (OTP is printed in the server terminal; demo only)
- Role-based access control (USER / ADMIN)
- Brute-force protection: 5 failed attempts, temporary lock
- Input validation
- Basic session cookie and logout
- Security audit events
- Responsive modern UI

## Run
1. Install JDK 17 or newer.
2. Open this folder in VS Code.
3. Open Terminal -> New Terminal.
4. Run: `javac -d out src/main/java/com/securegate/SecureGate.java`
5. Run: `java -cp out com.securegate.SecureGate`
6. Open [SecureGate Live Website](https://securegate-application-security.onrender.com)
Demo OTP: Register/login with a user. On login, check the terminal where the server runs for the OTP and enter it on the verification screen.

## Notes
This is an educational prototype, not production-ready. It uses in-memory users/sessions and a demo OTP delivery channel. For production, use HTTPS, persistent database, email/SMS or authenticator-based OTP delivery, secure cookie flags, CSRF protection, rate limiting, secrets management, and independent security review.

## Team
- Pathan Ansar Khan | 240160226248
- Golla Venkatesam | 240160226255
- M. Swetha Reddy | 240160227028
- Muskan Hussain | 240160227017
- Fifth member: details pending
