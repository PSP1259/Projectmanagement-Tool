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

    private final JTextArea taskTextArea;
    private final JTextField titleField;
    private final JTextField descriptionField;
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton toggleStatusButton;


    public MainView() {

        // 1. Top-Level Container JFrame: initialize and define Layout
        setTitle("Projectmanagement-Tool");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 2. Central Component: non-editable JTextArea wrapped in a JScrollPane
        taskTextArea = new JTextArea(20, 30);
        taskTextArea.setEditable(false);                            // non-editable
        JScrollPane scrollPane = new JScrollPane(taskTextArea);
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

    /**
     * Presentation layer: updates the visual representation of the task list.
     * The View receives data from the Model and renders it.
     * @param tasks List of Task objects provided by the Model.
     */
    public void updateTaskList(ArrayList<Task> tasks) {
        taskTextArea.setText("");

        for (Task t : tasks) {
            taskTextArea.append(t.toString() + "\n");
        }
    }
}
