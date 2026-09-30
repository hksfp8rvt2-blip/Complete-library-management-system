# Complete Library Management System

Web application using HTML, CSS, JavaScript, Java Spring Boot, JDBC, and MySQL.

Features: student/librarian login, borrow/return, credits, late penalties, book/student management, analytics.

Demo accounts:
- Librarian: librarian@gmail.com / admin123
- Student: student@gmail.com / student123

Run:
1. Run database/library.sql in MySQL.
2. Set your MySQL password in src/main/resources/application.properties.
3. Run `mvn spring-boot:run`.
4. Open http://localhost:8080

Note: Demo authentication uses plain-text passwords only for learning. Use BCrypt/session or JWT before production use.
