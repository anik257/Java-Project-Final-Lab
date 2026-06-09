package model;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private int organizerId;
    
    // Optional field for easy display
    private String organizerName;

    public Event() {}

    public Event(int id, String title, String description, LocalDateTime date, String location, int organizerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.organizerId = organizerId;
    }

    public Event(String title, String description, LocalDateTime date, String location, int organizerId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.organizerId = organizerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", organizerId=" + organizerId +
                (organizerName != null ? " (" + organizerName + ")" : "") +
                '}';
    }
}
