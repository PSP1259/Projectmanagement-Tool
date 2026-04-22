package controller;

import model.ProjectModel;
import model.Task;
import view.MainView;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * The Controller acts as the brain of the application.
 * It interprets user actions (like button clicks) and updates the Model accordingly.
 * It implements PropertyChangeListener to react to changes in the Model properties.
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

        // 1. The Controller registers itself with the Model to be notified of changes
        this.model.addPropertyChangeListener(this);

        // 2. Register Event-Handler (ActionListener) for the View's Add Button
        this.view.getAddButton().addActionListener((ActionEvent e) -> handleAddTask());
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
