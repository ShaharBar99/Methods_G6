package client;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.Parkingsession;
import javafx.scene.Node;

public class HistoryScreenController  extends Controller {
    @FXML private TableView<Parkingsession> parkingTable;
    @FXML private TableColumn<Parkingsession, Integer> colSessionId;
    @FXML private TableColumn<Parkingsession, Integer> colSpotId;
    @FXML private TableColumn<Parkingsession, String> colInTime;
    @FXML private TableColumn<Parkingsession, String> colOutTime;
    @FXML private TableColumn<Parkingsession, String> colLate;
    //from server DB sessions
    //private List<Parkingsession> sessions;
    @FXML
    public void initialize() {
        // קונפיגורציית עמודות
    	//this.sendToServerSafely(new SendObject<Integer>("history",subscriber.getId()));
    	colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        colSpotId.setCellValueFactory(new PropertyValueFactory<>("spotId"));

        // עמודות עם פורמט תאריך
        colInTime.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                sdf.format(cellData.getValue().getInTime()));
        });

        colOutTime.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                sdf.format(cellData.getValue().getOutTime()));
        });

        colLate.setCellValueFactory(cellData -> {
            return javafx.beans.binding.Bindings.createStringBinding(() ->
                cellData.getValue().isLate() ? "Yes" : "No");
        });

        displayHistory(); // טעינת דמה
    }


    /**
     * פעולה שמרעננת את התוכן ברשימה
     */
    @FXML
    public void displayHistory() {
        List<Parkingsession> sessions = HistoryController.generateDummySessions();
        ObservableList<Parkingsession> data = FXCollections.observableArrayList(sessions);
        parkingTable.setItems(data);
    }
}
