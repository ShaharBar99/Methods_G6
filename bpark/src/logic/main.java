package logic;

import java.sql.Connection;
import java.sql.DriverManager;

public class main {
	public static Connection connectToDB() {
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection con;
		con = connectToDB();

	}

}
