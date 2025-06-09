package client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Role;
import logic.subscriber;

public class LoginController {

    @FXML private TextField ipField;
    @FXML private ComboBox<String> loginMethodComboBox;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private TextField nameField;
    @FXML private TextField subscriberIdField;
    @FXML private TextField tagField;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button exitButton;
    @FXML private Button connectButton;
    private BParkClient client;
    @FXML
    public void initialize() {
        loginMethodComboBox.setItems(FXCollections.observableArrayList(
            "Name + Subscriber ID", "Tag"
        ));

        roleComboBox.setItems(FXCollections.observableArrayList(
            "Admin", "Subscriber", "Attendant"
        ));

        // שינוי תצוגת השדות לפי login method
        loginMethodComboBox.setOnAction(event -> updateFieldVisibility());
        roleComboBox.setOnAction(event -> updateFieldVisibility());
    }

    private void updateFieldVisibility() {
        String method = loginMethodComboBox.getValue();
        String role = roleComboBox.getValue();

        boolean isSubscriber = "Subscriber".equals(role);
        boolean isAdminOrAttendant = "Admin".equals(role) || "Attendant".equals(role);
        boolean isTag = "Tag".equals(method);

        // עבור Admin/Attendant – רק username ו־password
        usernameField.setVisible(isAdminOrAttendant);
        usernameField.setManaged(isAdminOrAttendant);

        passwordField.setVisible(isAdminOrAttendant);
        passwordField.setManaged(isAdminOrAttendant);

        // עבור Subscriber – name + id או tag
        nameField.setVisible(isSubscriber && !isTag);
        nameField.setManaged(isSubscriber && !isTag);

        subscriberIdField.setVisible(isSubscriber && !isTag);
        subscriberIdField.setManaged(isSubscriber && !isTag);

        tagField.setVisible(isSubscriber && isTag);
        tagField.setManaged(isSubscriber && isTag);
    }

    @FXML
    private void handleLogin() {
    	String ipAddress = ipField.getText().trim();
		String portText = "5555";
		if (ipAddress.isEmpty() || portText.isEmpty()) {
			ShowAlert.showAlert("Error", "Please enter both IP and port", Alert.AlertType.ERROR);
			return;
		}

		int port;
		try {
			port = Integer.parseInt(portText);
		} catch (NumberFormatException e) {
			ShowAlert.showAlert("Error", "Port must be a valid number", Alert.AlertType.ERROR);
			return;
		}
		
		connectToServer(ipAddress, port);
    }
    
    public subscriber createsubfromlogin() {
    	String box1 = loginMethodComboBox.getValue();
		String box2 = roleComboBox.getValue();
		subscriber sub = null;
		if(box2.equals("Admin")) {
			String passwordText = passwordField.getText().trim();
			int passwordInt = Integer.parseInt(passwordText);
			sub = new subscriber(0,usernameField.getText().trim(),null,null,Role.MANAGER,
					null,null,passwordInt);
		}
		else if(box2.equals("Attendant")) {
			String passwordText = passwordField.getText().trim();
			int passwordInt = Integer.parseInt(passwordText);
			sub = new subscriber(0,usernameField.getText().trim(),null,null,Role.ATTENDANT,
					null,null,passwordInt);
		}
		else {
			if(box1.equals("Tag")) {
				sub = new subscriber(0,nameField.getText().trim(),null,null,Role.SUBSCRIBER,
						null,tagField.getText(),0);
			}
			else if(box1.equals("Name + Subscriber ID")) {
				String subid = subscriberIdField.getText().trim();
				int subint = Integer.parseInt(subid);
				sub = new subscriber(0,nameField.getText().trim(),null,null,Role.SUBSCRIBER,
						null,null,subint);
			}
		}
    	return sub;	
    }
    
    public void connectToServer(String ipAddress, int port) {
		try {
			client = new BParkClient(ipAddress, port);
			connectButton.setDisable(true);
			exitButton.setDisable(false);
			client.start();
			
			//subscriber sub1 = createsubfromlogin();
			//client.start(sub1);
			
			// Creates the order table 
			client.setMessageListener(this::handleServerMessage);
			
		} catch (Exception e) {
			ShowAlert.showAlert("Error", "Failed to connect to the server at " + ipAddress + ":" + port, Alert.AlertType.ERROR);
			connectButton.setDisable(false);
		}
	}
    /*
     
     
     
     
     */
    private void handleServerMessage(Object msg) {
    	/*
    	//if(msg instaceof subscriber):
		//connect to server
		 else{
		 	ShowAlert.showAlert("Error", "Wrong details, Alert.AlertType.ERROR);
		 }
    	*/
    	Platform.runLater(() -> {
			System.out.println("[Server] " + msg);

			// Load next screen (Order Table)
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuScreen.fxml"));
			Parent tableRoot = null;
			try {
				tableRoot = loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("cant load main menu screen");
				e.printStackTrace();
			}
			// After Connection start the order table
   
            Stage stage = (Stage) connectButton.getScene().getWindow();
            Scene scene = new Scene(tableRoot);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
			stage.setTitle("Main Menu");
			stage.setMaximized(true);

			// Makes sure when X is pressed it closes the connection to the server
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					System.out.println("Closing application...");
					client.stop();
					System.gc();
					System.exit(0);
				}
			});
			MainMenuController controller = loader.getController();
			//controller.setOrders(orders);
			//controller.setClient(clientConnection);
			controller.setClient(client);
			//controller.setSub(msg);
			controller.setBackHandler(() -> { // Handle back button action using lambda
				try { // Stop the client connection
					client.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try { // Load the connection screen again
					FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
					Parent loginRoot = loginLoader.load();
					stage.setScene(new Scene(loginRoot));
					stage.setTitle("Connect to Server");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			// hand off all future messages to the BParkClientController
			//clientConnection.setMessageListener(controller::handleServerMessage);
		});
		/*else{
		ShowAlert.showAlert("Error", "Cant login to server wrong account", Alert.AlertType.ERROR);
		*/
	}
    @FXML
	public void handleExit() {
		// Exit application
		System.gc();
		Platform.exit();

	}
}
