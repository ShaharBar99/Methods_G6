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

public class MySQLConnection {
	private Connection con;
	
	protected Connection getCon() {
		return con;
	}
	
	private Connection connectToDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bpark?serverTimezone=IST&useSSL=false", "root",
					"Aa123456");
			System.out.println("DB connection succeed");
			return conn;
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("connection failed");
			return null;
		}

	}

	private void disconnectFromDB(Connection con) {
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
	protected ArrayList<Order> getallordersfromDB() {
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
		}
		finally {
			disconnectFromDB(con);
		}
		return orderslist;

	}

	protected String[][] getordersfromDB() {
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

	protected void updateDB(Order order) {
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
