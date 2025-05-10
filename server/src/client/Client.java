package client;

import ocsf.client.AbstractClient;

import java.util.Date;
import java.util.ResourceBundle.Control;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.Order;
import logic.ParkingSpot;
import logic.subscriber;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import logic.Role;



public class Client extends Application {

    private static MyClient clientConnection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create simple GUI for entering IP
            AnchorPane root = new AnchorPane();

            // Create TextField for IP
            TextField ipField = new TextField();
            ipField.setPromptText("Enter Server IP");
            ipField.setLayoutX(65);
            ipField.setLayoutY(30);

            // Create Connect Button
            Button connectButton = new Button("Connect");
            connectButton.setLayoutX(50);
            connectButton.setLayoutY(70);

            // Create Disconnect Button
            Button disconnectButton = new Button("Disconnect");
            disconnectButton.setLayoutX(150);
            disconnectButton.setLayoutY(70);
            disconnectButton.setDisable(false);  // Initially disabled
            
            Button testButton = new Button("Test");
            testButton.setLayoutX(250);
            testButton.setLayoutY(70);
            
            
            
            
            // Add elements to the scene
            root.getChildren().add(ipField);
            root.getChildren().add(connectButton);
            root.getChildren().add(disconnectButton);
            

            // Set up the scene and stage
            Scene scene = new Scene(root, 310, 150);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Client - Connect to Server");
            primaryStage.show();
            
            
            //exactly what it says madafaka
            displaySQLTable();
            


            // Set up actions for buttons
            connectButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String ipAddress = ipField.getText();
                    connectToServer(ipAddress); // Connect to the server using the entered IP
                }
            });

            disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    disconnectFromServer(); // Disconnect from the server
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static void displaySQLTable() {
        
    	// the root for the table daaaahh
    	AnchorPane tableRoot = new AnchorPane();
    	
        //create TableView
        TableView<Order> orderTable = new TableView<>();
        
        // Create columns for the table
        
        TableColumn<Order, Integer> spotColumn = new TableColumn<>("Spot");
        //spotColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("ParkingSpace.getSpotId()"));
        spotColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getParkingSpace().getSpotId()));
        //This one is because there's no getter for parkingSpaceID in the Order class
        
        TableColumn<Order, Integer> orderIdColumn = new TableColumn<>("Order Number");
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("orderID"));

        TableColumn<Order, Date> orderDateColumn = new TableColumn<>("Order Date");
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<Order, Date>("orderDate"));
        
        TableColumn<Order, Integer> confirmationCodeColumn = new TableColumn<>("Code");
        confirmationCodeColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("confirmationCode"));
        
        TableColumn<Order, Integer> subscriberIdColumn = new TableColumn<>("Subscriber ID");
        subscriberIdColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("subscriberID"));
        
        TableColumn<Order, Date> dateOfPlacingOrderColumn = new TableColumn<>("Date of Placing Order");
        dateOfPlacingOrderColumn.setCellValueFactory(new PropertyValueFactory<Order, Date>("dateOfPlacingAnOrder"));
                    
        
        // Add the columns to the table
        orderTable.getColumns().add(orderIdColumn);
        orderTable.getColumns().add(subscriberIdColumn);
        orderTable.getColumns().add(spotColumn);
        orderTable.getColumns().add(confirmationCodeColumn);
        orderTable.getColumns().add(orderDateColumn);
        orderTable.getColumns().add(dateOfPlacingOrderColumn);
        
        orderTable.setPrefWidth(600);
        orderTable.setPrefHeight(400);
        
        // Set the width of the columns
        orderIdColumn.setPrefWidth(110);
        subscriberIdColumn.setPrefWidth(110);
        spotColumn.setPrefWidth(100);
        confirmationCodeColumn.setPrefWidth(100);
        orderDateColumn.setPrefWidth(150);
    	dateOfPlacingOrderColumn.setPrefWidth(150);

        // Set the table's data???
        //orderTable.setItems(clientConnection.getallordersfromDB());
        //orderTable.getItems().addAll(clientConnection.getOrdersfromDB());//that's in the server
        
        
        // For testing purposes, add a sample order
        Order order1 = new Order(1001, new subscriber(12345, "name_1", "phone_1", "email_1", Role.SUBSCRIBER, null, 0)
        		, new Date(), new Date(), new ParkingSpot(1, null, null), 1);
        Order order2 = new Order(1002, new subscriber(67890, "name_2", "phone_2", "email_2", Role.SUBSCRIBER, null, 0)
        		, new Date(), new Date(), new ParkingSpot(1, null, null), 2);
        Order order3 = new Order(1003, new subscriber(11223, "name_3", "phone_3", "email_3", Role.SUBSCRIBER, null, 0)
        		, new Date(), new Date(), new ParkingSpot(1, null, null), 3);
        Order order4 = new Order(1004, new subscriber(99887, "name_4", "phone_4", "email_4", Role.SUBSCRIBER, null, 0)
        		, new Date(), new Date(), new ParkingSpot(1, null, null), 4);
        
        orderTable.getItems().add(order1);
        orderTable.getItems().add(order2);
        orderTable.getItems().add(order3);
        orderTable.getItems().add(order4);
        
        
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        tableRoot.getChildren().add(orderTable);
        Stage tableStage = new Stage();
        Scene tableScene = new Scene(tableRoot, 1000, 1000);

        tableStage.setTitle("Table Stage");
        tableStage.setWidth(700);
        tableStage.setHeight(500);
        tableStage.setX(100); // set the x position of the stage
        tableStage.setY(100); // set the y position of the stage
        
        tableStage.setScene(tableScene);
        tableStage.show();
    }
    
    

    // Method to connect to the server
    public static void connectToServer(String ipAddress) {
        try {
            // Create a new instance of MyClient
            clientConnection = new MyClient(ipAddress, 5555); // IP and port of the server
            clientConnection.openConnection();  // Open the connection

            // Show success message
            showAlert("Success", "Successfully connected to the server at " + ipAddress, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            // Handle connection failure
            showAlert("Error", "Failed to connect to the server at " + ipAddress, Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Method to disconnect from the server
    public static void disconnectFromServer() {
        if (clientConnection != null && clientConnection.isConnected()) {
            try {
            	clientConnection.sendToServer("Client disconnected");
                clientConnection.closeConnection();  // Close the connection
                showAlert("Disconnected", "Successfully disconnected from the server.", Alert.AlertType.INFORMATION);
                
            } catch (Exception e) {
                // Handle failure during disconnection
                showAlert("Error", "Failed to disconnect from the server.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    // Method to show alerts
    public static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Custom MyClient class to extend AbstractClient
    public static class MyClient extends AbstractClient {

        public MyClient(String host, int port) {
            super(host, port);  // Calling the superclass constructor
        }

        @Override
        public void handleMessageFromServer(Object msg) {
            // Handle message from the server
            System.out.println("Message from server: " + msg);
        }

        @Override
        public void connectionClosed() {
            // Handle connection closed
            System.out.println("Connection closed with the server.");
        }

        @Override
        public void connectionException(Exception exception) {
            // Handle connection exception
            System.out.println("Connection exception: " + exception);
        }
    }
}
