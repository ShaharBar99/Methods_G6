package client;

import java.time.LocalDate;

/**
 * Simple POJO to hold one reservation record.
 */
public class Reservation {
    private final String    spot;
    private final LocalDate date;
    private final String    startTime;
    private final String    endTime;

    public Reservation(String spot, LocalDate date, String startTime, String endTime) {
        this.spot      = spot;
        this.date      = date;
        this.startTime = startTime;
        this.endTime   = endTime;
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

