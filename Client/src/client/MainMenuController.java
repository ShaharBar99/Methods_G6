package client;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.*;



public class MainMenuController extends Controller{
	@FXML Button historybutton;

	
	@FXML private void handleBackButton() {
		// swap the TableView scene back to the connect screen
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle("Login");
			stage.setScene(new Scene(root));		
			stage.show();
			Stage currentStage = (Stage) historybutton.getScene().getWindow();
			currentStage.close();
			client.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
    @FXML public void reservationRequest() { 
    	this.setscreen("ReservationScreen","Reservation.fxml"); 
    }
    @FXML public void parkingRequest() { 
    	this.setscreen("DropOffScreen","DropOffScreen.fxml");
    }
    @FXML public void retrieveRequest() {
    	this.setscreen("Pickupscreen","PickUpScreen.fxml");
    }
    @FXML public void historyRequest() {
    	this.setscreen("HistoryScreen","HistoryScreen.fxml");
    }
    @FXML public void openTimeExtensionScreen() {
    	this.setscreen("TimeExtensionScreen", "TimeExtensionScreen.fxml");
    }
    private  void setscreen(String screen_name,String fxml) {
		try {
            // reload next screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(screen_name);
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
            // closed current screen
            Stage currentStage = (Stage) historybutton.getScene().getWindow();
            currentStage.close();
            Controller c = null;
            //open next controller
            if(screen_name.equals("Pickupscreen")){
            	PickUpScreenController controller = loader.getController();
            	c = controller;
            }
            else if(screen_name.equals("DropOffScreen")){
            	DropOffScreenController controller = loader.getController();
            	c = controller;
            }
            else if(screen_name.equals("ReservationScreen")){
            	ReservationScreenController controller = loader.getController();
            	c = controller;
            }
            else if(screen_name.equals("TimeExtensionScreen")) {
            	TimeExtensionScreenController controller = loader.getController();
            	c = controller;
            }
            else {
            	HistoryScreenController controller = loader.getController();
            	c = controller;
            }
            c.setClient(client,sub);
            client.setMessageListener(c::handleServerMessage);
            c.setBackHandler(() -> {
                try {    
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("MainMenuScreen.fxml"));
                    Parent loginRoot = loginLoader.load();
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle("Main");
                    currentStage.show();
                    MainMenuController controller = loginLoader.getController();
                    controller.setClient(client,sub);
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cant");
        }
    }
    protected void handleMessageFromServer(Object msg) {
		
		
	}


}
