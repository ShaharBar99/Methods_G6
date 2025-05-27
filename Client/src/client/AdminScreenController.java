package client;

import javafx.fxml.FXML;

public class AdminScreenController {
	private Runnable backHandler;
    @FXML
    public void viewCurrentParkingStatus() {
        System.out.println("Showing current parking status...");
        // תוסיף לוגיקה להצגת מצב החניון
    }

    @FXML
    public void viewSubscribersList() {
        System.out.println("Showing subscribers list...");
        // תוסיף לוגיקה להצגת רשימת מנויים
    }

    @FXML
    public void generateMonthlyReport() {
        System.out.println("Generating monthly report...");
        // תוסיף לוגיקה ליצירת דוח
    }
    public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;

	}

    @FXML
    private void handleBackButton() {
        if (backHandler != null) {
            backHandler.run();
        }
    }
    
}
