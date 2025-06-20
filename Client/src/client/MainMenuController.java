package client;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainMenuController extends Controller{
	@FXML Button historybutton;
	@FXML Button adminbutton;
	@FXML Button attendantbutton;
	
	@FXML private void handleBackButton() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle("Login");
			stage.setScene(new Scene(root));		
			stage.show();
			// close current window
			Stage currentStage = (Stage) historybutton.getScene().getWindow();
			currentStage.close();
			client.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
    public void admin() {
    	this.setscreen("AdminScreen", "AdminScreen.fxml", "MainMenuScreen.fxml", "Main Menu", adminbutton);
    }
    @FXML
    public void attendant() {
    	this.setscreen("AttendantScreen", "AttendantScreen.fxml", "MainMenuScreen.fxml", "Main Menu", attendantbutton);
    }
    
    @FXML // אני צריך להוסיף
	public void openTimeExtensionScreen() {
		this.setscreen("TimeExtensionScreen", "TimeExtensionScreen.fxml", "MainMenuScreen.fxml", "Main Menu", historybutton);
	}
}
