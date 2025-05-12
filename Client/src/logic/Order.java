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
    public Order(int code, subscriber subscriber, Date order_date, Date date_of_placing_an_order,ParkingSpot parking_space,int order_id) {
        this.code = code;
        this.subscriber = subscriber;
        this.order_date = order_date;
        this.date_of_placing_an_order = date_of_placing_an_order;
        this.parking_space = parking_space;
        this.order_id = order_id;
    }
    // Getters
    public int getCode() {
        return code;
    }
    public subscriber getSubscriber() {
        return subscriber;
    }

    public Date getorder_date() {
        return order_date;
    }

    public Date getdate_of_placing_an_order() {
        return date_of_placing_an_order;
    }
    
    public ParkingSpot get_ParkingSpot() {
    	return parking_space;
    }
    public int get_order_id() {
    	return order_id;
    }
    // Setters
    public void setCode(int code) {
        this.code = code;
    }

    public void setSubscriber(subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void setorder_date(Date order_date) {
        this.order_date = order_date;
    }
    
    public void set_ParkingSpot(ParkingSpot parking_space) {
    	this.parking_space = parking_space;
    }

    public void setdate_of_placing_an_order(Date date_of_placing_an_order) {
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
