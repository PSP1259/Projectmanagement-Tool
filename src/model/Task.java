package model;

/**
 * Repräsentiert eine einzelne Aufgabe (Task) im Projektmanagement-Tool.
 * Diese Klasse ist ein reines Datenobjekt (Model) und enthält keine GUI-Logik.
 * Sie wendet das Prinzip der Kapselung strikt an.
 */
public class Task {

    // encapsulation
    private String title;
    private String description;
    private String status;

    /**
     * constructor for a new Task
     *
     * @param title Der Titel der Aufgabe.
     * @param description Eine kurze Beschreibung der Aufgabe.
     * @param status Der aktuelle Bearbeitungsstatus (z.B. "Offen", "In Arbeit").
     */

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
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

    @Override
    public String toString() {
        return title + " :" + status;
    }
}
