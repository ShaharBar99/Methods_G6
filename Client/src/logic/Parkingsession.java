package logic;

import java.io.Serializable;
import java.util.Date;

public class Parkingsession implements Serializable{
	    private int sessionId;
	    private int subscriberId;
	    private int spotId;
	    private Date inTime;
	    private Date outTime;
	    private boolean extended;
	    private boolean late;

	    // Constructor
	    public Parkingsession(int sessionId, int subscriberId, int spotId, Date inTime, Date outTime, boolean extended, boolean late) {
	        this.sessionId = sessionId;
	        this.subscriberId = subscriberId;
	        this.spotId = spotId;
	        this.inTime = inTime;
	        this.outTime = outTime;
	        this.extended = extended;
	        this.late = late;
	    }

	    // Getters
	    public int getSessionId() {
	        return sessionId;
	    }

	    public int getSubscriberId() {
	        return subscriberId;
	    }

	    public int getSpotId() {
	        return spotId;
	    }

	    public Date getInTime() {
	        return inTime;
	    }

	    public Date getOutTime() {
	        return outTime;
	    }

	    public boolean isExtended() {
	        return extended;
	    }

	    public boolean isLate() {
	        return late;
	    }

	    // Setters
	    public void setSessionId(int sessionId) {
	        this.sessionId = sessionId;
	    }

	    public void setSubscriberId(int subscriberId) {
	        this.subscriberId = subscriberId;
	    }

	    public void setSpotId(int spotId) {
	        this.spotId = spotId;
	    }

	    public void setInTime(Date inTime) {
	        this.inTime = inTime;
	    }

	    public void setOutTime(Date outTime) {
	        this.outTime = outTime;
	    }

	    public void setExtended(boolean extended) {
	        this.extended = extended;
	    }

	    public void setLate(boolean late) {
	        this.late = late;
	    }
	

}
