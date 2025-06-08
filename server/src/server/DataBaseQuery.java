package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.Reservation;

/**
 * DataBaseQuery contains methods for reading from the BPark schema.
 * It extends MySQLConnection to inherit the connection logic.
 */
public class DataBaseQuery extends MySQLConnection {
    /**
     * Constructor: calls parent constructor to ensure the database/tables exist
     * and that `con` (the Connection) is initialized.
     */
    public DataBaseQuery() {
        super();
    }
    /**
     * Checks if the given parking code is currently in use by any active parking session.
     *
     * @param code the parking_code to look for
     * @return true if there is at least one row in parking_sessions
     *         where parking_code = code AND active = TRUE; false otherwise
     */
    protected boolean checkParkingCodeInAllActiveSessionsInDatabase(int code) {
        boolean exists = false;
        String sql = 
            "SELECT COUNT(*) " +
            "FROM parking_sessions " +
            "WHERE parking_code = ? " +
            "  AND active = TRUE";
        try (
            PreparedStatement ps = getCon().prepareStatement(sql)
        ) {
            // Bind the integer `code` into the SQL's first parameter (index = 1).
            ps.setInt(1, code);
            // Execute the query; it returns a ResultSet with exactly one row and one column (the count).
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Get the integer from the first column of that single row.
                    int count = rs.getInt(1);
                    // If count > 0, then at least one active session used this code.
                    exists = (count > 0);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
    protected void updateSpotToFreeInDatabase(int spotId) {
        //boolean exists = false;
        String sql =
        	"UPDATE parking_spots " +
            "SET active=FALSE" +
            "WHERE parking_code = ? ";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setInt(1, spotId);
            int rowsUpdated = ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Fetches all reservations for the given subscriber ID.
     *
     * @param subscriber_id the ID of the subscriber whose reservations we want
     * @return a list of Reservation objects (possibly empty if none found)
     */
    protected List<Reservation> getReservationListOfSubscriberbyIdFromDatabase(int subscriber_id)
    {
    	List<Reservation> reservationListOfSubscriber = new ArrayList<>();
    	String sql = 
                "SELECT spot_id,date,start_time,end_time" +
                "FROM reservations " +
                "WHERE subscriber_id = ?";
            try (
                PreparedStatement ps = getCon().prepareStatement(sql)
            ) {  
                ps.setInt(1, subscriber_id);
                // Execute the query; it returns a ResultSet with exactly one row and one column (the count).
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                    	int spotId       = rs.getInt("spot_id");
                    	java.time.LocalDate date = rs.getDate("date").toLocalDate();
                        String startTime   = rs.getString("start_time");
                        String endTime     = rs.getString("end_time");
                        Reservation r = new Reservation(spotId, subscriber_id ,date, startTime, endTime);

                        //Add it to our list.
                        reservationListOfSubscriber.add(r);
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
    	return reservationListOfSubscriber;
    }
    /**
     * Retrieves the most recent parking_code for the given subscriber, based on the latest in_time.
     *
     * @param subscriber_id the ID of the subscriber
     * @return the last parking_code used by that subscriber, or 0 if none found / on error
     */
    protected int getSubscriberLastParkingCode(int subscriber_id) {
        int code = 0;

        // Select the parking_code of the most recent session for this subscriber
        String sql =
            "SELECT parking_code " +
            "FROM parking_sessions " +
            "WHERE subscriber_id = ? " +
            "ORDER BY in_time DESC " +
            "LIMIT 1";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setInt(1, subscriber_id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    code = rs.getInt("parking_code");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return code;
    }
    /**
     * Finds and returns one free parking‐spot ID, or -1 if none available.
     *
     * @return a free spot_id, or -1 if there are no FREE spots / on error
     */
    protected Object getFreeSpotFromDatabase() {
        Integer spot = -1;

        // Note spaces around keywords, quote the FREE status, and limit to 1 row.
        String sql =
            "SELECT spot_id " +
            "FROM parking_spots " +
            "WHERE status = 'FREE' " +
            "LIMIT 1";

        // One try-with-resources block for both PreparedStatement and ResultSet
        try (
            PreparedStatement ps = getCon().prepareStatement(sql);
            ResultSet rs       = ps.executeQuery()
        ) {
            if (rs.next()) {
                // Read the spot_id column
                spot = rs.getInt("spot_id");
            }
           /* else
            {getReservedSpotFromDatabase();}*/
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return spot;
    }
    /*protected List<Reservation> getReservedSpotFromDatabase()
    {
    	List<Reservation> reservationListOfSubscriber = new ArrayList<>();
    	String sql = 
                "SELECT subscriber_id,spot_id,date,start_time,end_time" +
                "FROM reservations " ;
            try (
                PreparedStatement ps = getCon().prepareStatement(sql)
            ) {  
                // Execute the query; it returns a ResultSet with exactly one row and one column (the count).
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                    	int subscriberId = rs.getInt("subscriber_id");
                    	int spotId       = rs.getInt("spot_id");
                    	java.time.LocalDate date = rs.getDate("date").toLocalDate();
                        String startTime   = rs.getString("start_time");
                        String endTime     = rs.getString("end_time");
                        Reservation r = new Reservation(spotId, subscriberId,date, startTime, endTime);

                        //Add it to our list.
                        reservationListOfSubscriber.add(r);
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
    	return reservationListOfSubscriber;
    }*/

    
}
