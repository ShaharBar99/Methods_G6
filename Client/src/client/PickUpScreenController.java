package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import logic.*;

public class PickUpScreenController extends Controller{

	private boolean serverConnection = true;
	private ParkingController parkingController;
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

	public void setParkingController(ParkingController parkingController) {
		this.parkingController = parkingController;
	}

	/* this method initializes the parking controller if it is null */
	public void initilizeParkingControllerIfNeeded() {
		if (parkingController == null) {
			parkingController = ParkingController.getInstance(client); // get the singleton instance of
																		// ParkingController
			parkingController.setPickUpScreen(this); // set the DropOffScreenController for the ParkingController
			parkingController.setSubscriber1(sub); // set the  for the ParkingController
		}
	}

	/**
	 * Sends a message to server to send Email and sms to the , shows a
	 * popup
	 */
	@FXML
	public void handleLostCodeRequest() {
		parkingCode.setText("");
		initilizeParkingControllerIfNeeded();
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
				initilizeParkingControllerIfNeeded();
				parkingController.requestCarPickUp(parkingCodeInt);
				if (serverConnection) {
					showPickUpSuccess();
				}
			} catch (Exception e) {
				ShowAlert.showAlert("Error", "Server communication failure.", AlertType.ERROR);
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
		initilizeParkingControllerIfNeeded();
		parkingController.handleServerResponse(message);
	}
}
