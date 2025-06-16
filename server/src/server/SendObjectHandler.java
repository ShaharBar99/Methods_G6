package server;

import logic.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SendObjectHandler {
	public static <T extends Serializable, T1 extends Serializable> SendObject<T1> sendObjectHandle(SendObject<T> obj,
			DataBaseQuery con) throws Exception {
		String action = obj.getObjectMessage();
		T object = obj.getObj();
		if (action == null) {
			throw new Exception("Null Action was received");
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
			if (action.contains("time in session")) {
				Parkingsession session = (Parkingsession) obj.getObj();
				if (con.checkExtendTimeParkingsessionWithAllReservations(session)) {
					handleUpdateAction(object, con);
					return new SendObject<T1>("Time Extension", (T1) "Accapted");
				} else {
					return new SendObject<T1>("Time Extension", (T1) "Not Accapted");
				}

			} else {
				handleUpdateAction(object, con);
			}
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
		String reply = "received object";
		if (genericObject != null) {
			if (genericObject instanceof Boolean) {
				reply = "received Boolean";
			} else if (genericObject instanceof Parkingsession) {
				reply = "received Parkingsession";
			} else if (genericObject instanceof subscriber) {
				reply = "received subscriber";
			} else if (genericObject instanceof ParkingSpot) {
				reply = "received ParkingSpot";
			} else if (genericObject instanceof Reservation) {
				reply = "received Reservation";
			} else if (genericObject instanceof SendObject) {

				System.out.println(((SendObject<T1>) genericObject).getObjectMessage());
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
			} else if (action.equals("Check received Parking Code")) {
				Parkingsession mySession = null;
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
				historyParkingsessionsListOfSubscriber = con
						.gethistoryParkingsessionsListOfSubscriberbyIdFromDatabase(subscriberId);
				if (historyParkingsessionsListOfSubscriber == null) {
					System.out.println("history is null");
				}
				// send back the list
				return new SendObject<T1>("Parkingsession list of subscriber",
						(T1) (List<Parkingsession>) historyParkingsessionsListOfSubscriber);
			} else if (action.contains("Active Parkingsessions")) {
				List<Parkingsession> activeParkingsessionsListOfSubscriber = new ArrayList<>();
				int subscriberId = intObject;
				activeParkingsessionsListOfSubscriber = con
						.getActiveParkingsessionsListOfSubscriberbyIdFromDatabase(subscriberId);
				// send back the list
				return new SendObject<T1>("Active Sessions",
						(T1) (List<Parkingsession>) activeParkingsessionsListOfSubscriber);
			} else if (action.contains("Parkingsession")) {
				Parkingsession session = null;
				int sessionId = intObject;
				session = con.getParkingsessionById(sessionId);
				if (session != null) {
					return new SendObject<T1>("Session found", (T1) session);
				} else {
					return new SendObject<T1>("Session found:", (T1) "False");
				}
			} else if(action.contains("close to current time reservation")) {
				Reservation reservation = null;
				int subscriberId = intObject;
				// fake
				//reservation = new Reservation(109, 2001, LocalDate.of(2025, 5, 14), "10:00:00", "12:00:00");
				// end fake
				reservation = con.getReservationCloseToCurrentTimeOfSubscriber(subscriberId);
				return new SendObject<T1>("Received close to current time reservation",(T1)reservation);
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
						"Hello,\nYour last Parking Code in your last/current active session is: %d\nPlease enter the code you received in the app.",
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
		} else if (action.contains("Get")) {
			if (object.equals("Free spot")) {
				ParkingSpot spot;
				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
				spot = con.getFreeParkingSpotFromDatabase(LocalDate.now(), LocalTime.now().format(timeFormatter),
						LocalTime.now().plusHours(4).format(timeFormatter)).get(0);
				if (spot != null) {
					spot.setStatus(SpotStatus.OCCUPIED);
					handleUpdateAction(spot, con);
					return new SendObject<T1>("new Spot", (T1) (ParkingSpot) spot);
				}
			} else if (object.equals("all reservations")) {
				List<Reservation> allReservationList = new ArrayList<>();
				allReservationList = con.getAllReservationList();
				return new SendObject<T1>("Received all reservations", (T1) (List<Reservation>) allReservationList);
			} else if (object.equals("all subscribers")) {
				List<subscriber> allSubscribersList = new ArrayList<subscriber>();
				allSubscribersList = con.getAllSubscribersList();
				return new SendObject<T1>("Received all subscribers", (T1) (List<subscriber>) allSubscribersList);
			} else if (object.equals("active parking sessions")) {
				List<Parkingsession> allActiveParkingsessions = new ArrayList<Parkingsession>();
				allActiveParkingsessions = con.getAllActiveParkingsession();
				return new SendObject<T1>("Received active parking sessions",
						(T1) (List<Parkingsession>) allActiveParkingsessions);
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
				// Update User In the database using received object
				con.updateUserInDatabase(user);
			} else if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Update Parkingsession In the database received object
				con.updateParkingsessionInDatabase(session);
			} else if (object instanceof ParkingSpot) {
				ParkingSpot spot = (ParkingSpot) object;
				// Update ParkingSpot In the database received object
				con.updateParkingSpotInDatabase(spot);
			} else if(object instanceof Reservation) {
				Reservation reservation = (Reservation)object;
				// Update Reservation In the database received object
				//con.updateReservationInDatabase(reservation);
			}
		} catch (Exception e) { // SQLException e
			throw new Exception("Error updating data to database", e);
		}
	}

	private static <T extends Serializable, T1 extends Serializable> SendObject<T1> handleCreateAction(T object,
			DataBaseQuery con) throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				boolean checkEmail = false;
				checkEmail = con.checkUserEmailDuplicates(user);
				if (!checkEmail) {
					Object codeAndTag[] = new Object[2];
					codeAndTag[0] = (Integer) generateCode(con);
					codeAndTag[1] = (String) generateTag(con);
					user.setCode((Integer) codeAndTag[0]);
					user.setTag((String) codeAndTag[1]);
					// Create User In the database using received object
					con.createUserInDatabase(user);
					SendEmail.sendMail(user.getEmail(), "Welcome to BPark", String.format(
							"Hello %s!\nWe're happy you decided to join BPark. Here are your Log in options:\nCode:%d\nTag:%s",
							user.getName(), (Integer) codeAndTag[0], (String) codeAndTag[1]));
					return new SendObject<T1>("Subscriber created", (T1) (Object[]) codeAndTag);
				} else {
					return new SendObject<T1>("Subscriber not created", (T1) (String) "Email exists");
				}
			}
			if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Create Parkingsession In the database received object
				con.createParkingsessionInDatabase(session);
				return new SendObject<T1>("Parkingsession", (T1) (String) "Created");
			}
			if (object instanceof Reservation) {
				Reservation reservation = (Reservation) object;
				// create Reservation in the database using received object
				ParkingSpot spot;
				spot = con.getFreeParkingSpotFromDatabase(reservation.getDate(), reservation.getStartTime(),
						reservation.getEndTime()).get(0);
				if (spot != null) {
					spot.setStatus(SpotStatus.RESERVED);
					con.updateParkingSpotInDatabase(spot);
					Reservation reservationToBeSent = new Reservation(spot.getSpotId(), reservation.getSubscriberId(),
							reservation.getDate(), reservation.getStartTime(), reservation.getEndTime());
					con.createReservationInDatabase(reservationToBeSent); // needs to be implemented
					return new SendObject<T1>("Reservation", (T1) (String) "Created");
				} else {
					return new SendObject<T1>("Reservation", (T1) (String) "Not Created");
				}
			}
		} catch (Exception e) {// SQLException e
			throw new Exception("Error creating data in database", e);

		}
		return new SendObject<T1>("Error", (T1) (String) "creating data in database");
	}

	private static String generateTag(DataBaseQuery con) {
		Random random = new Random();
		StringBuilder tag;
		boolean isDifferent = false;
		do {
			tag = new StringBuilder();
			for (int i = 0; i < 12; i++) {
				int byteValue = random.nextInt(256); // 0 to 255
				tag.append(String.format("%02X", byteValue));
			}
			isDifferent = con.checkRFIDTagDifferentFromAllSubscribers(tag.toString());
		} while (!isDifferent);
		return tag.toString();
	}

	private static Integer generateCode(DataBaseQuery con) {
		int code;
		Random random = new Random(); // Generate code
		boolean isDifferent = false;
		do {
			code = 100000 + random.nextInt(900000);
			isDifferent = con.checkCodeDifferentFromAllSubscribers(code);
		} while (!isDifferent);
		return code;
	}
}
