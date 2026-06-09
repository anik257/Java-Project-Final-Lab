package service;

import dao.MentorshipRequestDAO;
import model.MentorshipRequest;

import java.util.List;

public class RequestService {
    private MentorshipRequestDAO requestDAO = new MentorshipRequestDAO();

    public boolean sendMentorshipRequest(int studentId, int alumniId, String notes) {
        // Simple business validation: check if request already exists
        List<MentorshipRequest> existing = requestDAO.getRequestsByStudent(studentId);
        for (MentorshipRequest r : existing) {
            if (r.getAlumniId() == alumniId && r.getStatus().equals("PENDING")) {
                System.out.println("You already have a pending mentorship request to this alumni.");
                return false;
            }
        }

        MentorshipRequest request = new MentorshipRequest(studentId, alumniId, notes);
        return requestDAO.createRequest(request);
    }

    public boolean respondToRequest(int requestId, boolean accept) {
        MentorshipRequest r = requestDAO.getRequestById(requestId);
        if (r == null) {
            System.out.println("Mentorship request not found.");
            return false;
        }
        if (!r.getStatus().equals("PENDING")) {
            System.out.println("This request has already been processed (Current status: " + r.getStatus() + ").");
            return false;
        }

        String status = accept ? "ACCEPTED" : "REJECTED";
        return requestDAO.updateRequestStatus(requestId, status);
    }

    public List<MentorshipRequest> getStudentRequests(int studentId) {
        return requestDAO.getRequestsByStudent(studentId);
    }

    public List<MentorshipRequest> getAlumniRequests(int alumniId) {
        return requestDAO.getRequestsByAlumni(alumniId);
    }
}
