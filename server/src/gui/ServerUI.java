package gui;

import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import server.BparkServer;

public class ServerUI extends Application {

	private ListView<String> clientInfoList; 

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
        try {
            // Load the FXML layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerGUI.fxml"));
            AnchorPane root = loader.load();
            ServerController serverController = loader.getController();
            // Set up the scene
            Scene scene = new Scene(root, 250, 450);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Server Client Connections");
            primaryStage.show();

            BparkServer server = new BparkServer(5555, serverController);
    		server.listen();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

