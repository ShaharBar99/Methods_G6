package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import logic.*;

public class PickUpScreenController {

	private Runnable backHandler;
	private BParkClient client;
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

	/**
	 * @param backHandler Sets the backHandler
	 */
	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}

	public void setParkingController(ParkingController parkingController) {
		this.parkingController = parkingController;
	}
	
	public void setClient(BParkClient client) {
		this.client = client;
	}

	private void initializeParkingControllerIfNeeded() {
		if (parkingController == null) {
			parkingController = ParkingController.getInstance(client);
		}
	}

	/**
	 * Sends a message to server to send Email and sms to the subscriber, shows a
	 * popup
	 */
	@FXML
	public void handleLostCodeRequest() {
		parkingCode.setText("");
		initializeParkingControllerIfNeeded();
		try {
			parkingController.handleLostCode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowAlert.showAlert("Error", "Server communication failure.", AlertType.ERROR);
			e.printStackTrace();
			serverConnection = false;
		}
		if(serverConnection)
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
				int parkingCode = Integer.parseInt(code);
				initializeParkingControllerIfNeeded();
				try {
					parkingController.requestCarPickUp(parkingCode);
				} catch (Exception e) {
					ShowAlert.showAlert("Error", "Server communication failure.", AlertType.ERROR);
					serverConnection = false;
				}
			} catch (Exception e) {
				ShowAlert.showAlert("Error", "Entered wrong code!", Alert.AlertType.ERROR);
			}
			if(serverConnection)
			showPickUpSuccess();
		}
	}

	/**
	 * Shows success popup
	 */
	public void showPickUpSuccess() {
		ShowAlert.showAlert("Success",
				"Pickup Success! Please wait while your vehicle moves to the vehicle collection point",
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
	@FXML
	private void handleBackButton() {
		if (backHandler != null) {
			backHandler.run();
		}
	}
}