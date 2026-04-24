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
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton toggleStatusButton;
    private final JButton startTimerButton;
    private final JButton editButton;
    private final JLabel progressLabel;

    public MainView() {

        // 1. Top-Level Container JFrame: initialize and define Layout
        setTitle("Projectmanagement-Tool");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        progressLabel = new JLabel("Progress: 0 of 0 Tasks Done", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(progressLabel, BorderLayout.NORTH);

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
        inputPanel.setLayout(new GridLayout(2, 2, 5, 5));

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

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

    public void clearInputs() {
        titleField.setText("");
        descriptionField.setText("");
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

        // We use StringBuilder to efficiently construct the HTML text
        StringBuilder html = new StringBuilder();

        // --- CSS STYLING ---
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; font-size: 13px; margin: 10px; }");
        html.append("h2 { margin-top: 0; margin-bottom: 5px; font-size: 16px; color: #333; }");
        // Added margin-bottom: 15px here to create the extra space after the description!
        html.append(".details { margin-left: 20px; margin-bottom: 15px; font-style: italic; color: #555; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 5px; }");
        html.append("td { padding: 2px; }");
        html.append(".time-warning { color: red; font-weight: bold; }");
        html.append(".status-open { color: blue; font-weight: bold; }");
        html.append(".status-done { color: green; font-weight: bold; }");
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
            int seconds = totalSeconds % 60; // Returns the remainder in seconds

            // Dynamic CSS classes based on logical state
            String statusClass = t.getStatus().equals("Done") ? "status-done" : "status-open";
            String timeClass = minutes >= 60 ? "time-warning" : "";

            // Build the HTML structure for a single task (Table layout)
            html.append("<div>");
            html.append("<h2>📌 ").append(t.getTitle().toUpperCase()).append("</h2>");
            html.append("<table border='0'><tr>");
            html.append("<td width='50%'>Status: <span class='").append(statusClass).append("'>").append(t.getStatus()).append("</span></td>");

            // Appending minutes AND seconds here
            html.append("<td>Time: <span class='").append(timeClass).append("'>").append(minutes).append(" min ").append(seconds).append(" sec</span></td>");

            html.append("</tr></table>");
            // The details div now automatically has more space below it thanks to CSS
            html.append("<div class='details'>").append(t.getDescription()).append("</div>");
            html.append("<hr>");
            html.append("</div>");
        }

        html.append("</body></html>");

        // Push the generated HTML to the UI
        taskEditorPane.setText(html.toString());

        // Auto-scroll back to the top of the list after an update
        taskEditorPane.setCaretPosition(0);

        // Update the top label
        progressLabel.setText("Progress: " + doneTasks + " of " + totalTasks + " Tasks Done");
    }
}
