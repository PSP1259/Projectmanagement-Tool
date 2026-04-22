package model;

// Import helper class
import utils.DataStorage;

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

    // Event-Listeners
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public ProjectModel() {
        this.tasks = new ArrayList<Task>();
    }

    /**
     * Sets the entire list of tasks (used during initial load).
     * @param tasks The list of tasks loaded from storage.
     */
    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task and triggers an automatic save to the XML file.
     * @param task The task to be added.
     */
    public void addTask(Task task) {
        ArrayList<Task> oldTasks = new ArrayList<>(this.tasks);
        this.tasks.add(task);

        // 1. Save data to XML using Properties
        DataStorage.saveTasks(this.tasks);

        // 2. Notify listeners that the data has changed
        pcs.firePropertyChange("tasks", oldTasks, this.tasks);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}
