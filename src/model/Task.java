package model;

/**
 * Repräsentiert eine einzelne Aufgabe (Task) im Projektmanagement-Tool.
 * Diese Klasse ist ein reines Datenobjekt (Model) und enthält keine GUI-Logik.
 * Sie wendet das Prinzip der Kapselung strikt an.
 */
public class Task {

    private String title;
    private String description;
    private String status;
    private String creationDate;
    private String deadline;
    private int timeSpentInSeconds;

    // Legacy constructor for creating a basic Task
    public Task(String title, String description, String status) {
        this(title, description, status, 0, "", "");
    }

    // Full constructor to initialize a Task with all 6 attributes
    public Task(String title, String description, String status, int timeSpentInSeconds, String creationDate, String deadline) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.timeSpentInSeconds = timeSpentInSeconds;
        this.creationDate = creationDate;
        this.deadline = deadline;
    }

    public String getTitle(){
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTimeSpentInSeconds () {
        return timeSpentInSeconds;
    }

    public void addTimeInSeconds(int seconds) {
        this.timeSpentInSeconds += seconds;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return title + " [" + status + "]";
    }
}
