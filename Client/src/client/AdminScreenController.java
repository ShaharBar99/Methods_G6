package client;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.SendObject;

public class AdminScreenController extends Controller{
	
	private BParkClient client;

	@FXML Button openReservationReportButton;
	@FXML Button openSubscribersReportButton;
	@FXML Button openActiveSessionsReportButton;
	@FXML Button backButton;
    
	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}
	
    @FXML
    public void openReservationReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportScreenUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Reservations Report");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();

            ReportReservationController controller = loader.getController();
            controller.setClient(client, sub); // only if you use this
            Stage attendantStage = (Stage) openReservationReportButton.getScene().getWindow();
            attendantStage.hide();
            
            // Set message listener to the new controller
            client.setMessageListener(controller::handleServerMessage);

            controller.setBackHandler(() -> {
                reportStage.close();
                attendantStage.show();
                // Restore attendant as the message listener
                client.setMessageListener(this::handleServerMessage);
            });

            // Send request to the server
            SendObject<String> request = new SendObject<>("Get", "all reservations");
            client.sendToServerSafely(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openSubscribersReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportSubscriberUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Subscribers Report");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();

            ReportSubscriberController controller = loader.getController();
            controller.setClient(client, sub); // only if you use this
            Stage attendantStage = (Stage) openSubscribersReportButton.getScene().getWindow();
            attendantStage.hide();
            
            // Set message listener to the new controller
            client.setMessageListener(controller::handleServerMessage);

            controller.setBackHandler(() -> {
                reportStage.close();
                attendantStage.show();
                // Restore attendant as the message listener
                client.setMessageListener(this::handleServerMessage);
            });

            // Send request to the server
            SendObject<String> request = new SendObject<>("Get", "all subscrcribers");
            client.sendToServerSafely(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openActiveSessionsReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportActiveSessionsUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Active Sessions Report");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();

            ReportActiveSessionsController controller = loader.getController();
            controller.setClient(client, sub); // only if you use this
            Stage attendantStage = (Stage) openActiveSessionsReportButton.getScene().getWindow();
            attendantStage.hide();
            
            // Set message listener to the new controller
            client.setMessageListener(controller::handleServerMessage);

            controller.setBackHandler(() -> {
                reportStage.close();
                attendantStage.show();
                // Restore attendant as the message listener
                client.setMessageListener(this::handleServerMessage);
            });

            // Send request to the server
            SendObject<String> request = new SendObject<>("Get", "active parking sessions");
            client.sendToServerSafely(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @FXML
	private void handleBackButton() {
		if (backHandler != null) {
			backHandler.run();
		}
	}
    
}