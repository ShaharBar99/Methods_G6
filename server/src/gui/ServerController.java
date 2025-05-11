package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * The class implements the ServerController, handles GUI
 */
public class ServerController {
	@FXML
	private Button exitButton;
	@FXML
	private TableView<List<String>> clientTable;

	@FXML
	private TableColumn<List<String>, String> clientIpColumn; // IP column
	@FXML
	private TableColumn<List<String>, String> clientHostNameColumn; // HostName column
	@FXML
	private TableColumn<List<String>, String> clientStatusColumn; // Status column

	@FXML
	private Label ipLabel; // Server IP

	private ObservableList<List<String>> clientData; // Returned list from the server of the clients

	@FXML
	public void initialize() {
		// This method is called once the FXML has been loaded and initialized
		setServerIP(); // Set the server IP when the scene is initialized
		
		// Updates the TableView
		clientData = FXCollections.observableArrayList();
		clientTable.setItems(clientData);
		clientIpColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1))); // IP at
																												// index
																												// 1
		clientHostNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2))); // HostName
																													// at
																													// index
																													// 2
		clientStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3))); // Status
																													// at
																													// index
																													// 3

	}

	/**
	 * @param e
	 * @throws Exception
	 */
	public void getExitButton(ActionEvent e) throws Exception {
		// Closes the server application when button is pressed
		System.out.println("Closing application...");
		System.gc();
		System.exit(0);
	}

	public void setServerIP() {
		// Sets IP of the server in the ipLabel
		try {
			// Get the IP address of the local machine
			String ip = InetAddress.getLocalHost().getHostAddress();
			ipLabel.setText("Server IP: " + ip);
		} catch (UnknownHostException e) {
			ipLabel.setText("Unable to retrieve server IP");
		}
	}

	/**
	 * @param clientInfo
	 */
	public void recievedServerUpdate(List<List<String>> clientInfo) {
		// Gets all clients and starts method that will update the TableView
		updateClientList(clientInfo);
	}

	/**
	 * @param clientInfo
	 */
	private void updateClientList(List<List<String>> clientInfo) {
		// Gets all clients and updates the TableView
		
		// Clear any old items
		clientData.clear();

		// Add updated client information to the TableView
		for (List<String> string : clientInfo) { // Each string has 4 Strings: id, IP, HostName, Status
			if (string.size() == 4) {
				clientData.add(0, string);
			}
		}
	}
}
