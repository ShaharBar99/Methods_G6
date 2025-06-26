package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GuestScreenController extends Controller {
	
	private double percent;
	@FXML Button backButton;
    @FXML
    private Label freeSpaceLabel;
    
    @FXML
    private void initialize() {
        updateFreeSpace(percent);
    }

    public void updateFreeSpace(double percent) {
        freeSpaceLabel.setText("Free Space: " + percent + "%");
    }

    @FXML
	protected void handleBackButton() {
		// swap the TableView scene back to the connect screen
    	handleButtonToLogin(backButton);
	}
    public void set_percent(double num) {
    	percent = num;
    	updateFreeSpace(percent);
    }
}
