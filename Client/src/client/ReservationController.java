package client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Reservation;
import logic.SendObject;
import logic.subscriber;

public class ReservationController extends Controller{

    // *** FXML-injected fields (no spot field any more) ***
    @FXML private DatePicker datePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    @FXML private TableView<Reservation> futureReservationsTable;
    @FXML private TableColumn<Reservation,String> colSpot;
    @FXML private TableColumn<Reservation,LocalDate> colDate;
    @FXML private TableColumn<Reservation,String> colStart;
    @FXML private TableColumn<Reservation,String> colEnd;

    @FXML
    public void initialize() {
        // configure table columns
        colSpot .setCellValueFactory(new PropertyValueFactory<>("spot"));
        colDate .setCellValueFactory(new PropertyValueFactory<>("date"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEnd  .setCellValueFactory(new PropertyValueFactory<>("endTime"));
        // DON'T call getFutureReservationsFor() here, client is still null
    }
    /** Validate date, times (HH:MM), start < end, and booking window 24 h – 7 days out */
    protected boolean validateReservation() {
        LocalDate date = datePicker.getValue();
        String start = startTimeField.getText().trim();
        String end   = endTimeField.getText().trim();

        // Basic non‐empty check
        if (date == null || start.isEmpty() || end.isEmpty()) {
            showAlert(AlertType.ERROR, "Date, start time and end time are required.");
            return false;
        }

        LocalTime startT, endT;
        try {
            startT = LocalTime.parse(start);
            endT   = LocalTime.parse(end);
        } catch (DateTimeParseException ex) {
            showAlert(AlertType.ERROR, "Time must be in HH:MM format.");
            return false;
        }

        // Ensure start < end
        if (!startT.isBefore(endT)) {
            showAlert(AlertType.ERROR, "Start time must be before end time.");
            return false;
        }

        // Combine date & start into a LocalDateTime
        LocalDateTime now            = LocalDateTime.now();
        LocalDateTime reservationStart = LocalDateTime.of(date, startT);

        // Must be at least 24h in the future
        if (reservationStart.isBefore(now.plusHours(24))) {
            showAlert(AlertType.ERROR, "Reservations must be placed at least 24 hours in advance.");
            return false;
        }

        // Must be no more than 7 days ahead
        if (reservationStart.isAfter(now.plusDays(7))) {
            showAlert(AlertType.ERROR, "Reservations cannot be made more than 7 days in advance.");
            return false;
        }

        return true;
    }

	/** Called by ReservationScreenController#submitReservationRequest() */
	/** Called when the user clicks “Reserve”. */
	protected void onReserve() {
		if (!validateReservation())
			return;
		
		// Build a simple payload like "2025-06-10 09:00 11:00"

		Reservation reservation = new Reservation(0, sub.getId(), datePicker.getValue(),
				startTimeField.getText().trim(), endTimeField.getText().trim());
		String payload = String.format("Create Reservation: %s %s %s", datePicker.getValue(),
				startTimeField.getText().trim(), endTimeField.getText().trim());
		if (!ShowAlert.showConfirmation("Confirm Reservation",
				"Are you sure you want to " +payload+" ?")) {
			clearForm();
			return; // user clicked Cancel
		}
		// Wrap <payload, subscriberId> in a SendObject<Integer>
		SendObject<Reservation> req = new SendObject<>(payload, reservation
		// subscribe.getId()
		);
		client.sendToServerSafely(req);

		showAlert(AlertType.INFORMATION,
				"Reservation request sent for subscriber #" + sub.getId() + ":\n" + payload);

		clearForm();
		getFutureReservationsFor(); // Immediately refresh the table
	}

	/** Called by ReservationScreenController#submitCancellation() */
	protected void onCancel() {
		clearForm();
	}

	private void clearForm() {
		datePicker.setValue(LocalDate.now());
		startTimeField.clear();
		endTimeField.clear();
	}

	/** Ask the server for this subscriber’s future reservations. */
	protected void getFutureReservationsFor() {
		// The “command” string can be anything your server expects, for example:
		String command = "GetSubscribersResesrvations";

		// Wrap <command, subscriberId> in a SendObject<Integer>
		SendObject<Integer> req = new SendObject<>(command, sub.getId());
		client.sendToServerSafely(req);
	}

	/** Entry point for all messages from the server */
	public void handleServerMessage(Object message) {
		if (message instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<Reservation> list = (List<Reservation>) message;
			Platform.runLater(() -> futureReservationsTable.getItems().setAll(list));
		} else if (message instanceof SendObject<?>) {
			SendObject<?> sendObject = (SendObject<?>)message;
			if(sendObject.getObj() instanceof String)
				Platform.runLater(() -> showAlert(AlertType.INFORMATION, sendObject.getObjectMessage()+" "+sendObject.getObj()));
			else if(sendObject.getObj() instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<Reservation> list = (List<Reservation>) sendObject.getObj();
				Platform.runLater(() -> futureReservationsTable.getItems().setAll(list));
			}
		} else {
			Platform.runLater(() -> showAlert(AlertType.ERROR, message.toString()));
		}
	}
    /** Utility alert pop-up */
    protected void showAlert(AlertType type, String text) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }
    @Override
    public void setClient(BParkClient client, subscriber sub) {
        super.setClient(client, sub); // 
        getFutureReservationsFor();
    }
}
