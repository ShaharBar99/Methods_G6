package client;

import java.util.Date;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import logic.*;

public class ParkingController {
	private subscriber subscriber1;
	private static volatile ParkingController parkingControllerObject = null;
	private PickUpScreenController pickUpScreen;
	private static final Object lock = new Object();
	private ParkingSpot spot;
	// private ParkingSpot spot = new ParkingSpot(0, SpotStatus.FREE, null); //
	// testin
	private boolean isAvailable = false;
	// private boolean isUsedCode = false;
	private volatile boolean isUsedCode = true;
	private volatile boolean responseReceived = false;

	private Parkingsession mySession;
	private DropOffScreenController dropOffScreen;
	private BParkClient client;
	/*
	 * public ParkingController(subscriber subscriber1, DropOffScreen dropOffScreen)
	 * { this.subscriber1 = subscriber1; this.dropOffScreen = dropOffScreen; }
	 */

	/*
	 * this method is used to handle messages from the server
	 */
//	private void handleServerMessage(Object msg) {
//		Platform.runLater(() -> {
//			System.out.println("[Server] " + msg);
//			if (msg instanceof subscriber) {
//				setSubscriber1((subscriber) msg);
//				if (parkingControllerObject == null) {
//					initializeParkingController();
//				}
//			} else if (msg instanceof ParkingSpot) {
//				setSpot((ParkingSpot) msg);
//			} else if (msg instanceof Parkingsession) {
//				setMySession((Parkingsession) msg);
//			} else if (msg instanceof String) {
//				String str = (String) msg;
//				if (str.startsWith("The Availablity is:")) {
//					String parts[] = str.split(":");
//					if (parts[1].equals("True")) // Note! should make sure what database returns True/False or
//													// true/false
//						isAvailable = true;
//					else
//						isAvailable = false;
//				} else if (str.startsWith("The suggested Parking Code is:")) {
//					String parts[] = str.split(":");
//					if (parts[1].equals("Used")) // Note! should make sure what database returns True/False or
//													// true/false
//						isUsedCode = true;
//					else
//						isUsedCode = false;
//				}
//			}
//		});
//	}

	public void handleServerResponse(Object message) {
		if (message instanceof String) {
			String msg = (String) message;
			if (msg.startsWith("Code status:")) {
				isUsedCode = Boolean.parseBoolean(msg.split(":")[1]); // true = used
				responseReceived = true; // signal main thread
			}
			if (msg.startsWith("The Availablity is:")) {
				String parts[] = msg.split(":");
				if (parts[1].equals("True")) // Note! should make sure what database returns True/False or true/false
					isAvailable = true;
				else {
					isAvailable = false;
				}
				responseReceived = true; // signal main thread
			}
		}
	}

	private ParkingController(BParkClient client) {
		this.client = client;
	}

	public void setSubscriber1(subscriber subscriber1) {
		this.subscriber1 = subscriber1;
	}

	private void setSpot(ParkingSpot spot) {
		this.spot = spot;
	}

	private void setMySession(Parkingsession mySession) {
		this.mySession = mySession;
	}

	public void setPickUpScreen(PickUpScreenController pickUpScreen) {
		this.pickUpScreen = pickUpScreen;
	}

	public void setDropOffScreen(DropOffScreenController dropOffScreen) {
		this.dropOffScreen = dropOffScreen;
	}

	// Initialize the ParkingController only after receiving the subscriber
	private void initializeParkingController() {
		try {
			if (parkingControllerObject == null) {
				// Synchronize to ensure thread safety when creating the instance
				synchronized (lock) { // BECAUSE ILIYA SAID
					if (parkingControllerObject == null) {
						parkingControllerObject = new ParkingController(client); // Use the already existing client
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error initializing ParkingController: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Singleton pattern to ensure only one instance of ParkingController
	 */
	public static ParkingController getInstance(BParkClient client) {
		if (parkingControllerObject == null) {
			// Synchronize to ensure thread safety when creating the instance
			synchronized (lock) {
				if (parkingControllerObject == null) {
					parkingControllerObject = new ParkingController(client);
				}
			}
		}
		return parkingControllerObject;
	}

	/*
	 * this method only assigns and returns a spot
	 */
	public ParkingSpot assignParkingSpot() throws Exception {
		responseReceived = false;
		// Send to server
		client.sendToServerSafely(new SendObject<String>("Get ", " Free spot"));
		// Poll until response is received
		waitForServerResponse(15000);
		return spot;
	}

	/*
	 * creates a new parking session and sends it to the database
	 */
	public void confirmDropOff() throws Exception {
		// if there's no available parking spot
		if (!checkParkingSpotAvailability()) {
			Platform.runLater(() -> {
				dropOffScreen.showNoAvailability(); // show no availability message
			});
		} else {
			Date inTime = new Date();
			Date outTime = new Date(inTime.getTime() + 4 * 60 * 60 * 1000); // 4 hour later
			ParkingSpot spot = assignParkingSpot();
			int parkingCode = generateParkingCode(); // generate parking code

			// subscriber1 != null for testing/checking
			if (subscriber1 != null) {
				Parkingsession session = new Parkingsession(0, subscriber1.getId(), spot.getSpotId(), parkingCode,
						inTime, outTime, false, false, true);
				// TO DO: send session to database
				client.sendToServerSafely(new SendObject<Parkingsession>("Create new", session));
				int spotId = spot.getSpotId();
				Platform.runLater(() -> {
					dropOffScreen.showParkingSuccess(); // show success message
					dropOffScreen.displayAssignedSpot(spotId);
					dropOffScreen.displayParkingCode(parkingCode);
				});
				// reset the controller for next use
				mySession = null;
				spot = null;
				isAvailable = false;
				isUsedCode = true;
			}

			else { // if subscriber1 is null, throw an exception
				throw new Exception("Subscriber is null, cannot create parking session.");
			}
		}
	}

	/*
	 * checks if there's any free parking spot returns true if it finds one
	 */
	public boolean checkParkingSpotAvailability() throws Exception {
		/*
		 * Happens in the server for (ParkingSpot spot : ParkingLot) { if
		 * (spot.getStatus() == SpotStatus.FREE) return true; } return false;
		 */

		// Reset flags
		isAvailable = false; // Assume no availability initially;
		responseReceived = false;

		client.sendToServerSafely(new SendObject<String>("Check ", "Availability"));

		// Poll until response is received
		waitForServerResponse(15000);

		// isAvailable = true; // For testing
		return isAvailable;
		// TO DO: maybe we should get this information from the server
	}

	public void requestCarPickUp(int code) throws Exception {
		// the client enters the parking code so they can find the session
		try {
			responseReceived = false;
			sendParkingCode(code);
			// Poll until response is received
			waitForServerResponse(15000);
			if (mySession != null) { // this should be retrieved from the database using the parking code

				// TO DO: get session from database using the parking code
				if (mySession.getOutTime().before(new Date())) {
					markLateArrival(mySession); // if the session is late, mark it
				}
				mySession.setActive(false);
				subscriber1.getHistory().add(mySession); // add session to subscriber's history
				// TO DO: send session and subscriber1 to the database for update
				releaseSpot(mySession.getSpotId()); // release the parking spot
				client.sendToServerSafely(new SendObject<Parkingsession>("Update Session", mySession));
				client.sendToServerSafely(new SendObject<subscriber>("Update Subscriber", subscriber1));
				// reset the controller for next use
				mySession = null;
				spot = null;
				isAvailable = false;
				isUsedCode = true;
			} else
				throw new Exception();
		} catch (Exception e) {
			// Log or handle exception
			System.err.println("Error processing parking code: " + e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}
	}

	public void markLateArrival(Parkingsession session) throws Exception {
		// this method is called when the car is picked up late
		session.setLate(true);
		// TO DO: update session in the database/send message to client about being late
		try {
			client.sendToServerSafely(new SendObject<subscriber>("Send late message by Email/SMS", subscriber1));
		} catch (Exception e) {
			System.err.println("Error getting parking code from server: " + e.getMessage());

			e.printStackTrace();
			throw new Exception();
		}
		Platform.runLater(() -> {
			pickUpScreen.showLateArrivalMessage(); // show late arrival message
		});
	}

	public void handleLostCode() throws Exception {
		// send to server a message that says "send the code to the user!"
		// the server goes to the database and gets the code and then uses the method
		// for creating a mail
		// should send the code to the user via email
		// בDB שולח לסרבר ומצליב בין הסאבסקראייבר לבין הסשן הכי עדכני שלו
		try {
			client.sendToServerSafely(new SendObject<subscriber>("Send Parking Code by Email/SMS", subscriber1));
		} catch (Exception e) {
			System.err.println("Error getting parking code from server: " + e.getMessage());

			e.printStackTrace();
			throw new Exception();
		}
	}

	/*
	 * this method is called when the car is picked up when the user wants to pick
	 * up the car, and clicks the "submit" button(Avigdor)
	 */
	public void sendParkingCode(int parkingCode) throws Exception {
		// should we move code from reservation to Parkingsession???
		try {
			client.sendToServerSafely(new SendObject<Integer>("Check recieved Parking Code", parkingCode)); // Should
																											// return
																											// the
																											// Parkingsession
																											// from the
																											// db and
																											// setActive(false)

		} catch (Exception e) {
			System.err.println("Error sending parking code to server: " + e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}
	}

	/*
	 * This method is called when the car is picked up gets the parking spot and
	 * releases it
	 */
	public void releaseSpot(int spotId) {
		client.sendToServerSafely(new SendObject<Integer>("Free spot", spotId));
		// TO DO: update the parking spot in the database
	}

	public int generateParkingCode() throws Exception {
		int parkingCode;

		do {
			Random random = new Random(); // Generate code
			parkingCode = 100000 + random.nextInt(900000);

			// Reset flags
			isUsedCode = true;
			responseReceived = false;

			// Send to server
			client.sendToServerSafely(new SendObject<Integer>("Check new Parking Code", parkingCode));
			waitForServerResponse(15000);
		} while (isUsedCode); // if used, repeat

		return parkingCode;
	}

	private boolean waitForServerResponse(long timeoutMillis) throws Exception {
		long startTime = System.currentTimeMillis();
		while (!responseReceived) {
			try {
				Thread.sleep(10); // sleep briefly to avoid busy-waiting
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Check if we've exceeded the timeout
			if (System.currentTimeMillis() - startTime > timeoutMillis) {
				Platform.runLater(() -> {
					ShowAlert.showAlert("Timeout Error",
							"The server did not respond within the expected time. Please try again later.",
							AlertType.ERROR);
				});
				throw new Exception("Server response timed out after " + timeoutMillis + " milliseconds");
			}
		}
		return true;
	}

}