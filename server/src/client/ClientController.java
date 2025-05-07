package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import client.Client.MyClient;
import javafx.event.ActionEvent;

public class ClientController {

    @FXML
    private TextField ipTextField;  // The TextField for entering the IP address
    @FXML
    private Button connectButton;  // Connect button
    @FXML
    private Button disconnectButton;  // Disconnect button

    private MyClient clientConnection;

    // Action handler for the "Connect" button
    @FXML
    public void handleConnectButton(ActionEvent event) {
        String ipAddress = ipTextField.getText().trim();
        
        if (ipAddress.isEmpty()) {
            showAlert("Error", "Please enter a valid IP address", Alert.AlertType.ERROR);
        } else {
            connectToServer(ipAddress);
        }
    }

    // Action handler for the "Disconnect" button
    @FXML
    public void handleDisconnectButton(ActionEvent event) {
        disconnectFromServer();
    }

    // Method to connect to the server
    public void connectToServer(String ipAddress) {
        try {
            // Create a new instance of MyClient
            clientConnection = new MyClient(ipAddress, 5555); // Use your server IP and port
            clientConnection.openConnection();  // Open the connection

            // Disable the connect button and enable the disconnect button
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);

            // Show success message
            showAlert("Success", "Successfully connected to the server at " + ipAddress, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            // Handle connection failure
            showAlert("Error", "Failed to connect to the server at " + ipAddress, Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Method to disconnect from the server
    public void disconnectFromServer() {
        if (clientConnection != null && clientConnection.isConnected()) {
            try {
                clientConnection.closeConnection();  // Close the connection
                connectButton.setDisable(false);  // Enable the connect button
                disconnectButton.setDisable(true);  // Disable the disconnect button
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
}
