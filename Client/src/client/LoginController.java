package client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.*;

public class LoginController {

	final int port = 5555;
    @FXML private TextField ipField;
    @FXML private ComboBox<String> loginMethodComboBox;
    @FXML private TextField nameField;
    @FXML private TextField subscriberIdField;
    @FXML private TextField tagField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button exitButton;
    @FXML private Button connectButton;
    private BParkClient client;
    @FXML private CheckBox consoleCheckBox;

    @FXML
    public void initialize() {
        loginMethodComboBox.setItems(FXCollections.observableArrayList("Name + Subscriber Code"));
        loginMethodComboBox.getSelectionModel().selectFirst();

        loginMethodComboBox.setOnAction(event -> updateFieldVisibility());
        consoleCheckBox.setOnAction(event -> updateLoginOptions());
    }

    private void updateLoginOptions() {
        boolean isConsoleChecked = consoleCheckBox.isSelected();

        if (isConsoleChecked) {
            if (!loginMethodComboBox.getItems().contains("Tag")) {
                loginMethodComboBox.getItems().add("Tag");
            }
        } else {
            if (loginMethodComboBox.getItems().contains("Tag")) {
                loginMethodComboBox.getItems().remove("Tag");
                if ("Tag".equals(loginMethodComboBox.getValue())) {
                    loginMethodComboBox.setValue("Name + Subscriber Code");
                }
            }
        }

        updateFieldVisibility();
    }

    private void updateFieldVisibility() {
        String method = loginMethodComboBox.getValue();
        boolean isTag = "Tag".equals(method);
        boolean isConsoleChecked = consoleCheckBox.isSelected();

        nameField.setVisible(!isTag);
        nameField.setManaged(!isTag);

        subscriberIdField.setVisible(!isTag);
        subscriberIdField.setManaged(!isTag);

        tagField.setVisible(isTag && isConsoleChecked);
        tagField.setManaged(isTag && isConsoleChecked);
    }

    @FXML
    private void handleLogin() {
    	String ipAddress = ipField.getText().trim();
		if (ipAddress.isEmpty()) {
			ShowAlert.showAlert("Error", "Please enter both IP and port", Alert.AlertType.ERROR);
			return;
		}
		subscriber sub1 = createsubfromlogin();
		connectToServer(ipAddress, port, sub1);
    }

    public subscriber createsubfromlogin() {
		String method = loginMethodComboBox.getValue();
		boolean isTag = "Tag".equals(method);
		boolean isConsoleChecked = consoleCheckBox.isSelected();

		try {
			if (isTag && isConsoleChecked) {
				// לא יוצרים אובייקט subscriber – השרת יקבל tag בלבד
				return null;
			} else {
				int passwordInt = Integer.parseInt(subscriberIdField.getText().trim());
				return new subscriber(0, nameField.getText().trim(), null, null, null,
						null, null, passwordInt);
			}
		} catch (Exception e) {
			ShowAlert.showAlert("Error", "The type of the fields is wrong", Alert.AlertType.ERROR);
			return null;
		}
	}

    public void connectToServer(String ipAddress, int port, subscriber sub1) {
		try {
			client = new BParkClient(ipAddress, port);
			exitButton.setDisable(false);
			client.start(sub1); // אם sub1 == null, ה־client כנראה ישתמש ב־tagField בעצמו
			client.setMessageListener(this::handleServerMessage);
		} catch (Exception e) {
			ShowAlert.showAlert("Error", "Failed to connect to the server at " + ipAddress + ":" + port, Alert.AlertType.ERROR);
			connectButton.setDisable(false);
		}
	}

    private void handleServerMessage(Object msg) {
    	Platform.runLater(() -> {
    		if(msg instanceof SendObject) {
    			SendObject send = (SendObject)msg;
    			if(send.getObj() instanceof subscriber) {
	    			subscriber sub = (subscriber) send.getObj();
	    			System.out.println("[Server] " + msg);
	    			switch (sub.getRole()) {
	    				case SUBSCRIBER -> connectclient(sub, "MainMenuScreen.fxml", "Main Menu", -1);
	    				case ATTENDANT  -> connectclient(sub, "AttendantScreen.fxml", "Attendant Menu", -1);
	    				case MANAGER    -> connectclient(sub, "AdminScreen.fxml", "Admin Menu", -1);
	    			}
    			}
    			else if(send.getObj() instanceof Double) {
    				System.out.println(send.getObj());
    				connectclient(null, "GuestScreenUI.fxml", "Guest Screen", (double)send.getObj());
    			}
    			else {
					client.stop();
					ShowAlert.showAlert("Error", "Cant login to server wrong account", Alert.AlertType.ERROR);
				}
    		}
    	});
    }

    private void connectclient(subscriber sub, String fxml, String title, double percent) {
		Controller controller = null;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		Parent tableRoot = null;

		try {
			tableRoot = loader.load();
			controller = loader.getController();
			controller.setClient(client, sub);
		} catch (IOException e) {
			System.out.println("cant load screen: " + fxml);
			e.printStackTrace();
		}

		if (percent != -1 && controller instanceof GuestScreenController guestController) {
			guestController.set_percent(percent);
		}

		if (fxml.equals("MainMenuScreen.fxml") && controller instanceof MainMenuController mainMenuController) {
			boolean useConsole = consoleCheckBox.isSelected();
			mainMenuController.hidebuttons(useConsole);
		}

        Stage stage = (Stage) connectButton.getScene().getWindow();
        Scene scene = new Scene(tableRoot);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
		stage.setTitle(title);
		stage.setMaximized(true);

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println("Closing application...");
				client.stop();
				System.gc();
				System.exit(0);
			}
		});

		controller.setBackHandler(() -> {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
				Parent loginRoot = loginLoader.load();
				stage.setScene(new Scene(loginRoot));
				stage.setTitle("Connect to Server");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		client.setMessageListener(controller::handleServerMessage);
	}

    @FXML
	public void handleExit() {
		System.gc();
		Platform.exit();
	}

    @FXML
    private void openGuestScreen() {
    	String ipAddress = ipField.getText().trim();
		if (ipAddress.isEmpty()) {
			ShowAlert.showAlert("Error", "Please enter both IP and port", Alert.AlertType.ERROR);
			return;
		}
		connectToServer(ipAddress, port, null);
    }
}
