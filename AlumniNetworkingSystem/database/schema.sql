-- SQL Database Schema for Alumni Networking System
CREATE DATABASE IF NOT EXISTS alumni_db;
USE alumni_db;

-- 1. Users Table (Core credentials and role-based access)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'ALUMNI', 'ADMIN'))
);

-- 2. Student Table (Child table extending users)
CREATE TABLE IF NOT EXISTS student (
    user_id INT PRIMARY KEY,
    department VARCHAR(100) NOT NULL,
    graduation_year INT NOT NULL,
    current_status VARCHAR(100) DEFAULT 'Looking for opportunities',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Alumni Table (Child table extending users)
CREATE TABLE IF NOT EXISTS alumni (
    user_id INT PRIMARY KEY,
    department VARCHAR(100) NOT NULL,
    company VARCHAR(100),
    job_title VARCHAR(100),
    skills TEXT, -- Comma-separated or descriptive skills (e.g. 'Java, SQL, REST APIs')
    years_of_experience INT DEFAULT 0,
    is_willing_to_mentor BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
