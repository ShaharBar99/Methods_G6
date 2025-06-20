package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logic.ParkingSpot;
import logic.Parkingsession;
import logic.Reservation;
import logic.Role;
import logic.SpotStatus;
import logic.subscriber;

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
    /**
     * Retrieves the active parking session for the given parking code,
     * or null if no such active session exists.
     *
     * @param parkingCode the parking_code to look up
     * @return the Parkingsession object, or null if none found
     */
    protected Parkingsession getActiveParkingsessionWithThatCodeFromDatabase(int parkingCode) {
        Parkingsession parking = null;
        String sql =
        	"SELECT *"+
            "FROM parking_sessions " +
            "WHERE parking_code = ? " +
            "AND active = TRUE " +
            "LIMIT 1";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setInt(1, parkingCode);

            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) {
                    int sessionId    = rs.getInt("session_id");
                    int subscriberId = rs.getInt("subscriber_id");
                    int spotId       = rs.getInt("spot_id");
                    int code         = rs.getInt("parking_code");
                    java.util.Date inTime  = rs.getTimestamp("in_time");
                    java.util.Date outTime = rs.getTimestamp("out_time"); 
                    boolean extended = rs.getBoolean("extended");
                    boolean late     = rs.getBoolean("late");
                    boolean active   = rs.getBoolean("active");
                    parking = new Parkingsession(sessionId,subscriberId,spotId,code,inTime,outTime,extended,late,active);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return parking;
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
                "SELECT spot_id,date,start_time,end_time " +
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
    protected int getSubscriberLastActiveParkingsessionParkingCode(int subscriber_id) {
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
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return spot;
    }
    protected List<Parkingsession> gethistoryParkingsessionsListOfSubscriberbyIdFromDatabase(int subscriber_id)
    {
    	List<Parkingsession> historyList  = new ArrayList<>();
        String sql =
            "SELECT *"+
            "FROM parking_sessions " +
            "WHERE subscriber_id = ? " +
            "AND active = FALSE " ;

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setInt(1, subscriber_id);

            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) {
                    int sessionId    = rs.getInt("session_id");
                    int subscriberId = rs.getInt("subscriber_id");
                    int spotId       = rs.getInt("spot_id");
                    int code         = rs.getInt("parking_code");
                    java.util.Date inTime  = rs.getTimestamp("in_time");
                    java.util.Date outTime = rs.getTimestamp("out_time"); 
                    boolean extended = rs.getBoolean("extended");
                    boolean late     = rs.getBoolean("late");
                    boolean active   = rs.getBoolean("active");
                    historyList.add(new Parkingsession(sessionId,subscriberId,spotId,code,inTime,outTime,extended,late,false));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return historyList ;
    }
    /**
     * Calculates the percentage of parking spots that are currently free.
     *
     * @return percentage of free spots (0.0–100.0), or 0.0 if there are none or on error
     */
    protected double getPrecentageAvailableSpaceFromDatabase() {
        int freeCnt = 0;
        int cntAll  = 0;

        // Correct SQL with proper spacing and aliases
        String sqlAll  = "SELECT COUNT(*) AS cntAll  FROM parking_spots";
        String sqlFree = "SELECT COUNT(*) AS freeCnt FROM parking_spots WHERE status = 'FREE'";

        // Use try-with-resources to clean up both statements and result sets
        try (
            PreparedStatement psAll  = getCon().prepareStatement(sqlAll);
            ResultSet         rsAll  = psAll.executeQuery();
            PreparedStatement psFree = getCon().prepareStatement(sqlFree);
            ResultSet         rsFree = psFree.executeQuery()
        ) {
            if (rsAll.next()) {
                cntAll = rsAll.getInt("cntAll");
            }
            if (rsFree.next()) {
                freeCnt = rsFree.getInt("freeCnt");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // Avoid division by zero and compute percentage
        if (cntAll > 0) {
            return ((double) freeCnt / cntAll) * 100.0;
        } else {
            return 0.0;
        }
    }
    /**
     * Looks up a subscriber by their (numeric) code and name.
     *
     * @param code the subscriber’s login/code
     * @param name the subscriber’s name
     * @return a subscriber object, or null if no match
     */
    protected subscriber getUserUsingCodeFromDatabase(int code, String name) {
        subscriber subscribe = null;

        String sql =
            "SELECT * " +                              // SELECT all subscriber fields
            "FROM subscribers " +                      //   from the subscribers table :contentReference[oaicite:0]{index=0}
            "WHERE code = ? " +
            "  AND name = ?";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setInt(1, code);
            ps.setString(2, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Read each column by its name
                    int    subscriberId = rs.getInt("subscriber_id");
                    String nameDb       = rs.getString("name");
                    String phone        = rs.getString("phone");
                    String email        = rs.getString("email");
                    Role   role         = Role.valueOf(rs.getString("role"));
                    String tag          = rs.getString("tag");
                    int    codeDb       = rs.getInt("code");
                    List<Parkingsession> history = new ArrayList<>();
                    // Construct the subscriber
                    subscribe = new subscriber(subscriberId,nameDb,phone, email,role,history,tag,codeDb);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return subscribe;
    }
    /**
     * Looks up a subscriber by their unique tag.
     *
     * @param tag the subscriber’s tag
     * @return a subscriber object, or null if no match
     */
    protected subscriber getUserUsingTagFromDatabase (String tag)
    {
    	subscriber subscribe = null;

        String sql =
            "SELECT * " +                              // SELECT all subscriber fields
            "FROM subscribers " +                      //   from the subscribers table :contentReference[oaicite:0]{index=0}
            "WHERE tag = ? ";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, tag);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Read each column by its name
                    int    subscriberId = rs.getInt("subscriber_id");
                    String nameDb       = rs.getString("name");
                    String phone        = rs.getString("phone");
                    String email        = rs.getString("email");
                    Role   role         = Role.valueOf(rs.getString("role"));                   
                    int    codeDb       = rs.getInt("code");
                    List<Parkingsession> history = new ArrayList<>();
                    // Construct the subscriber
                    subscribe = new subscriber(subscriberId,nameDb,phone, email,role,history,tag,codeDb);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return subscribe;
    }
    /**
     * Updates the given parking session in the database.
     *
     * @param session the Parkingsession object containing new values
     */
    protected void updateParkingsessionInDatabase(Parkingsession session) {
        String sql =
            "UPDATE parking_sessions " +
            "SET subscriber_id = ?, " +
            "    spot_id       = ?, " +
            "    parking_code  = ?, " +
            "    in_time       = ?, " +
            "    out_time      = ?, " +
            "    extended      = ?, " +
            "    late          = ?, " +
            "    active        = ? " +
            "WHERE session_id   = ?";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setInt(1, session.getSubscriberId());
            ps.setInt(2, session.getSpotId());
            ps.setInt(3, session.getParkingCode());
            // Convert java.util.Date to java.sql.Timestamp for DATETIME columns
            ps.setTimestamp(4, new java.sql.Timestamp(session.getInTime().getTime()));
            // outTime may be null
            java.util.Date out = session.getOutTime();
            if (out != null) {
                ps.setTimestamp(5, new java.sql.Timestamp(out.getTime()));
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }
            ps.setBoolean(6, session.isExtended());
            ps.setBoolean(7, session.isLate());
            ps.setBoolean(8, session.getActive());
   
            ps.setInt(9, session.getSessionId());

            int rowsAffected = ps.executeUpdate();
         }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the given subscriber’s data in the database.
     *
     * @param user the subscriber object containing updated fields
     */
    protected void updateUserInDatabase(subscriber user) {
        // 1) Prepare UPDATE statement for subscribers (see table definition) :contentReference[oaicite:0]{index=0}
        String sql =
            "UPDATE subscribers " +
            "SET name   = ?, " +
            "    phone  = ?, " +
            "    email  = ?, " +
            "    role   = ?, " +
            "    tag    = ?, " +
            "    code   = ? " +
            "WHERE subscriber_id = ?";

        // 2) Use try-with-resources to ensure the PreparedStatement is closed
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            // 3) Bind each field from the subscriber object in the same order
            ps.setString(1, user.getName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getTag());
            ps.setInt   (6, user.getCode());
            // 4) Bind the WHERE clause parameter
            ps.setInt   (7, user.getId());

            // 5) Execute the update
            int rowsAffected = ps.executeUpdate();
            // (Optional) you can log or check rowsAffected to ensure one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Updates the status of a parking spot in the database.
     *
     * @param spot the ParkingSpot object whose status has changed
     */
    protected void updateParkingSpotInDatabase(ParkingSpot spot) {
        String sql =
            "UPDATE parking_spots " +
            "SET status = ? " +
            "WHERE spot_id = ?";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, spot.getStatus().name());
            ps.setInt   (2, spot.getSpotId());
            int rows = ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Inserts a new subscriber into the database and updates its generated ID.
     *
     * @param user the subscriber to create (its subscriberId will be set after insertion)
     */
    protected void createUserInDatabase(subscriber user) {
        // 1) Prepare INSERT SQL (subscriber_id is AUTO_INCREMENT, so we skip it)
        String sql =
            "INSERT INTO subscribers " +
            "  (name, phone, email, role, tag, code) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        // 2) Use RETURN_GENERATED_KEYS so we can retrieve the new subscriber_id
        try (PreparedStatement ps = getCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 3) Bind each field from the subscriber object
            ps.setString(1, user.getName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole().name());  // Role enum to VARCHAR
            ps.setString(5, user.getTag());
            ps.setInt   (6, user.getCode());

            // 4) Execute the insert
            int rows = ps.executeUpdate();

            // 5) If insert succeeded, grab the generated auto‐increment key
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Inserts a new Parkingsession into the database and updates its generated sessionId.
     *
     * @param session the Parkingsession to create (its sessionId will be set after insertion)
     */
    protected void createParkingsessionInDatabase(Parkingsession session) {
        // 1) Prepare the INSERT statement. session_id is AUTO_INCREMENT, so we skip it.
        String sql =
            "INSERT INTO parking_sessions " +
            "  (subscriber_id, spot_id, parking_code, in_time, out_time, extended, late, active) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // 2) Tell JDBC to return the generated keys so we can grab the new session_id
        try (PreparedStatement ps = getCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 3) Bind each field from the Parkingsession object
            ps.setInt(1, session.getSubscriberId());
            ps.setInt(2, session.getSpotId());
            ps.setInt(3, session.getParkingCode());

            // Convert java.util.Date → java.sql.Timestamp for the DATETIME columns
            ps.setTimestamp(4, new java.sql.Timestamp(session.getInTime().getTime()));

            java.util.Date out = session.getOutTime();
            if (out != null) {
                ps.setTimestamp(5, new java.sql.Timestamp(out.getTime()));
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }

            ps.setBoolean(6, session.isExtended());
            ps.setBoolean(7, session.isLate());
            ps.setBoolean(8, session.getActive());

            // 4) Execute the insert
            int rows = ps.executeUpdate();

            // 5) If insert succeeded, retrieve the generated session_id
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        session.setSessionId(keys.getInt(1));
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Returns all parking spots that are not occupied (status = FREE or RESERVED)
     * and that have no reservation overlapping the given date/time window.
     *
     * @param date      the reservation date (java.time.LocalDate)
     * @param startTime the desired start time as "HH:mm:ss"
     * @param endTime   the desired end   time as "HH:mm:ss"
     * @return a list of ParkingSpot objects matching those criteria
     */
    protected List<ParkingSpot> getFreeParkingSpotFromDatabase(LocalDate date,String startTime,String endTime) {
        List<ParkingSpot> availableSpots = new ArrayList<>();

        String sql =
            "SELECT ps.spot_id, ps.status " +
            "FROM parking_spots ps " +
            "WHERE ps.status IN ('FREE', 'RESERVED') " +
            "  AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM reservations r " +
            "    WHERE r.spot_id    = ps.spot_id " +
            "      AND r.date       = ? " +
            "      AND r.start_time < ? " +
            "      AND r.end_time   > ? " +
            "  )";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            // bind the date/time parameters
            ps.setDate(1, java.sql.Date.valueOf(date));
            ps.setTime(2, java.sql.Time.valueOf(startTime+ ":00"));
            ps.setTime(3, java.sql.Time.valueOf(endTime+ ":00"));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int spotId = rs.getInt("spot_id");
                    // convert the VARCHAR status into your SpotStatus enum
                    SpotStatus status = SpotStatus.valueOf(rs.getString("status"));
                    availableSpots.add(new ParkingSpot(spotId, status));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableSpots;
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservation the Reservation to persist
     */
    protected void createReservationInDatabase(Reservation reservation) {
        // SQL inserts subscriber_id, spot_id, date, start_time, end_time; reservation_id is AUTO_INCREMENT
        String sql =
            "INSERT INTO reservations " +
            "  (subscriber_id, spot_id, date, start_time, end_time) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            // Bind subscriber_id then spot_id (Reservation.getSpot() returns the spot_id) :contentReference[oaicite:0]{index=0}
            ps.setInt(1, reservation.getSubscriberId());
            ps.setInt(2, reservation.getSpot());
            // Convert LocalDate → java.sql.Date for the DATE column
            ps.setDate(3, java.sql.Date.valueOf(reservation.getDate()));
            // Convert String ("HH:mm:ss") → java.sql.Time for the TIME columns
            ps.setTime(4, java.sql.Time.valueOf(reservation.getStartTime()+ ":00"));
            ps.setTime(5, java.sql.Time.valueOf(reservation.getEndTime()+ ":00"));
            
            // Execute the INSERT
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


}