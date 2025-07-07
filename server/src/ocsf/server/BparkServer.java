package ocsf.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import serverControllers.*;
import jdbc.*;
import logic.*;

/**
 * The class implements the Server side
 */
public class BparkServer extends AbstractServer {

	final public static int DEFAULT_PORT = 5555;
	private DataBaseQuery con; // Will be used any time an SQL Query is needed
	private List<ConnectionToClient> clientConnections = new ArrayList<>(); // Current connections
	private List<List<String>> requiredList = new ArrayList<>(); // Log of current and former connections
	private ServerController serverController;
	private DatabaseDailyUpdater dailyUpdater;
	/**
	 * Constructor for the class
	 * @param port
	 * @param controller 
	 */
	public BparkServer(ServerController controller) {
		super(DEFAULT_PORT);
		this.serverController = controller;
		con = new DataBaseQuery();
	}

	/**
	 * Handles objects that are sent to the server
	 * @param msg
	 * @param client
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {

		if (msg instanceof String) {
			String msgString = (String) msg;
			System.out.println(msgString);
			if (msgString.equals("Client disconnected"))
				clientDisconnected(client);
		}
		if (msg instanceof SendObject<?>) {
			SendObject<?> obj = (SendObject<?>) msg;
			try {
				// Call the handler, which could return any type
				Object result = SendObjectHandler.sendObjectHandle(obj, con);

				// If the result is a SendObject, you can send it directly
				if (result instanceof SendObject<?>) {

					SendObject<?> sendObjectResult = (SendObject<?>) result;
					sendToSingleClient(sendObjectResult, client);
				}
			} catch (Exception e) {
				System.out.println("sendObjectHandle error");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends a object msg to a client
	 * @param msg
	 * @param client 
	 * @return Object
	 */
	public void sendToSingleClient(Object msg, ConnectionToClient client) {
		try {
			if (msg instanceof String) // Sends a String
				client.sendToClient(msg);
			else if (msg instanceof SendObject<?>) {
				SendObject<?> sendObject = (SendObject<?>) msg;
				client.sendToClient(sendObject);
			} else if (msg instanceof ArrayList<?>) {
				ArrayList<?> list = (ArrayList<?>) msg;
				client.sendToClient(list);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Prints to console that the server started
	 * Starts DatabaseDailyUpdater
	 */
	protected void serverStarted() {
		System.out.println(("Server listening for connections on port " + getPort()));
		dailyUpdater = new DatabaseDailyUpdater();
        dailyUpdater.start();
	}

	/**
	 * Stops DatabaseDailyUpdater
	 */
	protected void serverStopped() {
		// Honestly never used it...
		if (dailyUpdater != null) {
            dailyUpdater.stopUpdater(); // Graceful shutdown
        }
		System.out.println("Server has stopped listening for connections.");
	}

	/**
	 * @param client 
	 * Add the client to the list of connected clients Each client
	 * has: id, IP, hostName, status{"Connected","Disconnected"} (all strings)
	 */
	@Override
	public void clientConnected(ConnectionToClient client) {
		if (!clientConnections.contains(client)) {
			clientConnections.add(client);
			List<String> clientInfo = new ArrayList<>();
			clientInfo.add(Long.toString(client.getId()));
			clientInfo.add(client.getInetAddress().getHostAddress());
			clientInfo.add(client.getInetAddress().getCanonicalHostName());
			clientInfo.add("Connected");
			requiredList.add(clientInfo);
			serverController.recievedServerUpdate(requiredList);
			System.out.println(String.format("Client:%s IP:%s HostName:%s %s", clientInfo.get(0), clientInfo.get(1),
					clientInfo.get(2), clientInfo.get(3)));

		} else { // In case the same client tries to reconnect, may never be used
			try {
				clientSetStatus(client, "Connected");
			} catch (Exception e) {
				System.out.println("Connect failed!");
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method is called whenever a client disconnects Remove the
	 * client from the list when they disconnect
	 * @param client 
	 */
	@Override
	public void clientDisconnected(ConnectionToClient client) {
		try {
			clientSetStatus(client, "Disconnected");

		} catch (Exception e) {
			System.out.println("Disconnect failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Updates the status of a client to either "Connected"/"Disconnected"
	 * @param client
	 * @param status
	 * @throws Exception
	 */
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
				serverController.recievedServerUpdate(requiredList);
				// Log the disconnection
				System.out.println(String.format("Client:%s IP:%s HostName:%s %s", string.get(0), string.get(1),
						string.get(2), string.get(3)));
				break;
			}
		}
	}
	
	
    /**
     * The DatabaseDailyUpdater class is responsible for performing daily updates
     * on parking session records in the database. It runs in a separate thread,
     * sleeping until midnight each day to execute its update logic.
     * 
     * Important Note: This class should be used within the context of the BparkServer
     * to ensure proper access to the database connection and other server resources.
     */
    private class DatabaseDailyUpdater extends Thread {
        private final long dailyMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        private volatile boolean running = true;
        
        /**
         * The main execution method of the thread. It calculates the time until
         * the next midnight and sleeps until that time. Upon waking, it performs
         * the daily reset of parking session values.
         */
        @Override
        public void run() {
            while (running) {
                try {
                    long currentTime = System.currentTimeMillis();
                    // Calculate the next midnight (00:00) time
                    var calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to tomorrow
                    calendar.set(Calendar.HOUR_OF_DAY, 0); 
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    long delay = calendar.getTimeInMillis() - currentTime; // Milliseconds until midnight
                    if (delay < 0) {
                        // If the delay is negative, it means we are past midnight
                        // You can either skip sleeping or set a default sleep time
                        System.out.println("Current time is past midnight. Skipping sleep.");
                        delay = dailyMillis; // Set to 24 hours to wait for the next midnight
                    }
                    Thread.sleep(delay); // Sleep exactly until midnight
                    // Perform the operation
                    resetDailyValues();
                } catch (InterruptedException e) {
                    System.out.println("Daily updater interrupted gracefully.");
                    break;
                } catch (Exception e) {
                    System.err.println("Error in daily updater: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        /**
         * Resets the status of all active parking sessions in the database.
         * This method checks each active parking session and marks it as inactive
         * and late if its out time has passed the current time.
         * 
         * @throws Exception if an error occurs while accessing the database or updating records.
         */
        private void resetDailyValues() {
            try {
                List<Parkingsession> parkingsessionList = con.getAllActiveParkingsession();
                for (Parkingsession parkingsession : parkingsessionList) {
					if (parkingsession.getOutTime()!=null&&parkingsession.getOutTime().before(new Date())) {
						parkingsession.setActive(false);
						parkingsession.setLate(true);
						con.updateParkingsessionInDatabase(parkingsession);
					}
				}
            } catch (Exception e) {
                System.err.println("Failed to reset daily values: " + e.getMessage());
            }
        }
        
        /**
         * Stops the daily updater thread gracefully. This method sets the running
         * flag to false and interrupts the thread if it is currently sleeping.
         */
        public void stopUpdater() {
            running = false;
            interrupt(); // Wake up if sleeping
        }
    }

}
