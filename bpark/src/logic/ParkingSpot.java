package logic;

import java.io.Serializable;

public class ParkingSpot implements Serializable{
    private int spotId;
    private SpotStatus status;
    private Order currentOrder;
    
    // Constructor
    public ParkingSpot(int spotId, SpotStatus status, Order currentReservation) {
        this.spotId = spotId;
        this.status = status;
        this.currentOrder = currentOrder;
    }

    // Getters
    public int getSpotId() {
        return spotId;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public Order getCurrentReservation() {
        return currentOrder;
    }

    // Setters
    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public void setCurrentReservation(Order order) {
        this.currentOrder = order;
    }
}

