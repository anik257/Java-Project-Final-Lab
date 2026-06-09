# Alumni Networking System

A console-based Java application designed to connect university students and alumni. It supports user profiles, role-based dashboards (Student, Alumni, Admin), mentor searching, mentorship requests, and networking event registrations.

---

## Directory Structure

```
AlumniNetworkingSystem/
│
├── src/
│   ├── model/
│   │   ├── User.java
│   │   ├── Student.java
│   │   ├── Alumni.java
│   │   ├── MentorshipRequest.java
│   │   └── Event.java
│   │
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── StudentDAO.java
│   │   ├── AlumniDAO.java
│   │   ├── MentorshipRequestDAO.java
│   │   └── EventDAO.java
│   │
│   ├── service/
│   │   ├── UserService.java
│   │   ├── StudentService.java
│   │   ├── AlumniService.java
│   │   ├── RequestService.java
│   │   └── EventService.java
│   │
│   ├── util/
│   │   ├── DBConnection.java
│   │   └── MockDatabase.java
│   │
│   ├── main/
│   │   └── Main.java
│   │
│   ├── test/
│   │   └── DemoTest.java
│   │
│   └── db.properties
│
├── database/
│   └── schema.sql
│
├── lib/
│   └── mysql-connector-j.jar
│
└── README.md
```

---

## Features

- **Role-Based Access**:
  - **Students**: Search available mentors by company/industry, send mentorship requests, view upcoming networking events, and RSVP to them.
  - **Alumni**: Maintain professional profile, view and respond to student mentorship requests (Accept/Reject), toggle mentor availability, and create networking events.
  - **Admins**: View all registered users in the system, create networking events, and view attendee lists for any scheduled event.
- **Dual Database Mode (MySQL & Mock In-Memory)**:
  - If a MySQL server is configured in `db.properties` and active, the system connects using the JDBC driver in `lib/mysql-connector-j.jar`.
  - If MySQL is unreachable, the system prints a warning and automatically falls back to a mock in-memory data store. This allows testing all features out-of-the-box.

---

## Setup & Running Guide

### 1. Database Setup (Optional)
If you wish to use MySQL:
1. Start your MySQL Server.
2. Log in and execute the script inside `database/schema.sql` to build the `alumni_db` database and its tables.
3. Edit `src/db.properties` to specify your MySQL host, port, username, and password:
   ```properties
   db.url=jdbc:mysql://localhost:3306/alumni_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.username=root
   db.password=your_mysql_password
   ```

*Note: If you skip database setup, the application will automatically boot in **In-Memory Fallback Mode** with mock data.*

### 2. Compilation
Compile all Java source files from the `AlumniNetworkingSystem` directory:
```powershell
# Open terminal inside the AlumniNetworkingSystem directory
javac -cp "lib/*" -d bin src/model/*.java src/util/*.java src/dao/*.java src/service/*.java src/controller/*.java src/main/*.java src/test/*.java
```

### 3. Run Automated Tests
Run the test suite to verify services and model operations:
```powershell
java -cp "bin;lib/*" test.DemoTest
```

### 4. Run Main Application
Start the graphical user interface (GUI) application:
```powershell
java -cp "bin;lib/*" main.Launcher
```
When running `Main` for the first time, you can log in as Admin using the default seeded account:
- **Email**: `admin@alumni.com`
- **Password**: `admin123`
- **Role**: `ADMIN`
