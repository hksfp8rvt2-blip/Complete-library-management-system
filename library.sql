CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;
DROP TABLE IF EXISTS borrow_records;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;
CREATE TABLE users (
 id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, email VARCHAR(150) NOT NULL UNIQUE,
 password VARCHAR(255) NOT NULL, role ENUM('STUDENT','LIBRARIAN') NOT NULL,
 credits INT NOT NULL DEFAULT 0, penalty DECIMAL(10,2) NOT NULL DEFAULT 0
);
CREATE TABLE books (
 id INT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(200) NOT NULL, author VARCHAR(150) NOT NULL,
 category VARCHAR(100), quantity INT NOT NULL DEFAULT 1, available_quantity INT NOT NULL DEFAULT 1
);
CREATE TABLE borrow_records (
 id INT PRIMARY KEY AUTO_INCREMENT, student_id INT NOT NULL, book_id INT NOT NULL,
 borrow_date DATE NOT NULL, due_date DATE NOT NULL, return_date DATE NULL,
 status ENUM('BORROWED','RETURNED') NOT NULL DEFAULT 'BORROWED', credits_earned INT NOT NULL DEFAULT 0,
 penalty DECIMAL(10,2) NOT NULL DEFAULT 0,
 FOREIGN KEY (student_id) REFERENCES users(id), FOREIGN KEY (book_id) REFERENCES books(id)
);
INSERT INTO users(name,email,password,role) VALUES
('Admin Librarian','librarian@gmail.com','admin123','LIBRARIAN'),
('Demo Student','student@gmail.com','student123','STUDENT');
INSERT INTO books(title,author,category,quantity,available_quantity) VALUES
('Java Programming','Herbert Schildt','Programming',5,5),
('Database Management Systems','Raghu Ramakrishnan','Database',3,3),
('Computer Networks','Andrew Tanenbaum','Networking',4,4),
('Operating System Concepts','Silberschatz','Operating Systems',4,4),
('Clean Code','Robert C. Martin','Programming',3,3);
