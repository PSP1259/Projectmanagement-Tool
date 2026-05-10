package utils;

import model.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Provides utility methods to handle data persistence using {@link Properties}.
 * <p>
 * This class is responsible for saving and loading the managed task list
 * to and from a local XML file.
 */
public class DataStorage {

    /** The file path pointing to the local directory where task data is persistently stored. */
    private static final String FILE_PATH = "data/tasks.xml";

    /**
     * Saves a list of tasks to an XML file using the properties format.
     * <p>
     * This method iterates through the provided list, converts each task's
     * attributes into sequential key-value pairs, and securely writes them
     * to the designated storage file. If an I/O error occurs during the save
     * process, it is caught and an error message is printed.
     *
     * @param tasks  the list of tasks to be saved, not null
     */
    public static void saveTasks(ArrayList<Task> tasks) {

        Properties props = new Properties();

        // Convert the task list into sequential key-value pairs for the properties object
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            props.setProperty("task." + i + ".title", t.getTitle());
            props.setProperty("task." + i + ".description", t.getDescription());
            props.setProperty("task." + i + ".status", t.getStatus());
            props.setProperty("task." + i + ".time", String.valueOf(t.getTimeSpentInSeconds()));
            props.setProperty("task." + i + ".creationDate", t.getCreationDate());
            props.setProperty("task." + i + ".deadline", t.getDeadline());
            props.setProperty("task." + i + ".assignees", t.getAssignees());
            props.setProperty("task." + i + ".comments", t.getComments());
        }

        // Store the total count of tasks to facilitate the loading process later
        props.setProperty("task.count", String.valueOf(tasks.size()));

        // Persist the properties into the designated XML file securely
        try (FileOutputStream fos = new FileOutputStream(new File(FILE_PATH))) {
            props.storeToXML(fos, "Project Management Tool - Saved Tasks");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Loads the persistently stored tasks from the local XML file.
     * <p>
     * This method reads the properties file and reconstructs the task objects
     * sequentially, including their respective tracking metrics, dates, and comments.
     * If the storage file does not yet exist, it safely defaults to returning an empty list.
     *
     * @return  a new list containing the loaded tasks, or an empty list if no file exists, not null
     */
    public static ArrayList<Task> loadTasks() {

        ArrayList<Task> loadedTasks = new ArrayList<>();
        Properties props = new Properties();
        File file = new File(FILE_PATH);

        // Safely return the empty list to prevent errors during the initial application startup
        if (!file.exists()) {
            return loadedTasks;
        }

        // Access the local storage and load the XML-formatted properties
        try (FileInputStream fis = new FileInputStream(file)) {
            props.loadFromXML(fis);

            // Extract the total count of saved tasks to define the reconstruction loop boundaries
            String countStr = props.getProperty("task.count", "0");
            int count = Integer.parseInt(countStr);

            // Reconstruct each task object iteratively with safe default fallbacks
            for (int i = 0; i < count; i++) {
                String title = props.getProperty("task." + i + ".title", "Unknown");
                String description = props.getProperty("task." + i + ".description", "");
                String status = props.getProperty("task." + i + ".status", "Open");

                String timeStr = props.getProperty("task." + i + ".time", "0");
                int time = Integer.parseInt(timeStr);

                String creationDate = props.getProperty("task." + i + ".creationDate", "Unknown");
                String deadline = props.getProperty("task." + i + ".deadline", "None");

                String assignees = props.getProperty("task." + i + ".assignees", "");
                String comments = props.getProperty("task." + i + ".comments", "");

                loadedTasks.add(new Task(title, description, status, time, creationDate, deadline, assignees, comments));
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }

        return loadedTasks;
    }
}
