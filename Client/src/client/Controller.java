package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.*;

/**
 * Abstract base class for all JavaFX client-side controllers in the parking management system.
 * Provides common functionality such as client reference management, subscriber data passing,
 * scene transitions, and back navigation handling. Controllers that extend this class are expected
 * to manage scenes and server communication for specific client views.
 */
public abstract class Controller {
    protected Runnable backHandler;
    protected subscriber sub;
    protected BParkClient client;
    protected Boolean isConsole = false;

    /**
     * Sets the back navigation handler.
     *
     * @param backHandler A {@link Runnable} to execute when the back button is triggered.
     */
    public void setBackHandler(Runnable backHandler) {
        this.backHandler = backHandler;
    }

    /**
     * Returns the current client instance.
     *
     * @return The {@link BParkClient} instance associated with the controller.
     */
    public BParkClient getClient() {
        return client;
    }

    /**
     * Sets the client and subscriber reference for this controller.
     *
     * @param client The {@link BParkClient} instance managing server communication.
     * @param sub    The {@link subscriber} instance associated with the logged-in user.
     */
    public void setClient(BParkClient client, subscriber sub) {
        this.sub = sub;
        this.client = client;
    }

    /**
     * Sets whether the console (terminal) mode is active.
     *
     * @param bool {@code true} to enable console mode; {@code false} otherwise.
     */
    public void setIsConsole(boolean bool) {
        isConsole = bool;
    }

    /**
     * Returns the subscriber associated with the current session.
     *
     * @return The {@link subscriber} instance.
     */
    public subscriber getSub() {
        return sub;
    }

    /**
     * Handles the back button event to return to the previous screen.
     * Executed when the FXML-defined back button is clicked.
     */
    @FXML
    private void handleBackButton() {
        if (backHandler != null) {
            backHandler.run();
        }
    }

    /**
     * Handles incoming messages from the server.
     * Subclasses can override this method to define specific behavior.
     *
     * @param msg The message object received from the server.
     */
    protected void handleServerMessage(Object msg) {
        System.err.println("test");
    }

    /**
     * Switches the current view to a new screen while preserving client and subscriber context.
     * Also configures the back navigation to return to the previous screen.
     *
     * @param screen_name  The title of the new screen.
     * @param fxml         The FXML file for the new screen.
     * @param returnFxml   The FXML file to return to when navigating back.
     * @param return_name  The title of the return screen.
     * @param sourceButton The button that triggered the screen transition.
     */
    protected void setscreen(String screen_name, String fxml, String returnFxml, String return_name, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(screen_name);
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            Stage currentStage = (Stage) sourceButton.getScene().getWindow();
            currentStage.close();

            Controller c = loader.getController();
            c.setClient(client, sub);
            c.setIsConsole(this.isConsole);
            client.setMessageListener(c::handleServerMessage);

            c.setBackHandler(() -> {
                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource(returnFxml));
                    Parent loginRoot = loginLoader.load();
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle(return_name);

                    Controller backC = loginLoader.getController();
                    backC.setClient(client, sub);
                    backC.setIsConsole(this.isConsole);

                    if (backC instanceof MainMenuController) {
                        ((MainMenuController) backC).hidebuttons(this.isConsole);
                    }

                    currentStage.show();
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cant");
        }
    }

    /**
     * Switches to the login screen and stops the current client session.
     * Typically called when a logout or session reset is triggered.
     *
     * @param node The button that initiated the action.
     */
    @FXML
    protected void handleButtonToLogin(Button node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) node.getScene().getWindow();
            currentStage.close();

            client.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
