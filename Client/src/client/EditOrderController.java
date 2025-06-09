package client;

import java.time.LocalDate;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Order;
import logic.ParkingSpot;

public class EditOrderController {

    @FXML private TextField orderIdField;
    @FXML private DatePicker orderDatePicker;
    @FXML private TextField spotIdField;
    @FXML private Label statusLabel;

    private BParkClient client;
    private Order loadedOrder;
    private BParkClientController parentController;

    public void setClient(BParkClient client) {
        this.client = client;
        // Whenever the server broadcasts a new Object, calls handleServerMessage
        client.setMessageListener(this::handleServerMessage);
    }
    
    public void setParentController(BParkClientController parent) {
        this.parentController = parent;
    }
    
    private void handleServerMessage(Object msg) {
        if (msg instanceof Order || msg instanceof String) {
            // “get_order” reply or the “Updated” message
        	Platform.runLater(() -> {
                if (msg instanceof Order order) {
                    loadedOrder = order;
                    orderDatePicker.setValue(((java.sql.Date) order.getorder_date()).toLocalDate());
                    spotIdField.setText(String.valueOf(order.get_ParkingSpot().getSpotId()));
                    statusLabel.setText("Order loaded.");
                } else if (msg instanceof String s) {
                    statusLabel.setText(s);
                }
            });

        } else if (msg instanceof List<?>) {
            // server broadcast: hand the listener back to the table,
            // forward the List<Order> there, then close this window
            List<Order> updated = (List<Order>) msg;

            // Restore the table-listener
            client.setMessageListener(parentController::handleServerMessage);

            // Forward the data so the table controller does its updateOrders()
            parentController.handleServerMessage(updated);

            // Close the EditOrder window
            Platform.runLater(() -> {
                Stage myStage = (Stage) orderIdField.getScene().getWindow();
                myStage.close();
            });
        }
    }

    // get order from server by order id
    @FXML
    private void handleLoadOrder() {
        String idText = orderIdField.getText().trim();
        if (idText.isEmpty()) {
            statusLabel.setText("Please enter order number");
            return;
        }

        try {
            int orderId = Integer.parseInt(idText);
            client.sendToServerSafely("get_order: " + orderId);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid order number");
        }
    }



    // update order in server
    @FXML
    private void handleUpdateOrder() {
        if (loadedOrder == null) {
            statusLabel.setText("Load an order first");
            return;
        }

        LocalDate date = orderDatePicker.getValue();
        String spotText = spotIdField.getText().trim();

        if (date == null || spotText.isEmpty()) {
            statusLabel.setText("Date and spot ID required");
            return;
        }

        try {
            int spotId = Integer.parseInt(spotText);
            System.out.println("Updating order with date: " + date);
            loadedOrder.setorder_date(java.sql.Date.valueOf(date));
            //loadedOrder.set_ParkingSpot(new ParkingSpot(spotId, null, null));
            client.sendToServerSafely(loadedOrder);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid spot ID");
        }
    }   
}
