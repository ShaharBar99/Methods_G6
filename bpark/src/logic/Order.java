package logic;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable{
	private int confirmationCode;
    private subscriber subscriber;
    private Date orderDate;
    private Date dateOfPlacingAnOrder;
    private ParkingSpot parkingSpace;
    private int orderID;
    
    // Constructor
    public Order(int confirmationCode, subscriber subscriber, Date orderDate, Date dateOfPlacingAnOrder,ParkingSpot parkingSpace,int orderID){
        this.confirmationCode = confirmationCode;
        this.subscriber = subscriber;
        this.orderDate = orderDate;
        this.dateOfPlacingAnOrder = dateOfPlacingAnOrder;
        this.parkingSpace = parkingSpace;
        this.orderID = orderID;
    }
    
    
    // Getters
    public int getOrderID() {
    	return orderID;
    }
    
    public int getConfirmationCode() {
        return confirmationCode;
    }
    public subscriber getSubscriber() {
        return subscriber;
    }
        
    public int getSubscriberID() {
        return subscriber != null ? subscriber.getId() : -1;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }

    public Date getDateOfPlacingAnOrder() {
        return dateOfPlacingAnOrder;
    }
    
    public ParkingSpot getParkingSpace() {
    	return parkingSpace;
    }
    
	public int getParkingSpaceID() {
		return parkingSpace != null ? parkingSpace.getSpotId() : -1;
	}

    
    
    
    // Setters
    public void setCode(int confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public void setSubscriber(subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public void setParkingSpace(ParkingSpot parkingSpace) {
    	this.parkingSpace = parkingSpace;
    }

    public void setDateOfPlacingAnOrder(Date dateOfPlacingAnOrder) {
        this.dateOfPlacingAnOrder = dateOfPlacingAnOrder;
    }
    @Override
    public String toString() {
        return "Order {" +
               "order_id=" + orderID +
               ", confirmationCode=" + confirmationCode +
               ", subscriber_id=" + (subscriber != null ? subscriber.getId() : "null") +
               ", parking_space=" + (parkingSpace != null ? parkingSpace.getSpotId() : "null") +
               ", order_date=" + orderDate +
               ", date_of_placing_an_order=" + dateOfPlacingAnOrder +
               '}';
    }
}
