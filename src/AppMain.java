import controller.TaskController;
import model.ProjectModel;
import view.MainView;

import javax.swing.SwingUtilities;

/**
 * The main entry point for the Project Management Tool.
 * It initializes the MVC components and ensures the GUI is created safely.
 */

public class AppMain {

    public static void main(String[] args) {

        // Safety practice in Java Swing, using a Lambda expression.
        SwingUtilities.invokeLater(() -> {

            // 1. Create the Model (Data)
            ProjectModel model = new ProjectModel();

            // 2. Create the View (GUI)
            MainView view = new MainView();

            // 3. Create the Controller (Brain) and wire them together
            // The Controller links the Model and View internally
            new TaskController(model, view);

            // 4. Make the window visible
            // Centers the window on the screen and shows it
            view.setLocationRelativeTo(null);
            view.setVisible(true);
        });
    }

}
