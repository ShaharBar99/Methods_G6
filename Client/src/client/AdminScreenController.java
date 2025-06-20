package client;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.SendObject;

public class AdminScreenController extends Controller{
	
	//private BParkClient client;

	@FXML Button openReservationReportButton;
	@FXML Button openSubscribersReportButton;
	@FXML Button openActiveSessionsReportButton;
	@FXML Button backButton;
    
	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}
	
	@FXML
	public void openReservationReport() {
		try {
			setscreen("Reservations Report", "ReportReservationScreenUI.fxml", "AdminScreen.fxml", "Admin Menu",
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
			setscreen("Subscribers Report", "ReportSubscriberUI.fxml", "AdminScreen.fxml", "Admin Menu",
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
			setscreen("Active Sessions Report", "ReportActiveSessionsUI.fxml", "AdminScreen.fxml", "Admin Menu",
					openActiveSessionsReportButton);
			// Send request to the server
			SendObject<String> request = new SendObject<>("Get", "active parking sessions");
			client.sendToServerSafely(request);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    
    @FXML private void handleBackButton() {
		// swap the TableView scene back to the connect screen
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle("Login");
			stage.setScene(new Scene(root));		
			stage.show();
			Stage currentStage = (Stage) openSubscribersReportButton.getScene().getWindow();
			currentStage.close();
			client.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}