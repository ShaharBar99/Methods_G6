package client;



import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.SendObject;

public class AttendentScreenController extends Controller{
	

	@FXML Button openReservationReportButton;
	@FXML Button openSubscribersReportButton;
	@FXML Button openActiveSessionsReportButton;
	@FXML Button registerNewSubscriberButton;
	@FXML Button backButton;
    
	public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;
	}
	
    @FXML
    public void openReservationReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewReservationScreenUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Reservations Report");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();

            ViewReservationController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewSubscriberUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Subscribers Report");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();

            ViewSubscriberController controller = loader.getController();
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
            SendObject<String> request = new SendObject<>("Get", "all subscribers");
            client.sendToServerSafely(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openActiveSessionsReport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewActiveSessionsUI.fxml"));
            Parent root = loader.load();
            Stage reportStage = new Stage();
            reportStage.setTitle("Active Sessions Report");
            reportStage.setScene(new Scene(root));
            reportStage.setMaximized(true);
            reportStage.show();

            ViewActiveSessionsController controller = loader.getController();
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
    public void registerNewSubscriber() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistrationScreen.fxml"));
            Parent root = loader.load();
            Stage registrationStage = new Stage();
            registrationStage.setTitle("Registration Screen");
            registrationStage.setScene(new Scene(root));
            registrationStage.setMaximized(true);
            registrationStage.show();
            
            // hide the admin screen via an existing button
            Stage attendantStage = (Stage) registerNewSubscriberButton.getScene().getWindow();
            attendantStage.hide();
            // Get the controller for the registration screen
            RegistrationController controller = loader.getController();
            controller.setClient(client,sub);
            client.setMessageListener(controller::handleServerMessage);
            // Back handler – close registration and return to attendant screen
            controller.setBackHandler(() -> {
                registrationStage.close();
                attendantStage.show();
                // Restore attendant as the message listener
                client.setMessageListener(this::handleServerMessage);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@FXML private void handleBackButton() {
		// swap the TableView scene back to the connect screen
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle("Login");
			stage.setScene(new Scene(root));		
			stage.show();
			Stage currentStage = (Stage) registerNewSubscriberButton.getScene().getWindow();
			currentStage.close();
			client.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}