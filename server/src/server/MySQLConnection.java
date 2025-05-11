package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import logic.Order;
import logic.ParkingSpot;
import logic.Role;
import logic.subscriber;

/**
 * The class handles any SQL query needed
 */
public class MySQLConnection {
	private Connection con;

	/**
	 * @return con
	 */
	protected Connection getCon() {
		// Getter of con
		return con;
	}

	/**
	 * @return con
	 */
	private Connection connectToDB() {
		// Start a connection to DB bpark returns con
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}
		try {
			Connection conn = DriverManager
					.getConnection("jdbc:mysql://localhost/bpark?serverTimezone=IST&useSSL=false", "root", "Aa123456");
			System.out.println("DB connection succeed");
			return conn;
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("connection failed");
			return null;
		}

	}

	/**
	 * @param con
	 */
	private void disconnectFromDB(Connection con) {
		// Closes the connection
		try {
			if (con != null && !con.isClosed()) {
				con.close(); // Close the connection after the operation is complete
				System.out.println("Connection closed after retrieving orders.");
			}
		} catch (SQLException e) {
			System.out.println("Failed to close the connection.");
			e.printStackTrace();
		}
	}

	/**
	 * @return ordersList
	 */
	protected ArrayList<Order> getallordersfromDB() {
		// The method returns an ArrayList<Order> of all orders in DB
		ArrayList<Order> orderslist = new ArrayList<>();
		try {
			con = connectToDB();
			if (con == null)
				throw new SQLException();
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

				// Create Order
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
		} finally {
			disconnectFromDB(con);
		}
		return orderslist;

	}

	/**
	 * @return orders
	 */
	protected String[][] getordersfromDB() {
		// Returns a matrix of strings that have only parking_space and order_date
		// Query to get the size of the matrix
		int size = 0;
		try {
			con = connectToDB();
			if (con == null)
				throw new SQLException();
			String sizetable = "SELECT COUNT(*) AS total FROM `order`; ";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sizetable);
			if (rs.next()) {
				size = rs.getInt("total");
				System.out.println("got size!");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Failed to get size of table");
		} finally {
			disconnectFromDB(con);
		}
		// Query to put parking_space and order_date in orders
		String orders[][] = new String[size][2];
		try {
			con = connectToDB();
			if (con == null)
				throw new SQLException();
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
		} finally {
			disconnectFromDB(con);
		}

		return orders;
	}

	/**
	 * @param order
	 */
	protected void updateDB(Order order) {
		// Gets an order and updates it in the DB
		try {
			con = connectToDB();
			if (con == null)
				throw new SQLException();
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
		} finally {
			disconnectFromDB(con);
		}
	}
}
