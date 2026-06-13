-- SQL Database Schema for Alumni Networking System
CREATE DATABASE IF NOT EXISTS alumni_db;
USE alumni_db;

-- 1. Users Table (Core credentials and role-based access)
DROP TABLE IF EXISTS event_rsvps;
DROP TABLE IF EXISTS mentorship_requests;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS alumni;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'ALUMNI', 'ADMIN'))
);

-- 2. Students Table (Child table extending users, matches StudentDAO.java)
CREATE TABLE students (
    user_id INT PRIMARY KEY,
    major VARCHAR(100) NOT NULL,
    graduation_year INT NOT NULL,
    current_status VARCHAR(100) DEFAULT 'Looking for opportunities',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Alumni Table (Child table extending users, matches AlumniDAO.java)
CREATE TABLE alumni (
    user_id INT PRIMARY KEY,
    job_title VARCHAR(100),
    company VARCHAR(100),
    industry VARCHAR(100),
    years_of_experience INT DEFAULT 0,
    is_willing_to_mentor BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Events Table (Matches EventDAO.java)
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    location VARCHAR(100) NOT NULL,
    organizer_id INT,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. Event RSVPs Table (Matches EventDAO.java)
CREATE TABLE event_rsvps (
    event_id INT,
    user_id INT,
    PRIMARY KEY (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Mentorship Requests Table (Matches MentorshipRequestDAO.java)
CREATE TABLE mentorship_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    alumni_id INT,
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (alumni_id) REFERENCES users(id) ON DELETE CASCADE
);
