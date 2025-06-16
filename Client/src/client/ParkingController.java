package client;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import logic.*;

public class ParkingController {
	private BParkClient client;
	private subscriber sub;
	private PickUpScreenController pickUpScreen;
	private ParkingSpot spot;
	// private ParkingSpot spot = new ParkingSpot(0, SpotStatus.FREE, null); //
	// testin
	private boolean isAvailable = false;
	// private boolean isUsedCode = false;
	private volatile boolean isUsedCode = true;
	private volatile boolean responseReceived = false;
	private Parkingsession mySession;
	private DropOffScreenController dropOffScreen;
	private TimeExtensionScreenController TimeExtensionScreen;
	private String respond = null;
	private List<Parkingsession> sessions;
	private Parkingsession timeExtendSession;
	private Reservation reservation;

	public void setClient(BParkClient client, subscriber sub) {
		this.client = client;
		this.sub = sub;
	}

	public void handleServerResponse(Object message) {
		if (message instanceof SendObject<?>) {
			SendObject<?> msg = (SendObject<?>) message;
			String action = msg.getObjectMessage();
			System.out.println(action);
			System.out.println(msg.getObj());
			Object obj = msg.getObj();
			if (action.equals("Availability")) {
				isAvailable = (Boolean) obj;
			} else if (action.equals("Parkingsession from code")) {
				setMySession((Parkingsession) obj);
			} else if (action.equals("isUsed")) {
				System.out.println("isused");
				isUsedCode = (Boolean) obj;
			} else if (action.equals("new Spot")) {
				System.out.println("last");
				setSpot((ParkingSpot) obj);
			} else if (action.equals("Time Extension")) {
				respond = (String) obj;
			} else if (action.equals("Active Sessions")) {
				setSessions((List<Parkingsession>) obj);
			} else if (action.equals("Session found")) {
				if (obj instanceof Parkingsession)
					timeExtendSession = (Parkingsession) obj;
			}else if (action.contains("close to current time reservation")) {
				setReservation((Reservation)obj);
			}
			responseReceived = true;
		}
	}
	
	private void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	
	private void setSpot(ParkingSpot spot) {
		this.spot = spot;
	}

	private void setMySession(Parkingsession mySession) {
		this.mySession = mySession;
	}

	public void setSessions(List<Parkingsession> sessions) {
		this.sessions = sessions;
	}

	public void setPickUpScreen(PickUpScreenController pickUpScreen) {
		this.pickUpScreen = pickUpScreen;
	}

	public void setDropOffScreen(DropOffScreenController dropOffScreen) {
		this.dropOffScreen = dropOffScreen;
	}

	public void setTimeExtensionScreen(TimeExtensionScreenController TimeExtensionScreen) {
		this.TimeExtensionScreen = TimeExtensionScreen;
	}

	/*
	 * this method only assigns and returns a spot
	 */
	public ParkingSpot assignParkingSpot() throws Exception {
		responseReceived = false;
		// Send to server
		client.sendToServerSafely(new SendObject<String>("Get", "Free spot")); // V
		// Poll until response is received
		System.out.println("waiting");
		waitForServerResponse(20000);
		System.out.println("ended waiting from server");
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
			if (spot != null)
				System.out.println(spot.getSpotId());
			int parkingCode = generateParkingCode(); // generate parking code
			System.out.println("gen");
			// subscriber1 != null for testing/checking
			if (sub != null) {
				Parkingsession session = new Parkingsession(0, sub.getId(), spot.getSpotId(), parkingCode, inTime,
						outTime, false, false, true);
				// TO DO: send session to database
				client.sendToServerSafely(new SendObject<Parkingsession>("Create new", session));// V
				int spotId = spot.getSpotId();
				System.out.println(spotId + ", " + parkingCode);
				Platform.runLater(() -> {
					System.out.println(spotId + ", " + parkingCode);
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

		client.sendToServerSafely(new SendObject<String>("Check", "Availability")); // V

		// Poll until response is received
		waitForServerResponse(20000);
		// isAvailable = true; // For testing
		return isAvailable;
		// TO DO: maybe we should get this information from the server
	}

	public void requestCarPickUp(int code) throws Exception {
		// the client enters the parking code so they can find the session
		try {
			responseReceived = false;
			sendParkingCode(code);
			System.out.println(code);
			// Poll until response is received
			waitForServerResponse(20000);
			if (mySession != null) { // this should be retrieved from the database using the parking code

				// TO DO: get session from database using the parking code
				if (mySession.getOutTime().before(new Date())) {
					markLateArrival(mySession); // if the session is late, mark it
				}
				mySession.setOutTime(new Date());
				mySession.setActive(false);
				sub.getHistory().add(mySession); // add session to subscriber's history
				// TO DO: send session and subscriber1 to the database for update
				releaseSpot(mySession.getSpotId()); // release the parking spot
				client.sendToServerSafely(new SendObject<Parkingsession>("Update Session", mySession));// V
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
			client.sendToServerSafely(new SendObject<subscriber>("Send late message by Email/SMS", sub));// V
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
			client.sendToServerSafely(new SendObject<subscriber>("Send Parking Code by Email/SMS", sub));// V
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
			client.sendToServerSafely(new SendObject<Integer>("Check received Parking Code", parkingCode)); // V Should
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
		client.sendToServerSafely(new SendObject<Integer>("Upadte spot to Free", spotId)); // V
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
			client.sendToServerSafely(new SendObject<Integer>("Check new Parking Code", parkingCode));// V
			waitForServerResponse(20000);
		} while (isUsedCode); // if used, repeat
		return parkingCode;
	}

	public List<Parkingsession> getActiveParkingSessions() throws Exception {
		responseReceived = false;
		client.sendToServerSafely(new SendObject<Integer>("Get Active Parkingsessions", sub.getId()));
		waitForServerResponse(20000);
		List<Parkingsession> activeSessions = new ArrayList<>();
		activeSessions.addAll(sessions);
		setSessions(null);
		return activeSessions;
	}

	public String ExtendTime(Parkingsession session) throws Exception {
		responseReceived = false;
		client.sendToServerSafely(new SendObject<Parkingsession>("Update time in session", session));
		waitForServerResponse(20000);
		String TimeExtensionRespond = respond;
		respond = null;
		return TimeExtensionRespond;
	}

	private boolean waitForServerResponse(long timeoutMillis) throws Exception {
		long startTime = System.currentTimeMillis();
		while (!responseReceived) {
			try {
				Thread.sleep(10); // sleep briefly to avoid busy-waiting
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// responseReceived = false;
			// Check if we've exceeded the timeout
			if (System.currentTimeMillis() - startTime > timeoutMillis) {

				ShowAlert.showAlert("Timeout Error",
						"The server did not respond within the expected time. Please try again later.",
						AlertType.ERROR);

				throw new Exception("Server response timed out after " + timeoutMillis + " milliseconds");
			}
		}
		return true;
	}

	public Parkingsession getSessionById(int parkingId) throws Exception {
		// TODO Auto-generated method stub
		responseReceived = false;
		client.sendToServerSafely(new SendObject<Integer>("Get Parkingsession", parkingId));
		waitForServerResponse(20000);
		Parkingsession session = null;
		if (timeExtendSession != null) {
			if (timeExtendSession.getSubscriberId() == sub.getId()) {
				session = new Parkingsession(timeExtendSession.getSessionId(), timeExtendSession.getSubscriberId(),
						timeExtendSession.getSpotId(), timeExtendSession.getParkingCode(),
						timeExtendSession.getInTime(), timeExtendSession.getOutTime(), true, false, true);
				timeExtendSession = null;
			} else {
				timeExtendSession = null;
				TimeExtensionScreen.ShowFail("Session was not found");
			}
		} else
			TimeExtensionScreen.ShowFail("Session was not found");
		return session;
	}

	public void implementDropoffUsingReservation() throws Exception {
		// TODO Auto-generated method stub
		reservation = getReservation();
		
		if (reservation != null) {
			if (!isWithin15Minutes(reservation)) {
				reservation.setStartTime(null);
				client.sendToServerSafely(new SendObject<Reservation>("Update",reservation)); // Indicates reservation hasn't been used
				throw new Exception("subscriber didn't come in the right time, cannot create parking session through reservation. Try Submit Parking Request.");
			}
			LocalDateTime inDateTime = LocalDateTime.of(reservation.getDate(), LocalTime.parse(reservation.getStartTime()));
	        LocalDateTime outDateTime = LocalDateTime.of(reservation.getDate(), LocalTime.parse(reservation.getEndTime()));

	        // Convert LocalDateTime to Date
	        Date inTime = Date.from(inDateTime.atZone(ZoneId.systemDefault()).toInstant());
	        Date outTime = Date.from(outDateTime.atZone(ZoneId.systemDefault()).toInstant());
			spot = new ParkingSpot(reservation.getSpot(), SpotStatus.OCCUPIED);
			if (spot != null)
				System.out.println(spot.getSpotId()); // for testing
			int parkingCode = generateParkingCode(); // generate parking code
			// sub != null for testing/checking
			if (sub != null) {
				Parkingsession session = new Parkingsession(0, sub.getId(), spot.getSpotId(), parkingCode, inTime,
						outTime, false, false, true);
				// TO DO: send session to database
				client.sendToServerSafely(new SendObject<Parkingsession>("Create new", session));// V
				reservation.setEndTime(null);
				client.sendToServer(new SendObject<Reservation>("Update",reservation)); // Indicates reservation has been used
				int spotId = spot.getSpotId();
				Platform.runLater(() -> {
					System.out.println(spotId + ", " + parkingCode);
					dropOffScreen.showParkingSuccess(); // show success message
					dropOffScreen.displayAssignedSpot(spotId);
					dropOffScreen.displayParkingCode(parkingCode);
				});
				// reset the controller for next use
				reservation = null;
				mySession = null;
				spot = null;
				isAvailable = false;
				isUsedCode = true;
			}

			else { // if subscriber1 is null, throw an exception
				throw new Exception("Subscriber is null, cannot create parking session.");
			}
		} else {
			throw new Exception("Reservation is null, cannot create parking session.");
		}
	}

	private Reservation getReservation() throws Exception {
		responseReceived = false;
		client.sendToServerSafely(new SendObject("Get close to current time reservation", sub.getId()));
		waitForServerResponse(20000);
		return reservation;
	}
	
	private boolean isWithin15Minutes(Reservation reservation) {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Combine reservation date and start time (parsing time from String)
        LocalDateTime reservationDateTime = reservation.getDate()
                .atTime(LocalTime.parse(reservation.getStartTime(), DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Check if the reservation is within 15 minutes (before or after) from the current time
        long minutesDifference = Math.abs(Duration.between(now, reservationDateTime).toMinutes());

        return minutesDifference <= 15;
    }

}