package client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import logic.Parkingsession;

public class HistoryController {
	public static List<Parkingsession> generateDummySessions() {
        List<Parkingsession> sessions = new ArrayList<>();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            sessions.add(new Parkingsession(1, 2001, 135,
                    sdf.parse("2025-05-01 08:00"),
                    sdf.parse("2025-05-01 10:30"),
                    false, false));

            sessions.add(new Parkingsession(2, 2002, 102,
                    sdf.parse("2025-05-02 09:15"),
                    sdf.parse("2025-05-02 11:00"),
                    false, true));

            sessions.add(new Parkingsession(3, 2003, 103,
                    sdf.parse("2025-05-03 07:45"),
                    sdf.parse("2025-05-03 09:00"),
                    false, false));

            sessions.add(new Parkingsession(4, 2004, 104,
                    sdf.parse("2025-05-04 10:00"),
                    sdf.parse("2025-05-04 12:45"),
                    true, false));

            sessions.add(new Parkingsession(5, 2005, 105,
                    sdf.parse("2025-05-05 14:00"),
                    sdf.parse("2025-05-05 16:30"),
                    true, true));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }
}
