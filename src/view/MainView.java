package view;

import model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Provides the main graphical user interface for the project management tool.
 * <p>
 * This class extends {@link JFrame} to serve as the primary standalone window.
 * Acting strictly as the View in the MVC architecture, it contains purely
 * display elements and layout configurations without maintaining any business logic.
 */
public class MainView extends JFrame {

    /** The editor pane used to display the formatted list of tasks using HTML. */
    private final JEditorPane taskEditorPane;

    /** The text field for entering or displaying a task's title. */
    private final JTextField titleField;

    /** The text field for entering or displaying a task's description. */
    private final JTextField descriptionField;

    /** The text field for capturing the initial time logged for a task. */
    private final JTextField initialTimeField;

    /** The text field for defining the deadline of a task. */
    private final JTextField deadlineField;

    /** The button used to add a new task to the system. */
    private final JButton addButton;

    /** The button used to delete a selected task. */
    private final JButton deleteButton;

    /** The button used to toggle a task's status between open and done. */
    private final JButton toggleStatusButton;

    /** The button used to start the time tracking for a specific task. */
    private final JButton startTimerButton;

    /** The button used to edit an existing task's properties. */
    private final JButton editButton;

    /** The label displaying the overall progress and completion status of tasks. */
    private final JLabel progressLabel;

    /** The combo box allowing the user to filter tasks by their status. */
    private final JComboBox<String> filterComboBox;

    /** The combo box allowing the user to sort tasks by various criteria. */
    private final JComboBox<String> sortComboBox;

    /** The button used to add comments to a specific task. */
    private final JButton commentButton;

    /** The combo box for selecting an assignee when creating or editing a task. */
    private final JComboBox<String> assigneeComboBox;

    /** The combo box for filtering the displayed tasks by a specific assignee. */
    private final JComboBox<String> assigneeFilterComboBox;

    /** The button used to display help or documentation to the user. */
    private final JButton helpButton;

    /**
     * Creates the main view and initializes the graphical user interface components.
     * <p>
     * This constructor sets up the top-level container layout, defines the
     * interactive controls (such as buttons and combo boxes), and prepares the
     * central HTML-capable editor pane for rendering tasks.
     */
    public MainView() {

        // 1. Top-Level Container JFrame: initialize and define Layout
        setTitle("Projectmanagement-Tool");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        progressLabel = new JLabel("Progress: 0 of 0 Tasks Done", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(progressLabel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        controlPanel.add(new JLabel("Filter:"));
        filterComboBox = new JComboBox<>(new String[]{"All", "Open", "Done"});
        controlPanel.add(filterComboBox);

        controlPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Default", "Deadline"});
        controlPanel.add(sortComboBox);

        controlPanel.add(new JLabel("Assignee:"));
        assigneeFilterComboBox = new JComboBox<>(new String[]{"All"});
        controlPanel.add(assigneeFilterComboBox);

        topPanel.add(controlPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 2. Central Component: HTML-capable JEditorPane
        taskEditorPane = new JEditorPane();
        taskEditorPane.setContentType("text/html");
        taskEditorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(taskEditorPane);
        add(scrollPane, BorderLayout.CENTER);

        // 3. South Input Container: Set up the panel for user input and action buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        // Configure the input grid for task attributes
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 5, 5));

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Initial Time (min):"));
        initialTimeField = new JTextField("0");
        inputPanel.add(initialTimeField);

        inputPanel.add(new JLabel("Deadline (dd.mm.yyyy):"));
        deadlineField = new JTextField("");
        inputPanel.add(deadlineField);

        inputPanel.add(new JLabel("Assignee:"));
        assigneeComboBox = new JComboBox<>(new String[]{"psp", "adm", "dev"});
        assigneeComboBox.setEditable(true);
        inputPanel.add(assigneeComboBox);

        // Configure the button panel for user actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        addButton = new JButton("Add Task");
        buttonPanel.add(addButton);

        deleteButton = new JButton("Delete Task");
        buttonPanel.add(deleteButton);

        toggleStatusButton = new JButton("Toggle Status");
        buttonPanel.add(toggleStatusButton);

        startTimerButton = new JButton("Start Time Tracking");
        buttonPanel.add(startTimerButton);

        editButton = new JButton("Edit Task");
        buttonPanel.add(editButton);

        commentButton = new JButton("Add Comment");
        buttonPanel.add(commentButton);

        helpButton = new JButton("❓ Help / FAQ");
        buttonPanel.add(helpButton);

        // Assemble the bottom panel and attach it to the main frame
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

// --- Public interface (View API) for the Controller ---

    /**
     * Gets the button used to add a new task.
     * <p>
     * This method exposes the component so the controller can register an action listener.
     *
     * @return the add button, not null
     */
    public JButton getAddButton() {
        return addButton;
    }

    /**
     * Gets the button used to delete a task.
     *
     * @return the delete button, not null
     */
    public JButton getDeleteButton() {
        return deleteButton;
    }

    /**
     * Gets the button used to toggle a task's status.
     *
     * @return the toggle status button, not null
     */
    public JButton getToggleStatusButton() {
        return toggleStatusButton;
    }

    /**
     * Gets the button used to start time tracking.
     *
     * @return the start timer button, not null
     */
    public JButton getStartTimerButton() {
        return startTimerButton;
    }

    /**
     * Gets the button used to edit a task.
     *
     * @return the edit button, not null
     */
    public JButton getEditButton() {
        return editButton;
    }

    /**
     * Gets the current text input for the task's title.
     *
     * @return the title input string, not null
     */
    public String getTitleInput() {
        return titleField.getText();
    }

    /**
     * Gets the current text input for the task's description.
     *
     * @return the description input string, not null
     */
    public String getDescriptionInput() {
        return descriptionField.getText();
    }

    /**
     * Gets the current text input for the initial time.
     *
     * @return the initial time input string, not null
     */
    public String getInitialTimeInput() {
        return initialTimeField.getText();
    }

    /**
     * Gets the current text input for the deadline.
     *
     * @return the deadline input string, not null
     */
    public String getDeadlineInput() {
        return deadlineField.getText();
    }

    /**
     * Gets the currently selected assignee from the dropdown.
     * <p>
     * If no item is selected, an empty string is returned.
     *
     * @return the selected assignee, not null
     */
    public String getAssigneeInput() {
        return assigneeComboBox.getSelectedItem() != null ? assigneeComboBox.getSelectedItem().toString() : "";
    }

    /**
     * Gets the button used to display help and FAQ.
     *
     * @return the help button, not null
     */
    public JButton getHelpButton() {
        return helpButton;
    }

    /**
     * Adds a new assignee to the dropdown list if it does not already exist.
     * <p>
     * This method prevents duplication and ignores empty strings.
     *
     * @param newAssignee  the name of the assignee to add, not null
     */
    public void addAssigneeToDropdown(String newAssignee) {
        boolean exists = false; // Check for duplicates
        for (int i = 0; i < assigneeComboBox.getItemCount(); i++) {
            if (assigneeComboBox.getItemAt(i).equalsIgnoreCase(newAssignee)) {
                exists = true;
                break;
            }
        }
        if (!exists && !newAssignee.isEmpty()) {
            assigneeComboBox.addItem(newAssignee);
        }
    }

    /**
     * Gets the combo box used for filtering tasks by status.
     *
     * @return the filter combo box, not null
     */
    public JComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }

    /**
     * Gets the combo box used for sorting tasks.
     *
     * @return the sort combo box, not null
     */
    public JComboBox<String> getSortComboBox() {
        return sortComboBox;
    }

    /**
     * Gets the button used to add comments to a task.
     *
     * @return the comment button, not null
     */
    public JButton getCommentButton() {
        return commentButton;
    }

    /**
     * Gets the combo box used for filtering tasks by assignee.
     *
     * @return the assignee filter combo box, not null
     */
    public JComboBox<String> getAssigneeFilterComboBox() {
        return assigneeFilterComboBox;
    }

    /**
     * Updates the assignee filter dropdown with a new set of unique names.
     * <p>
     * This method temporarily disconnects action listeners to prevent an infinite
     * event loop while the list is being repopulated. It also attempts to restore
     * the previously selected item after the update.
     *
     * @param uniqueAssignees  the set of unique assignee names, not null
     */
    public void updateAssigneeFilterList(java.util.Set<String> uniqueAssignees) {
        // 1. Temporarily disconnect all listeners
        java.awt.event.ActionListener[] listeners = assigneeFilterComboBox.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) {
            assigneeFilterComboBox.removeActionListener(l);
        }

        // 2. Recreate the list
        String currentSelection = (String) assigneeFilterComboBox.getSelectedItem();
        assigneeFilterComboBox.removeAllItems();
        assigneeFilterComboBox.addItem("All");
        for (String name : uniqueAssignees) {
            assigneeFilterComboBox.addItem(name);
        }

        // Restore last selection if possible
        if (currentSelection != null) {
            assigneeFilterComboBox.setSelectedItem(currentSelection);
        }

        // 3. Reconnect listeners
        for (java.awt.event.ActionListener l : listeners) {
            assigneeFilterComboBox.addActionListener(l);
        }
    }

    /**
     * Clears all user input fields in the view.
     * <p>
     * This resets standard text fields to empty strings and the initial
     * time field back to its default value of zero.
     */
    public void clearInputs() {
        titleField.setText("");
        descriptionField.setText("");
        initialTimeField.setText("0");
        deadlineField.setText("");
    }

    /**
     * Updates the visual representation of the task list using formatted HTML.
     * <p>
     * This method calculates the total and completed tasks, and constructs an HTML
     * string with embedded CSS styling to display the task details, including
     * assignees, tracked time, and current status.
     *
     * @param tasks  the list of tasks to be displayed, not null
     */
    public void updateTaskList(ArrayList<Task> tasks) {
        int totalTasks = tasks.size();
        int doneTasks = 0;

        // Initialize a string builder for the HTML document
        StringBuilder html = new StringBuilder();

        // Define embedded CSS for styling the task display
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; font-size: 13px; margin: 10px; }");
        html.append("h2 { margin-top: 0; margin-bottom: 5px; font-size: 16px; color: #333; }");
        html.append(".details { margin-bottom: 15px; font-style: italic; color: #555; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 5px; }");
        html.append("td { padding: 2px; }");
        html.append(".time-warning { color: red; font-weight: bold; }");
        html.append(".status-open { color: blue; font-weight: bold; }");
        html.append(".status-done { color: green; font-weight: bold; }");
        html.append(".assignee-pill { color: #FF5E00; background-color: transparent; border: 1px solid #FF5E00; padding: 2px 6px; font-size: 11px; font-weight: bold; margin-right: 4px; }");
        html.append(".comments-box { margin-left: 20px; margin-top: 5px; margin-bottom: 15px; padding: 5px; background-color: #f0f8ff; border-left: 3px solid #007bff; font-size: 12px; }");html.append(".comments-box { margin-left: 20px; margin-top: 10px; padding: 5px; background-color: #f9f9f9; border-left: 3px solid #007bff; font-size: 12px; }");
        html.append("hr { border: 0; border-top: 1px solid #ccc; margin-top: 5px; margin-bottom: 15px; }");
        html.append("</style></head><body>");

        // Iterate through all tasks to build their HTML representation
        for (Task t : tasks) {
            if (t.getStatus().equals("Done")) {
                doneTasks++;
            }

            // Calculate the total tracked time into minutes and seconds
            int totalSeconds = t.getTimeSpentInSeconds();
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            // Determine specific CSS classes based on task status and tracked time
            String statusClass = t.getStatus().equals("Done") ? "status-done" : "status-open";
            String timeClass = minutes >= 60 ? "time-warning" : "";

            // Construct the HTML layout for the current task

            // Render the assignee tags at the top of the task block
            html.append("<div>");
            if (!t.getAssignees().trim().isEmpty()) {
                html.append("<div style='margin-bottom: 3px;'>");
                String[] persons = t.getAssignees().split(",");
                for (String p : persons) {
                    if (!p.trim().isEmpty()) {
                        // Format each assignee as a distinct visual pill
                        html.append("<span class='assignee-pill'>[").append(p.trim().toUpperCase()).append("]</span> ");
                    }
                }
                html.append("</div>");
            }
            // Render the task title
            html.append("<h2>📌 ").append(t.getTitle().toUpperCase()).append("</h2>");
            html.append("<table border='0'><tr>");

            // Column 1: Render the current status
            html.append("<td width='35%'>Status: <span class='").append(statusClass).append("'>").append(t.getStatus()).append("</span></td>");

            // Column 2: Render the tracked time
            html.append("<td width='30%'>Time: <span class='").append(timeClass).append("'>").append(minutes).append(" min ").append(seconds).append(" sec</span></td>");

            // Column 3: Render the creation date and the optional deadline
            html.append("<td width='35%' align='right' valign='top'>");
            html.append("Created: ").append(t.getCreationDate());
            if (!t.getDeadline().isEmpty() && !t.getDeadline().equals("None")) {
                html.append("<br>");
                html.append("Deadline: ").append(t.getDeadline());
            }
            html.append("</td>");
            html.append("</tr></table>");

            // Render the detailed task description
            html.append("<div class='details'>").append(t.getDescription()).append("</div>");

            // Render the comments section if comments exist
            if (!t.getComments().isEmpty()) {
                html.append("<div class='comments-box'>").append(t.getComments()).append("</div>");
            }
            html.append("<hr>");
            html.append("</div>");
        }

        html.append("</body></html>");

        // Preserve the current scroll position before updating the view
        JViewport viewport = (JViewport) taskEditorPane.getParent();
        Point scrollPosition = viewport.getViewPosition();

        // Apply the newly constructed HTML string to the editor pane
        taskEditorPane.setText(html.toString());

        // Restore the scroll position asynchronously after the UI update
        SwingUtilities.invokeLater(() -> viewport.setViewPosition(scrollPosition));

        // Update the header label with the calculated progress statistics
        progressLabel.setText("Progress: " + doneTasks + " of " + totalTasks + " Tasks Done");
    }
}