package service;

import dao.StudentDAO;
import model.Student;

public class StudentService {
    private StudentDAO studentDAO = new StudentDAO();
    private UserService userService = new UserService();

    public boolean registerStudent(String name, String email, String password, String major, int graduationYear) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Registration fields (name, email, password) cannot be empty.");
            return false;
        }
        if (userService.isEmailTaken(email)) {
            System.out.println("Email is already registered in the system.");
            return false;
        }
        
        Student student = new Student(name, email, password, major, graduationYear, "Looking for opportunities");
        return studentDAO.registerStudent(student);
    }

    public Student getStudentProfile(int id) {
        return studentDAO.getStudentById(id);
    }

    public boolean updateProfile(Student student) {
        if (student == null) return false;
        return studentDAO.updateStudent(student);
    }
}
