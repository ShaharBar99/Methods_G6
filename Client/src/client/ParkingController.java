package client;

import java.util.Date;
import java.util.Random;
import javafx.application.Platform;
import logic.*;

public class ParkingController {
	private subscriber subscriber1;
	private static volatile ParkingController parkingControllerObject = null;
	private PickUpScreenController pickUpScreen;
	private static final Object lock = new Object();
	private ParkingSpot spot;
	private boolean isAvailable = false;
	private boolean isUsedCode = false;
	private Parkingsession mySession;
	private DropOffScreenController dropOffScreen;
	private BParkClient client;
	/*
	 * public ParkingController(subscriber subscriber1, DropOffScreen dropOffScreen)
	 * { this.subscriber1 = subscriber1; this.dropOffScreen = dropOffScreen; }
	 */

	private void handleServerMessage(Object msg) {
		Platform.runLater(() -> {
			System.out.println("[Server] " + msg);
			if (msg instanceof subscriber) {
				setSubscriber1((subscriber) msg);
				if (parkingControllerObject == null) {
					initializeParkingController();
				}
			} else if (msg instanceof ParkingSpot) {
				setSpot((ParkingSpot) msg);
			} else if (msg instanceof Parkingsession) {
				setMySession((Parkingsession) msg);
			} else if (msg instanceof String) {
				String str = (String) msg;
				if (str.startsWith("The Availablity is:")) {
					String parts[] = str.split(":");
					if (parts[1].equals("True")) // Note! should make sure what database returns True/False or
													// true/false
						isAvailable = true;
					else
						isAvailable = false;
				} else if (str.startsWith("The suggested Parking Code is:")) {
					String parts[] = str.split(":");
					if (parts[1].equals("Used")) // Note! should make sure what database returns True/False or
													// true/false
						isUsedCode = true;
					else
						isUsedCode = false;
				}
			}
		});
	}

	private ParkingController(BParkClient client) {
		this.client = client;
	}

	private void setSubscriber1(subscriber subscriber1) {
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
	 * ?רק לעכשיו public void setClient(BParkClient client) { // this method is used
	 * to set the client for the controller // it can be used to send messages to
	 * the server // this.client = client; }
	 */

	/*
	 * this method only assigns and returns a spot
	 */
	public ParkingSpot assignParkingSpot() {
		/*
		 * Happens in the server for (ParkingSpot spot : ParkingLot) { if
		 * (spot.getStatus() == SpotStatus.FREE) { spot.setStatus(SpotStatus.OCCUPIED);
		 * return spot; } }
		 */
		if (checkParkingSpotAvailability())
			client.sendToServerSafely("get free spot");
		return spot;
	}

	/*
	 * creates a new parking session and sends it to the database
	 */
	public void confirmDropOff() throws Exception {
		// if there's no available parking spot
		if (!checkParkingSpotAvailability()) {
			dropOffScreen.showNoAvailability(); // show no availability message
			// it stops here
		}
		
		else {
			System.out.println("else");
			Date inTime = new Date();
			Date outTime = new Date(inTime.getTime() + 4 * 60 * 60 * 1000); // 4 hour later
			ParkingSpot spot = assignParkingSpot();
			System.out.println("assignParkingSpot");
			int parkingCode = generateParkingCode(); // generate parking code
			System.out.println("generateParkingCode");
			
			// subscriber1 != null for testing/checking
			if (subscriber1 != null) {
				Parkingsession session = new Parkingsession(0, subscriber1.getId(), spot.getSpotId(), parkingCode,
						inTime, outTime, false, false);
				// TO DO: send session to database
				client.sendToServerSafely(session);
				dropOffScreen.showParkingSuccess(); // show success message	
			}
			
			else { // if subscriber1 is null, throw an exception
				throw new Exception("Subscriber is null, cannot create parking session.");
			}
		}
	}

	public void requestCarPickUp(int code) throws Exception {
		// the client enters the parking code so they can find the session
		try {
			sendParkingCode(code);
			if (mySession != null) { // this should be retrieved from the database using the parking code

				// TO DO: get session from database using the parking code
				if (mySession.getOutTime().before(new Date())) {
					markLateArrival(mySession); // if the session is late, mark it
				}
				subscriber1.getHistory().add(mySession); // add session to subscriber's history
				// TO DO: send session and subscriber1 to the database for update/deletion
				releaseSpot(mySession.getSpotId()); // release the parking spot
				client.sendToServerSafely(subscriber1);
				// reset the controller for next use
				mySession = null;
				spot = null;
				isAvailable = false;
				isUsedCode = false;
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
			client.sendToServerSafely("Send late message by Email/SMS to:" + subscriber1.getId());
		} catch (Exception e) {
			System.err.println("Error getting parking code from server: " + e.getMessage());

			e.printStackTrace();
			throw new Exception();
		}
		pickUpScreen.showLateArrivalMessage(); // show late arrival message

	}

	public void handleLostCode() throws Exception {
		// send to server a message that says "send the code to the user!"
		// the server goes to the database and gets the code and then uses the method
		// for creating a mail
		// should send the code to the user via email
		// בDB שולח לסרבר ומצליב בין הסאבסקראייבר לבין הסשן הכי עדכני שלו
		try {
			client.sendToServerSafely("Send code by Email/SMS to:" + subscriber1.getId());
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
			client.sendToServerSafely(parkingCode); // Should return the Parkingsession from the db and delete it from
													// there
		} catch (Exception e) {
			System.err.println("Error sending parking code to server: " + e.getMessage());

			e.printStackTrace();
			throw new Exception();
		}
	}

	/*
	 * this method is called when the car is picked up gets the parking spot and
	 * releases it
	 */
	public void releaseSpot(int spotId) {
		client.sendToServerSafely("Free spot:" + spotId);
		// TO DO: update the parking spot in the database
	}

	/// זאת דוגמא בינתיים
	// maybe we should move this method to the server?
	//
	public int generateParkingCode() {
		// should we move code from reservation to parkingsession???
		int parkingCode = 0;
		do {
			// generate a random 6-digit code for parking
			Random random = new Random();
			parkingCode = 100000 + random.nextInt(900000); // range: 100000–999999

			client.sendToServerSafely("Check new parking code:" + parkingCode); // we should send the parking code to server
		} // this is a problem because of the time difference between the client and server
		while (!isUsedCode);
		
		return parkingCode;
	}

	/*
	 * checks if there's any free parking spot returns true if it finds one
	 */
	public boolean checkParkingSpotAvailability() {
		/*
		 * Happens in the server for (ParkingSpot spot : ParkingLot) { if
		 * (spot.getStatus() == SpotStatus.FREE) return true; } return false;
		 */
		client.sendToServerSafely("Check Availability");
		return isAvailable;
		// TO DO: maybe we should get this information from the server
	}

}