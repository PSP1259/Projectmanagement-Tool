package model;

import utils.DataStorage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * Manages the list of tasks for the project management tool.
 * <p>
 * This class serves as the Model in the MVC architecture. It utilizes
 * {@link PropertyChangeSupport} to notify the View about data changes
 * without maintaining a direct dependency on the View itself.
 */
public class ProjectModel {

    /** The internal list of tasks managed by this model. */
    private ArrayList<Task> tasks;

    /** The support mechanism for managing event listeners and firing property changes. */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Creates a new project model initialized with an empty list of tasks.
     */
    public ProjectModel() {
        this.tasks = new ArrayList<Task>();
    }

    /**
     * Sets the entire list of tasks.
     * <p>
     * This method is primarily used during the initial load from the data storage.
     *
     * @param tasks  the list of tasks loaded from storage, not null
     */
    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list, saves the state, and notifies listeners.
     * <p>
     * The data is automatically saved to the persistent XML file, and a
     * property change event is fired to update the view.
     *
     * @param task  the task to be added, not null
     */
    public void addTask(Task task) {
        ArrayList<Task> oldTasks = new ArrayList<>(this.tasks);
        this.tasks.add(task);

        // 1. Save data to XML using Properties
        DataStorage.saveTasks(this.tasks);

        // 2. Notify listeners that the data has changed
        pcs.firePropertyChange("tasks", oldTasks, this.tasks);
    }

    /**
     * Gets a shallow copy of the current list of tasks.
     *
     * @return a new list containing the currently managed tasks, not null
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Adds a property change listener to receive state change events.
     *
     * @param listener  the listener to be added, not null
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Removes a task from the list based on its exact title.
     * <p>
     * If the task is successfully found and removed, the updated list is saved
     * to the storage and listeners are notified about the structural change.
     *
     * @param title  the title of the task to be removed, may be null
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
     * Toggles the status of a specific task between "Open" and "Done".
     * <p>
     * If the task is successfully found, its status is inverted, the updated
     * list is saved to persistent storage, and listeners are notified.
     *
     * @param title  the exact title of the task to toggle, not null
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
     * Forces an update in the view and saves the current data.
     * <p>
     * This method is typically used when the internal properties of an existing task
     * have changed (such as logged time) without altering the list structure itself.
     */
    public void forceUpdateAndSave() {
        utils.DataStorage.saveTasks(this.tasks);
        pcs.firePropertyChange("tasks", null, this.tasks);
    }

    /**
     * Finds a task in the managed list by its exact title.
     * <p>
     * The search compares the titles in a case-insensitive manner after trimming whitespace.
     *
     * @param title  the title to search for, not null
     * @return the matching task, or null if no match is found
     */
    public Task findTaskByTitle(String title) {
        for (Task t : tasks) {
            if (t.getTitle().equalsIgnoreCase(title.trim())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Gets a filtered list of tasks based on status and assignee criteria.
     * <p>
     * The status filter requires an exact match, whereas the assignee filter
     * checks if the given string is contained within the task's assignees.
     * Providing "All" for either parameter bypasses that specific filter.
     *
     * @param statusFilter  the status to filter by, or "All" for no status filtering, not null
     * @param assigneeFilter  the assignee name to filter by, or "All" for no assignee filtering, not null
     * @return a new list containing only the tasks that match both filters, not null
     */
    public ArrayList<Task> getFilteredTasks(String statusFilter, String assigneeFilter) {
        ArrayList<Task> filteredList = new ArrayList<>();

        for (Task t : tasks) {
            // 1. Check Status
            boolean statusMatch = statusFilter.equals("All") || t.getStatus().equals(statusFilter);

            // 2. Check Assignee
            // We use contains() and toLowerCase() to find the name within the comma-separated string
            boolean assigneeMatch = assigneeFilter.equals("All") ||
                    t.getAssignees().toLowerCase().contains(assigneeFilter.toLowerCase());

            if (statusMatch && assigneeMatch) {
                filteredList.add(t);
            }
        }
        return filteredList;
    }
}


