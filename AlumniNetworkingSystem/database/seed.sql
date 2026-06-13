-- Seed Data for Alumni Networking System
USE alumni_db;

-- Seed Users (Admin, Alumni, Students)
INSERT INTO users (id, name, email, password, role) VALUES
(1, 'System Admin', 'admin@alumni.com', 'admin123', 'ADMIN'),
(2, 'Jane Smith', 'jane@alumni.com', 'jane123', 'ALUMNI'),
(3, 'John Doe', 'john@alumni.com', 'john123', 'ALUMNI'),
(4, 'Alice Johnson', 'alice@alumni.com', 'alice123', 'ALUMNI'),
(5, 'Bob Lee', 'bob@student.com', 'bob123', 'STUDENT'),
(6, 'Charlie Brown', 'charlie@student.com', 'charlie123', 'STUDENT');

-- Seed Alumni specific details
INSERT INTO alumni (user_id, job_title, company, industry, years_of_experience, is_willing_to_mentor) VALUES
(2, 'Senior Software Engineer', 'Google', 'Tech', 5, TRUE),
(3, 'Product Manager', 'Meta', 'Tech', 8, TRUE),
(4, 'Data Scientist', 'Amazon', 'E-Commerce', 3, FALSE);

-- Seed Student specific details
INSERT INTO students (user_id, major, graduation_year, current_status) VALUES
(5, 'Computer Science', 2027, 'Looking for internships'),
(6, 'Software Engineering', 2026, 'Employed');

-- Seed Events
INSERT INTO events (id, title, description, event_date, location, organizer_id) VALUES
(1, 'Alumni Tech Panel 2026', 'Hear from alumni working at FAANG companies', DATE_ADD(NOW(), INTERVAL 5 DAY), 'Main Auditorium & Zoom', 1),
(2, 'Resume Review Workshop', 'Get your resume reviewed 1-on-1 by experienced alumni', DATE_ADD(NOW(), INTERVAL 12 DAY), 'Career Services Room 302', 2);

-- Seed RSVPs
INSERT INTO event_rsvps (event_id, user_id) VALUES
(1, 6);

-- Seed Mentorship Request (Charlie requesting John)
INSERT INTO mentorship_requests (id, student_id, alumni_id, notes, status, request_date) VALUES
(1, 6, 3, 'Hello John, I would love to learn more about product management at Meta.', 'PENDING', NOW() - INTERVAL 1 DAY);
