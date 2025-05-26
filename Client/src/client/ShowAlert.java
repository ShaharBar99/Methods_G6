package client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ShowAlert {

	public static void showAlert(String title, String message, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}


