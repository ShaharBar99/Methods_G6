package client;

import java.lang.management.PlatformLoggingMXBean;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import logic.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UpdateUserDetailsController extends Controller {

	@FXML
	private TextField nameField;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private void handleUpdate() {
		String name = nameField.getText();
		String phone = phoneField.getText();
		String email = emailField.getText();
		if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
			showAlert(AlertType.ERROR, "ERROR", "please fill every field");
		} else {
			if (!name.matches("[a-zA-Z\\s]+")) {
	            ShowAlert.showAlert("Error", "Name must contain only letters and spaces.", AlertType.ERROR);
	            return;
	        }
	        // Validate phone is a 10-digit number XXX-XXX-XXXX
	        if (!phone.matches("\\d{3}-\\d{3}-\\d{4}")) {
	            ShowAlert.showAlert("Error", "Phone number must be exactly 10 digits, and in this format:XXX-XXX-XXXX", AlertType.ERROR);
	            return;
	        }
	        // Validate email format using regex
	        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
	            ShowAlert.showAlert("Error",
	            		"Email must be of the format: username@domain.tld\n\n"
	            		+ "Examples of valid email:\n"
	            		+ "definitely.not.a.bot@skynet.ai\n"
	            		+ "nobody@nowhere.org\n"
	            		+ "ilove.spam@gmail.com\n"
	            		+ "nobody.important@e.braude.ac.il\n"
	            		, AlertType.ERROR);
	            return;
	        }
			sub.setEmail(email);
			sub.setName(name);
			sub.setPhone(phone);
			client.sendToServerSafely(new SendObject<subscriber>("Update", sub));
		}
	}

	public void update(subscriber sub) {
		nameField.setText(sub.getName());
		phoneField.setText(sub.getPhone());
		emailField.setText(sub.getEmail());
	}

	private void showAlert(AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@Override
	public void setClient(BParkClient client, subscriber sub) {
		super.setClient(client, sub);
		update(sub);
	}
	
	public void handleServerMessage(Object msg) {
		// If the message is a SendObject, it contains the subscriber object
		if (msg instanceof SendObject<?>) {
			SendObject<?> response = (SendObject<?>) msg;
			if(response.getObjectMessage().equals("Subscriber")) {
				if(((String)response.getObj()).equals("updated successfully"))
					Platform.runLater(()->{ShowAlert.showAlert("Success",(String)response.getObj() , AlertType.INFORMATION);});
				else
					Platform.runLater(()->{ShowAlert.showAlert("Error",(String)response.getObj() , AlertType.ERROR);});
			}
		}
	}
}