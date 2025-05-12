package logic;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable{
	private int code;
    private subscriber subscriber;
    private Date order_date;
    private Date date_of_placing_an_order;
    private ParkingSpot parking_space;
    private int order_id;
    
    // Constructor
    public Order(int code, subscriber subscriber, Date order_date, Date date_of_placing_an_order,ParkingSpot parking_space,int order_id){
        this.code = code;
        this.subscriber = subscriber;
        this.order_date = order_date;
        this.date_of_placing_an_order = date_of_placing_an_order;
        this.parking_space = parking_space;
        this.order_id = order_id;
    }
      
    // Getters
    public int getOrder_id() {
    	return order_id;
    }
    
    public int getCode() {
        return code;
    }
    public subscriber getSubscriber() {
        return subscriber;
    }
        
    public int getSubscriberID() {
        return subscriber != null ? subscriber.getId() : -1;
    }
    
    public Date getOrder_date() {
        return order_date;
    }

    public Date getDate_of_placing_an_order() {
        return date_of_placing_an_order;
    }
    
    public ParkingSpot getParkingSpot() {
    	return parking_space;
    }
	public int getParkingSpaceID() {
		return parking_space != null ? parking_space.getSpotId() : -1;
	}
    
    
    // Setters
    public void setCode(int code) {
        this.code = code;
    }

    public void setSubscriber(subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void setOrder_date(Date orderDate) {
        this.order_date = orderDate;
    }
    
    public void setParkingSpot(ParkingSpot parking_space) {
    	this.parking_space = parking_space;
    }

    public void setDate_of_placing_an_order(Date date_of_placing_an_order) {
        this.date_of_placing_an_order = date_of_placing_an_order;
    }
    @Override
    public String toString() {
        return "Order {" +
               "order_id=" + order_id +
               ", code=" + code +
               ", subscriber_id=" + (subscriber != null ? subscriber.getId() : "null") +
               ", parking_space=" + (parking_space != null ? parking_space.getSpotId() : "null") +
               ", order_date=" + order_date +
               ", date_of_placing_an_order=" + date_of_placing_an_order +
               '}';
    }
}
