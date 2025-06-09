package client;

import java.time.LocalDate;
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

    protected BParkClient client;

    /** Called by ReservationScreenController after load */
    public void setClient(BParkClient client) {
        this.client = client;
    }

    @FXML
    public void initialize() {
        // configure table columns
        colSpot .setCellValueFactory(new PropertyValueFactory<>("spot"));
        colDate .setCellValueFactory(new PropertyValueFactory<>("date"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEnd  .setCellValueFactory(new PropertyValueFactory<>("endTime"));
        // DON'T call getFutureReservationsFor() here, client is still null
    }

    /** Validate date, times (HH:MM), start < end, date ≥ today */
    protected boolean validateReservation() {
        LocalDate date = datePicker.getValue();
        String start = startTimeField.getText().trim();
        String end   = endTimeField.getText().trim();

        if (date == null || start.isEmpty() || end.isEmpty()) {
            showAlert(AlertType.ERROR, "Date, start time and end time are required.");
            return false;
        }

        try {
            LocalTime s = LocalTime.parse(start);
            LocalTime e = LocalTime.parse(end);
            if (!s.isBefore(e)) {
                showAlert(AlertType.ERROR, "Start time must be before end time.");
                return false;
            }
        } catch (DateTimeParseException ex) {
            showAlert(AlertType.ERROR, "Time must be in HH:MM format.");
            return false;
        }

        if (date.isBefore(LocalDate.now())) {
            showAlert(AlertType.ERROR, "Date cannot be in the past.");
            return false;
        }

        return true;
    }

    /** Called by ReservationScreenController#submitReservationRequest() */
    protected void onReserve() {
        if (!validateReservation()) return;

        // Server will choose an available spot for us:
        String msg = String.format("RESERVE %s %s %s",
            datePicker.getValue(),
            startTimeField.getText().trim(),
            endTimeField.getText().trim()
        );
        client.sendToServerSafely(msg);
        showAlert(AlertType.INFORMATION, "Reservation request sent:\n" + msg);
        clearForm();
        getFutureReservationsFor();
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

    /** Fetch the up-to-date list of future reservations */
    protected void getFutureReservationsFor() {
        client.sendToServerSafely("GET_FUTURE_RESERVATIONS");
    }

    /** Entry point for all messages from the server */
    public void handleServerMessage(Object message) {
        if (message instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Reservation> list = (List<Reservation>) message;
            Platform.runLater(() ->
                futureReservationsTable.getItems().setAll(list)
            );
        } else {
            Platform.runLater(() ->
                showAlert(AlertType.INFORMATION, message.toString())
            );
        }
    }

    /** Utility alert pop-up */
    protected void showAlert(AlertType type, String text) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }
}
