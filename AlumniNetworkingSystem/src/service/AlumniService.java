package service;

import dao.AlumniDAO;
import model.Alumni;

import java.util.List;

public class AlumniService {
    private AlumniDAO alumniDAO = new AlumniDAO();
    private UserService userService = new UserService();

    public boolean registerAlumni(String name, String email, String password, String jobTitle, String company, String industry, int yearsOfExperience, boolean isWillingToMentor) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Registration fields (name, email, password) cannot be empty.");
            return false;
        }
        if (userService.isEmailTaken(email)) {
            System.out.println("Email is already registered in the system.");
            return false;
        }

        Alumni alumni = new Alumni(name, email, password, jobTitle, company, industry, yearsOfExperience, isWillingToMentor);
        return alumniDAO.registerAlumni(alumni);
    }

    public Alumni getAlumniProfile(int id) {
        return alumniDAO.getAlumniById(id);
    }

    public boolean updateProfile(Alumni alumni) {
        if (alumni == null) return false;
        return alumniDAO.updateAlumni(alumni);
    }

    public List<Alumni> getAvailableMentors() {
        return alumniDAO.getAllMentors();
    }

    public List<Alumni> searchMentors(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAvailableMentors();
        }
        return alumniDAO.searchAlumni(query);
    }
}
