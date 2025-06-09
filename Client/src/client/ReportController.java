package client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.Order;

public class ReportController extends Controller{

	@FXML
	private TableView<Order> orderTable;

	@FXML
	private TableColumn<Order, Integer> colOrderId;

	@FXML
	private TableColumn<Order, Date> colDate;

	@FXML
	private TableColumn<Order, Integer> colSpot;

	@FXML
	private TableColumn<Order, Integer> colCode;

	@FXML
	private TableColumn<Order, Integer> colSubscriberID;

	@FXML
	private TableColumn<Order, Date> colDatePlacingOrder;
	
	@FXML
	private Button exportCsvButton;
	
	@FXML
	private Button sortByDateButton;
	
	@FXML
	private Button sortByIdButton;
	private List<Order> orders;
	private Stage editStage;

	@FXML
	public void initialize() {
		colOrderId.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().get_order_id()).asObject());
		colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getorder_date()));
		colSpot.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().get_ParkingSpot().getSpotId()).asObject());
		colCode.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCode()).asObject());

		colSubscriberID.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getSubscriber().getId()).asObject());

		colDatePlacingOrder.setCellValueFactory(
				cellData -> new SimpleObjectProperty<>(cellData.getValue().getdate_of_placing_an_order()));
		if (exportCsvButton != null) { // check if the button exists
		    exportCsvButton.setOnAction(e -> {
		        try {
		            exportToCSV(orderTable, "orders.csv");
		            showAlert("Exported table to orders.csv!");
		        } catch (Exception ex) {
		            showAlert("Failed to export CSV: " + ex.getMessage());
		        }
		    });
		}
		if (sortByDateButton != null) {
		    sortByDateButton.setOnAction(e -> sortByDate());
		}
		if (sortByIdButton != null) {
		    sortByIdButton.setOnAction(e -> sortById());
		}

	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	private void updateOrders() {
		orderTable.getItems().setAll(orders);
	}
	
	private void sortByDate() {
	    colDate.setSortType(TableColumn.SortType.ASCENDING);
	    orderTable.getSortOrder().setAll(colDate);
	    orderTable.sort();
	}

	private void sortById() {
	    colOrderId.setSortType(TableColumn.SortType.ASCENDING);
	    orderTable.getSortOrder().setAll(colOrderId);
	    orderTable.sort();
	}

	public void handleServerMessage(Object msg) {
		if (msg instanceof List<?>) {
			List<Order> updated = (List<Order>) msg;
			Platform.runLater(() -> {
				setOrders(updated); // update the orders list				 
				updateOrders(); // update the table
			});
		}
	}
	
	private void exportToCSV(TableView<?> table, String filename) throws Exception {
	    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
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
	                // Escape commas/quotes
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

	private void showAlert(String msg) {
	    Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
	    alert.showAndWait();
	}


}
