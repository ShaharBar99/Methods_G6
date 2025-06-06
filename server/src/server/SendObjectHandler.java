package server;

import logic.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SendObjectHandler {
	public static <T extends Serializable, T1 extends Serializable> SendObject<T1> sendObjectHandle(SendObject<T> obj)
			throws Exception {
		String action = obj.getObjectMessage();
		T object = obj.getObj();
		if (action == null) {
			throw new Exception("Null Action was recieved");
		} else if (object instanceof String) {
			if (action.contains("Get") || action.contains("Check")) {
				Object genericObject = handleStringType(action, (String) object);
				return replyDefiner(genericObject);
			}
		} else if (object instanceof Integer) {
			Object genericObject = handleIntegerType(action, (Integer) object);
			return replyDefiner(genericObject);
		} else if (action.contains("Get")) {
			Object genericObject = handleGetAction(object);
			return replyDefiner(genericObject);
		} else if (action.contains("Update")) {
			handleUpdateAction(object);
		} else if (action.contains("Create")) {
			Object genericObject = handleCreateAction(object);
			return replyDefiner(genericObject);
		} else if (action.contains("Delete")) {
			Boolean isExist = (Boolean) handleCheckAction(object);
			if (isExist) {
				handleDeleteAction(object);
			}
		} else if (action.contains("Check")) {
			Boolean isExist = handleCheckAction(object);
			return replyDefiner(isExist);
		} else if (action.contains("Send")) {
			if (object instanceof subscriber) {
				handleSendAction(action, (subscriber) object);
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
			Integer object) {
		if (action.contains("Check")) {
			if (action.equals("Check new Parking Code")) {
				boolean isUsed = false; // fake
				int code = object;
				// isUsed = checkParkingCodeInAllActiveSessionsInDatabase(code);
				return new SendObject<T1>("isUsed", (T1) (Boolean) isUsed);
			} else if (action.equals("Check recieved Parking Code")) {
				Parkingsession mySession = null;
				mySession = new Parkingsession(0, 0, 0, 0, null, new Date(), false, false, false); // fake
				int parkingcode = object;
				// mySession = getActiveParkingsessionWithThatCodeFromDatabase(parkingcode);
				return new SendObject<T1>("Parkingsession from code", (T1) (Parkingsession) mySession);
			}
		} else if (action.contains("Update")) {
			if (action.contains("Upadte spot to Free")) {
				int spotId = object;
				// updateSpotToFreeInDatabase(object);
			}
		} else if (action.contains("Get")) {
			if (action.contains("SubscribersResesrvations")) {
				List<Reservation> reservationListOfSubscriber = new ArrayList<>();
				// reservationListOfSubscriber =
				// getReservationListOfSubscriberbyIdFromDatabase(object);
				// fakes to check if a list has been created and transfered to controller
				reservationListOfSubscriber.add(new Reservation(1, 1001, LocalDate.of(2025, 6, 5), "09:00", "12:00"));
				reservationListOfSubscriber.add(new Reservation(2, 1002, LocalDate.of(2025, 6, 5), "14:00", "16:00"));
				reservationListOfSubscriber.add(new Reservation(1, 1003, LocalDate.of(2025, 6, 6), "10:00", "12:00"));
				reservationListOfSubscriber.add(new Reservation(3, 1004, LocalDate.of(2025, 6, 6), "13:00", "15:00"));
				return new SendObject<T1>("Reservation list of subscriber",
						(T1) (List<Reservation>) reservationListOfSubscriber);
			}
			if (action.contains("history")) {
				List<Parkingsession> historyParkingsessionsListOfSubscriber = new ArrayList<>();
				// historyParkingsessionsListOfSubscriber =
				// gethistoryParkingsessionsListOfSubscriberbyIdFromDatabase(object);
				// fakes
				int commonSubscriberId = 101; // same subscriber ID for all sessions
				Calendar cal = Calendar.getInstance();
				cal.set(2025, Calendar.JUNE, 1, 9, 0);
				Date inTime1 = cal.getTime();
				cal.set(2025, Calendar.JUNE, 1, 12, 0);
				Date outTime1 = cal.getTime();
				historyParkingsessionsListOfSubscriber.add(
						new Parkingsession(1, commonSubscriberId, 10, 1001, inTime1, outTime1, false, false, false));
				cal.set(2025, Calendar.JUNE, 2, 14, 30);
				Date inTime2 = cal.getTime();
				cal.set(2025, Calendar.JUNE, 2, 18, 0);
				Date outTime2 = cal.getTime();
				historyParkingsessionsListOfSubscriber.add(
						new Parkingsession(2, commonSubscriberId, 11, 1002, inTime2, outTime2, true, false, false));
				cal.set(2025, Calendar.JUNE, 3, 8, 0);
				Date inTime3 = cal.getTime();
				cal.set(2025, Calendar.JUNE, 3, 11, 15);
				Date outTime3 = cal.getTime();
				historyParkingsessionsListOfSubscriber.add(
						new Parkingsession(3, commonSubscriberId, 12, 1003, inTime3, outTime3, false, true, false));
				// send back the list
				return new SendObject<T1>("Parkingsession list of subscriber",
						(T1) (List<Parkingsession>) historyParkingsessionsListOfSubscriber);
			}
		}
		return null;
	}

	private static <T extends Serializable> void handleSendAction(String action, subscriber object) throws Exception {
		if (action.contains("Email/SMS")) {
			String to = object.getEmail();
			if (to == null || !to.contains("@"))
				throw new Exception("Subscriber doesn't have a legal Email");
			else if (action.equals("Send late message by Email/SMS")) {
				SendEmail.sendMail(to, "Late retrivel!",
						"Hello,\nWe inform you picked up your vehicle later than expected.\n Note that in the future it might incur additional charges.");
			} else if (action.equals("Send Parking Code by Email/SMS")) {
				int parkingCode = 99999; // fake
				// parkingCode = getSubscriberLastParkingCode();
				// if(parkingCode==99999){throw new Exception("No active parking sessions for
				// the user");}
				SendEmail.sendMail(to, "Parking Code reminder", String.format(
						"Hello,\nYour last Parking Code in your last/current active session is: %d\nPlease enter the code you recieved in the app.",
						parkingCode));
			}
		}

	}

	private static <T extends Serializable, T1 extends Serializable> SendObject<T1> handleStringType(String action,
			String object) throws Exception {
		if (action.contains("Check") && object.contains("Availability")) {
			Boolean isAvailable;
			isAvailable = true; // fake
			// isAvailable = getPrecentageAvailableSpaceFromDatabase();
			return new SendObject<T1>("Availability", (T1) (Boolean) isAvailable);
		} else if (action.contains("Get") && object.equals("Free spot")) {

			ParkingSpot spot;
			spot = new ParkingSpot(0, SpotStatus.OCCUPIED); // fake
			// spot = getFreeSpotFromDatabase();
			spot.setStatus(SpotStatus.OCCUPIED);
			handleUpdateAction(spot);
			return new SendObject<T1>("new Spot", (T1) (ParkingSpot) spot);
		}
		// Default or fallback return value
		return new SendObject<T1>("Invalid request", null);
	}

	/**
	 * @param <T>
	 * @return Object from the database
	 * @throws Exception
	 */
	private static <T extends Serializable, T1 extends Serializable> T1 handleGetAction(T object) throws Exception {
		try {
			if (object instanceof subscriber) {
				boolean isUser = handleCheckAction((subscriber) object);
				subscriber user = (subscriber) object;
				if (isUser) {
					if (user.getCode() > 100000 && user.getName() != null) {
						// Retrieve User from the database using Code
						// return getUserUsingCodeFromDatabase(user.getCode(),user.getName());
					} else if (user.getTag() != null) {
						// Retrieve User from the database using Tag
						// return getUserUsingTagFromDatabase(user.getTag());
					}
				} else {
					return (T1) new SendObject<T1>("Error", (T1) "Name or Code or Tag is incorrect");
				}
			}
			if (object instanceof Parkingsession) {
				// Retrieve Parkingsession from the database using ID
				// return getParkingsessionFromDatabase(session.getSessionId());
			}
			if (object instanceof ParkingSpot) {
				// Retrieve ParkingSpot from the database using ID
				// return getParkingSpotFromDatabase(spot.SpotId());
			}
			if (object instanceof Reservation) {
				// Retrieve Reservation from the database using ID
				// return getReservationFromDatabase(reservation.getId());
			}
			return null;
		} catch (Exception e) {// SQLException e
			throw new Exception("Error retrieving data from database", e);
		}
	}

	private static <T extends Serializable> void handleUpdateAction(T object) throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				// Update User In the database using recieved object
				// updateUserInDatabase(user);
			}
			if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Update Parkingsession In the database recieved object
				// updateParkingsessionInDatabase(session);
			}
			if (object instanceof ParkingSpot) {
				ParkingSpot spot = (ParkingSpot) object;
				// Update Parkingsession In the database recieved object
				// updateParkingsessionInDatabase(spot);
			}
			if (object instanceof Reservation) {
				Reservation reservation = (Reservation) object;
				// update Reservation in the database using recieved object
				// updateReservationInDatabase(reservation);
			}
		} catch (Exception e) { // SQLException e
			throw new Exception("Error updating data to database", e);
		}
	}

	private static <T extends Serializable> SendObject<String> handleCreateAction(T object) throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				// Create User In the database using recieved object
				// createUserInDatabase(user);
				return new SendObject<String>("subscriber", "Created");
			}
			if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Create Parkingsession In the database recieved object
				// createParkingsessionInDatabase(session);
				return new SendObject<String>("Parkingsession", "Created");
			}
			if (object instanceof Reservation) {
				Reservation reservation = (Reservation) object;
				// create Reservation in the database using recieved object
				// int resId = getLastReservationId() + 1;
				ParkingSpot spot;
				spot = new ParkingSpot(0, SpotStatus.FREE); // fake
				// spot = getFreeParkingSpotFromDatabase();
				spot.setStatus(SpotStatus.RESERVED);
				// updateParkingSpotInDatabase(spot);
				List<Reservation> reservationList = new ArrayList<>();
				// reservationList = getAllReservationsFromDatabase();

				boolean isOverlapping = isReservationOverlapping(reservation, reservationList);
				if (!isOverlapping) {
					Reservation reservationToBeSent = new Reservation(spot.getSpotId(), reservation.getSubscriberId(),
							reservation.getDate(), reservation.getStartTime(), reservation.getEndTime());
					// createReservationInDatabase(reservationToBeSent);
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

	/**
	 * Check if a reservation overlaps with existing reservations for the same spot
	 * and date.
	 *
	 * @param reservation     the reservation to check
	 * @param reservationList the list of existing reservations
	 * @return true if the reservation overlaps with an existing one, false
	 *         otherwise
	 */
	public static boolean isReservationOverlapping(Reservation reservation, List<Reservation> reservationList) {
		try {
			int startTime[] = convertTimeToIntegers(reservation.getStartTime());
			int endTime[] = convertTimeToIntegers(reservation.getEndTime());
			int startMinutes = convertToMinutes(startTime);
			int endMinutes = convertToMinutes(endTime);
			for (Reservation reservation2 : reservationList) {
				int startTime2[] = convertTimeToIntegers(reservation2.getStartTime());
				int endTime2[] = convertTimeToIntegers(reservation2.getEndTime());
				int existingStartMinutes = convertToMinutes(startTime2);
				int existingEndMinutes = convertToMinutes(endTime2);
				if (reservation2.getDate().equals(reservation.getDate())
						&& reservation2.getSpot() == reservation.getSpot()) {
					// Check if times overlap: start and end time should not overlap
					if ((startMinutes >= existingStartMinutes && startMinutes < existingEndMinutes)
							|| (endMinutes > existingStartMinutes && endMinutes <= existingEndMinutes)
							|| (startMinutes <= existingStartMinutes && endMinutes >= existingEndMinutes)) {
						return true; // Overlap detected
					}
				}
			}
		} catch (Exception e) {
			System.out.println("isReservationOverlapping Error");
		}
		return false;
	}

	public static int[] convertTimeToIntegers(String timeString) {
		// Split the time string at the colon ":"
		String[] parts = timeString.split(":");

		// Parse hours and minutes as integers
		int hour = Integer.parseInt(parts[0]);
		int minute = Integer.parseInt(parts[1]);

		// Return the integers as an array
		return new int[] { hour, minute };
	}

	/**
	 * Convert hours and minutes into the total number of minutes since midnight.
	 * This allows for easier comparison.
	 *
	 * @param timeArray the array of integers [hour, minute]
	 * @return the total number of minutes since midnight
	 */
	public static int convertToMinutes(int[] timeArray) {
		return timeArray[0] * 60 + timeArray[1];
	}

	private static <T extends Serializable> void handleDeleteAction(T object) throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				// Delete User In the database using recieved object
				// deleteUserInDatabase(user);
			}
			if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Delete Parkingsession In the database recieved object
				// deleteParkingsessionInDatabase(session);
			}
			if (object instanceof Reservation) {
				Reservation reservation = (Reservation) object;
				// Delete Reservation in the database using recieved object
				// deleteReservationInDatabase(reservation);
			}
		} catch (Exception e) {// SQLException e
			throw new Exception("Error deleting data from database", e);
		}
	}

	private static <T extends Serializable> Boolean handleCheckAction(T object) throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				boolean isUser = false;
				if (user.getCode() > 100000 && user.getName() != null) {
					// Retrieve User from the database using Code
					// return checkUserUsingCodeFromDatabase(user.getCode(),user.getName());
				} else if (user.getTag() != null) {
					// Retrieve User from the database using Tag
					// return checkUserUsingTagFromDatabase(user.getTag());
				}
				return isUser;
			}
			if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// check Parkingsession In the database recieved object
				// checkParkingsessionInDatabase(session.getId());
				return true;
			}
			if (object instanceof ParkingSpot) {
				ParkingSpot spot = (ParkingSpot) object;
				// check Parkingsession In the database recieved object
				// checkParkingsessionInDatabase(spot.getId());
				return true;
			}
			if (object instanceof Reservation) {
				Reservation reservation = (Reservation) object;
				// check Reservation in the database using recieved object
				// checkReservationInDatabase(reservation.getId());
				return true;
			}
			return false;
		} catch (Exception e) {// SQLException e
			throw new Exception("Error checking data from database", e);
		}
	}
}
