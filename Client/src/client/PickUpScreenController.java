package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import logic.*;

public class PickUpScreenController {

	private Runnable backHandler;
	private BParkClient client;
	private user user;
	@FXML
	TextArea parkingCode;
	
	// private ParkingController parkingController=new ParkingController(this,user);
	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}
	
	@FXML
	public void handleLostCodeRequest() {
		// parkingController.handleLostCode();
		ShowAlert.showAlert("Code sent!", "Code was sent to Email and phone",Alert.AlertType.INFORMATION);
	}

	@FXML
	public void submitParkingCode() {
		String code = parkingCode.getText().trim();
		if (code.isEmpty()) {
			ShowAlert.showAlert("Error", "Please enter a parking code", Alert.AlertType.ERROR);
		} else {
			try {
				// parkingController.requestCarPickUp(code);
				ShowAlert.showAlert("Message sent!", "Message was sent to Email and phone",
						Alert.AlertType.INFORMATION);
			} catch (Exception e) {
				ShowAlert.showAlert("Error", "Entered wrong code!", Alert.AlertType.ERROR);
			}
		}
	}

	public void showPickUpSuccess() {
		ShowAlert.showAlert("Success",
				"Pickup Success! Please wait while your vehicle moves to the vehicle collection point",
				Alert.AlertType.INFORMATION);
	}

	public void showLateFeeNotice() {
		ShowAlert.showAlert("Late!", "You were late to pick up your vehicle", Alert.AlertType.WARNING);
	}

	/**
	 * swap the PickUpScreen back to the MainMenuScreen
	 */
	@FXML
	private void handleBackButton() {
		if (backHandler != null) {
			backHandler.run();
		}
	}
}
