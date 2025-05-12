package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.Order;
import logic.ParkingSpot;

import java.time.LocalDate;
import java.util.Date;

public class EditOrderController {

    @FXML private TextField orderIdField;
    @FXML private DatePicker orderDatePicker;
    @FXML private TextField spotIdField;
    @FXML private Label statusLabel;

    private BParkClient client;
    private Order loadedOrder;

    public void setClient(BParkClient client) {
        this.client = client;
        client.setMessageListener(this::handleServerResponse);
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
            client.sendToServerSafely("get_order:" + orderId);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid order number");
        }
    }
    
    /*
    // need to implement inside BparkServer.handleMessageFromClient():
    if (msg instanceof String str && str.startsWith("get_order:")) {
        int orderId = Integer.parseInt(str.split(":")[1]);
        Order order = getOrderById(orderId); // ← you implement this
        sendToSingleClient(order, client);
    }
    else if (msg instanceof Order updatedOrder) {
        updateDB(updatedOrder); // already exists
        sendToSingleClient("Order updated successfully", client);
    }*/


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
            loadedOrder.setorder_date(java.sql.Date.valueOf(date));
            loadedOrder.set_ParkingSpot(new ParkingSpot(spotId, null, null));
            client.sendToServerSafely(loadedOrder);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid spot ID");
        }
    }

    private void handleServerResponse(Object msg) {
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
    }
}
