package app;

import controller.TaskController;
import model.ProjectModel;
import view.MainView;

import javax.swing.SwingUtilities;

/**
 * Serves as the main entry point for the project management tool.
 * <p>
 * This class is responsible for initializing the Model-View-Controller (MVC)
 * architecture and ensuring that the graphical user interface is constructed
 * safely on the Event Dispatch Thread.
 */
public class AppMain {

    /**
     * Launches the application by instantiating the core MVC components.
     * <p>
     * To adhere to Java Swing threading rules, the user interface assembly
     * and startup process are wrapped within a lambda expression executed
     * asynchronously by the Swing application framework.
     *
     * @param args  the command line arguments, not null but generally ignored
     */
    public static void main(String[] args) {

        // Ensure GUI creation is safely executed on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {

            // Instantiate the data model containing the core business logic
            ProjectModel model = new ProjectModel();

            // Instantiate the primary graphical user interface
            MainView view = new MainView();

            // Initialize the controller to wire the model and view together
            new TaskController(model, view);

            // Center the application window on the screen and display it
            view.setLocationRelativeTo(null);
            view.setVisible(true);
        });
    }
}
