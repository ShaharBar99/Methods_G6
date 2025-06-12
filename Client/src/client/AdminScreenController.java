package client;



import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.*;

public class AdminScreenController extends Controller{

	@FXML Button viewParkingButton;
	@FXML Button viewSubscribersButton;
	@FXML Button generateReportButton;
	@FXML Button backButton;
	private List<Order> orders;
    @FXML
    public void viewCurrentParkingStatus() {
        System.out.println("Showing current parking status...");
        // תוסיף לוגיקה להצגת מצב החניון
    }

    @FXML
    public void viewSubscribersList() {
        System.out.println("Showing subscribers list...");
        // תוסיף לוגיקה להצגת רשימת מנויים
    }

    @FXML
    public void generateReport() {
    	//
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportScreenUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Report Screen");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();
            //send to server
            /*
		    try {
		        // Create a SendObject with message "Request_Orders" and null object
		        SendObject<Void> request = new SendObject<>("Request_Orders", null);
		        clientConnection.sendToServer(request);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
             */
            // החבא את admin stage דרך כפתור קיים
            Stage adminStage = (Stage) generateReportButton.getScene().getWindow();
            adminStage.hide();

            ReportController controller = loader.getController();
            //add orders to report controller
            //controller.setOrders(orders);
            controller.setClient(client,sub);

            // Back handler – החזרת admin וסגירת דוח
            controller.setBackHandler(() -> {
                reportStage.close();
                adminStage.show();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*private void handleServerMessage(Object msg) {
		Platform.runLater(() -> {
			System.out.println("[Server] " + msg);

			// Load next screen (Order Table)
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportScreenUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Report Screen");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();
			ReportController controller = loader.getController();
			controller.setClient(client);
			Parent tableRoot = null;
			try {
				tableRoot = loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("cant load report screen");
				e.printStackTrace();
			}
			// After Connection start the order table
			if (msg instanceof SendObject<?>) {
			    SendObject<?> sendObj = (SendObject<?>) msg;
			    if ("Send_Orders".equals(sendObj.getObjectMessage())) {
			        // Safe cast because you expect List<Order>
			        @SuppressWarnings("unchecked")
			        List<Order> orders = (List<Order>) sendObj.getObj();
			        System.out.println(orders);
			        System.out.println("Orders added");
			        // Now update the table/controller with the new orders
			        // Example if you have a controller:
			        controller.setOrders(orders);
			    }
			}
			Stage adminStage = (Stage) generateReportButton.getScene().getWindow();
            adminStage.hide();
            controller.setBackHandler(() -> {
                reportStage.close();
                adminStage.show();
            });
			// Makes sure when X is pressed it closes the connection to the server
			
			// hand off all future messages to the BParkClientController
			//clientConnection.setMessageListener(controller::handleServerMessage);
		});
	}*/
}
