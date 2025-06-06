package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        // We'll default to false; only set to true if we find a match.
        boolean exists = false;

        // The SQL counts how many active sessions have this parking_code.
        // If count > 0, then the code is in use.
        String sql = 
            "SELECT COUNT(*) " +
            "FROM parking_sessions " +
            "WHERE parking_code = ? " +
            "  AND active = TRUE";

        // Use try-with-resources so PreparedStatement and ResultSet are closed automatically.
        try (
            // Prepare the statement on the existing Connection
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
            // In case of any SQL exception (e.g., connection lost, syntax error),
            // print stack trace (or log it), but return false by default.
            e.printStackTrace();
        }

        // Return whether we found at least one active session with that code.
        return exists;
    }
}
