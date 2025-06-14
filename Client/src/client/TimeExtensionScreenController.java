package client;

import java.text.SimpleDateFormat;
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

	public void setClient(BParkClient client, subscriber sub) {
		this.client = client;
		this.sub = sub;
		parkingController.setClient(client, sub);
		parkingController.setTimeExtensionScreen(this);
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
		colOutTime.setCellFactory(column -> {
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
	}

	public void displayActiveSessions() {
		System.out.println("display");
		List<Parkingsession> sessions;
		try {
			sessions = parkingController.getActiveParkingSessions();
			ObservableList<Parkingsession> data = FXCollections.observableArrayList(sessions);
			currentParkingSessionTable.setItems(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowAlert.showAlert("Error", "Error getting Active sessions", AlertType.ERROR);
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
		}
		catch(NumberFormatException e) {
			ShowAlert.showAlert("Error", "Enter a valid Parkingsession", AlertType.ERROR);
		}
	}

	@FXML
	private void handleExtendTime() {
		// Handle time extension logic
		long hour = hourSpinner.getValue();
		long minute = minuteSpinner.getValue();
		if (hour > 3 && minute > 0) {
			ShowAlert.showAlert("Error", "Time Extension must be up to 4 hours", AlertType.ERROR);
		}
		if (session != null) {
			session.setOutTime(new Date(session.getOutTime().getTime() + hour * 60 * 60 * 1000 + minute * 60 * 1000));
			try {
				parkingController.ExtendTime(session);
				ShowAlert.showAlert("Extended Time", "Time was successfully extended", AlertType.INFORMATION);
				Platform.runLater(() -> {
					displayActiveSessions();
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ShowAlert.showAlert("Error", "Time hasn't extended", AlertType.ERROR);
				e.printStackTrace();
			}
		} else {
			this.ShowFail("Session was not found");
		}
	}


	public void handleServerMessage(Object message) {
		parkingController.handleServerResponse(message);
	}

	public void ShowFail(String string) {
		// TODO Auto-generated method stub
		ShowAlert.showAlert("Error", string, AlertType.ERROR);
	}
}
