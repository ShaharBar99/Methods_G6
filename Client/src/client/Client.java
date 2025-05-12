/*package client;

import ocsf.client.AbstractClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Client extends Application {

    private static MyClient clientConnection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create simple GUI for entering IP
            AnchorPane root = new AnchorPane();

            // Create TextField for IP
            TextField ipField = new TextField();
            ipField.setPromptText("Enter Server IP");
            ipField.setLayoutX(50);
            ipField.setLayoutY(30);

            // Create Connect Button
            Button connectButton = new Button("Connect");
            connectButton.setLayoutX(50);
            connectButton.setLayoutY(70);

            // Create Disconnect Button
            Button disconnectButton = new Button("Disconnect");
            disconnectButton.setLayoutX(150);
            disconnectButton.setLayoutY(70);
            disconnectButton.setDisable(false);  // Initially disabled

            // Add elements to the scene
            root.getChildren().add(ipField);
            root.getChildren().add(connectButton);
            root.getChildren().add(disconnectButton);

            // Set up the scene and stage
            Scene scene = new Scene(root, 300, 150);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Client - Connect to Server");
            primaryStage.show();

            // Set up actions for buttons
            connectButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String ipAddress = ipField.getText();
                    connectToServer(ipAddress); // Connect to the server using the entered IP
                }
            });

            disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    disconnectFromServer(); // Disconnect from the server
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to connect to the server
    public static void connectToServer(String ipAddress) {
        try {
            // Create a new instance of MyClient
            clientConnection = new MyClient(ipAddress, 5555); // IP and port of the server
            clientConnection.openConnection();  // Open the connection

            // Show success message
            showAlert("Success", "Successfully connected to the server at " + ipAddress, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            // Handle connection failure
            showAlert("Error", "Failed to connect to the server at " + ipAddress, Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Method to disconnect from the server
    public static void disconnectFromServer() {
        if (clientConnection != null && clientConnection.isConnected()) {
            try {
            	clientConnection.sendToServer("Client disconnected");
                clientConnection.closeConnection();  // Close the connection
                showAlert("Disconnected", "Successfully disconnected from the server.", Alert.AlertType.INFORMATION);
                
            } catch (Exception e) {
                // Handle failure during disconnection
                showAlert("Error", "Failed to disconnect from the server.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    // Method to show alerts
    public static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Custom MyClient class to extend AbstractClient
    public static class MyClient extends AbstractClient {

        public MyClient(String host, int port) {
            super(host, port);  // Calling the superclass constructor
        }

        @Override
        public void handleMessageFromServer(Object msg) {
            // Handle message from the server
            System.out.println("Message from server: " + msg);
        }

        @Override
        public void connectionClosed() {
            // Handle connection closed
            System.out.println("Connection closed with the server.");
        }

        @Override
        public void connectionException(Exception exception) {
            // Handle connection exception
            System.out.println("Connection exception: " + exception);
        }
    }
}
*/