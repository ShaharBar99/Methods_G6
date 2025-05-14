package client;

import java.io.IOException;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.Order;

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


	private BParkClient client;
	private List<Order> orders;
	private Runnable backHandler;
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

	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	private void updateOrders() {
		orderTable.getItems().setAll(orders);
	}

	
	/**
	 * @param msg
	 * Updates the table after Edit happens
	 */
	public void handleServerMessage(Object msg) {
		if (msg instanceof List<?>) {
			List<Order> updated = (List<Order>) msg;
			Platform.runLater(() -> {
				setOrders(updated); // update the orders list				 
				updateOrders(); // update the table
			});
		}
	}

	public void setClient(BParkClient client) {
		this.client = client;

		// whenever the server broadcasts a new orders‐list, calls handleServerMessage
		client.setMessageListener(this::handleServerMessage);
		if (orders != null)
			updateOrders();
		else
			System.out.println("Orders is null");
	}

	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;

	}

	@FXML
	public void handleEditButton(ActionEvent e) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditOrder.fxml"));
			Parent newRoot = loader.load();
			EditOrderController controller = loader.getController();

			// Refreshes the table after the edit happens (Back to the order Table)
			controller.setClient(client);
			controller.setParentController(this);

			// Show the EditOrder screen
			Stage editStage = new Stage();
			editStage.setScene(new Scene(newRoot));
			editStage.setTitle("Edit Order");
			editStage.show();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@FXML
	private void handleBackButton() {
		// Close the EditOrder screen if it actually exists and is open
		if (editStage != null && editStage.isShowing()) {
			editStage.close();
		}
		// swap the TableView scene back to the connect screen
		if (backHandler != null) {
			backHandler.run();
		}
	}

}
