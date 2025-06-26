package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.*;

public class DropOffScreenController extends Controller {
	// subscriber for the parking controller
	// testing
	// private subscriber subscriber1 = new subscriber(0, "", "", "",
	// Role.SUBSCRIBER, null, 0);
	//
	private ParkingController parkingController = new ParkingController();

	@FXML
	private Label assignedSpotLabel;

	@FXML
	private Label parkingCodeLabel;

	/////////////////////////////////////////////////
	/*----------SET AND INITIALIZE THINGS----------*/
	////////////////////////////////////////////////

	@Override
	public void setClient(BParkClient client, subscriber sub) {
		this.client = client;
		this.sub = sub;
		parkingController.setClient(client, sub);
		parkingController.setDropOffScreen(this);
	}

	//////////////////////////////////////////////
	/*-------------------ACTIONS----------------*/
	//////////////////////////////////////////////

	/*
	 * operates the Confirm Drop Off button
	 */
	public void submitParkingRequest() {
		// if there's a free spot assigns it, generates a code,
		// creates a parking session, and shows success message
		try {
			if (!ShowAlert.showConfirmation("Confirm Vehicle dropoff",
					"Are you sure you want to dropoff your vehicle?")) {
				return; // user clicked Cancel
			}
			parkingController.confirmDropOff();
		} catch (Exception e) { // if subscriber1 is null, throw an exception
			ShowAlert.showAlert("Error", "An error occurred while processing your request: "+e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}
	
	public void submitParkingRequestUsingReservation() {
		try {
			if (!ShowAlert.showConfirmation("Confirm Vehicle dropoff",
					"Are you sure you want to dropoff your vehicle using reservation?")) {
				return; // user clicked Cancel
			}
			parkingController.implementDropoffUsingReservation();
		} catch (Exception e) {
			ShowAlert.showAlert("Error", "An error occurred while processing your request: "+e.getMessage(), Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}

	/*
	 * displays the assigned parking spot in the text field
	 */
	public void displayAssignedSpot(int spotId) {
		// זה אמור להיות Alert או setText?
		assignedSpotLabel.setText("Your parking spot is " + spotId);
		ShowAlert.showAlert("Assigned Spot", "Your parking spot is: " + spotId, Alert.AlertType.INFORMATION);
	}


	/*
	 * gets the parking code from ParkingController and displays it
	 */
	public void displayParkingCode(int parkingCode) {
		// זה אמור להיות Alert או setText?
		parkingCodeLabel.setText("Your parking code is " + String.valueOf(parkingCode)); // display the parking code in
																							// the text field
		ShowAlert.showAlert("Parking Code", "Your parking code is: " + parkingCode, Alert.AlertType.INFORMATION);
	}

	
	/*
	 * Shows success message when parking is successful
	 */
	public void showParkingSuccess() {
		ShowAlert.showAlert("Success", "Parking Success! spot assigned successfully!", Alert.AlertType.INFORMATION);
	}

	/*
	 * Shows error message when parking is unsuccessful
	 */
	public void showNoAvailability() {
		ShowAlert.showAlert("Error", "No parking spots available at the moment.", Alert.AlertType.ERROR);
	}

	public void handleServerMessage(Object message) {
		parkingController.handleServerResponse(message);
	}
}