package controller;

import model.ProjectModel;
import model.Task;
import view.MainView;

// Import helper class
import utils.DataStorage;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.TextField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
        this.view.getStartTimerButton().addActionListener((ActionEvent e) -> handleStartTimer());
        this.view.getEditButton().addActionListener((ActionEvent e) -> handleEditTask());

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
        String initialTimeStr = view.getInitialTimeInput();

        // Set a time
        int initialSeconds = 0;
        try {
            initialSeconds = Integer.parseInt(initialTimeStr.trim()) * 60;
        }
        catch (NumberFormatException e) {
        }

        // Error handling: ensure title is not empty
        if (title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Task title cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // default status: "Open"
        Task newTask = new Task(title, description, "Open", initialSeconds);

        model.addTask(newTask);

        view.clearInputs();
    }

    // Opens an input dialog to ask for the task title and tells the model to remove it.
    private void handleDeleteTask() {

        String titleToDelete = JOptionPane.showInputDialog(view, "Enter the exact title of the task to delete:");

        // Validation
        if (titleToDelete != null && !titleToDelete.trim().isEmpty()) {
            model.removeTaskByTitle(titleToDelete);
        }
    }

    // Opens an input dialog to ask for the task title and tells the model to change the status.
    private void handleToggleStatus() {
        String titleToToggle = JOptionPane.showInputDialog(view, "Enter the title of the task to mark Open/Done:");

        // Validation
        if (titleToToggle != null && !titleToToggle.trim().isEmpty()) {
            model.toggleTaskStatus(titleToToggle);
        }
    }

    // Opens input dialog → resolves Task → starts tracking dialog
    /**
     * Initiates the time tracking process for a specific task.
     * Uses the Model's search function to locate the task.
     */
    private void handleStartTimer() {
        String titleToTrack = JOptionPane.showInputDialog(view, "Enter the exact title of the task to track time for:");

        if (titleToTrack != null && !titleToTrack.trim().isEmpty()) {

            Task taskToTrack = model.findTaskByTitle(titleToTrack);

            if (taskToTrack != null) {
                startTimerDialog(taskToTrack);
            } else {
                JOptionPane.showMessageDialog(view, "Task not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Modal dialog for time tracking.
     * Starts background Timer → updates UI every second → persists result on stop.
     */
    private void startTimerDialog(Task task) {

        // Modal dialog setup (blocks main UI)
        JDialog timerDialog = new JDialog(view, "Tracking Time: " + task.getTitle(), true);
        timerDialog.setSize(300, 150);
        timerDialog.setLayout(new BorderLayout());
        timerDialog.setLocationRelativeTo(view);

        JLabel timeLabel = new JLabel("Time tracking active...", SwingConstants.CENTER);
        timerDialog.add(timeLabel, BorderLayout.CENTER);

        JButton stopButton = new JButton("Stop Tracking");
        timerDialog.add(stopButton, BorderLayout.SOUTH);

        // Timer state
        Timer timer = new Timer();
        final int[] secondsPassed = {0};

        // Periodic task (1s interval)
        TimerTask trackingTask = new TimerTask() {
            @Override
            public void run() {
                secondsPassed[0]++;
                timeLabel.setText("Time elapsed: " + secondsPassed[0] + " seconds");
            }
        };

        // Schedule execution (0 delay, 1s period)
        timer.schedule(trackingTask, 0, 1000);

        // Stop event → cleanup + persist
        stopButton.addActionListener(e -> {
            timer.cancel();                         // Stops the timer
            timerDialog.dispose();                  // Closes the input

            task.addTimeInSeconds(secondsPassed[0]);

            model.forceUpdateAndSave();
        });

        timerDialog.setVisible(true);
    }

    /**
     * Opens a dialog to edit an existing task's title and description.
     * Finds the task using the model's search function.
     */
    private void handleEditTask() {
        String titleToEdit = JOptionPane.showInputDialog(view, "Enter the exact title of the task to edit:");

        if (titleToEdit != null && !titleToEdit.trim().isEmpty()) {
            Task taskToEdit = model.findTaskByTitle(titleToEdit);

            if (taskToEdit != null) {
                JTextField titleField = new JTextField(taskToEdit.getTitle());
                JTextField descriptionField = new JTextField(taskToEdit.getDescription());

                Object[] inputFields = {
                        "New Title:", titleField,
                        "New Description:", descriptionField
                };

                int result = JOptionPane.showConfirmDialog(view, inputFields, "Edit Task", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String newTitle = titleField.getText().trim();

                    // Validation
                    if (newTitle.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    taskToEdit.setTitle(newTitle);
                    taskToEdit.setDescription(descriptionField.getText());

                    model.forceUpdateAndSave();
                }
            } else {
                JOptionPane.showMessageDialog(view, "Task not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
