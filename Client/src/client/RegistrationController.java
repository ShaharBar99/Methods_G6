package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import logic.Role;
import logic.SendObject;
import logic.subscriber;


public class RegistrationController extends Controller{
	

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
	
    
    @FXML
    public void registerNewSubscriber() {
		// Get subscriber details from the screen
    	String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
		if (name.isEmpty() || email.isEmpty() || idText.isEmpty() || phone.isEmpty()) {
        	ShowAlert.showAlert("Error","One of the fields is empty!.",AlertType.ERROR);
            return;
        }
        int id;
        try {id = Integer.parseInt(idText);}
		catch (NumberFormatException e) {
			ShowAlert.showAlert("Error","ID must be a valid number.",AlertType.ERROR);
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
        
    }

	public void sendNewSubscriberToServer(subscriber newSubscriber) {
		System.out.println("starting sendNewSubscriberToServer");
        try {
            client.sendToServerSafely(new SendObject<subscriber>("Create new Subscriber", newSubscriber));
            
        } catch (Exception e) {
            Platform.runLater(() -> ShowAlert.showAlert("Error","Registration failed: " + e.getMessage(),AlertType.ERROR));
            e.printStackTrace();
        }
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
			if ("Subscriber created".equals(response.getObjectMessage())) {
				Object codeAndTag[] = (Object[])response.getObj(); 
				Platform.runLater(() -> ShowAlert.showAlert("Success","Subscriber registered successfully!",AlertType.INFORMATION));
				Platform.runLater(
						() -> {ShowAlert.showAlert("Success","Subscriber registered successfully!",AlertType.INFORMATION);
						ShowAlert.showAlert("Information","Subscriber Log In Code: " + codeAndTag[0] + "\nRFID Tag: " + codeAndTag[1],AlertType.INFORMATION);});
				} 
			else {
				Platform.runLater(() -> ShowAlert.showAlert("Error","Unexpected message type received: " + response.getObjectMessage()+" "+response.getObj(),AlertType.ERROR));
			}
			return;
		}
		// If the message is a String, it indicates a response message
		if (msg instanceof String) {
			String response = (String) msg;
			Platform.runLater(() -> ShowAlert.showAlert("Error",response,AlertType.ERROR));
			return;
		}
	}

}