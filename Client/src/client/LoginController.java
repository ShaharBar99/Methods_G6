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
import logic.*;

public class LoginController {

    @FXML private TextField ipField;
    @FXML private ComboBox<String> loginMethodComboBox;
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
            "Name + Subscriber Code", "Tag"
        ));


        // שינוי תצוגת השדות לפי login method
        loginMethodComboBox.setOnAction(event -> updateFieldVisibility());

    }

    private void updateFieldVisibility() {
        String method = loginMethodComboBox.getValue();
        boolean isTag = "Tag".equals(method);

        // עבור Subscriber – name + id או tag
        nameField.setVisible(!isTag);
        nameField.setManaged(!isTag);

        subscriberIdField.setVisible(!isTag);
        subscriberIdField.setManaged(!isTag);

        tagField.setVisible(isTag);
        tagField.setManaged(isTag);
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
		subscriber sub = null;
		String passwordText = subscriberIdField.getText().trim();
		try {
		int passwordInt = Integer.parseInt(passwordText);
		sub = new subscriber(0,nameField.getText().trim(),null,null,null,
				null,null,passwordInt);	
	    } catch (Exception e) {
			ShowAlert.showAlert("Error", "The type of the fields is wrong", Alert.AlertType.ERROR);
		}
    	return sub;
    }
    
    public void connectToServer(String ipAddress, int port) {
		try {
			client = new BParkClient(ipAddress, port);
			//connectButton.setDisable(true);
			exitButton.setDisable(false);
			//client.start();
			subscriber sub1 = createsubfromlogin();
			client.start(sub1);
			
			// Creates the order table 
			client.setMessageListener(this::handleServerMessage);
			
		} catch (Exception e) {
			ShowAlert.showAlert("Error", "Failed to connect to the server at " + ipAddress + ":" + port, Alert.AlertType.ERROR);
			connectButton.setDisable(false);
		}
	}

    private void handleServerMessage(Object msg) {
    	Platform.runLater(() -> {
    		if(msg instanceof SendObject) {
    			SendObject send = (SendObject)msg;
    			if(send.getObj() instanceof subscriber) {
    		//connect to server
    			subscriber sub = (subscriber) send.getObj();
    			System.out.println("[Server] " + msg);
    			if(sub.getRole().equals(Role.SUBSCRIBER)) {
    				this.connectclient(sub,"MainMenuScreen.fxml","Main Menu");
    			}
    			else if(sub.getRole().equals(Role.ATTENDANT)) {
    				this.connectclient(sub,"AttendantScreen.fxml","Attendant Menu");
    			}
    			else if(sub.getRole().equals(Role.MANAGER)) {
    				this.connectclient(sub,"AdminScreen.fxml","Admin Menu");
    			}

		}
 
		else{
		client.stop();
		ShowAlert.showAlert("Error", "Cant login to server wrong account", Alert.AlertType.ERROR);
		}
    		}
	});
    }
    private void connectclient(subscriber sub,String fxml,String title) {
    	// Load next screen (Order Table)
		Controller controller = null;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		Parent tableRoot = null;
		
		try {
			tableRoot = loader.load();
			controller = loader.getController();
			controller.setClient(client,sub);
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
		stage.setTitle(title);
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
				client.setMessageListener(controller::handleServerMessage);
}
    @FXML
	public void handleExit() {
		// Exit application
		System.gc();
		Platform.exit();

	}
}
