package client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GuestScreenController extends Controller {
	
	private double percent;
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
    private void handleBack() {
        if (backHandler != null) {
            backHandler.run();
        }
    }
    public void set_percent(double num) {
    	percent = num;
    	updateFreeSpace(percent);
    }
}
