package logic;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Simple POJO to hold one reservation record.
 */
public class Reservation implements Serializable{
	private final int id;
	private final int subscriberId;
	private final int spotId;
	private final LocalDate date;
	private String startTime;
	private String endTime;
 
	public Reservation(int id,int spotId,int subscriber_id , LocalDate date, String startTime, String endTime) {
		this.id = id;
		this.subscriberId = subscriber_id;
		this.spotId = spotId;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}


	public int getId() {
		return id;
	}
	
	public int getSubscriberId() {
		return subscriberId;
	}


	public int getSpot() {
		return spotId;
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
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
