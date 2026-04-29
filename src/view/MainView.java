package view;

import model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Die Hauptansicht (View) des Projektmanagement-Tools.
 * Erweitert JFrame, um ein eigenständiges Fenster zu sein.
 * Enthält KEINE Geschäftslogik, sondern nur reine Anzeige-Elemente.
 */

public class MainView extends JFrame {

    // private final JTextArea taskTextArea;
    private final JEditorPane taskEditorPane;           // Design optimization
    private final JTextField titleField;
    private final JTextField descriptionField;
    private final JTextField initialTimeField;
    private final JTextField deadlineField;
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton toggleStatusButton;
    private final JButton startTimerButton;
    private final JButton editButton;
    private final JLabel progressLabel;
    private final JComboBox<String> filterComboBox;
    private final JComboBox<String> sortComboBox;
    private final JButton commentButton;
    private final JComboBox<String> assigneeComboBox;
    private final JComboBox<String> assigneeFilterComboBox;

    public MainView() {

        // 1. Top-Level Container JFrame: initialize and define Layout
        setTitle("Projectmanagement-Tool");
        setSize(700, 500);
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

        /** OLD Design (from line 50 the new design)
         *
         * 2. Central Component: non-editable JTextArea wrapped in a JScrollPane
         *
         * taskTextArea = new JTextArea(20, 30);
         * taskTextArea.setEditable(false);                            // non-editable
         * JScrollPane scrollPane = new JScrollPane(taskTextArea);
         * add(scrollPane, BorderLayout.CENTER);
         */

        // NEW Design: 2. Central Component: HTML-capable JEditorPane
        taskEditorPane = new JEditorPane();
        taskEditorPane.setContentType("text/html"); // Tell Java to expect HTML
        taskEditorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(taskEditorPane);
        add(scrollPane, BorderLayout.CENTER);

        // 3. South Input Container JPanel:
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 5, 5));

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Initial Time (min):"));
        initialTimeField = new JTextField("0"); // Standardwert 0
        inputPanel.add(initialTimeField);

        inputPanel.add(new JLabel("Deadline (dd.mm.yyyy):"));
        deadlineField = new JTextField("");
        inputPanel.add(deadlineField);

        inputPanel.add(new JLabel("Assignee:"));
        assigneeComboBox = new JComboBox<>(new String[]{"psp", "adm", "dev"});
        assigneeComboBox.setEditable(true);
        inputPanel.add(assigneeComboBox);

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

        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- Public interface (View API) for the Controller ---

    //Used by the Controller to register an ActionListener.

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getToggleStatusButton() {
        return toggleStatusButton;
    }

    public JButton getStartTimerButton() {
        return startTimerButton;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public String getTitleInput() {
        return titleField.getText();
    }

    public String getDescriptionInput() {
        return descriptionField.getText();
    }

    public String getInitialTimeInput() {
        return initialTimeField.getText();
    }

    public String getDeadlineInput() {
        return deadlineField.getText();
    }

    public String getAssigneeInput() {
        return assigneeComboBox.getSelectedItem() != null ? assigneeComboBox.getSelectedItem().toString() : "";
    }

    public void addAssigneeToDropdown(String newAssignee) {
        boolean exists = false; // Eliminate duplication
        for (int i = 0; i < assigneeComboBox.getItemCount(); i++) {
            if (assigneeComboBox.getItemAt(i).equalsIgnoreCase(newAssignee)) {
                exists = true; break;
            }
        }
        if (!exists && !newAssignee.isEmpty()) {
            assigneeComboBox.addItem(newAssignee);
        }
    }

    public JComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }

    public JComboBox<String> getSortComboBox() {
        return sortComboBox;
    }

    public JButton getCommentButton() {
        return commentButton;
    }

    public JComboBox<String> getAssigneeFilterComboBox() {
        return assigneeFilterComboBox;
    }

    public void updateAssigneeFilterList(java.util.Set<String> uniqueAssignees) {
        // 1. Temporarily disconnect all listeners (to prevent the loop)
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

        // Restore last selection (if possible)
        if (currentSelection != null) {
            assigneeFilterComboBox.setSelectedItem(currentSelection);
        }

        // 3. Connect Listener again
        for (java.awt.event.ActionListener l : listeners) {
            assigneeFilterComboBox.addActionListener(l);
        }
    }

    // In clearInputs() auch das Zeitfeld zurücksetzen
    public void clearInputs() {
        titleField.setText("");
        descriptionField.setText("");
        initialTimeField.setText("0");
        deadlineField.setText("");
    }

    /** OLD Design (from line xy the new design)
     *
     * Presentation layer: updates the visual representation of the task list.
     * The View receives data from the Model and renders it.
     * @param tasks List of Task objects provided by the Model.

    public void updateTaskList(ArrayList<Task> tasks) {

        taskTextArea.setText("");

        int totalTasks = tasks.size();
        int doneTasks = 0;

        for (Task t : tasks) {
            if (t.getStatus().equals("Done")) {
                doneTasks++;
            }

            taskTextArea.append(t.toString() + "\n");
            taskTextArea.append("Details: " + t.getDescription() + "\n");
            taskTextArea.append("--------------------------------------------------\n");
        }

        // Update the progress label at the top of the window
        progressLabel.setText("Progress: " + doneTasks + " of " + totalTasks + " Tasks Done");
    }
     */

    /** NEW Design
     *
     * Updates the visual list with the new tasks from the Model.
     * Uses HTML and CSS via JEditorPane for styling and layouting.
     *
     * * @param tasks The current ArrayList of all tasks.
     */
    public void updateTaskList(ArrayList<Task> tasks) {
        int totalTasks = tasks.size();
        int doneTasks = 0;

        // StringBuilder to construct the HTML text
        StringBuilder html = new StringBuilder();

        // --- CSS STYLING ---
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; font-size: 13px; margin: 10px; }");
        html.append("h2 { margin-top: 0; margin-bottom: 5px; font-size: 16px; color: #333; }");
        html.append(".details { margin-bottom: 15px; font-style: italic; color: #555; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 5px; }");
        html.append("td { padding: 2px; }");
        html.append(".time-warning { color: red; font-weight: bold; }");
        html.append(".status-open { color: blue; font-weight: bold; }");
        html.append(".status-done { color: green; font-weight: bold; }");
        html.append(".comments-box { margin-left: 20px; margin-top: 5px; margin-bottom: 15px; padding: 5px; background-color: #f0f8ff; border-left: 3px solid #007bff; font-size: 12px; }");html.append(".comments-box { margin-left: 20px; margin-top: 10px; padding: 5px; background-color: #f9f9f9; border-left: 3px solid #007bff; font-size: 12px; }");
        html.append("hr { border: 0; border-top: 1px solid #ccc; margin-top: 5px; margin-bottom: 15px; }");
        html.append("</style></head><body>");

        // --- LOOP THROUGH TASKS ---
        for (Task t : tasks) {
            if (t.getStatus().equals("Done")) {
                doneTasks++;
            }

            // Calculate tracked time in both minutes AND seconds
            int totalSeconds = t.getTimeSpentInSeconds();
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            // Dynamic CSS classes based on logical state
            String statusClass = t.getStatus().equals("Done") ? "status-done" : "status-open";
            String timeClass = minutes >= 60 ? "time-warning" : "";

            // --- Build the HTML structure for a single task (Table layout) ---

            // North: generate assignees & show title
            html.append("<div>");
            if (!t.getAssignees().trim().isEmpty()) {
                html.append("<div style='margin-bottom: 3px;'>");
                String[] persons = t.getAssignees().split(","); // Seperate assignees with ,
                for (String p : persons) {
                    if (!p.trim().isEmpty()) {
                        // Orange CSS pill for every assignee
                        html.append("<span class='assignee-pill'>[").append(p.trim().toUpperCase()).append("]</span> ");
                    }
                }
                html.append("</div>");
            }
            html.append("<h2>📌 ").append(t.getTitle().toUpperCase()).append("</h2>");
            html.append("<table border='0'><tr>");
            // 1. column: status
            html.append("<td width='35%'>Status: <span class='").append(statusClass).append("'>").append(t.getStatus()).append("</span></td>");
            // 2. column: time
            html.append("<td width='30%'>Time: <span class='").append(timeClass).append("'>").append(minutes).append(" min ").append(seconds).append(" sec</span></td>");
            // 3. column: dates
            html.append("<td width='35%' align='right' valign='top'>");
            html.append("Created: ").append(t.getCreationDate());
            // Only view dates if set
            if (!t.getDeadline().isEmpty() && !t.getDeadline().equals("None")) {
                html.append("<br>");
                html.append("Deadline: ").append(t.getDeadline());
            }
            html.append("</td>");
            html.append("</tr></table>");
            // South: Details
            html.append("<div class='details'>").append(t.getDescription()).append("</div>");
            // Show comments (if existing)
            if (!t.getComments().isEmpty()) {
                html.append("<div class='comments-box'>").append(t.getComments()).append("</div>");
            }
            html.append("<hr>");
            html.append("</div>");
        }

        html.append("</body></html>");

        // Remember scroll position
        JViewport viewport = (JViewport) taskEditorPane.getParent();
        Point scrollPosition = viewport.getViewPosition();

        // Update text
        taskEditorPane.setText(html.toString());

        // Restore position
        SwingUtilities.invokeLater(() -> viewport.setViewPosition(scrollPosition));

        // Update the top label
        progressLabel.setText("Progress: " + doneTasks + " of " + totalTasks + " Tasks Done");
    }
}
