// === ViewActiveSessionsController with LineChart ===
package client;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import logic.Parkingsession;
import logic.SendObject;

public class ViewActiveSessionsController extends Controller {

	@FXML
	protected TableView<Parkingsession> sessionTable;
	@FXML
	protected TableColumn<Parkingsession, Integer> colSessionId;
	@FXML
	protected TableColumn<Parkingsession, Integer> colSubscriberId;
	@FXML
	protected TableColumn<Parkingsession, Integer> colSpotId;
	@FXML
	protected TableColumn<Parkingsession, Integer> colParkingCode;
	@FXML
	protected TableColumn<Parkingsession, java.util.Date> colInTime;
	@FXML
	protected TableColumn<Parkingsession, java.util.Date> colOutTime;
	@FXML
	protected TableColumn<Parkingsession, Boolean> colExtended;
	@FXML
	protected TableColumn<Parkingsession, Boolean> colLate;
	@FXML
	protected TableColumn<Parkingsession, Boolean> colActive;
	@FXML
	protected Button backButton;

	protected List<Parkingsession> allSessions = new ArrayList<>();
	protected Runnable backHandler;

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
			if (((SendObject<?>) msg).getObj() instanceof List<?>) {
				List<?> updated = (List<?>) ((SendObject<?>) msg).getObj();
				if (!updated.isEmpty() && updated.get(0) instanceof Parkingsession) {
					Platform.runLater(() -> setSessions((List<Parkingsession>) updated));
				}
			}
		}
	}

	protected void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		alert.showAndWait();
	}
}