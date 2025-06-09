package client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.*;

public class HistoryScreenController extends Controller{


	private HistoryController controller = new HistoryController();

	@FXML
	private TableView<Parkingsession> parkingTable;
	@FXML
	private TableColumn<Parkingsession, Integer> colSessionId;
	@FXML
	private TableColumn<Parkingsession, Integer> colSpotId;
	@FXML
	private TableColumn<Parkingsession, String> colInTime;
	@FXML
	private TableColumn<Parkingsession, String> colOutTime;
	@FXML
	private TableColumn<Parkingsession, String> colLate;



	public void setClient(BParkClient client, subscriber sub) {
		this.client = client;
		this.sub = sub;
		controller.setClient(client, sub);
		Platform.runLater(() -> {
			displayHistory();
		});

	}

	@FXML
	public void initialize() {
		// קונפיגורציית עמודות
		colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
		colSpotId.setCellValueFactory(new PropertyValueFactory<>("spotId"));

		// עמודות עם פורמט תאריך
		colInTime.setCellValueFactory(cellData -> {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return javafx.beans.binding.Bindings.createStringBinding(() -> sdf.format(cellData.getValue().getInTime()));
		});

		colOutTime.setCellValueFactory(cellData -> {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return javafx.beans.binding.Bindings
					.createStringBinding(() -> sdf.format(cellData.getValue().getOutTime()));
		});

		colLate.setCellValueFactory(cellData -> {
			return javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().isLate() ? "Yes" : "No");
		});
	}

	/**
	 * פעולה שמרעננת את התוכן ברשימה
	 */
	@FXML
	public void displayHistory() {
		System.out.println("display");
		List<Parkingsession> sessions;
		try {
			sessions = controller.getSessions();
			ObservableList<Parkingsession> data = FXCollections.observableArrayList(sessions);
			parkingTable.setItems(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ShowAlert.showAlert("Error", "Error getting history", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	public void handleServerMessage(Object message) {
		System.out.println("got in controller screen");
		controller.handleServerMessage(message);
	}


}