package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import logic.Order;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class BParkClientController {

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
	private Button editButton;
	@FXML
	private Button backButton;
	private BParkClient client;

	private List<Order> orders;

	private Runnable backHandler;

	private Stage editStage;

	private EditOrderController controller;

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

	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	private void updateOrders() {
		for (Order order : orders) {
			orderTable.getItems().add(order);
		}
	}

/*	public void handleServerMessage(Object msg) {
		if (msg instanceof List<?>) {
			setOrders((List<Order>) msg);
			setClient(client);
		}
	}*/

	public void setClient(BParkClient client) {
		this.client = client;
		if (orders != null)
			updateOrders();
		else
			System.out.println("Orders is null");
	}

	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;

	}

	public void handleEditButton(ActionEvent e) {
		try {
			// Load the new FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditOrder.fxml"));
			Parent newRoot = loader.load();
			controller = loader.getController();
			controller.setClient(client);

			// Create a new stage
			editStage = new Stage();
			editStage.setScene(new Scene(newRoot));
			editStage.setTitle("Edit Order");
			editStage.show();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	private void handleBackButton() {
		if (editStage.isShowing()) {
			editStage.close(); // Close the editStage window
		}
		System.out.println("editStage closed");
		if (backHandler != null) {
			backHandler.run();
		}
	}

}
