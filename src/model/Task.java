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
    private int timeSpentInSeconds;

    /**
     * constructor for a new Task
     *
     * @param title Der Titel der Aufgabe.
     * @param description Eine kurze Beschreibung der Aufgabe.
     * @param status Der aktuelle Bearbeitungsstatus ('Open' or 'Done').
     */

    public Task(String title, String description, String status) {
        this(title, description, status, 0);
    }

    public Task(String title, String description, String status, int timeSpentInSeconds) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.timeSpentInSeconds = timeSpentInSeconds;
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

    @Override
    public String toString() {
        int minutes = timeSpentInSeconds / 60;
        return title + " [" + status + "] - Time: " + minutes + " min " + timeSpentInSeconds + "sec";
    }
}
