package client;

import java.io.IOException;

import javafx.fxml.FXML;
import logic.*;

public abstract class Controller {
    protected Runnable backHandler;
    protected subscriber sub;
    protected BParkClient client;
    /*protected  AdaptableClient service;
    public final void sendToServer(Object msg) throws IOException {
        this.service.sendToServer(msg);
     }*/
    public void setBackHandler(Runnable backHandler) {
        this.backHandler = backHandler;
    }

	public BParkClient getClient() {
		return client;
	}

	public void setClient(BParkClient client,subscriber sub) {
		this.sub = sub;
		this.client = client;
	}

	public subscriber getSub() {
		return sub;
	}

	@FXML private void handleBackButton() {
		// swap the TableView scene back to the connect screen
		if (backHandler != null) {
			backHandler.run();
		}
	}
	/*public void sendToServerSafely(Object msg) {
		try {
			sendToServer(msg);
		} catch (IOException e) {
			System.err.println("Failed to send message to server: " + e.getMessage());
		}
	}*/
	protected void handleServerMessage(Object msg) {
		System.err.println("test");
	}
	
}
