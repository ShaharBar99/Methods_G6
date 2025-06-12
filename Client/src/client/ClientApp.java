package client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

/**
 * The class implements the start of the client application, holds the Main method
 */
public class ClientApp extends Application {
    /**
     *	The main entry point for the JavaFX application (start the GUI)
     */
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect to Server");
        primaryStage.show();
    }
    /**
	 * @param args
	 * Starts the application
	 */
    public static void main(String[] args) {
        launch(args);
    }
}