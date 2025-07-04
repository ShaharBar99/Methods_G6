package client;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.*;

public class MainMenuController extends Controller {
	@FXML
	Button historybutton;
	@FXML
	Button dropoff;
	@FXML
	Button Pickup;
	@FXML
	Button Reservation;
	@FXML
	Button Extension;
	@FXML
	Button Update;
	@FXML
	protected void handleBackButton() {
		// swap the TableView scene back to the connect screen
		handleButtonToLogin(historybutton);
	}

	@FXML
	public void reservationRequest() {
		this.setscreen("ReservationScreen", "Reservation.fxml", "MainMenuScreen.fxml", "Main Menu", historybutton);
	}

	@FXML
	public void parkingRequest() {
		this.setscreen("DropOffScreen", "DropOffScreen.fxml", "MainMenuScreen.fxml", "Main Menu", historybutton);
	}

	@FXML
	public void retrieveRequest() {
		this.setscreen("Pickupscreen", "PickUpScreen.fxml", "MainMenuScreen.fxml", "Main Menu", historybutton);
	}

	@FXML
	public void historyRequest() {
		this.setscreen("HistoryScreen", "HistoryScreen.fxml", "MainMenuScreen.fxml", "Main Menu", historybutton);
	}

	@FXML
	public void openTimeExtensionScreen() {
		this.setscreen("TimeExtensionScreen", "TimeExtensionScreen.fxml", "MainMenuScreen.fxml", "Main Menu",
				historybutton);
	}
	
	@FXML public void openUpdateUserDetails() {
        this.setscreen("UpdateUserDetails", "UpdateUserDetails.fxml","MainMenuScreen.fxml", "Main Menu",historybutton);
    }
	public void hidebuttons(boolean terminal) {
	    if(terminal) {
	    	historybutton.setVisible(false);
	    	historybutton.setManaged(false);
	    	Reservation.setVisible(false);
	    	Reservation.setManaged(false);
	    	Update.setVisible(false);
	    	Update.setManaged(false);
	    }
	    else {
	    	dropoff.setVisible(false);
	    	dropoff.setManaged(false);
	    	Pickup.setVisible(false);
	    	Pickup.setManaged(false);
	    }
	}
}