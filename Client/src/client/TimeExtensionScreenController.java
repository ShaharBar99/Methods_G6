package client;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import logic.*;

public class TimeExtensionScreenController extends Controller {

	@FXML
	private Spinner<Integer> hourSpinner;

	@FXML
	private Spinner<Integer> minuteSpinner;

	@FXML
	private TextField ParkingsessionIdField;

	@FXML
	private TableView<Parkingsession> currentParkingSessionTable;

	@FXML
	private TableColumn<Parkingsession, Integer> colSessionId;

	@FXML
	private TableColumn<Parkingsession, Integer> colSpotId;

	@FXML
	private TableColumn<Parkingsession, Date> colInTime;

	@FXML
	private TableColumn<Parkingsession, Date> colOutTime;

	private ParkingController parkingController = new ParkingController();

	private Parkingsession session;

	private boolean extendedSessionId = false;

	public void setClient(BParkClient client, subscriber sub) {
		this.client = client;
		this.sub = sub;
		parkingController.setClient(client, sub);
		parkingController.setTimeExtensionScreen(this);
		client.setMessageListener(parkingController::handleServerResponse);
		Platform.runLater(() -> {
			displayActiveSessions();
		});
	}

	@FXML
	private void initialize() {
		// Initialize Spinner for Hours
		SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 4, 0);
		hourSpinner.setValueFactory(hourValueFactory);
		// Initialize Spinner for Minutes
		SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
		minuteSpinner.setValueFactory(minuteValueFactory);
		ParkingsessionIdField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				ParkingsessionIdField.setText(oldValue); // Revert to old value if invalid
			}
		});
		// Initialize TableView columns
		colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
		colSpotId.setCellValueFactory(new PropertyValueFactory<>("spotId"));

		// Set up Date formatting for InTime and OutTime columns
		colInTime.setCellValueFactory(new PropertyValueFactory<>("inTime"));
		colInTime.setCellFactory(column -> {
			return new TableCell<Parkingsession, Date>() {
				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						setText(format.format(item));
					} else {
						setText(null);
					}
				}
			};
		});

		colOutTime.setCellValueFactory(new PropertyValueFactory<>("outTime"));
		colOutTime.setCellFactory(column -> new TableCell<Parkingsession, Date>() {
			@Override
			protected void updateItem(Date item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
					setStyle("");
					return;
				}

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				setText(format.format(item));

				int index = getIndex(); // ✅ this is valid inside TableCell
				ObservableList<Parkingsession> items = getTableView().getItems();

				if (index >= 0 && index < items.size()) {
					Parkingsession rowItem = items.get(index);
					if (session != null && extendedSessionId && rowItem.getSessionId() == session.getSessionId()) {
						setStyle("-fx-background-color: #ffe599;"); // light yellow
					} else {
						setStyle(""); // reset
					}
				} else {
					setStyle(""); // clear if index is out of range
				}
			}
		});

		currentParkingSessionTable.setRowFactory(tv -> new TableRow<Parkingsession>() {
			@Override
			protected void updateItem(Parkingsession item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setStyle("");
				} else {
					if (session != null && item.getSessionId() == session.getSessionId()) {
						setStyle("-fx-background-color: #a3d2ca;"); // Light green or any color
					} else {
						setStyle("");
					}
				}
			}
		});
	}

	public void displayActiveSessions() {
		List<Parkingsession> sessions;
		try {
			sessions = parkingController.getActiveParkingSessions();
			ObservableList<Parkingsession> data = FXCollections.observableArrayList(sessions);
			currentParkingSessionTable.setItems(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowFail("Error getting Active sessions");
			e.printStackTrace();
		}
	}

	@FXML
	private void handleLoadSession() throws Exception {
		// Handle load session logic
		try {
			String parkingsessionId = ParkingsessionIdField.getText();
			int parkingId = Integer.parseInt(parkingsessionId);
			session = parkingController.getSessionById(parkingId);
			extendedSessionId = false;
			currentParkingSessionTable.refresh();
			for (Parkingsession ps : currentParkingSessionTable.getItems()) {
				if (ps.getSessionId() == session.getSessionId()) {
					currentParkingSessionTable.scrollTo(ps);
					break;
				}
			}
		} catch (NumberFormatException e) {
			ShowFail("Enter a valid Parkingsession");
		}
	}

	@FXML
	private void handleExtendTime() {
		// Handle time extension logic

		long hour = hourSpinner.getValue();
		long minute = minuteSpinner.getValue();
		if (hour > 3 && minute > 0 || hour == 0 && minute == 0 || session == null) {
			if (hour > 3 && minute > 0)
				ShowFail("Time Extension must be up to 4 hours!");
			else if (hour == 0 && minute == 0) {
				ShowFail("Time Extension must be at least a minute!");
			} else {
				ShowFail("Session was not found");
			}
		} else {

			session.setOutTime(new Date(session.getOutTime().getTime() + hour * 60 * 60 * 1000 + minute * 60 * 1000));
			try {
				if (!ShowAlert.showConfirmation("Confirm Time Extension",
						"Are you sure you want to extend time by " + hour + " hour(s) and " + minute + " minute(s)?")) {
					extendedSessionId = false;
					session = null;
					clearForm();
					currentParkingSessionTable.refresh();

					return; // user clicked Cancel
				}
				parkingController.ExtendTime(session);
				ShowAlert.showAlert("Extended Time", "Time was successfully extended", AlertType.INFORMATION);
				Platform.runLater(() -> {
					displayActiveSessions();
				});
				clearForm();
				currentParkingSessionTable.refresh();
				extendedSessionId = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ShowFail("Time hasn't extended");
				e.printStackTrace();
			}
		}
	}

	private void clearForm() {
		ParkingsessionIdField.setText("");
		initialize();
	}

	public void handleServerMessage(Object message) {
		parkingController.handleServerResponse(message);
	}

	public void ShowFail(String string) {
		ShowAlert.showAlert("Error", string, AlertType.ERROR);
	}
}
