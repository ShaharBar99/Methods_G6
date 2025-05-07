package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import server.BparkServer;

public class ServerController {
	@FXML
	private Button exitButton;
	@FXML
	private ListView<String> clientInfoList = new ListView<>();

	@FXML
	private Label ipLabel;

	@FXML
	public void initialize() {
		// This method is called once the FXML has been loaded and initialized
		setServerIP(); // Set the server IP when the scene is initialized\
		
	}

	public void getExitButton(ActionEvent e) throws Exception {
		System.gc();
		System.exit(0);
	}

	public void setServerIP() {
		try {
			// Get the IP address of the local machine
			String ip = InetAddress.getLocalHost().getHostAddress();
			ipLabel.setText("Server IP: " + ip);
		} catch (UnknownHostException e) {
			ipLabel.setText("Unable to retrieve server IP");
		}
	}

	public void updateClientList(List<String> clientInfo) {
		
		clientInfoList.getItems().clear(); // Clear any old items from the ListView
		clientInfoList.getItems().addAll(clientInfo); // Add the new list of clients
	}
}
