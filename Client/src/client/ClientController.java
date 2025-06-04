package client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Role;
import logic.subscriber;

public class ClientController {

    @FXML private TextField ipTextField;    // server IP
    @FXML private TextField portTextField;  // server port
    @FXML private Button    connectButton;  // “Connect”
    @FXML private Button    disconnectButton; // “Disconnect/Exit”

    private BParkClient clientConnection;
    private subscriber subscribe;

    // once we swap to the reservation UI, remember its controller here:
    private boolean reservationScreenLoaded = false;
    private ReservationScreenController reservationController;

    @FXML
    public void handleConnectButton() {
        String ip   = ipTextField.getText().trim();
        String port = portTextField.getText().trim();
        if (ip.isEmpty() || port.isEmpty()) {
            showAlert("Error", "Please enter both IP and port", Alert.AlertType.ERROR);
            return;
        }
        int p;
        try { p = Integer.parseInt(port); }
        catch (NumberFormatException ex) {
            showAlert("Error", "Port must be a number", Alert.AlertType.ERROR);
            return;
        }
        connectToServer(ip, p);
        subscribe = new subscriber(1,"temp","1235","@gmail",Role.SUBSCRIBER,null,123546);
    }

    @FXML
    public void handleDisconnectButton() {
        Platform.exit();
    }

    private void connectToServer(String ip, int port) {
        try {
            clientConnection = new BParkClient(ip, port);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
            clientConnection.start();
            // first messages come here:
            clientConnection.setMessageListener(this::handleServerMessage);
        } catch (Exception ex) {
            showAlert("Error",
                      "Cannot connect to " + ip + ":" + port,
                      Alert.AlertType.ERROR);
            connectButton.setDisable(false);
        }
    }

    private void handleServerMessage(Object msg) {
        Platform.runLater(() -> {
            System.out.println("[Server] " + msg);

            if (!reservationScreenLoaded) {
                // — first server message: load Reservation.fxml —
                try {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("Reservation.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) connectButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Parking Reservations");
                    stage.setOnCloseRequest((WindowEvent e) -> {
                        clientConnection.stop();
                        System.exit(0);
                    });

                    // grab and init the screen controller
                    reservationController = loader.getController();
                    reservationController.setClient(clientConnection,subscribe);

                    // now route all future messages to it:
                    clientConnection.setMessageListener(
                        reservationController::handleServerMessage
                    );

                    // kick off the first data fetch
                    reservationController.getFutureReservationsFor();

                    reservationScreenLoaded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // — after that, delegate every message —
                reservationController.handleServerMessage(msg);
            }
        });
    }

    private static void showAlert(String title,
                                  String message,
                                  Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
