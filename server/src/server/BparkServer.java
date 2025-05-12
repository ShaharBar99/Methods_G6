package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gui.ServerController;
import logic.Order;
import ocsf.server.*;

/**
 * The class implements the Server side
 */
public class BparkServer extends AbstractServer {

	final public static int DEFAULT_PORT = 5555;
	private MySQLConnection con = new MySQLConnection(); // Will be used any time an SQL Query is needed
	private List<ConnectionToClient> clientConnections = new ArrayList<>(); // Current connections
	private List<List<String>> requiredList = new ArrayList<>(); // Log of current and former connections
	private ServerController serverController;

	/**
	 * @param port
	 * @param controller
	 */
	public BparkServer(int port, ServerController controller) {
		// Constructor for the class, gets DEFAULT_PORT and ServerController
		super(port);
		this.serverController = controller;
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param msg
	 * @param client
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		// Handles objects that are sent to the server
		if (msg instanceof Order) {
			// An updated order from the client, checks if the change is valid and if so
			// updates the DB
			String[][] orders;
			orders = con.getordersfromDB();
			Order order = (Order) msg;
			String spot, date;
			spot = String.format("%s", order.get_ParkingSpot().getSpotId());
			date = order.getorder_date().toString();
			int size = orders.length;
			for (int i = 0; i < size; i++) {
				if (orders[i][0].equals(spot) && orders[i][1].equals(date)) {
					// Invalid order
					System.out.println("cant place order!!!!!!!!!");
					sendToSingleClient("cant place order!!!!!!!!!", client);
					return;
				}
			}
			// Valid order
			con.updateDB(order);

			System.out.println("order placed");
			sendToSingleClient("order placed", client);
		} else if (msg instanceof String) {
			String msgString = (String)msg;
			if (msgString.equals("Client disconnected")) // Note, make sure client sends a message before it disconnects
				clientDisconnected(client);
			if (msgString.startsWith("get_order: ")) {
				String parts[] = msgString.split("get_order: ");
				con.getOrdersFromDB(parts[1]);
			}
		}
	}

	/**
	 * @param msg
	 * @param client
	 */
	public void sendToSingleClient(Object msg, ConnectionToClient client) {
		// Sends a object msg to a client
		try {
			if (msg instanceof String) // Sends a String
				client.sendToClient(msg);
			else if (msg instanceof List) { // Sends a list of orders
				List<Order> orderList = (List<Order>) msg;
				
				client.sendToClient(orderList);
				System.out.println(orderList);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void serverStarted() {
		// Prints to console that the server started
		System.out.println(("Server listening for connections on port " + getPort()));
	}

	protected void serverStopped() {
		// Honestly never used
		System.out.println("Server has stopped listening for connections.");
	}

	/**
	 * @param client
	 */
	@Override
	public void clientConnected(ConnectionToClient client) {
		// Add the client to the list of connected clients
		// Each client has: id, IP, hostName, status{"Connected","Disconnected"} (all
		// strings)

		if (!clientConnections.contains(client)) {
			clientConnections.add(client);
			List<String> clientInfo = new ArrayList<>();
			clientInfo.add(Long.toString(client.getId()));
			clientInfo.add(client.getInetAddress().getHostAddress());
			clientInfo.add(client.getInetAddress().getCanonicalHostName());
			clientInfo.add("Connected");
			requiredList.add(clientInfo);
			serverController.recievedServerUpdate(requiredList);
			sendToSingleClient(con.getallordersfromDB(), client);
			// Log the connection
			System.out.println(String.format("Client:%s IP:%s HostName:%s %s", clientInfo.get(0), clientInfo.get(1),
					clientInfo.get(2), clientInfo.get(3)));

		} else { // In case the same client tries to reconnect
			try {
				clientSetStatus(client, "Connected");
			} catch (Exception e) {
				System.out.println("Connect failed!");
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param client
	 */
	@Override
	public void clientDisconnected(ConnectionToClient client) {
		// This method is called whenever a client disconnects
		// Remove the client from the list when they disconnect
		try {
			clientSetStatus(client, "Disconnected");

		} catch (Exception e) {
			System.out.println("Disconnect failed!");
			e.printStackTrace();
		}
	}

	/**
	 * @param client
	 * @param status
	 * @throws Exception
	 */
	private void clientSetStatus(ConnectionToClient client, String status) throws Exception {
		// Updates the status of a client to either "Connected"/"Disconnected"
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
				serverController.recievedServerUpdate(requiredList);
				// Log the disconnection
				System.out.println(String.format("Client:%s IP:%s HostName:%s %s", string.get(0), string.get(1),
						string.get(2), string.get(3)));
				break;
			}
		}
	}

}
