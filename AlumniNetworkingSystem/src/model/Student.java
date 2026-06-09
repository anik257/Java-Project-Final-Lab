package model;

public class Student extends User {
    private String major;
    private int graduationYear;
    private String currentStatus; // e.g., "Looking for internships", "Employed", etc.

    public Student() {
        super();
        this.setRole("STUDENT");
    }

    public Student(int id, String name, String email, String password, String major, int graduationYear, String currentStatus) {
        super(id, name, email, password, "STUDENT");
        this.major = major;
        this.graduationYear = graduationYear;
        this.currentStatus = currentStatus;
    }

    public Student(String name, String email, String password, String major, int graduationYear, String currentStatus) {
        super(name, email, password, "STUDENT");
        this.major = major;
        this.graduationYear = graduationYear;
        this.currentStatus = currentStatus;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", major='" + major + '\'' +
                ", graduationYear=" + graduationYear +
                ", currentStatus='" + currentStatus + '\'' +
                '}';
    }
}
