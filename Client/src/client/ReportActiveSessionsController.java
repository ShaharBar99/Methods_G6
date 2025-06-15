package client;

import java.io.File;
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
import javafx.stage.FileChooser;
import logic.Parkingsession;
import logic.SendObject;

public class ReportActiveSessionsController extends Controller {

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
	private Button exportCsvButton;
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

		if (exportCsvButton != null) {
			exportCsvButton.setOnAction(e -> {
				try {
					FileChooser fileChooser = new FileChooser();
		            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

		            // Show the save file dialog and get the selected file
		            File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
					exportToCSV(sessionTable, file);
					showAlert("Exported table to active_sessions.csv!");
				} catch (Exception ex) {
					showAlert("Failed to export CSV: " + ex.getMessage());
				}
			});
		}
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

	private void exportToCSV(TableView<?> table, File file) throws Exception {
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			// Header
			for (int i = 0; i < table.getColumns().size(); i++) {
				TableColumn<?, ?> col = table.getColumns().get(i);
				writer.print(col.getText());
				if (i < table.getColumns().size() - 1)
					writer.print(",");
			}
			writer.println();
			// Rows
			for (Object item : table.getItems()) {
				for (int i = 0; i < table.getColumns().size(); i++) {
					TableColumn col = table.getColumns().get(i);
					Object cell = col.getCellData(item);
					String cellText = (cell != null ? cell.toString() : "");
					cellText = cellText.replace("\"", "\"\"");
					if (cellText.contains(",") || cellText.contains("\""))
						cellText = "\"" + cellText + "\"";
					writer.print(cellText);
					if (i < table.getColumns().size() - 1)
						writer.print(",");
				}
				writer.println();
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
