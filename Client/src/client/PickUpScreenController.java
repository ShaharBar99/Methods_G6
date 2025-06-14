package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import logic.*;

public class PickUpScreenController extends Controller{

	private boolean serverConnection = true;
	private ParkingController parkingController = new ParkingController();
	@FXML
	TextField parkingCode;

	/**
	 * Allow only digits and limit to 6 characters to the textbox
	 */
	@FXML
	public void initialize() {
		parkingCode.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d{0,6}")) {
				parkingCode.setText(oldValue); // Revert to old value if invalid
			}
		});
	}
	@Override
	public void setClient(BParkClient client, subscriber sub) {
		this.client = client;
		this.sub = sub;
		parkingController.setClient(client, sub);
		parkingController.setPickUpScreen(this);
	}


	/**
	 * Sends a message to server to send Email and sms to the , shows a
	 * popup
	 */
	@FXML
	public void handleLostCodeRequest() {
		parkingCode.setText("");
		try {
			parkingController.handleLostCode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowAlert.showAlert("Error", "Server communication failure.", AlertType.ERROR);
			e.printStackTrace();
			serverConnection = false;
		}
		if (serverConnection)
			ShowAlert.showAlert("Code sent!", "Code was sent to Email and phone", Alert.AlertType.INFORMATION);
	}

	/**
	 * Sends the code to the ParkingController verifies it and shows a popup
	 */
	@FXML
	public void submitParkingCode() {
		String code = parkingCode.getText().trim();
		if (code.length() < 6) {
			ShowAlert.showAlert("Error", "Enter a 6 digit code!", Alert.AlertType.ERROR);
		} else {
			parkingCode.setText("");
			try {
				int parkingCodeInt = Integer.parseInt(code);
				parkingController.requestCarPickUp(parkingCodeInt);
				if (serverConnection) {
					showPickUpSuccess();
				}
			} catch (Exception e) {
				ShowAlert.showAlert("Error", "Server communication failure\n"+e.getMessage(), AlertType.ERROR);
				serverConnection = false;
			}
		}
	}

	/**
	 * Shows success popup
	 */
	public void showPickUpSuccess() {
		ShowAlert.showAlert("Success",
				"Pickup Success!\nPlease wait while your vehicle moves to the vehicle collection point",
				Alert.AlertType.INFORMATION);
	}

	/**
	 * Shows late pickup popup
	 */
	public void showLateArrivalMessage() {
		ShowAlert.showAlert("Late!", "You were late to pick up your vehicle", Alert.AlertType.WARNING);
	}

	/**
	 * swap the PickUpScreen back to the MainMenuScreen
	 */

	public void handleServerMessage(Object message) {
		parkingController.handleServerResponse(message);
	}
}
