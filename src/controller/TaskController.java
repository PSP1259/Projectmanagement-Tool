package controller;

import model.ProjectModel;
import model.Task;
import view.MainView;

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

/**
 * Controls the application startup and user interactions.
 * <p>
 * This class acts as the Controller in the MVC architecture. It initializes the
 * view with data loaded from persistent storage and handles model updates.
 */
public class TaskController implements PropertyChangeListener {

    /** The data model containing the business logic and managed tasks. */
    private final ProjectModel model;

    /** The main graphical user interface of the application. */
    private final MainView view;

    /**
     * Creates a task controller linking the specified model and view.
     * <p>
     * This constructor registers the necessary event listeners to handle user
     * interactions and triggers the initial data load from the storage.
     *
     * @param model  the data model containing business logic, not null
     * @param view  the visual representation of the application, not null
     */
    public TaskController(ProjectModel model, MainView view) {
        this.model = model;
        this.view = view;

        // Register event listeners for UI components and model updates
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
        this.view.getHelpButton().addActionListener(e -> showHelpFAQ());

        // Load existing tasks from the persistent XML storage
        ArrayList<Task> loadedTasks = DataStorage.loadTasks();

        // Populate the model with the initially loaded data
        this.model.setTasks(loadedTasks);

        // Trigger an initial visual update of the view
        this.view.updateTaskList(loadedTasks);

        // Apply default sorting and filtering to the initial view
        applyFilterAndSort();
    }
    /**
     * Handles the addition of a new task triggered by the user interface.
     * <p>
     * Reads the input fields from the view, validates the required title and
     * deadline formats, creates a new task model, and updates the application state.
     */
    private void handleAddTask() {
        String title = view.getTitleInput();
        String description = view.getDescriptionInput();
        String initialTimeStr = view.getInitialTimeInput();
        String deadline = view.getDeadlineInput().trim();
        String assignees = view.getAssigneeInput().trim();

        // Ensure the task title is not empty before proceeding
        if (title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Task title cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parse the initial time input, defaulting to zero if invalid
        int initialSeconds = 0;
        try {
            initialSeconds = Integer.parseInt(initialTimeStr.trim()) * 60;
        } catch (NumberFormatException e) {
            // Silently ignore format errors and keep default 0
        }

        // Validate the deadline against a strict dd.MM.yyyy format
        if (!deadline.isEmpty() && !deadline.matches("^(0[1-8, 10]|[11][1-8, 10]|3[10])\\.(0[1-8, 10]|1[11])\\.\\d{4}$")) {
            JOptionPane.showMessageDialog(view, "Invalid Deadline Format!\nPlease use exactly: dd.MM.yyyy (e.g. 24.04.2026)", "Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generate the current date as the creation timestamp
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String creationDate = formatter.format(new Date());

        // Create the task with default "Open" status and add it to the model
        Task newTask = new Task(title, description, "Open", initialSeconds, creationDate, deadline, assignees, "");
        model.addTask(newTask);

        // Store the newly used assignee in the dropdown for future use
        if (!assignees.isEmpty()) {
            view.addAssigneeToDropdown(assignees);
        }
        view.clearInputs();
    }

    /**
     * Prompts the user to delete an existing task by its exact title.
     * <p>
     * If a valid, non-empty title is provided, it delegates the removal
     * process directly to the project model.
     */
    private void handleDeleteTask() {

        String titleToDelete = JOptionPane.showInputDialog(view, "Enter the exact title of the task to delete:");

        // Ensure the user input is valid before passing it to the model
        if (titleToDelete != null && !titleToDelete.trim().isEmpty()) {
            model.removeTaskByTitle(titleToDelete);
        }
    }

    /**
     * Prompts the user to toggle the status of a specific task.
     * <p>
     * Instructs the model to invert the status between "Open" and "Done"
     * based on the exact task title provided in the dialog.
     */
    private void handleToggleStatus() {
        String titleToToggle = JOptionPane.showInputDialog(view, "Enter the title of the task to mark Open/Done:");

        // Ensure the user input is valid before toggling the status
        if (titleToToggle != null && !titleToToggle.trim().isEmpty()) {
            model.toggleTaskStatus(titleToToggle);
        }
    }

    /**
     * Initiates the time tracking process for a specific task.
     * <p>
     * Prompts the user for a task title, searches for the exact match
     * in the model, and opens the timer dialog if the task is found.
     */
    private void handleStartTimer() {
        String titleToTrack = JOptionPane.showInputDialog(view, "Enter the exact title of the task to track time for:");

        // Ensure the user input is valid before starting the search
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
     * Opens a modal dialog to track the time spent on a specific task.
     * <p>
     * This method initiates a background timer that continuously updates the
     * user interface every second. Upon stopping the tracking process, the
     * elapsed time is added to the given task and the updated state is persisted.
     *
     * @param task  the task to track time for, not null
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


        // Initialize the internal timer state
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

        // Handle the stop event by cleaning up the timer, persisting data, and updating the model
        stopButton.addActionListener(e -> {
            timer.cancel();                                 // Stops the timer
            timerDialog.dispose();                          // Closes the input

            task.addTimeInSeconds(secondsPassed[0]);

            model.forceUpdateAndSave();
        });

        timerDialog.setVisible(true);
    }

    /**
     * Prompts the user to edit the properties of an existing task.
     * <p>
     * This method requests the exact title of a task, searches for it within the model,
     * and opens a comprehensive dialog with pre-filled fields if found. User inputs
     * for the title, deadline, and tracked time are strictly validated before the
     * task state is updated and persisted.
     */
    private void handleEditTask() {
        String titleToEdit = JOptionPane.showInputDialog(view, "Enter the exact title of the task to edit:");

        // Ensure the user input is valid before initiating the search
        if (titleToEdit != null && !titleToEdit.trim().isEmpty()) {
            Task taskToEdit = model.findTaskByTitle(titleToEdit);

            if (taskToEdit != null) {
                // Prepare pre-filled input fields based on the current task state
                JTextField titleField = new JTextField(taskToEdit.getTitle());
                JTextField descriptionField = new JTextField(taskToEdit.getDescription());
                int currentMinutes = taskToEdit.getTimeSpentInSeconds() / 60;
                JTextField timeField = new JTextField(String.valueOf(currentMinutes));
                String currentDeadline = taskToEdit.getDeadline().equals("None") ? "" : taskToEdit.getDeadline();
                JTextField deadlineField = new JTextField(currentDeadline);
                JTextField assigneesField = new JTextField(taskToEdit.getAssignees());

                // Assemble the input dialog layout
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

                    // Validate that the new title is not empty
                    if (newTitle.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validate the deadline against a strict date format regex
                    if (!newDeadline.isEmpty() && !newDeadline.matches("^(0[1-8, 10]|[11][1-8, 10]|3[10])\\.(0[1-8, 10]|1[11])\\.\\d{4}$")) {
                        JOptionPane.showMessageDialog(view, "Invalid Deadline Format!\nPlease use exactly: dd.MM.yyyy", "Format Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Parse the updated time, retaining the original value if parsing fails
                    int newTimeInSeconds = taskToEdit.getTimeSpentInSeconds();
                    try {
                        newTimeInSeconds = Integer.parseInt(timeField.getText().trim()) * 60;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(view, "Invalid time entered. Time was not updated.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                    // Apply the validated changes to the task object
                    taskToEdit.setTitle(newTitle);
                    taskToEdit.setDescription(descriptionField.getText());
                    taskToEdit.setDeadline(newDeadline.isEmpty() ? "None" : newDeadline);
                    taskToEdit.setTimeSpentInSeconds(newTimeInSeconds);
                    taskToEdit.setAssignees(assigneesField.getText().trim());

                    // Persist the modifications and notify listeners
                    model.forceUpdateAndSave();
                }
            } else {
                JOptionPane.showMessageDialog(view, "Task not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Responds to property change events triggered by the model.
     * <p>
     * When the internal task list is modified, this method automatically
     * reapplies the current filter and sorting criteria to refresh the view.
     *
     * @param evt  the property change event describing the source and the modified property, not null
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("tasks".equals(evt.getPropertyName())) {
            applyFilterAndSort();
        }
    }

    /**
     * Applies the currently selected filter and sorting criteria to the task list.
     * <p>
     * This method retrieves the filtered tasks from the model based on status
     * and assignee. It then optionally sorts the list by deadline, dynamically
     * updates the assignee filter dropdown, and refreshes the view.
     */
    private void applyFilterAndSort() {
        String statusFilter = (String) view.getFilterComboBox().getSelectedItem();
        String assigneeFilter = (String) view.getAssigneeFilterComboBox().getSelectedItem();
        String selectedSort = (String) view.getSortComboBox().getSelectedItem();

        // Retrieve the filtered list of tasks from the model
        ArrayList<Task> processedTasks = model.getFilteredTasks(statusFilter, assigneeFilter);

        // Sort the tasks chronologically by deadline, placing tasks without deadlines at the end
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

        // Extract all unique assignees to dynamically update the filter dropdown
        java.util.Set<String> uniqueNames = new java.util.TreeSet<>();
        for (Task t : model.getTasks()) {
            String[] names = t.getAssignees().split(",");
            for (String n : names) {
                if (!n.trim().isEmpty()) uniqueNames.add(n.trim().toLowerCase());
            }
        }
        view.updateAssigneeFilterList(uniqueNames);

        // Update the view with the fully processed list of tasks
        view.updateTaskList(processedTasks);
    }

    /**
     * Prompts the user to add a comment to an existing task.
     * <p>
     * This method requests the exact title of a task and, if a match is found,
     * opens a secondary dialog to collect the author's name, the comment text,
     * and the date. The input is strictly validated before the comment is appended
     * to the task and the model is updated.
     */
    private void handleAddComment() {
        String titleToComment = JOptionPane.showInputDialog(view, "Enter the exact title of the task to comment on:");

        // Ensure the user input is valid before searching the model
        if (titleToComment != null && !titleToComment.trim().isEmpty()) {
            Task taskToComment = model.findTaskByTitle(titleToComment);

            if (taskToComment != null) {
                // Prepare the input fields for the comment dialog
                JTextField authorField = new JTextField();
                JTextField commentField = new JTextField();

                // Pre-fill the date field with the current system date
                String currentDate = new java.text.SimpleDateFormat("dd.mm.yyyy").format(new java.util.Date());
                JTextField dateField = new JTextField(currentDate);

                // Assemble the input dialog layout
                Object[] inputFields = {
                        "Author Name:", authorField,
                        "Comment:", commentField,
                        "Date (dd.mm.yyyy):", dateField
                };

                int result = JOptionPane.showConfirmDialog(view, inputFields, "Add New Comment", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String author = authorField.getText().trim();
                    String comment = commentField.getText().trim();
                    String date = dateField.getText().trim();

                    // Validate that neither the author nor the comment are empty
                    if (author.isEmpty() || comment.isEmpty()) {
                        JOptionPane.showMessageDialog(view, "Author and Comment cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validate the date against a strict format regex
                    if (!date.matches("^(0[1-8, 10]|[11][1-8, 10]|3[10])\\.(0[1-8, 10]|1[11])\\.\\d{4}$")) {
                        JOptionPane.showMessageDialog(view, "Invalid Date Format! Please use dd.MM.yyyy", "Format Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Append the validated comment to the task and persist the changes
                    taskToComment.addComment(author, comment, date);
                    model.forceUpdateAndSave();
                }
            } else {
                JOptionPane.showMessageDialog(view, "Task not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Displays a comprehensive help and FAQ dialog to the user.
     * <p>
     * This method constructs a formatted HTML document containing instructions
     * on task creation, assignee management, time tracking warnings, and
     * communication features. The resulting information is presented in a
     * modal message dialog.
     */
    private void showHelpFAQ() {

        // Initialize a string builder to construct the HTML-formatted FAQ document
        StringBuilder faq = new StringBuilder();
        faq.append("<html><body style='width: 400px; font-family: Arial, sans-serif;'>");
        faq.append("<h2>🛠️ Tool Instructions & FAQ</h2>");

        faq.append("<h3>📝 Task Creation & Editing</h3>");
        faq.append("<ul>");
        faq.append("<li><b>Mandatory Fields:</b> Only the <i>Title</i> is strictly required to create a task.</li>");
        faq.append("<li><b>Deadlines:</b> Must be strictly formatted as <b>dd.MM.yyyy</b>.</li>");
        faq.append("<li><b>Editing:</b> You can retroactively change the tracked time (in minutes) and the deadline using the Edit button.</li>");
        faq.append("</ul>");

        faq.append("<h3>👥 Assignees & Team Tags</h3>");
        faq.append("<ul>");
        faq.append("<li><b>Multiple Assignees:</b> You can assign multiple people by separating their names with a comma (e.g., <i>psp, adm, ceo</i>).</li>");
        faq.append("<li><b>Visual Badges:</b> The tool automatically generates colored neon badges for each assigned individual.</li>");
        faq.append("<li><b>Dynamic Filtering:</b> New assignees are automatically added to the dropdown filter at the top right.</li>");
        faq.append("</ul>");

        faq.append("<h3>⏱️ Time Tracking & Warnings</h3>");
        faq.append("<ul>");
        faq.append("<li><b>Overtime Warning:</b> Once the tracked time for a task exceeds <b>60 minutes</b>, the time indicator will automatically turn <span style='color: red; font-weight: bold;'>RED</span>.</li>");
        faq.append("</ul>");

        faq.append("<h3>💬 Communication</h3>");
        faq.append("<ul>");
        faq.append("<li><b>Comments:</b> Use 'Add Comment' to leave notes. Timestamps are generated automatically.</li>");
        faq.append("</ul>");

        faq.append("</body></html>");

        // Display the constructed FAQ document in a modal information dialog
        JOptionPane.showMessageDialog(view, faq.toString(), "Help & FAQ", JOptionPane.INFORMATION_MESSAGE);
    }
}
