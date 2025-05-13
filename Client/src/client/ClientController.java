package client;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Order;

public class ClientController {

	@FXML
	private TextField ipTextField; // The TextField for entering the IP address

	@FXML
	private TextField portTextField; // The TextField for entering the port number

	@FXML
	private Button connectButton; // Connect button

	@FXML
	private Button disconnectButton; // Exit button (renamed use)

	private BParkClient clientConnection;

	private List<Order> orders;

	@FXML
	public void handleConnectButton() {
		String ipAddress = ipTextField.getText().trim();
		String portText = portTextField.getText().trim();

		if (ipAddress.isEmpty() || portText.isEmpty()) {
			showAlert("Error", "Please enter both IP and port", Alert.AlertType.ERROR);
			return;
		}

		int port;
		try {
			port = Integer.parseInt(portText);
		} catch (NumberFormatException e) {
			showAlert("Error", "Port must be a valid number", Alert.AlertType.ERROR);
			return;
		}

		connectToServer(ipAddress, port);
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
			clientConnection.setMessageListener(this::handleServerMessage);
			
		} catch (Exception e) {
			showAlert("Error", "Failed to connect to the server at " + ipAddress + ":" + port, Alert.AlertType.ERROR);
			e.printStackTrace();
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("BParkClientUI.fxml"));
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

			BParkClientController controller = loader.getController();
			controller.setOrders(orders);
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
		});
	}
	
	public static void showAlert(String title, String message, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
