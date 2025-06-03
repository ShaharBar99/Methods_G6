package server;

import logic.*;
import java.io.Serializable;
import java.sql.SQLException;

public class SendObjectHandler {
	public static <T extends Serializable, T1 extends Serializable> SendObject<T1> sendObjectHandle(SendObject<T> obj)
			throws Exception {
		String action = obj.getObjectMessage();
		T object = obj.getObj();
		if (action == null) {
			throw new Exception("Null Action was recieved");
		}
		if (action.contains("Get")) {
			Object genericObject = handleGetAction(object);
			return new SendObject<T1>("recieved object", (T1) genericObject);
		} else if (action.contains("Update")) {
			handleUpdateAction(object);
		} else if (action.contains("Create")) {
			handleCreateAction(object);
		} else if (action.contains("Delete")) {
			Boolean isExist = (Boolean) handleCheckAction(object);
			if (isExist) {
				handleDeleteAction(object);
			}
		} else if (action.contains("Check")) {
			Boolean isExist = handleCheckAction(object);
			return new SendObject<T1>("recieved boolean", (T1) isExist);
		} else {
			return new SendObject<T1>("recieved boolean", null);
		}
		throw new Exception("No possible classes were chosen");
	}

	/**
	 * @param <T>
	 * @return Object from the database
	 * @throws Exception
	 */
	private static <T extends Serializable, T1 extends Serializable> T1 handleGetAction(T object) throws Exception {
		try {
			if (object instanceof subscriber) {
				// Retrieve User from the database using ID
				// return getUserFromDatabase(user.getId());
			}
			if (object instanceof Parkingsession) {
				// Retrieve Parkingsession from the database using ID
				// return getParkingsessionFromDatabase(session.getSessionId());
			}
			if (object instanceof ParkingSpot) {
				// Retrieve Parkingsession from the database using ID
				// return getParkingsessionFromDatabase(spot.SpotId());
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

	private static <T extends Serializable> void handleCreateAction(T object) throws Exception {
		try {
			if (object instanceof subscriber) {
				subscriber user = (subscriber) object;
				// Create User In the database using recieved object
				// createUserInDatabase(user);
			}
			if (object instanceof Parkingsession) {
				Parkingsession session = (Parkingsession) object;
				// Create Parkingsession In the database recieved object
				// createParkingsessionInDatabase(session);
			}
			if (object instanceof ParkingSpot) {
				ParkingSpot spot = (ParkingSpot) object;
				// Create Parkingsession In the database recieved object
				// createParkingsessionInDatabase(spot);
			}
			if (object instanceof Reservation) {
				Reservation reservation = (Reservation) object;
				// create Reservation in the database using recieved object
				// createReservationInDatabase(reservation);
			}
		} catch (Exception e) {// SQLException e
			throw new Exception("Error creating data in database", e);
		}
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
			if (object instanceof ParkingSpot) {
				ParkingSpot spot = (ParkingSpot) object;
				// Delete Parkingsession In the database recieved object
				// deleteParkingsessionInDatabase(spot);
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
				// check User In the database using recieved object Id
				// checkUserInDatabase(user.getId());
				return true;
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
