package model;

public class Alumni extends User {
    private String jobTitle;
    private String company;
    private String industry;
    private int yearsOfExperience;
    private boolean willingToMentor;

    public Alumni() {
        super();
        this.setRole("ALUMNI");
        this.willingToMentor = true;
    }

    public Alumni(int id, String name, String email, String password, String jobTitle, String company, String industry, int yearsOfExperience, boolean willingToMentor) {
        super(id, name, email, password, "ALUMNI");
        this.jobTitle = jobTitle;
        this.company = company;
        this.industry = industry;
        this.yearsOfExperience = yearsOfExperience;
        this.willingToMentor = willingToMentor;
    }

    public Alumni(String name, String email, String password, String jobTitle, String company, String industry, int yearsOfExperience, boolean willingToMentor) {
        super(name, email, password, "ALUMNI");
        this.jobTitle = jobTitle;
        this.company = company;
        this.industry = industry;
        this.yearsOfExperience = yearsOfExperience;
        this.willingToMentor = willingToMentor;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public boolean isWillingToMentor() {
        return willingToMentor;
    }

    public void setWillingToMentor(boolean willingToMentor) {
        this.willingToMentor = willingToMentor;
    }

    @Override
    public String toString() {
        return "Alumni{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", company='" + company + '\'' +
                ", industry='" + industry + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", willingToMentor=" + willingToMentor +
                '}';
    }
}
