package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.*;

public abstract class Controller {
    protected Runnable backHandler;
    protected subscriber sub;
    protected BParkClient client;
    protected Boolean isConsole = false;
    public void setBackHandler(Runnable backHandler) {
        this.backHandler = backHandler;
    }

	public BParkClient getClient() {
		return client;
	}

	public void setClient(BParkClient client,subscriber sub) {
		this.sub = sub;
		this.client = client;
	}
	
	public void setIsConsole(boolean bool) {
		isConsole = bool;
	}

	public subscriber getSub() {
		return sub;
	}

	@FXML private void handleBackButton() {
		// swap the TableView scene back to the connect screen
		if (backHandler != null) {
			backHandler.run();
		}
	}
	
	protected void handleServerMessage(Object msg) {
		System.err.println("test");
	}
	
	protected void setscreen(String screen_name,String fxml,String returnFxml,String return_name, Button sourceButton) {
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
            Stage currentStage = (Stage) sourceButton.getScene().getWindow();
            currentStage.close();
            Controller c = null;
            c = loader.getController();
            c.setClient(client,sub);
            c.setIsConsole(this.isConsole);
            client.setMessageListener(c::handleServerMessage);
            c.setBackHandler(() -> {
                try {    
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource(returnFxml));
                    Parent loginRoot = loginLoader.load();
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle(return_name);
                         
                    Controller backC = loginLoader.getController();
                    backC.setClient(client,sub);
                    backC.setIsConsole(this.isConsole);
                    if(backC instanceof MainMenuController) {
                    	((MainMenuController)backC).hidebuttons(this.isConsole);
                    }
                    currentStage.show();
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
	
	@FXML
    protected void handleButtonToLogin(Button node) {
        // swap the TableView scene back to the connect screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
            Stage currentStage = (Stage) node.getScene().getWindow();
            currentStage.close();
            client.stop();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }//
}
