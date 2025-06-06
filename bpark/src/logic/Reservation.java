package logic;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Simple POJO to hold one reservation record.
 */
public class Reservation implements Serializable{
    
	private final int    spot;
    private final int    subscriberId;
    private final LocalDate date;
    private final String    startTime;
    private final String    endTime;

    public Reservation(int spot,int subscriberId, LocalDate date, String startTime, String endTime) {
        this.spot      = spot;
        this.date      = date;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.subscriberId=subscriberId;
    }

    public int getSpot() {
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

	public int getSubscriberId() {
		return subscriberId;
	}
}

