package client;

class AdaptableClient extends AbstractClient {
   private ObservableClient client;

   public AdaptableClient(String host, int port, ObservableClient client) {
      super(host, port);
      this.client = client;
   }

   protected final void connectionClosed() {
      this.client.connectionClosed();
   }

   protected final void connectionException(Exception exception) {
      this.client.connectionException(exception);
   }

   protected final void connectionEstablished() {
      this.client.connectionEstablished();
   }

   protected final void handleMessageFromServer(Object msg) {
      this.client.handleMessageFromServer(msg);
   }
}