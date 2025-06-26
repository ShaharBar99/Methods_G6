package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.SendObject;

public class AttendentScreenController extends Controller {

	@FXML
	Button openReservationReportButton;
	@FXML
	Button openSubscribersReportButton;
	@FXML
	Button openActiveSessionsReportButton;
	@FXML
	Button registerNewSubscriberButton;
	@FXML
	Button backButton;

	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}

	@FXML
	public void openReservationReport() {
		try {
			setscreen("Reservations Report", "ViewReservationScreenUI.fxml", "AttendantScreen.fxml", "Attendant Menu",
					openReservationReportButton);
			// Send request to the server
			SendObject<String> request = new SendObject<>("Get", "all reservations");
			client.sendToServerSafely(request);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void openSubscribersReport() {
		try {
			setscreen("Subscribers Report", "ViewSubscriberUI.fxml", "AttendantScreen.fxml", "Attendant Menu",
					openSubscribersReportButton);
			// Send request to the server
			SendObject<String> request = new SendObject<>("Get", "all subscribers");
			client.sendToServerSafely(request);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void openActiveSessionsReport() {
		try {
			setscreen("Active Sessions Report", "ViewActiveSessionsUI.fxml", "AttendantScreen.fxml", "Attendant Menu",
					openActiveSessionsReportButton);
			// Send request to the server
			SendObject<String> request = new SendObject<>("Get", "active parking sessions");
			client.sendToServerSafely(request);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void registerNewSubscriber() {
		try {
			setscreen("Registration Screen", "RegistrationScreen.fxml", "AttendantScreen.fxml", "Attendant Menu",
					registerNewSubscriberButton);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	protected void handleBackButton() {
		// swap the TableView scene back to the connect screen
		handleButtonToLogin(backButton);
	}
}