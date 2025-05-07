package server;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gui.ServerController;
import logic.Order;
import logic.ParkingSpot;
import logic.Parkingsession;
import logic.Role;
import logic.SpotStatus;
import logic.subscriber;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class BparkServer extends AbstractServer {

	final public static int DEFAULT_PORT = 5555;
	private Connection con;
	private List<ConnectionToClient> clientConnections = new ArrayList<>();
	private ServerController serverController;

	public BparkServer(int port, ServerController controller) {
		super(port);
		this.serverController = controller;
		// TODO Auto-generated constructor stub
	}

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (msg instanceof Order) {
			String[][] orders = getordersfromDB();
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
			updateDB(order);
			System.out.println("order placed");
			sendToSingleClient("order placed", client);
		} else if (msg instanceof String) {
			if(msg.equals("Client disconnected"))
				clientDisconnected(client);	
		}
	}

	public ArrayList<Order> getallordersfromDB() {
		ArrayList<Order> orderslist = new ArrayList<>();
		try {
			String table = "SELECT* FROM `order`; ";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(table);
			while (rs.next()) {
				int order_number = rs.getInt("order_number");
				int parking_space = rs.getInt("parking_space");
				Date order_date = rs.getDate("order_date");
				int confirmation_code = rs.getInt("confirmation_code");
				int subscriber_id = rs.getInt("subscriber_id");
				Date placing_date = rs.getDate("date_of_placing_an_order");
				subscriber sub = new subscriber(subscriber_id, "", "", "", Role.SUBSCRIBER, null, 0);
				ParkingSpot spot = new ParkingSpot(parking_space, null, null);

				// יצירת Order
				Order temp = new Order(confirmation_code, // code
						sub, // subscriber
						order_date, // order_date
						placing_date, // date_of_placing_an_order
						spot, // parking_space
						order_number // order_id
				);
				orderslist.add(temp);
			}
			rs.close();
			stmt.close();
			System.out.println("got all orders");
		} catch (SQLException e) {
			System.out.println("Failed to get size of table");
		}
		return orderslist;

	}

	private String[][] getordersfromDB() {
		int size = 0;
		try {
			String sizetable = "SELECT COUNT(*) AS total FROM `order`; ";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sizetable);
			if (rs.next()) {
				size = rs.getInt("total");
				System.out.println("got size!");
			}
		} catch (SQLException e) {
			System.out.println("Failed to get size of table");
		}

		String orders[][] = new String[size][2];
		try {
			String getorders = "SELECT parking_space,order_date FROM `order`;";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(getorders);
			int index = 0;
			while (rs.next()) {
				String parking_space = rs.getString("parking_space");
				String order_date = rs.getString("order_date");
				orders[index][0] = parking_space;
				orders[index][1] = order_date;
				index++;
			}
			rs.close();
			stmt.close();
			System.out.println("got orders!");
		} catch (SQLException e) {
			System.out.println("Failed to get orders");
		}

		return orders;
	}

	private void updateDB(Order order) {
		try {
			String update_order = "UPDATE `order` SET parking_space = ?, order_date = ? WHERE order_number = ?";
			PreparedStatement stmt = con.prepareStatement(update_order);
			stmt.setInt(1, order.get_ParkingSpot().getSpotId());
			stmt.setDate(2, new java.sql.Date(order.getorder_date().getTime()));
			stmt.setInt(3, order.get_order_id());
			stmt.executeUpdate();
			stmt.close();
			System.out.println("Updated");
		} catch (SQLException e) {
			System.out.println("Failed to Update");
		}
	}

	public void sendToSingleClient(Object msg, ConnectionToClient client) {
		try {
			client.sendToClient(msg + " " + client.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void serverStarted() {
		con = connectToDB();
		System.out.println(("Server listening for connections on port " + getPort()));
	}

	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	@Override
	public void clientConnected(ConnectionToClient client) {
		// Add the client to the list of connected clients
		clientConnections.add(client);
		serverController.updateClientList(getConnectedClientsInfo());
		// Log the connection
		System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
	}

	// This method is called whenever a client disconnects
	@Override
	public void clientDisconnected(ConnectionToClient client) {
		// Remove the client from the list when they disconnect
		clientConnections.remove(client);
		serverController.updateClientList(getConnectedClientsInfo());
		// Log the disconnection
		System.out.println("Client disconnected: " + client.getInetAddress().getHostAddress());
	}

	public List<String> getConnectedClientsInfo() {
		List<String> clientsInfo = new ArrayList<>();
		for (ConnectionToClient client : clientConnections) {
			InetAddress clientAddress = client.getInetAddress(); // Get the client's InetAddress
			String ipAddress = clientAddress.getHostAddress(); // Get the client's IP address
			String hostname = clientAddress.getHostName(); // Get the client's hostname

			String clientInfo = "IP: " + ipAddress + ", Hostname: " + hostname;
			clientsInfo.add(clientInfo);
		}

		return clientsInfo;
	}

	public Connection connectToDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bpark?serverTimezone=IST", "root",
					"Aa123456");
			System.out.println("SQL connection succeed");
			return conn;
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("connection failed");
			return null;
		}

	}
	/*
	 * public static void main(String[] args) { int port = BparkServer.DEFAULT_PORT;
	 * // ברירת מחדל = 5555 BparkServer server = new BparkServer(port);
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
