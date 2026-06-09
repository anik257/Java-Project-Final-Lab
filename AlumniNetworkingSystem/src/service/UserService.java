package service;

import dao.UserDAO;
import model.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("Email and password cannot be empty.");
            return null;
        }
        return userDAO.authenticate(email, password);
    }

    public boolean isEmailTaken(String email) {
        return userDAO.isEmailExists(email);
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
}
