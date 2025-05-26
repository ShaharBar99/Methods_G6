package client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import boundary.*;
import logic.*;

public class ParkingController {
	ArrayList<ParkingSpot> ParkingLot = new ArrayList<>();
	private subscriber subscriber1;
	private PickUpScreen pickUpScreen;
	private DropOffScreen dropOffScreen;
	//private BParkClient client;
	
	public ParkingController(subscriber subscriber1, DropOffScreen dropOffScreen) {
		this.subscriber1 = subscriber1;
		this.dropOffScreen = dropOffScreen;
	}
	
	public ParkingController(subscriber subscriber1, PickUpScreen pickUpScreen) {
		this.subscriber1 = subscriber1;
		this.pickUpScreen = pickUpScreen;
	}
	
	
	
	/* ?רק לעכשיו
	public void setClient(BParkClient client) {
		// this method is used to set the client for the controller
		// it can be used to send messages to the server
		// this.client = client;
	}*/
	
	
	
	
	/*
	 * this method only assigns and returns a spot
	 */
	public ParkingSpot assignParkingSpot() {
		for(ParkingSpot spot:ParkingLot) {
			if (spot.getStatus() == SpotStatus.FREE) {
				spot.setStatus(SpotStatus.OCCUPIED);
				return spot;
			}
		}
		return null; // if no free spot is available
	}
	
	/*
	 * creates a new parking session and sends it to the database
	 */
	public void confirmDropOff() {
		// if there's no available parking spot
		if (!checkParkingSpotAvailability()) {
			dropOffScreen.showNoAvailability(); // show no availability message
		}
		
		else {
			Date inTime = new Date();
			Date outTime = new Date(inTime.getTime() + 4 * 60 * 60 * 1000); // 4 hour later
			ParkingSpot spot = assignParkingSpot();
			int parkingCode = generateParkingCode(); // generate parking code
			// subscriber1 != null for testing/checking
			if (subscriber1 != null) {
				Parkingsession session = new Parkingsession(0, subscriber1.getId(), spot.getSpotId(), parkingCode,
						inTime, outTime, false, false);
				// TO DO: send session to database
				dropOffScreen.showParkingSuccess(); // show success message
			}
		}
	}

	public void requestCarPickUp() {
		// the client enters the parking code so they can find the session
		//sendParkingCode();
		Parkingsession mySession = null; // this should be retrieved from the database using the parking code
		
		// TO DO: get session from database using the parking code
		if (mySession.getOutTime().before(new Date())) {
			markLateArrival(mySession); // if the session is late, mark it
		}
		subscriber1.getHistory().add(mySession); // add session to subscriber's history
		// TO DO: send session and subscriber1 to the database for update/deletion
		releaseSpot(ParkingLot.get(mySession.getSpotId())); // release the parking spot
		
	}

	
	
	public void markLateArrival(Parkingsession session) {
		// this method is called when the car is picked up late
		session.setLate(true);
		// TO DO: update session in the database
		pickUpScreen.showLateArrivalMessage(); // show late arrival message
		
	}

	public void handleLostCode() {
		//send to server a message that says "send the code to the user!"
		// the server goes to the database and gets the code and then uses the method for creating a mail
		//should send the code to the user via email
		// בDB שולח לסרבר ומצליב בין הסאבסקראייבר לבין הסשן הכי עדכני שלו
		//client.sendToServerSafely(subscriber1.getId());
	}

	/*
	 * this method is called when the car is picked up
	 * when the user wants to pick up the car, and clicks the "submit" button(Avigdor)
	 */
	public void sendParkingCode() {
		//should we move code from reservation to Parkingsession???
	}
	
	/*
	 * this method is called when the car is picked up
	 * gets the parking spot and releases it
	 */
	public void releaseSpot(ParkingSpot spot) {
		spot.setStatus(SpotStatus.FREE);
		// TO DO: update the parking spot in the database
	}

	
	/// זאת דוגמא בינתיים
	// maybe we should move this method to the server?
	//
	public int generateParkingCode() {
		//should we move code from reservation to parkingsession???
		
		// generate a random 6-digit code for parking
		Random random = new Random();
		int parkingCode = 100000 + random.nextInt(900000); // range: 100000–999999
		
		//sendToServerSafely(parkingCode) //we should send the parking code to the server
		return parkingCode;	
	}
	
	
	
	
	/*
	 * checks if there's any free parking spot
	 * returns true if it finds one
	 */
	public boolean checkParkingSpotAvailability() {
		for(ParkingSpot spot:ParkingLot) {
			if (spot.getStatus() == SpotStatus.FREE) 
				return true;
		}
		return false;
		
		// TO DO: maybe we should get this information from the server
	}

}