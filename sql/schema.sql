DROP DATABASE IF EXISTS queuesmart;
CREATE DATABASE queuesmart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE queuesmart;

CREATE TABLE roles (
 role_id INT PRIMARY KEY AUTO_INCREMENT,
 role_name VARCHAR(20) NOT NULL UNIQUE
);
INSERT INTO roles(role_name) VALUES ('STUDENT'),('STAFF'),('ADMIN');

CREATE TABLE users (
 user_id INT PRIMARY KEY AUTO_INCREMENT,
 username VARCHAR(50) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL,
 full_name VARCHAR(100) NOT NULL,
 email VARCHAR(100) NOT NULL UNIQUE,
 role_id INT NOT NULL,
 is_active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_users_role FOREIGN KEY(role_id) REFERENCES roles(role_id)
 ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE departments (
 department_id INT PRIMARY KEY AUTO_INCREMENT,
 department_name VARCHAR(100) NOT NULL UNIQUE,
 is_active BOOLEAN NOT NULL DEFAULT TRUE
);
INSERT INTO departments(department_name) VALUES
('Information and Communication Technology'),('Computer Science'),('Business Management'),
('Education'),('Natural and Applied Sciences'),('Humanities');

CREATE TABLE students (
 student_id INT PRIMARY KEY AUTO_INCREMENT,
 user_id INT NOT NULL UNIQUE,
 student_number VARCHAR(20) NOT NULL UNIQUE,
 department_id INT NULL,
 FOREIGN KEY(user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
 FOREIGN KEY(department_id) REFERENCES departments(department_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE staff (
 staff_id INT PRIMARY KEY AUTO_INCREMENT,
 user_id INT NOT NULL UNIQUE,
 staff_number VARCHAR(20) NOT NULL UNIQUE,
 position VARCHAR(50) NOT NULL,
 FOREIGN KEY(user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE services (
 service_id INT PRIMARY KEY AUTO_INCREMENT,
 service_name VARCHAR(80) NOT NULL UNIQUE,
 description VARCHAR(255),
 is_active BOOLEAN NOT NULL DEFAULT TRUE
);
INSERT INTO services(service_name,description) VALUES
('Laptop Collection','Collect an allocated university laptop'),
('Laptop Return','Return a university laptop to the IT department'),
('Wi-Fi Assistance','Get assistance with campus Wi-Fi'),
('Software Installation','Install approved software'),
('General IT Support','General university IT support');

CREATE TABLE appointments (
 appointment_id INT PRIMARY KEY AUTO_INCREMENT,
 student_id INT NOT NULL,
 service_id INT NOT NULL,
 appointment_date DATE NOT NULL,
 appointment_time TIME NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(student_id) REFERENCES students(student_id) ON UPDATE CASCADE ON DELETE RESTRICT,
 FOREIGN KEY(service_id) REFERENCES services(service_id) ON UPDATE CASCADE ON DELETE RESTRICT,
 CONSTRAINT chk_appointment_status CHECK(status IN('BOOKED','CANCELLED','COMPLETED','MISSED')),
 CONSTRAINT unique_appointment_slot UNIQUE(service_id,appointment_date,appointment_time)
);

CREATE TABLE queue_entries (
 queue_id INT PRIMARY KEY AUTO_INCREMENT,
 student_id INT NOT NULL,
 service_id INT NOT NULL,
 appointment_id INT NULL,
 staff_id INT NULL,
 queue_number INT NOT NULL,
 queue_date DATE NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
 joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 called_at DATETIME NULL,
 completed_at DATETIME NULL,
 FOREIGN KEY(student_id) REFERENCES students(student_id) ON UPDATE CASCADE ON DELETE RESTRICT,
 FOREIGN KEY(service_id) REFERENCES services(service_id) ON UPDATE CASCADE ON DELETE RESTRICT,
 FOREIGN KEY(appointment_id) REFERENCES appointments(appointment_id) ON UPDATE CASCADE ON DELETE SET NULL,
 FOREIGN KEY(staff_id) REFERENCES staff(staff_id) ON UPDATE CASCADE ON DELETE SET NULL,
 CONSTRAINT chk_queue_status CHECK(status IN('WAITING','CALLED','SERVING','COMPLETED','CANCELLED','NO_SHOW')),
 CONSTRAINT unique_queue_number UNIQUE(service_id,queue_date,queue_number)
);

CREATE TABLE laptops (
 laptop_id INT PRIMARY KEY AUTO_INCREMENT,
 asset_number VARCHAR(20) NOT NULL UNIQUE,
 model VARCHAR(80) NOT NULL,
 serial_number VARCHAR(80) UNIQUE,
 status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
 added_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT chk_laptop_status CHECK(status IN('AVAILABLE','ALLOCATED','MAINTENANCE','RETIRED'))
);

CREATE TABLE laptop_allocations (
 allocation_id INT PRIMARY KEY AUTO_INCREMENT,
 laptop_id INT NOT NULL,
 student_id INT NOT NULL,
 allocated_by INT NULL,
 allocated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 returned_at DATETIME NULL,
 FOREIGN KEY(laptop_id) REFERENCES laptops(laptop_id) ON UPDATE CASCADE ON DELETE RESTRICT,
 FOREIGN KEY(student_id) REFERENCES students(student_id) ON UPDATE CASCADE ON DELETE RESTRICT,
 FOREIGN KEY(allocated_by) REFERENCES staff(staff_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE feedback (
 feedback_id INT PRIMARY KEY AUTO_INCREMENT,
 student_id INT NOT NULL,
 queue_id INT NULL,
 rating INT NOT NULL,
 comment VARCHAR(500),
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(student_id) REFERENCES students(student_id) ON UPDATE CASCADE ON DELETE RESTRICT,
 FOREIGN KEY(queue_id) REFERENCES queue_entries(queue_id) ON UPDATE CASCADE ON DELETE SET NULL,
 CONSTRAINT chk_feedback_rating CHECK(rating BETWEEN 1 AND 5)
);

CREATE TABLE notifications (
 notification_id INT PRIMARY KEY AUTO_INCREMENT,
 user_id INT NOT NULL,
 message VARCHAR(255) NOT NULL,
 is_read BOOLEAN NOT NULL DEFAULT FALSE,
 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE audit_logs (
 log_id INT PRIMARY KEY AUTO_INCREMENT,
 user_id INT NULL,
 action VARCHAR(100) NOT NULL,
 details VARCHAR(255),
 logged_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_students_department ON students(department_id);
CREATE INDEX idx_appointments_student ON appointments(student_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_queue_student ON queue_entries(student_id);
CREATE INDEX idx_queue_service_date ON queue_entries(service_id,queue_date);
CREATE INDEX idx_queue_status ON queue_entries(status);
CREATE INDEX idx_queue_staff ON queue_entries(staff_id);
CREATE INDEX idx_laptop_status ON laptops(status);
CREATE INDEX idx_allocation_student ON laptop_allocations(student_id);
CREATE INDEX idx_allocation_laptop ON laptop_allocations(laptop_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_date ON audit_logs(logged_at);

-- Demo accounts. Passwords are SHA-256 Base64 values matching PasswordUtil.java.
INSERT INTO users(username,password_hash,full_name,email,role_id) VALUES
('admin','JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=','System Administrator','admin@queuesmart.local',3),
('staff01','EBdue3sk0xes/PjSBkz9LyThVPe1qWYDB31e+BPWprY=','IT Support Staff','staff@queuesmart.local',2),
('student1','cDsKPWrXW2SaKK3efYPGJR2kV1SSY7x/9F7HCbCoRIs=','Demo Student','student@queuesmart.local',1);

INSERT INTO students(user_id,student_number,department_id)
VALUES((SELECT user_id FROM users WHERE username='student1'),'STU001',
       (SELECT department_id FROM departments WHERE department_name='Information and Communication Technology'));

INSERT INTO staff(user_id,staff_number,position)
VALUES((SELECT user_id FROM users WHERE username='staff01'),'STAFF001','IT Support Technician');

INSERT INTO laptops(asset_number,model,serial_number,status) VALUES
('LEN-V15-001','Lenovo V15',NULL,'AVAILABLE'),
('LEN-V14-001','Lenovo V14',NULL,'AVAILABLE'),
('LEN-IPS3-001','Lenovo IdeaPad Slim 3',NULL,'AVAILABLE'),
('LEN-IPS5-001','Lenovo IdeaPad Slim 5',NULL,'AVAILABLE'),
('LEN-T14-001','Lenovo ThinkPad T14',NULL,'AVAILABLE'),
('LEN-T14S-001','Lenovo ThinkPad T14s',NULL,'AVAILABLE'),
('LEN-E14-001','Lenovo ThinkPad E14',NULL,'AVAILABLE'),
('LEN-L14-001','Lenovo ThinkPad L14',NULL,'AVAILABLE'),
('HP-250-001','HP 250',NULL,'AVAILABLE'),
('HP-255-001','HP 255',NULL,'AVAILABLE'),
('HP-ESS15-001','HP Essential 15s',NULL,'AVAILABLE'),
('HP-PB440-001','HP ProBook 440',NULL,'AVAILABLE'),
('HP-PB450-001','HP ProBook 450',NULL,'AVAILABLE'),
('HP-EB840-001','HP EliteBook 840',NULL,'AVAILABLE');

SHOW TABLES;
