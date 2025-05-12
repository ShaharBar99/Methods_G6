package client;

import java.io.IOException;

public class BParkClient extends ObservableClient {

    private MessageListener messageListener;

    public BParkClient(String host, int port) {
        super(host, port); // This sets the host and port to be used in openConnection()
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
       // System.out.println("Received from server: " + msg);
        if (messageListener != null) {
            messageListener.onMessage(msg);
        }
    }

    public void sendToServerSafely(Object msg) {
        try {
            sendToServer(msg);
        } catch (IOException e) {
            System.err.println("Failed to send message to server: " + e.getMessage());
        }
    }

    public void start() {
        try {
            openConnection();
        } catch (IOException e) {
            System.err.println("Failed to open connection: " + e.getMessage());
        }
    }

    public void stop() {
        try {
        	this.sendToServerSafely("Client disconnected");
            closeConnection();
        } catch (IOException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}
