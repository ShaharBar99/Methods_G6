package client;

import logic.*;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class RegistrationController extends Controller{
	
	private BParkClient client;
	protected Runnable backHandler;
	@FXML
	private Button backButton;
    @FXML
    private TextField idField;   // ID field
    @FXML
    private TextField nameField;  // Name field
    @FXML
    private TextField phoneField;  // Phone field
    @FXML
    private TextField emailField;  // Email field
	
//	private AttendantController attendantController;
//
//	public RegistrationController(AttendantController attendantController) {
//		this.attendantController = attendantController;
//	}
	
//	public RegistrationController(BParkClient client) {
//		this.client = client;
//	}
//	public RegistrationController() {
//	}
    
    @FXML
    public void registerNewSubscriber() {
		// Get subscriber details from the screen
    	String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
		if (name.isEmpty() || email.isEmpty() || idText.isEmpty() || phone.isEmpty()) {
        	showAlert("One of the fields is empty!.");
            return;
        }
        int id;
        try {id = Integer.parseInt(idText);}
		catch (NumberFormatException e) {
			showAlert("ID must be a valid number.");
			return;
		}
        
        // create the subscriber locally
        subscriber newSubscriber = new subscriber(id, name, phone, email, Role.SUBSCRIBER, null, "", 0);;
        // send it to server to check if it already exists
        // if it does, the server will return an error message
        // if it doesn't, the server will create the new subscriber and return success
        sendNewSubscriberToServer(newSubscriber);
        
        // clear the screen fields after sending
        nameField.clear();
        emailField.clear();
        idField.clear();
        phoneField.clear();
        
        getSubscriberCodeAndRFIDTag(newSubscriber);
    }

	public void sendNewSubscriberToServer(subscriber newSubscriber) {
		System.out.println("starting sendNewSubscriberToServer");
        try {
            client.sendToServerSafely(new SendObject<subscriber>("Create new Subscriber", newSubscriber));
            Platform.runLater(() -> showAlert("Subscriber registered successfully!"));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Registration failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }
	
	public void getSubscriberCodeAndRFIDTag(subscriber newSubscriber) {
		// the server will return the subscriber's code and RFID tag
		try {
			SendObject<subscriber> request = new SendObject<>("Get Subscriber Code and RFID Tag", newSubscriber);
			client.sendToServerSafely(request);
			Platform.runLater(() -> showAlert(
					"Subscriber Log In Code: " + newSubscriber.getCode() + "\nRFID Tag: " + newSubscriber.getTag()));
		} catch (Exception e) {
			Platform.runLater(() -> showAlert("Failed to retrieve subscriber code and RFID tag: " + e.getMessage()));
			e.printStackTrace();
		}	
	}
    
	private void showAlert(String msg) {
	    Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
	    alert.showAndWait();
	}
	
	@FXML
	private void handleBackButton() {
		if (backHandler != null) {
			backHandler.run();
		}
	}

	public void handleServerMessage(Object msg) {
		System.out.println("starting handleServerMessage");
		System.out.println("[Server] " + msg);
		// If the message is a SendObject, it contains the subscriber object
		if (msg instanceof SendObject<?>) {
			SendObject<?> response = (SendObject<?>) msg;
			if ("Subscriber Code and RFID Tag".equals(response.getObjectMessage())) {
				subscriber sub = (subscriber) response.getObj();
				Platform.runLater(
						() -> showAlert("Subscriber Log In Code: " + sub.getCode() + "\nRFID Tag: " + sub.getTag()));
				} 
			else {
				Platform.runLater(() -> showAlert("Unexpected message type received: " + response.getObjectMessage()));
			}
			return;
		}
		// If the message is a String, it indicates a response message
		if (msg instanceof String) {
			String response = (String) msg;
			Platform.runLater(() -> showAlert(response));
			return;
		}
	}
	
//	@FXML private void handleBackButton() {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
//			Parent root = loader.load();
//			Stage stage = new Stage();
//			stage.setTitle("Login");
//			stage.setScene(new Scene(root));		
//			stage.show();
//			// סגור את החלון הנוכחי
//			Stage currentStage = (Stage) historybutton.getScene().getWindow();
//			currentStage.close();
//			client.stop();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

}
