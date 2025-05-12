package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;

import client.Client.MyClient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import logic.Order;

public class ClientController {

    @FXML
    private TextField ipTextField;  // The TextField for entering the IP address
    @FXML
    private Button connectButton;  // Connect button
    @FXML
    private Button disconnectButton;  // Disconnect button

    
    // ─── Connect Pane controls ───
    @FXML private Pane    connectPane;

    
    // ─── Main Pane controls ───
    @FXML private VBox    mainPane;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, Date>    colOrderDate;
    @FXML private TableColumn<Order, Integer> colSpot;

    @FXML private TextField orderNumField;
    @FXML private Label lblOrderId, lblCode, lblDate, lblSpot;

    

    private MyClient clientConnection;
    
    
    
    
    @FXML
    public void initialize() {
        // configure table columns:
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        colSpot.setCellValueFactory(cell -> 
            new SimpleIntegerProperty(
              cell.getValue().getParkingSpot().getSpotId()
            ).asObject()
        );

        // register listener for incoming messages:
        clientConnection = null;  // will be set on connect
    }
    
   
    
    
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
