package client;


import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;



public class MainMenuController {
	@FXML Button historybutton;
	@FXML Button adminbutton;
	private Runnable backHandler;
	@FXML public void parkingRequest() { System.out.println("Parking Request"); }
    @FXML public void retrieveRequest() { System.out.println("Retrieve Request"); }
    @FXML public void reservationRequest() { System.out.println("Reservation Request"); }

    public void setBackHandler(Runnable backHandler) {
		this.backHandler = backHandler;

	}
    @FXML private void handleBackButton() {
		// swap the TableView scene back to the connect screen
		if (backHandler != null) {
			backHandler.run();
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.setTitle("Login");
			stage.setScene(new Scene(root));
			
			stage.show();
			

			// סגור את החלון הנוכחי
			Stage currentStage = (Stage) historybutton.getScene().getWindow();
			currentStage.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    @FXML
    private void historyRequest() {
        try {
            // טען את המסך החדש
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HistoryScreen.fxml"));
            Parent root = loader.load();

            // פתח חלון חדש
            Stage stage = new Stage();
            stage.setTitle("History");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // סגור את החלון הנוכחי
            Stage currentStage = (Stage) historybutton.getScene().getWindow();
            currentStage.close();

            // קבל את הקונטרולר והעבר לו פעולת חזרה
            HistoryScreenController controller = loader.getController();
            controller.setBackHandler(() -> {
                try {
                	
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("MainMenuScreen.fxml"));
                    Parent loginRoot = loginLoader.load();
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle("Connect to Server");
                    currentStage.show();
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void admin() {
    	try {
            // טען את המסך החדש
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminScreen.fxml"));
            Parent root = loader.load();
            //scaling check
            GraphicsDevice gd = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();

                GraphicsConfiguration gc = gd.getDefaultConfiguration();
                AffineTransform transform = gc.getDefaultTransform();

                double scaleX = transform.getScaleX();
                double scaleY = transform.getScaleY();
            if(scaleX == 1.25) {
            	scaleX = 0.8;
            	scaleY = 0.8;
            }

                //System.out.println("Windows Display Scaling (X): " + scaleX);
                //System.out.println("Windows Display Scaling (Y): " + scaleY);
            //////////////////
            // פתח חלון חדש
            Stage stage = new Stage();
            stage.setTitle("AdminScreen");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setRenderScaleX(0);
            stage.setRenderScaleY(0);
            //root.setScaleX(scaleX); // zoom in
            //root.setScaleY(scaleY);
            stage.show();

            // סגור את החלון הנוכחי
            Stage currentStage = (Stage) adminbutton.getScene().getWindow();
            currentStage.close();

            // קבל את הקונטרולר והעבר לו פעולת חזרה
            AdminScreenController controller = loader.getController();
            controller.setBackHandler(() -> {
                try {
                	
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("MainMenuScreen.fxml"));
                    Parent loginRoot = loginLoader.load();
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle("Connect to Server");
                    currentStage.show();
                    stage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
