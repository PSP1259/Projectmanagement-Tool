package controller;

import model.ProjectModel;
import model.Task;
import view.MainView;

// Import helper class
import utils.DataStorage;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        this.view.getFilterComboBox().addActionListener(e -> applyFilterAndSort());
        this.view.getSortComboBox().addActionListener(e -> applyFilterAndSort());
        this.view.getAssigneeFilterComboBox().addActionListener(e -> applyFilterAndSort());
        this.view.getCommentButton().addActionListener(e -> handleAddComment());

        // INITIAL LOAD LOGIC
        // 1. Load existing tasks from the XML file
        ArrayList<Task> loadedTasks = DataStorage.loadTasks();

        // 2. Put the tasks into the model
        this.model.setTasks(loadedTasks);

        // 3. Manually update the view for the first time
        this.view.updateTaskList(loadedTasks);

        applyFilterAndSort();
    }

    /**
     * This method is executed when the user clicks the "Add Task" button.
     * It reads inputs from the View, validates them, and updates the Model.
     */
    private void handleAddTask() {
        String title = view.getTitleInput();
        String description = view.getDescriptionInput();
        String initialTimeStr = view.getInitialTimeInput();
        String deadline = view.getDeadlineInput().trim();
        String assignees = view.getAssigneeInput().trim();

        // Error handling: ensure title is not empty
        if (title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Task title cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Set a time
        int initialSeconds = 0;
        try {
            initialSeconds = Integer.parseInt(initialTimeStr.trim()) * 60;
        } catch (NumberFormatException e) { // Default to 0
        }

        // Strict Date Format Validation using Regex
        // Ensures exactly DD.MM.YYYY format
        if (!deadline.isEmpty() && !deadline.matches("^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.\\d{4}$")) {
            JOptionPane.showMessageDialog(view, "Invalid Deadline Format!\nPlease use exactly: dd.mm.yyyy (e.g. 24.04.2026)", "Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Auto-generate the current creation date
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
        String creationDate = formatter.format(new Date());

        // default status: "Open"
        Task newTask = new Task(title, description, "Open", initialSeconds, creationDate, deadline, assignees, "");

        model.addTask(newTask);

        // Safe the assignee in the dropdown
        if (!assignees.isEmpty()) {
            view.addAssigneeToDropdown(assignees);
        }
        view.clearInputs();
    }

    // Opens an input dialog to ask for the task title and tells the model to remove it
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
                int currentMinutes = taskToEdit.getTimeSpentInSeconds() / 60;
                JTextField timeField = new JTextField(String.valueOf(currentMinutes));
                String currentDeadline = taskToEdit.getDeadline().equals("None") ? "" : taskToEdit.getDeadline();
                JTextField deadlineField = new JTextField(currentDeadline);
                JTextField assigneesField = new JTextField(taskToEdit.getAssignees());

                Object[] inputFields = {
                        "Title:", titleField,
                        "Description:", descriptionField,
                        "Total Time Spent (in minutes):", timeField,
                        "Deadline (dd.mm.yyyy or leave empty):", deadlineField,
                        "Assignees (comma-separated):", assigneesField,
                };

                int result = JOptionPane.showConfirmDialog(view, inputFields, "Edit Task", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String newTitle = titleField.getText().trim();
                    String newDeadline = deadlineField.getText().trim();

                    // Validation: title cannot be empty
                    if (newTitle.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validation: deadline regex
                    if (!newDeadline.isEmpty() && !newDeadline.matches("^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.\\d{4}$")) {
                        JOptionPane.showMessageDialog(view, "Invalid Deadline Format!\nPlease use exactly: dd.MM.yyyy", "Format Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validation: time in int and convert in seconds
                    int newTimeInSeconds = taskToEdit.getTimeSpentInSeconds(); // Fallback auf alten Wert
                    try {
                        newTimeInSeconds = Integer.parseInt(timeField.getText().trim()) * 60;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(view, "Invalid time entered. Time was not updated.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                    taskToEdit.setTitle(newTitle);
                    taskToEdit.setDescription(descriptionField.getText());
                    taskToEdit.setDeadline(newDeadline.isEmpty() ? "None" : newDeadline);
                    taskToEdit.setTimeSpentInSeconds(newTimeInSeconds);
                    taskToEdit.setAssignees(assigneesField.getText().trim());

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
        if ("tasks".equals(evt.getPropertyName())) {
            applyFilterAndSort(); // Ruft unsere neue Kombi-Methode auf
        }
    }


    // Applies and updates the currently selected filter and sorting criteria
    private void applyFilterAndSort() {
        String statusFilter = (String) view.getFilterComboBox().getSelectedItem();
        String assigneeFilter = (String) view.getAssigneeFilterComboBox().getSelectedItem();
        String selectedSort = (String) view.getSortComboBox().getSelectedItem();

        // 1. Get filtered list from model (using the new combined method)
        ArrayList<Task> processedTasks = model.getFilteredTasks(statusFilter, assigneeFilter);

        // 2. Sorting Logic (Deadline)
        if ("Deadline".equals(selectedSort)) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            processedTasks.sort((t1, t2) -> {
                if (t1.getDeadline().equals("None") && t2.getDeadline().equals("None")) return 0;
                if (t1.getDeadline().equals("None")) return 1;
                if (t2.getDeadline().equals("None")) return -1;
                try {
                    return format.parse(t1.getDeadline()).compareTo(format.parse(t2.getDeadline()));
                } catch (Exception ex) { return 0; }
            });
        }

        // 3. Update Assignee Filter Dropdown dynamically
        java.util.Set<String> uniqueNames = new java.util.TreeSet<>();
        for (Task t : model.getTasks()) {
            String[] names = t.getAssignees().split(",");
            for (String n : names) {
                if (!n.trim().isEmpty()) uniqueNames.add(n.trim().toLowerCase());
            }
        }
        view.updateAssigneeFilterList(uniqueNames);

        // 4. Finally, send the processed results to the View
        view.updateTaskList(processedTasks);
    }


    // Allows you to add a comment to an existing task.
    private void handleAddComment() {
        // 1. Ask which task to comment on
        String titleToComment = JOptionPane.showInputDialog(view, "Enter the exact title of the task to comment on:");

        if (titleToComment != null && !titleToComment.trim().isEmpty()) {
            Task taskToComment = model.findTaskByTitle(titleToComment);

            if (taskToComment != null) {
                // 2. Prepare the input fields for the unified dialog
                JTextField authorField = new JTextField();
                JTextField commentField = new JTextField();

                // Pre-fill the date field with today's date
                String currentDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date());
                JTextField dateField = new JTextField(currentDate);

                // Group them into an array
                Object[] inputFields = {
                        "Author Name:", authorField,
                        "Comment:", commentField,
                        "Date (dd.mm.yyyy):", dateField
                };

                // 3. Show the single, clean dialog
                int result = JOptionPane.showConfirmDialog(view, inputFields, "Add New Comment", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String author = authorField.getText().trim();
                    String comment = commentField.getText().trim();
                    String date = dateField.getText().trim();

                    // Validation: Ensure author and comment are not empty
                    if (author.isEmpty() || comment.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Author and Comment cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Date Validation
                    if (!date.matches("^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.\\d{4}$")) {
                        JOptionPane.showMessageDialog(view, "Invalid Date Format! Please use dd.mm.yyyy", "Format Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 4. Send the data to the task and update
                    taskToComment.addComment(author, comment, date);
                    model.forceUpdateAndSave();
                }
            } else {
                JOptionPane.showMessageDialog(view, "Task not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
