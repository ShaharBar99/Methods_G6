package client;

import logic.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AttendantController {
	
	//private BParkClient client;
	//private AttendentScreenController attendantScreen;

    private static AttendantController instance;

    public static AttendantController getInstance() {
        if (instance == null) {
            instance = new AttendantController();
        }
        return instance;
    }

    /**
     * פעולה 1 - מחזירה את מצב החניה הנוכחי: רשימת מקומות עם סטטוס והזמנה אם קיימת.
     */
    public List<ParkingSpot> getCurrentParkingStatus() {
        List<ParkingSpot> spots = new ArrayList<>();

//        //יצירת מנויים מדומים
//        subscriber sub1 = new subscriber(2001, "Alice", "050-1234567", "alice@example.com",
//                Role.SUBSCRIBER, new ArrayList<>(), null, 1234);
//        subscriber sub2 = new subscriber(2002, "Bob", "052-9876543", "bob@example.com",
//                Role.SUBSCRIBER, new ArrayList<>(), null, 5678);
//		
//        // יצירת מקומות חניה
//        ParkingSpot spot1 = new ParkingSpot(101, SpotStatus.FREE);
//        ParkingSpot spot2 = new ParkingSpot(102, SpotStatus.OCCUPIED);
//        ParkingSpot spot3 = new ParkingSpot(103, SpotStatus.RESERVED);
//        ParkingSpot spot4 = new ParkingSpot(104, SpotStatus.FREE);
//
//        // יצירת הזמנות וקישור
//        Order order1 = new Order(123456, sub1, new Date(), new Date(), spot2, 1);
//        Order order2 = new Order(234567, sub2, new Date(), new Date(), spot3, 2);
////        spot2.setCurrentReservation(order1);
////        spot3.setCurrentReservation(order2);
//
//        // הוספה לרשימה
//        spots.add(spot1);
//        spots.add(spot2);
//        spots.add(spot3);
//        spots.add(spot4);
//
        return spots;
    }

    /**
     * פעולה 2 - מחזירה את כל המנויים במערכת כולל היסטוריית חניה וקוד אישי.
     */
    public List<subscriber> getAllSubscribers() {
        List<subscriber> subs = new ArrayList<>();

        // היסטוריות חניה לדוגמה
        /*List<Parkingsession> history1 = Arrays.asList(
                new Parkingsession(1, 2001, 101, new Date(), new Date(), false, false),
                new Parkingsession(2, 2001, 102, new Date(), new Date(), true, false)
        );

        List<Parkingsession> history2 = Arrays.asList(
                new Parkingsession(3, 2002, 103, new Date(), new Date(), false, true)
        );*/

        // יצירת מנויים
        //subscriber sub1 = new subscriber(2001, "Alice", "050-1234567", "alice@example.com",
         //       Role.SUBSCRIBER, history1, 1234);

        //subscriber sub2 = new subscriber(2002, "Bob", "052-9876543", "bob@example.com",
         //       Role.SUBSCRIBER, history2, 5678);

        //subs.add(sub1);
        //subs.add(sub2);

        return subs;
    }

    /**
     * פעולה 3 - מחזירה את כל הסשנים הפעילים כעת (inTime != null && outTime == null).
     */
    public List<Parkingsession> getAllActiveSessions() {
        List<Parkingsession> allSessions = new ArrayList<>();

//        allSessions.add(new Parkingsession(10, 2001, 101, 0, new Date(), null, false, false, false)); // פעיל
//        allSessions.add(new Parkingsession(11, 2002, 102, 0, new Date(), new Date(), false, false, false)); // הסתיים
//        allSessions.add(new Parkingsession(12, 2003, 103, 0, new Date(), null, true, false, false)); // פעיל

        List<Parkingsession> activeSessions = new ArrayList<>();
        for (Parkingsession session : allSessions) {
            if (session.getInTime() != null && session.getOutTime() == null) {
                activeSessions.add(session);
            }
        }
        return activeSessions;
    }
} 
