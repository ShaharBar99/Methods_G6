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
	public List<Parkingsession> getSessions() throws Exception {
		responseReceived = false;
		client.sendToServerSafely(new SendObject<Integer>("Get history", subscriber.getId()));
		Util.waitForServerResponse(15000,()->this.responseReceived);
		return sessions;
	}

}