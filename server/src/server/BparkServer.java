package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gui.ServerController;
import javafx.application.Platform;
import logic.Order;
import ocsf.server.*;

public class BparkServer extends AbstractServer {

	final public static int DEFAULT_PORT = 5555;
	private Object obj1 = new Object();
	private MySQLConnection con = new MySQLConnection();
	private List<ConnectionToClient> clientConnections = new ArrayList<>();
	private List<List<String>> requiredList = new ArrayList<>();
	private ServerController serverController;

	public BparkServer(int port, ServerController controller) {
		super(port);
		this.serverController = controller;
		// TODO Auto-generated constructor stub
	}

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (msg instanceof Order) {
			String[][] orders;
			synchronized (obj1) {
				orders = con.getordersfromDB();
			}
			Order order = (Order) msg;
			String spot, date;
			spot = String.format("%s", order.get_ParkingSpot().getSpotId());
			date = order.getorder_date().toString();
			int size = orders.length;
			for (int i = 0; i < size; i++) {
				if (orders[i][0].equals(spot) && orders[i][1].equals(date)) {
					System.out.println("cant place order!!!!!!!!!");
					sendToSingleClient("cant place order!!!!!!!!!", client);
					return;
				}
			}
			synchronized (obj1) {
				con.updateDB(order);
			}
			System.out.println("order placed");
			sendToSingleClient("order placed", client);
		} else if (msg instanceof String) {
			if (msg.equals("Client disconnected"))
				clientDisconnected(client);
		}
	}

	public void sendToSingleClient(Object msg, ConnectionToClient client) {
		try {
			if(msg instanceof String)
				client.sendToClient(msg + " " + client.getId());
			else if(msg instanceof List) {
				List<Order> orderList = (List<Order>)msg;
				client.sendToClient(orderList);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void serverStarted() {
		System.out.println(("Server listening for connections on port " + getPort()));
	}

	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	@Override
	public void clientConnected(ConnectionToClient client) {
		// Add the client to the list of connected clients
		synchronized (clientConnections) {
			synchronized (requiredList) {
				if (!clientConnections.contains(client)) {
					clientConnections.add(client);
					List<String> clientInfo = new ArrayList<>();
					clientInfo.add(Long.toString(client.getId()));
					clientInfo.add(client.getInetAddress().getHostAddress());
					clientInfo.add(client.getInetAddress().getHostName());
					clientInfo.add("Connected");
					requiredList.add(clientInfo);
					// serverController.recievedServerUpdate(requiredList);
					Platform.runLater(() -> serverController.recievedServerUpdate(requiredList));
					sendToSingleClient(con.getallordersfromDB(),client);
					// Log the connection
					System.out.println(String.format("Client:%s IP:%s HostName:%s %s", clientInfo.get(0),
							clientInfo.get(1), clientInfo.get(2), clientInfo.get(3)));

				} else {
					try {
						clientSetStatus(client, "Connected");
					} catch (Exception e) {
						System.out.println("Connect failed!");
						e.printStackTrace();
					}
				}
			}
		}
	}

	// This method is called whenever a client disconnects
	@Override
	public void clientDisconnected(ConnectionToClient client) {
		// Remove the client from the list when they disconnect
		try {
			synchronized (clientConnections) {
				synchronized (requiredList) {
					clientSetStatus(client, "Disconnected");
				}
			}
		} catch (Exception e) {
			System.out.println("Disconnect failed!");
			e.printStackTrace();
		}
	}

	private void clientSetStatus(ConnectionToClient client, String status) throws Exception {
		for (List<String> string : requiredList) {
			if (string.get(0).equals(Long.toString(client.getId()))) {
				string.set(3, status);
				requiredList.set(requiredList.indexOf(string), string);
				if (status.equals("Disconnected"))
					clientConnections.remove(client);
				else if (status.equals("Connected"))
					clientConnections.add(client);
				else {
					throw new Exception();
				}
				Platform.runLater(() -> serverController.recievedServerUpdate(requiredList));
				// serverController.recievedServerUpdate(requiredList);
				// Log the disconnection
				System.out.println(String.format("Client:%s IP:%s HostName:%s %s", string.get(0), string.get(1),
						string.get(2), string.get(3)));
				break;
			}
		}
	}

	/*
	 * public List<String> getConnectedClientsInfo() { List<String> clientsInfo =
	 * new ArrayList<>(); for (ConnectionToClient client : clientConnections) {
	 * InetAddress clientAddress = client.getInetAddress(); // Get the client's
	 * InetAddress String ipAddress = clientAddress.getHostAddress(); // Get the
	 * client's IP address String hostname = clientAddress.getHostName(); // Get the
	 * client's hostname
	 * 
	 * String clientInfo = ipAddress + ", " + hostname; clientsInfo.add(clientInfo);
	 * }
	 * 
	 * return clientsInfo; }
	 * 
	 * /* public static void main(String[] args) { int port =
	 * BparkServer.DEFAULT_PORT; // ברירת מחדל = 5555 BparkServer server = new
	 * BparkServer(port);
	 * 
	 * try { server.listen(); // מפעיל את השרת } catch (Exception e) {
	 * System.out.println("ERROR - Could not listen for clients!"); }
	 * 
	 * // תאריך ההזמנה Date orderDate = new Date(); // עכשיו // תאריך יצירת ההזמנה
	 * Date placingDate = new Date(); // נניח גם עכש
	 * 
	 * // אובייקט זמני של Order בשביל currentReservation Order dummyOrder = null; //
	 * נניח שאין הזמנה כרגע למקום הזה
	 * 
	 * // יצירת מקום חניה ParkingSpot spot = new ParkingSpot(2, null, dummyOrder);
	 * 
	 * // יצירת ההזמנה המלאה Order order = new Order( 2, null, orderDate,
	 * placingDate, spot, 1 ); server.con = server.connectToDB();
	 * getallordersfromDB(); //updateDB(order); // הדפסה לבדיקה
	 * 
	 * 
	 * //System.out.println("Subscriber: " + order.getSubscriber().getFirstName());
	 * }
	 * 
	 * 
	 * 
	 */

}
