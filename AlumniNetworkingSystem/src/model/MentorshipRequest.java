package model;

import java.sql.Timestamp;

public class MentorshipRequest {
    private int id;
    private int studentId;
    private int alumniId;
    private String status; // PENDING, ACCEPTED, REJECTED
    private Timestamp requestDate;
    private String notes;

    // Optional fields for easy display
    private String studentName;
    private String alumniName;

    public MentorshipRequest() {
        this.status = "PENDING";
        this.requestDate = new Timestamp(System.currentTimeMillis());
    }

    public MentorshipRequest(int id, int studentId, int alumniId, String status, Timestamp requestDate, String notes) {
        this.id = id;
        this.studentId = studentId;
        this.alumniId = alumniId;
        this.status = status;
        this.requestDate = requestDate;
        this.notes = notes;
    }

    public MentorshipRequest(int studentId, int alumniId, String notes) {
        this();
        this.studentId = studentId;
        this.alumniId = alumniId;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getAlumniId() {
        return alumniId;
    }

    public void setAlumniId(int alumniId) {
        this.alumniId = alumniId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAlumniName() {
        return alumniName;
    }

    public void setAlumniName(String alumniName) {
        this.alumniName = alumniName;
    }

    @Override
    public String toString() {
        return "MentorshipRequest{" +
                "id=" + id +
                ", studentId=" + studentId +
                (studentName != null ? " (" + studentName + ")" : "") +
                ", alumniId=" + alumniId +
                (alumniName != null ? " (" + alumniName + ")" : "") +
                ", status='" + status + '\'' +
                ", requestDate=" + requestDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
