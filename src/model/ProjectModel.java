package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * Das ProjectModel verwaltet die Liste aller Aufgaben.
 * Es nutzt PropertyChangeSupport, um die View über Änderungen zu informieren,
 * ohne eine direkte Abhängigkeit zur View zu haben (MVC-Pattern).
 */

public class ProjectModel {

    private ArrayList<Task> tasks;

    // Hilfklasse Event-Listeners
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * constructor: initialization the empty task list
     */

    public ProjectModel() {
        this.tasks = new ArrayList<Task>();
    }

    /**
     * Fügt eine neue Aufgabe zur Liste hinzu und informiert die Listener.
     * @param task Die hinzuzufügende Aufgabe.
     */

    public void addTask(Task task) {
        // Alten Zustand speichern
        ArrayList<Task> oldTasks = new ArrayList<>(this.tasks);

        this.tasks.add(task);

        // Listener über Aenderung informieren
        pcs.firePropertyChange("tasks", oldTasks, this.tasks);
    }

    /**
     * Ermöglicht es anderen Klassen (z.B. der View oder dem Controller),
     * sich für Änderungen an diesem Model anzumelden.
     * @param listener Das Objekt, das benachrichtigt werden möchte.
     */

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
