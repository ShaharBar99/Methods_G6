package client;



import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.*;

public class AttendentScreenController extends Controller{
	
	//private BParkClient client;
	//private AttendantController attendantController;

	@FXML Button viewParkingButton;
	@FXML Button viewSubscribersButton;
	@FXML Button registerNewSubscriberButton;
	@FXML Button backButton;
//	@FXML
//	private Button resendCodeButton; // Button to resend a subscriber's access code
//
//    @FXML
//	private Label logInCodeLabel;  
//    @FXML
//    private Label assignedRFIDTagLabel;
	
//	public void setClient(BParkClient client) {
//		this.client = client;
//	}
//	public BParkClient getClient() {
//		return client;
//	}
//	public void setAttendantController(AttendantController controller) {
//		this.attendantController = controller;
//	}
//	public void setBackHandler(Runnable backHandler) {
//		this.backHandler = backHandler;
//	}
	
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
    public void registerNewSubscriber() {
    	//
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistrationScreen.fxml"));
            Parent root = loader.load();
            Stage registrationStage = new Stage();
            registrationStage.setTitle("Registration Screen");
            registrationStage.setScene(new Scene(root));
            registrationStage.setMaximized(true);
            registrationStage.show();
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
            // hide the admin screen via an existing button
            Stage adminStage = (Stage) registerNewSubscriberButton.getScene().getWindow();
            adminStage.hide();
            // Get the controller for the registration screen
            RegistrationController controller = loader.getController();
            controller.setClient(client,sub);

            // Back handler – close registration and return to admin
            controller.setBackHandler(() -> {
                registrationStage.close();
                adminStage.show();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
//	public void handleServerMessage(Object message) {
//        // Implement if this screen needs to respond to server replies
//    }
	
	
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
    
    
//    public void resendSubscriberCode() {
//        // TODO: Resend a subscriber's access code
//    }
//    /* displays the assigned parking spot in the text field */
//	public void displayRFIDTag(String RFIDTag) {
//		// display both tag and code
//		// we get them from the server using handleserverrespose
//		assignedRFIDTagLabel.setText("Your RFID tag is " + RFIDTag); // display the RFID tag in the text field
//		ShowAlert.showAlert("Assigned Spot", "Your RFID tag is: " + RFIDTag, Alert.AlertType.INFORMATION); // popUp
//	}
//	public void displayLogInCode(int logInCode) {
//		logInCodeLabel.setText("Your logIn code is " + String.valueOf(logInCode)); // display the code in the text field																				
//		ShowAlert.showAlert("logIn Code", "Your logIn code is: " + logInCode, Alert.AlertType.INFORMATION); // popUp
//	}
}
