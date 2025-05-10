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

public class ServerController {
	@FXML
	private Button exitButton;
	@FXML
    private TableView<List<String>> clientTable; // Use List<String> directly for the table data

    @FXML
    private TableColumn<List<String>, String> clientIpColumn; // IP column
    @FXML
    private TableColumn<List<String>, String> clientHostNameColumn; // HostName column
    @FXML
    private TableColumn<List<String>, String> clientStatusColumn;

	@FXML
	private Label ipLabel;

	private ObservableList<List<String>> clientData;

	@FXML
	public void initialize() {
		// This method is called once the FXML has been loaded and initialized
		setServerIP(); // Set the server IP when the scene is initialized
		clientData = FXCollections.observableArrayList();
        clientTable.setItems(clientData);
		clientIpColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1))); // IP at index 1
        clientHostNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2))); // HostName at index 2
        clientStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3))); // Status at index 3

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

	public void recievedServerUpdate(List<List<String>> clientInfo) {
		updateClientList(clientInfo);
	}

	private void updateClientList(List<List<String>> clientInfo) {
	    // Clear any old items
		clientData.clear();

	    // Add updated client information to the TableView
	    for (List<String> string : clientInfo) {
	        if (string.size() == 4) {  // Assuming each list contains IP, Hostname, Status, etc.
	        	clientData.add(string);
	        }
	    }
	}
}
