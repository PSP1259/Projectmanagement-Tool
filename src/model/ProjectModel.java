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

    /**
     * Removes a task from the list based on its title.
     *
     * @param title The title of the task to be removed.
     */
    public void removeTaskByTitle(String title) {
        Task taskToRemove = findTaskByTitle(title);

        if (taskToRemove != null) {
            tasks.remove(taskToRemove);

            // Since the list structure actually changed (an item was removed),
            // we can trigger the standard property change event.
            utils.DataStorage.saveTasks(this.tasks);
            pcs.firePropertyChange("tasks", null, this.tasks);
        }
    }

    /**
     * Toggles the status of a task between "Open" and "Done".
     *
     * @param title The title of the task to toggle.
     */
    public void toggleTaskStatus(String title) {

        Task taskToToggle = findTaskByTitle(title);

        if (taskToToggle != null) {
            if (taskToToggle.getStatus().equals("Open")) {
                taskToToggle.setStatus("Done");
            } else {
                taskToToggle.setStatus("Open");
            }

            forceUpdateAndSave();
        }
    }

    /**
     * Force an update in the View and save the data
     * Used when the properties of an existing task have changed (e.g., time has been logged).
     */
    public void forceUpdateAndSave() {
        utils.DataStorage.saveTasks(this.tasks);
        pcs.firePropertyChange("tasks", null, this.tasks);
    }

    /**
     * Searches for a task in the list by its exact title (case-insensitive).
     *
     * @param title The title of the task to find.
     * @return The Task object if found, or null if no task matches the title.
     */
    public Task findTaskByTitle(String title) {
        for (Task t : tasks) {
            if (t.getTitle().equalsIgnoreCase(title.trim())) {
                return t;
            }
        }
        return null;
    }

}
