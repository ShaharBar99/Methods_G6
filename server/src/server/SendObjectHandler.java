package server;

import logic.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SendObjectHandler {
	public static <T extends Serializable, T1 extends Serializable> SendObject<T1> sendObjectHandle(SendObject<T> obj,
			DataBaseQuery con) throws Exception {
		String action = obj.getObjectMessage();
		T object = obj.getObj();
		if (action == null) {
			throw new Exception("Null Action was recieved");
		} else if (object instanceof String) {
			if (action.contains("Get") || action.contains("Check")) {
				Object genericObject = handleStringType(action, (String) object, con);
				return replyDefiner(genericObject);
			}
		} else if (object instanceof Integer) {
			Object genericObject = handleIntegerType(action, (Integer) object, con);
			return replyDefiner(genericObject);
		} else if (action.contains("connect")) {
			Object genericObject = handleGetAction(object, con);
			return replyDefiner(genericObject);
		} else if (action.contains("Update")) {
			handleUpdateAction(object, con);
		} else if (action.contains("Create")) {
			Object genericObject = handleCreateAction(object, con);
			return replyDefiner(genericObject);
		} else if (action.contains("Send")) {
			if (object instanceof subscriber) {
				handleSendAction(action, (subscriber) object, con);
			} else {
				System.err.println("Unknown SendObject received: action = " + action + ", object = " + object);
				throw new Exception("No send option is capable without subscriber object");
			}
		} else {
			System.err.println("Unknown SendObject received: action = " + action + ", object = " + object);
			throw new Exception("No possible classes were chosen");
		}
		return null;
	}

	private static <T1 extends Serializable> SendObject<T1> replyDefiner(Object genericObject) {
		String reply = "recieved object";
		if (genericObject != null) {
			if (genericObject instanceof Boolean) {
				reply = "recieved Boolean";
			} else if (genericObject instanceof Parkingsession) {
				reply = "recieved Parkingsession";
			} else if (genericObject instanceof subscriber) {
				reply = "recieved subscriber";
			} else if (genericObject instanceof ParkingSpot) {
				reply = "recieved ParkingSpot";
			} else if (genericObject instanceof Reservation) {
				reply = "recieved Reservation";
			} else if (genericObject instanceof SendObject) {

				return (SendObject<T1>) genericObject;
			}

		}
		return new SendObject<T1>(reply, (T1) genericObject);
	}

	private static <T extends Serializable, T1 extends Serializable> SendObject<T1> handleIntegerType(String action,
			Integer intObject, DataBaseQuery con) {
		if (action.contains("Check")) {
			if (action.equals("Check new Parking Code")) {
				boolean isUsed = true;
				int code = intObject;
				isUsed = con.checkParkingCodeInAllActiveSessionsInDatabase(code);
				return new SendObject<T1>("isUsed", (T1) (Boolean) isUsed);
			} else if (action.equals("Check recieved Parking Code")) {
				Parkingsession mySession = null;
				// mySession = new Parkingsession(0, 0, 0, 0, null, new Date(), false, false,
				// false); // fake
				int parkingcode = intObject;
				mySession = con.getActiveParkingsessionWithThatCodeFromDatabase(parkingcode);
				return new SendObject<T1>("Parkingsession from code", (T1) (Parkingsession) mySession);
			}
		} else if (action.contains("Update")) {
			if (action.contains("Upadte spot to Free")) {
				int spotId = intObject;
				con.updateSpotToFreeInDatabase(spotId);
			}
		} else if (action.contains("Get")) {
			if (action.contains("SubscribersResesrvations")) {
				List<Reservation> reservationListOfSubscriber = new ArrayList<>();
				reservationListOfSubscriber = con.getReservationListOfSubscriberbyIdFromDatabase(intObject);
				return new SendObject<T1>("Reservation list of subscriber",
						(T1) (List<Reservation>) reservationListOfSubscriber);
			} else if (action.contains("history")) {
				List<Parkingsession> historyParkingsessionsListOfSubscriber = new ArrayList<>();
				int subscriberId = intObject;
				historyParkingsessionsListOfSubscriber = con.gethistoryParkingsessionsListOfSubscriberbyIdFromDatabase(subscriberId);
				if(historyParkingsessionsListOfSubscriber == null) {
					System.out.println("history is null");
				}
				// send back the list
				return new SendObject<T1>("Parkingsession list of subscriber",
						(T1) (List<Parkingsession>) historyParkingsessionsListOfSubscriber);
			}
		}
		return null;
	}

	private static <T extends Serializable> void handleSendAction(String action, subscriber object, DataBaseQuery con)
			throws Exception {
		if (action.contains("Email/SMS")) {
			String to = object.getEmail();
			if (to == null || !to.contains("@"))
				throw new Exception("Subscriber doesn't have a legal Email");
			else if (action.equals("Send late message by Email/SMS")) {
				SendEmail.sendMail(to, "Late retrivel!",
						"Hello,\nWe inform you picked up your vehicle later than expected.\n Note that in the future it might incur additional charges.");
			} else if (action.equals("Send Parking Code by Email/SMS")) {
				int parkingCode = 99999; // fake
				parkingCode = con.getSubscriberLastActiveParkingsessionParkingCode(object.getId());
				if (parkingCode == 99999) {
					throw new Exception("No active parking sessions for the user");
				}
				SendEmail.sendMail(to, "Parking Code reminder", String.format(
						"Hello,\nYour last Parking Code in your last/current active session is: %d\nPlease enter the code you recieved in the app.",
						parkingCode));
			}
		}

	}

	private static <T extends Serializable, T1 extends Serializable> SendObject<T1> handleStringType(String action,
			String object, DataBaseQuery con) throws Exception {
		if (action.contains("Check") && object.contains("Availability")) {
			double availablePrecentage = 0.6; // fake
			availablePrecentage = con.getPrecentageAvailableSpaceFromDatabase();
			if (availablePrecentage > 0.4)
				return new SendObject<T1>("Availability", (T1) (Boolean) true);
			else
				return new SendObject<T1>("Availability", (T1) (Boolean) false);
		} else if (action.contains("Get") && object.equals("Free spot")) {

			ParkingSpot spot;
			// spot = new ParkingSpot(0, SpotStatus.FREE); // fake
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
			spot = con.getFreeParkingSpotFromDatabase(LocalDate.now(), LocalTime.now().format(timeFormatter),
					LocalTime.now().plusHours(4).format(timeFormatter)).get(0);
			if (spot != null) {
				spot.setStatus(SpotStatus.OCCUPIED);
				handleUpdateAction(spot, con);
				return new SendObject<T1>("new Spot", (T1) (ParkingSpot) spot);
			}
		}
		// Default or fallback return value
		return new SendObject<T1>("Invalid request", null);
	}

	/**
	 * @param <T>
	 * @return Object from the database
	 * @throws Exception
	 */
	private static <T extends Serializable, T1 extends Serializable> T1 handleGetAction(T object, DataBaseQuery con)
			throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				subscriber subscriber = null;
				if (user.getCode() > 100000 && user.getName() != null) {
					// Retrieve User from the database using Code
					subscriber = con.getUserUsingCodeFromDatabase(user.getCode(), user.getName());
				} else if (user.getTag() != null) {
					// Retrieve User from the database using Tag
					subscriber = con.getUserUsingTagFromDatabase(user.getTag());
				}
				if (subscriber != null)
					return (T1) subscriber;
			} else {
				return (T1) new SendObject<T1>("Error", (T1) "Name or Code or Tag is incorrect");
			}
			return null;
		} catch (Exception e) {// SQLException e
			throw new Exception("Error retrieving data from database", e);
		}
	}

	private static <T extends Serializable> void handleUpdateAction(T object, DataBaseQuery con) throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				// Update User In the database using recieved object
				con.updateUserInDatabase(user);
			} else if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Update Parkingsession In the database recieved object
				con.updateParkingsessionInDatabase(session);
			} else if (object instanceof ParkingSpot) {
				ParkingSpot spot = (ParkingSpot) object;
				// Update ParkingSpot In the database recieved object
				con.updateParkingSpotInDatabase(spot);
			}
		} catch (Exception e) { // SQLException e
			throw new Exception("Error updating data to database", e);
		}
	}

	private static <T extends Serializable> SendObject<String> handleCreateAction(T object, DataBaseQuery con)
			throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				// Create User In the database using recieved object
				con.createUserInDatabase(user);
				return new SendObject<String>("subscriber", "Created");
			}
			if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Create Parkingsession In the database recieved object
				con.createParkingsessionInDatabase(session);
				return new SendObject<String>("Parkingsession", "Created");
			}
			if (object instanceof Reservation) {
				Reservation reservation = (Reservation) object;
				// create Reservation in the database using recieved object
				ParkingSpot spot;
				spot = new ParkingSpot(0, SpotStatus.FREE); // fake
				spot = con.getFreeParkingSpotFromDatabase(reservation.getDate(), reservation.getStartTime(),
						reservation.getEndTime()).get(0);
				if (spot != null) {
					spot.setStatus(SpotStatus.RESERVED);
					con.updateParkingSpotInDatabase(spot);
					Reservation reservationToBeSent = new Reservation(spot.getSpotId(), reservation.getSubscriberId(),
							reservation.getDate(), reservation.getStartTime(), reservation.getEndTime());
					con.createReservationInDatabase(reservationToBeSent); // needs to be implemented
					return new SendObject<String>("Reservation", "Created");
				} else {
					return new SendObject<String>("Reservation", "Not Created");
				}
			}
		} catch (Exception e) {// SQLException e
			throw new Exception("Error creating data in database", e);

		}
		return new SendObject<String>("Error", "creating data in database");
	}
}
