package client;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import logic.Parkingsession;
import logic.SendObject;

public class ViewActiveSessionsController extends Controller {

	@FXML
	private TableView<Parkingsession> sessionTable;

	@FXML
	private TableColumn<Parkingsession, Integer> colSessionId;
	@FXML
	private TableColumn<Parkingsession, Integer> colSubscriberId;
	@FXML
	private TableColumn<Parkingsession, Integer> colSpotId;
	@FXML
	private TableColumn<Parkingsession, Integer> colParkingCode;
	@FXML
	private TableColumn<Parkingsession, java.util.Date> colInTime;
	@FXML
	private TableColumn<Parkingsession, java.util.Date> colOutTime;
	@FXML
	private TableColumn<Parkingsession, Boolean> colExtended;
	@FXML
	private TableColumn<Parkingsession, Boolean> colLate;
	@FXML
	private TableColumn<Parkingsession, Boolean> colActive;

	@FXML
	private Button backButton;

	private List<Parkingsession> allSessions = new ArrayList<>();

	private Runnable backHandler;

	@FXML
	public void initialize() {
		colSessionId.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getSessionId()).asObject());
		colSubscriberId.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getSubscriberId()).asObject());
		colSpotId
				.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSpotId()).asObject());
		colParkingCode.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getParkingCode()).asObject());
		colInTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getInTime()));
		colOutTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOutTime()));
		colExtended.setCellValueFactory(
				cellData -> new SimpleBooleanProperty(cellData.getValue().isExtended()).asObject());
		colLate.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isLate()).asObject());
		colActive
				.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getActive()).asObject());
	}

	public void setSessions(List<Parkingsession> sessions) {
		this.allSessions = sessions;
		sessionTable.getItems().setAll(allSessions);
	}

	public void handleServerMessage(Object msg) {
		if (msg instanceof SendObject<?>) {
			if (((SendObject) msg).getObj() instanceof List<?>) {
				List<?> updated = (List<?>) ((SendObject) msg).getObj();
				if (!updated.isEmpty() && updated.get(0) instanceof Parkingsession) {
					Platform.runLater(() -> setSessions((List<Parkingsession>) updated));
				}
			}
		}
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

	private void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		alert.showAndWait();
	}
}
