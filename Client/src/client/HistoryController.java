package client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import logic.*;

public class HistoryController {
	private List<Parkingsession> sessions;
	private BParkClient client;
	private subscriber subscriber;
	private boolean responseReceived;

	public void setClient(BParkClient client, subscriber subscriber) {
		this.client = client;
		this.subscriber = subscriber;
	}

	private void setSessions(List<Parkingsession> sessions) {
		this.sessions = sessions;
	}

	public void handleServerMessage(Object message) {
		if (message instanceof SendObject<?>) {
			SendObject<?> sendObject = (SendObject<?>) message;
			
			if (sendObject.getObjectMessage().equals("Parkingsession list of subscriber")&&sendObject.getObj() instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<Parkingsession> list = (List<Parkingsession>) sendObject.getObj();
				setSessions(list);
				
			}
			responseReceived = true;
		}
		
	}

	/*
	 * public static List<Parkingsession> generateDummySessions() {
	 * List<Parkingsession> sessions = new ArrayList<>();
	 * 
	 * try { SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	 * 
	 * sessions.add(new Parkingsession(1, 2001, 135,113355,
	 * sdf.parse("2025-05-01 08:00"), sdf.parse("2025-05-01 10:30"), false,
	 * false,false));
	 * 
	 * sessions.add(new Parkingsession(2, 2002, 102, sdf.parse("2025-05-02 09:15"),
	 * sdf.parse("2025-05-02 11:00"), false, true));
	 * 
	 * sessions.add(new Parkingsession(3, 2003, 103, sdf.parse("2025-05-03 07:45"),
	 * sdf.parse("2025-05-03 09:00"), false, false));
	 * 
	 * sessions.add(new Parkingsession(4, 2004, 104, sdf.parse("2025-05-04 10:00"),
	 * sdf.parse("2025-05-04 12:45"), true, false));
	 * 
	 * sessions.add(new Parkingsession(5, 2005, 105, sdf.parse("2025-05-05 14:00"),
	 * sdf.parse("2025-05-05 16:30"), true, true));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return sessions; }
	 */
	public List<Parkingsession> getSessions() throws Exception {
		responseReceived = false;
		client.sendToServerSafely(new SendObject<Integer>("Get history", subscriber.getId()));
		waitForServerResponse(15000);
		return sessions;
	}

	private boolean waitForServerResponse(long timeoutMillis) throws Exception {
		long startTime = System.currentTimeMillis();

		while (!responseReceived) {
			try {
				Thread.sleep(10); // sleep briefly to avoid busy-waiting
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// responseReceived = false;
			// Check if we've exceeded the timeout
			if (System.currentTimeMillis() - startTime > timeoutMillis) {

				ShowAlert.showAlert("Timeout Error",
						"The server did not respond within the expected time. Please try again later.",
						AlertType.ERROR);

				throw new Exception("Server response timed out after " + timeoutMillis + " milliseconds");
			}
		}
		return true;
	}
}
