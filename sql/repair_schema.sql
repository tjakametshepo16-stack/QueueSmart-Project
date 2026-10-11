-- ============================================================
-- QueueSmart schema REPAIR script (MySQL 8.0.29+)
--
-- Use this ONLY if you already have a `queuesmart` database from before
-- (that's what causes "Unknown column 'role' in 'field list'" - your
-- live `users` table was created by an older version of schema.sql that
-- didn't have the role/full_name/email columns yet).
--
-- This script is safe to re-run any number of times: every change is
-- guarded with IF NOT EXISTS, so it only adds what's missing and never
-- touches data that's already there.
--
-- Run it with:
--   mysql -u root -p queuesmart < sql/repair_schema.sql
--
-- If you would rather start completely fresh instead (you don't care
-- about existing data), just drop the database and re-run schema.sql:
--   mysql -u root -p -e "DROP DATABASE IF EXISTS queuesmart;"
--   mysql -u root -p < sql/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS queuesmart
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE queuesmart;

-- --------------------------------------------------------------
-- users: add any columns missing from an older version of the table
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('STUDENT', 'STAFF', 'ADMIN') NOT NULL,
    full_name     VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS role ENUM('STUDENT', 'STAFF', 'ADMIN') NOT NULL DEFAULT 'STUDENT' AFTER password_hash;
ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(150) NOT NULL DEFAULT '' AFTER role;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(150) NOT NULL DEFAULT '' AFTER full_name;
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER email;

-- --------------------------------------------------------------
-- Every other table, created only if it isn't there yet
-- --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    student_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT NOT NULL UNIQUE,
    student_number VARCHAR(30) NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS staff (
    staff_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL UNIQUE,
    staff_number VARCHAR(30) NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS services (
    service_id   INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    description  VARCHAR(255),
    is_active    BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id       INT NOT NULL,
    service_id       INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status           ENUM('BOOKED', 'CANCELLED', 'COMPLETED', 'NO_SHOW') DEFAULT 'BOOKED',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(service_id),
    UNIQUE KEY unique_appointment_slot (service_id, appointment_date, appointment_time)
);

CREATE TABLE IF NOT EXISTS queue_entries (
    queue_id      INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    service_id    INT NOT NULL,
    queue_number  INT NOT NULL,
    queue_date    DATE NOT NULL,
    status        ENUM('WAITING', 'IN_SERVICE', 'COMPLETED', 'NO_SHOW') DEFAULT 'WAITING',
    joined_at     TIMESTAMP NULL,
    called_at     TIMESTAMP NULL,
    completed_at  TIMESTAMP NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(service_id),
    UNIQUE KEY unique_queue_slot (service_id, queue_date, queue_number)
);

CREATE TABLE IF NOT EXISTS laptops (
    laptop_id              INT AUTO_INCREMENT PRIMARY KEY,
    asset_tag              VARCHAR(30) NOT NULL UNIQUE,
    status                 ENUM('AVAILABLE', 'ALLOCATED', 'RETURNED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    allocated_to_student_id INT NULL,
    allocated_at           TIMESTAMP NULL,
    FOREIGN KEY (allocated_to_student_id) REFERENCES students(student_id)
);

CREATE TABLE IF NOT EXISTS feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT NOT NULL,
    rating      TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     VARCHAR(1000),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NULL,
    action     VARCHAR(50) NOT NULL,
    details    VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- --------------------------------------------------------------
-- Seed an admin account ONLY if the users table is completely empty,
-- so re-running this never duplicates or overwrites real accounts.
-- Login: admin / admin123
-- --------------------------------------------------------------
INSERT INTO users (username, password_hash, role, full_name, email)
SELECT 'admin', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'ADMIN', 'System Administrator', 'admin@university.ac.za'
WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO services (service_name, description, is_active)
SELECT * FROM (SELECT 'Laptop Collection', 'Collection of institutional laptops for registered students', TRUE) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM services);
