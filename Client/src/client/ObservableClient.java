package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Observable;

public class ObservableClient extends Observable {
   public static final String CONNECTION_EXCEPTION = "#OC:Connection error.";
   public static final String CONNECTION_CLOSED = "#OC:Connection closed.";
   public static final String CONNECTION_ESTABLISHED = "#OC:Connection established.";

   private AdaptableClient service;

   public ObservableClient(String host, int port) {
      this.service = new AdaptableClient(host, port, this);
   }

   public final void openConnection() throws IOException {
      this.service.openConnection();
   }

   public final void closeConnection() throws IOException {
      this.service.closeConnection();
   }

   public final void sendToServer(Object msg) throws IOException {
      this.service.sendToServer(msg);
   }

   public final boolean isConnected() {
      return this.service.isConnected();
   }

   public final int getPort() {
      return this.service.getPort();
   }

   public final void setPort(int port) {
      this.service.setPort(port);
   }

   public final String getHost() {
      return this.service.getHost();
   }

   public final void setHost(String host) {
      this.service.setHost(host);
   }

   public final InetAddress getInetAddress() {
      return this.service.getInetAddress();
   }

   protected void handleMessageFromServer(Object message) {
      setChanged();
      notifyObservers(message);
   }

   protected void connectionClosed() {
      setChanged();
      notifyObservers(CONNECTION_CLOSED);
   }

   protected void connectionException(Exception exception) {
      setChanged();
      notifyObservers(CONNECTION_EXCEPTION);
   }

   protected void connectionEstablished() {
      setChanged();
      notifyObservers(CONNECTION_ESTABLISHED);
   }
}
