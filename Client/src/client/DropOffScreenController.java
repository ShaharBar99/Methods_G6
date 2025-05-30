package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import logic.ParkingSpot;

public class DropOffScreenController {
	private BParkClient client;
	private ParkingController parkingController;
	private Runnable backHandler; // to handle the "back" button
	@FXML
	TextField parkingCode; // TextField to display the parking code
	
	
	/////////////////////////////////////////////////
	/*----------SET AND INITIALIZE THINGS----------*/
	////////////////////////////////////////////////
	
	public void setClient(BParkClient client) {
		this.client = client;
	}
	
	public void setParkingController(ParkingController parkingController) {
		this.parkingController = parkingController;
	}
	
	/* this method initializes the parking controller if it is null*/
	public void initilizeParkingControllerIfNeeded() {
		if (parkingController == null) {
			parkingController = parkingController.getInstance(client);
			parkingController.setDropOffScreen(this); // set the DropOffScreenController for the ParkingController
		}	
	}
	
	/*
	 * @param backHandler Sets the backHandler
	 */
	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}
	
	/*
	 * swap the DropOffScreen back to the MainMenuScreen
	 */
	@FXML
	private void handleBackButton() {
		if (backHandler != null) {
			backHandler.run();
		}
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
		initilizeParkingControllerIfNeeded(); // make sure the parking controller is initialized);
		try {
			parkingController.confirmDropOff();
			}
		catch (Exception e) {  // if subscriber1 is null, throw an exception
			ShowAlert.showAlert("Error", "An error occurred while processing your request: ",
					Alert.AlertType.ERROR);
			e.printStackTrace();
		}
	}
	
	/*
	 * displays the assigned parking spot in the text field
	 */
	public void displayAssignedSpot(ParkingSpot spot) {
		//זה אמור להיות Alert או setText?
		parkingCode.setText("Your parking spot is " + spot.getSpotId());
		ShowAlert.showAlert("Assigned Spot", "Your parking spot is: " + spot.getSpotId(), Alert.AlertType.INFORMATION);
	}

	/*
	 * gets the parking code from ParkingController and displays it
	 */
	public void displayParkingCode() {
		int parkingCode = parkingController.generateParkingCode();
		
		//זה אמור להיות Alert או setText?
		this.parkingCode.setText(String.valueOf(parkingCode)); // display the parking code in the text field
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
		System.out.println("inside showNoAvailability");
		//ShowAlert.showAlert("Error", "No parking spots available at the moment.", Alert.AlertType.ERROR);
	}
}