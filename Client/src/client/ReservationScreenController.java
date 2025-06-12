package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

/**
 * UI-level controller for Reservation.fxml.
 * Delegates all of the real work to ReservationController.
 */
public class ReservationScreenController extends ReservationController{

    // (These fx:id’s match what’s already in your Reservation.fxml)
    @FXML private Button reserveButton;      // already wired in FXML
    @FXML private Button cancelButton;       // already wired in FXML
    /**
     * Called when the user clicks “Reserve”.
     * Just call into the business logic in the superclass.
     * added to abstract
     */
    /*
    public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;

	}*/
    @FXML
    private void submitReservationRequest() {
        // ReservationController.onReserve() will run the validation,
        // send the message to the server, show alerts, etc.
        onReserve();    
    }

    /**
     * Called when the user clicks “Clear”.
     */
    @FXML
    private void submitCancellation() {
        onCancel();
    }

    /**
     * Optionally override how confirmations/errors are shown
     * (super.showAlert is protected in the superclass).
     */
    @Override
    protected void showAlert(AlertType type, String text) {
        super.showAlert(type, text);
    }
}
