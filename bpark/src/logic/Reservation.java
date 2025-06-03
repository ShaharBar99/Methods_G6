package logic;

import java.time.LocalDate;

/**
 * Simple POJO to hold one reservation record.
 */
public class Reservation {
	private final int id;
	private final String spot;
	private final LocalDate date;
	private final String startTime;
	private final String endTime;

	public Reservation(int id, String spot, LocalDate date, String startTime, String endTime) {
		this.id = id;
		this.spot = spot;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	
	public int getId() {
		return id;
	}

	public String getSpot() {
		return spot;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}
}
