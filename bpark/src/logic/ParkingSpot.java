package logic;

import java.io.Serializable;

public class ParkingSpot implements Serializable{
    private int spotId;
    private SpotStatus status;

    // Constructor
    public ParkingSpot(int spotId, SpotStatus status) {
        this.spotId = spotId;
        this.status = status;
    }

    // Getters
    public int getSpotId() {
        return spotId;
    }

    public SpotStatus getStatus() {
        return status;
    }
    
    // Setters
    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

}