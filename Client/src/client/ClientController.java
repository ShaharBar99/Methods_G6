package client;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Order;

public class ClientController {

	@FXML
	private TextField ipTextField; // The TextField for entering the IP address

	@FXML
	private Button connectButton; // Connect button

	@FXML
	private Button disconnectButton; // Exit button (renamed use)

	private BParkClient clientConnection;
	private Stage stage;
	private List<Order> orders;

	@FXML
	public void handleConnectButton() {
		String ipAddress = ipTextField.getText().trim();

		if (ipAddress.isEmpty()) {
			ShowAlert.showAlert("Error", "Please enter IP Address", Alert.AlertType.ERROR);
			return;
		}

		connectToServer(ipAddress, 5555); // Connect to the server on port 5555);
	}

	@FXML
	public void handleDisconnectButton() {
		// Exit application
		System.gc();
		Platform.exit();

	}

	public void connectToServer(String ipAddress, int port) {
		try {
			clientConnection = new BParkClient(ipAddress, port);
			connectButton.setDisable(true);
			disconnectButton.setDisable(false);
			clientConnection.start();
			// Creates the order table 
			clientConnection.setMessageListener(this::handleServerMessage);
			
		} catch (Exception e) {
			ShowAlert.showAlert("Error", "Failed to connect to the server at " + ipAddress + ":" + port, Alert.AlertType.ERROR);
			connectButton.setDisable(false);
		}
	}

	private void handleServerMessage(Object msg) {
		Platform.runLater(() -> {
			System.out.println("[Server] " + msg);
			if (msg instanceof List<?>) {
				orders = (List<Order>) msg;
				System.out.println(orders);
				System.out.println("Orders added");
			}

			// Load next screen (Order Table)
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DropOffScreen.fxml"));
			Parent tableRoot = null;
			try {
				tableRoot = loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// After Connection start the order table
			Stage stage = (Stage) connectButton.getScene().getWindow();
			stage.setScene(new Scene(tableRoot));
			stage.setTitle("Orders Table");
			// Makes sure when X is pressed it closes the connection to the server
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					System.out.println("Closing application...");
					clientConnection.stop();
					System.gc();
					System.exit(0);
				}
			});
			stage.setMaximized(true); // Maximize the window
			DropOffScreenController controller = loader.getController();
			controller.setClient(clientConnection);
			controller.setBackHandler(() -> { // Handle back button action using lambda
				try { // Stop the client connection
					clientConnection.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try { // Load the connection screen again
					FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("client.fxml"));
					Parent loginRoot = loginLoader.load();
					stage.setScene(new Scene(loginRoot));
					stage.setTitle("Connect to Server");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			// hand off all future messages to the BParkClientController
			clientConnection.setMessageListener(controller::handleServerMessage);
		});
	}
	
}
