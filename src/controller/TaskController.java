package controller;

import model.ProjectModel;
import model.Task;
import view.MainView;

// Import helper class
import utils.DataStorage;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * The TaskController handles the application startup and user interactions.
 * It initializes the view with data loaded from storage.
 */

public class TaskController implements PropertyChangeListener {

    private final ProjectModel model;
    private final MainView view;

    /**
     * Constructor that links the Model and the View.
     * Registers the necessary listeners to handle user interaction and model updates.
     *
     * @param model The data model containing business logic.
     * @param view  The visual representation of the application.
     */

    public TaskController(ProjectModel model, MainView view) {
        this.model = model;
        this.view = view;

        this.model.addPropertyChangeListener(this);
        this.view.getAddButton().addActionListener((ActionEvent e) -> handleAddTask());
        this.view.getDeleteButton().addActionListener((ActionEvent e) -> handleDeleteTask());
        this.view.getToggleStatusButton().addActionListener((ActionEvent e) -> handleToggleStatus());

        // INITIAL LOAD LOGIC
        // 1. Load existing tasks from the XML file
        ArrayList<Task> loadedTasks = DataStorage.loadTasks();

        // 2. Put the tasks into the model
        this.model.setTasks(loadedTasks);

        // 3. Manually update the view for the first time
        this.view.updateTaskList(loadedTasks);
    }

    /**
     * This method is executed when the user clicks the "Add Task" button.
     * It reads inputs from the View, validates them, and updates the Model.
     */
    private void handleAddTask() {
        String title = view.getTitleInput();
        String description = view.getDescriptionInput();

        // Error handling: ensure title is not empty
        if (title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Task title cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // default status: "Open"
        Task newTask = new Task(title, description, "Open");

        model.addTask(newTask);

        view.clearInputs();
    }

    // Opens an input dialog to ask for the task title and tells the model to remove it.
    private void handleDeleteTask() {

        String titleToDelete = JOptionPane.showInputDialog(view, "Enter the exact title of the task to delete:");

        if (titleToDelete != null && !titleToDelete.trim().isEmpty()) {
            model.removeTaskByTitle(titleToDelete);
        }
    }

    // Opens an input dialog to ask for the task title and tells the model to change the status.
    private void handleToggleStatus() {
        String titleToToggle = JOptionPane.showInputDialog(view, "Enter the title of the task to mark Open/Done:");

        // Is the Input valid?
        if (titleToToggle != null && !titleToToggle.trim().isEmpty()) {
            model.toggleTaskStatus(titleToToggle);
        }
    }

    /**
     * This method is automatically invoked when a property in the Model changes.
     * It updates the View to reflect the new state.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        // Check if the changed property is our "tasks" list
        if ("tasks".equals(evt.getPropertyName())) {
            // Extract the new list of tasks from the event and cast it safely
            @SuppressWarnings("unchecked")
            ArrayList<Task> updatedTasks = (ArrayList<Task>) evt.getNewValue();

            // The Controller updates the View with the new state
            view.updateTaskList(updatedTasks);
        }
    }
}
