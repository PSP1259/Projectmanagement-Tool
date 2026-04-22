package utils;

import model.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Utility class to handle data persistence using java.util.Properties.
 * It saves and loads the Task list to/from a local XML file.
 */

public class DataStorage {

    // The file path where data will be stored, pointing to the 'data' directory
    private static final String FILE_PATH = "data/tasks.xml";

    /**
     * Saves a list of Tasks to an XML file using the Properties class.
     *
     * @param tasks The ArrayList of Tasks to save.
     */
    public static void saveTasks(ArrayList<Task> tasks) {

        Properties props = new Properties();

        // Convert the ArrayList into key-value pairs for the Properties object
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            props.setProperty("task." + i + ".title", t.getTitle());
            props.setProperty("task." + i + ".description", t.getDescription());
            props.setProperty("task." + i + ".status", t.getStatus());
            props.setProperty("task." + i + ".time", String.valueOf(t.getTimeSpentInSeconds()));
        }

        // Save the total count
        props.setProperty("task.count", String.valueOf(tasks.size()));

        // Write to the XML file
        try (FileOutputStream fos = new FileOutputStream(new File(FILE_PATH))) {
            props.storeToXML(fos, "Project Management Tool - Saved Tasks");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Loads the list of Tasks from the XML file.
     *
     * @return An ArrayList containing the loaded Tasks. Returns an empty list if the file doesn't exist.
     */
    public static ArrayList<Task> loadTasks() {

        ArrayList<Task> loadedTasks = new ArrayList<>();
        Properties props = new Properties();
        File file = new File(FILE_PATH);

        // If the file doesn't exist yet -> return the empty list
        if (!file.exists()) {
            return loadedTasks;
        }

        // Read from the XML file
        try (FileInputStream fis = new FileInputStream(file)) {
            props.loadFromXML(fis);

            // Get the total count
            String countStr = props.getProperty("task.count", "0");
            int count = Integer.parseInt(countStr);

            // Reconstruct the Task objects from the properties
            for (int i = 0; i < count; i++) {
                String title = props.getProperty("task." + i + ".title", "Unknown");
                String description = props.getProperty("task." + i + ".description", "");
                String status = props.getProperty("task." + i + ".status", "Open");

                String timeStr = props.getProperty("task." + i + ".time", "0");
                int time = Integer.parseInt(timeStr);

                loadedTasks.add(new Task(title, description, status, time));
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }

        return loadedTasks;
    }
}
