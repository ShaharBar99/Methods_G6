package client;

import logic.*;

import java.io.IOException;
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
	
	//this.sendToServerSafely(new SendObject<subscriber>("reports",subscriber.getId()));
//	private AttendantController attendantController;
//
//	public RegistrationController(AttendantController attendantController) {
//		this.attendantController = attendantController;
//	}
	
	public RegistrationController(BParkClient client) {
		this.client = client;
	}
	public RegistrationController() {
	}
    
    @FXML
    public void registerNewSubscriber() {
    	System.out.println("start registerNewSubscriber");
		// Get subscriber details from the screen
    	String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
		if (name.isEmpty() || email.isEmpty() || idText.isEmpty() || phone.isEmpty()) {
        	showAlert("One of the fields is empty!.");
            return;
        }
        
        int id; // Validate ID is a valid number
        try {id = Integer.parseInt(idText);}
		catch (NumberFormatException e) {
			showAlert("ID must be a valid number.");
			return;
		}
        
        // לא בטוח אם ליצור אותו פה
        subscriber newSubscriber = new subscriber(id, name, phone, email, Role.SUBSCRIBER, null, "", 0);;
        sendNewSubscriberToServer(newSubscriber);
        
        // צריך את זה?
//        nameField.clear();
//        emailField.clear();
//        idField.clear();
//        phoneField.clear();
    }
    
	
	public void sendNewSubscriberToServer(subscriber newSubscriber) {
		System.out.println("start sendNewSubscriberToServer");
		// the server will check if it already exists and return an error if it does
		// if it doesn't, it will create the new subscriber and return success
        try {
            client.sendToServerSafely(new SendObject<subscriber>("Create new Subscriber", newSubscriber));
            Platform.runLater(() -> showAlert("Subscriber registered successfully!"));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Registration failed: " + e.getMessage()));
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
//	
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
