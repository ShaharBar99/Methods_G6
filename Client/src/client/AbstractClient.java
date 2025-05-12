package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public abstract class AbstractClient implements Runnable {
   private Socket clientSocket;
   private ObjectOutputStream output;
   private ObjectInputStream input;
   private Thread clientReader;
   private boolean readyToStop = false;
   private String host;
   private int port;

   public AbstractClient(String host, int port) {
      this.host = host;
      this.port = port;
   }

   public final void openConnection() throws IOException {
      if (!this.isConnected()) {
         try {
            this.clientSocket = new Socket(this.host, this.port);
            this.output = new ObjectOutputStream(this.clientSocket.getOutputStream());
            this.input = new ObjectInputStream(this.clientSocket.getInputStream());
         } catch (IOException var4) {
            try {
               this.closeAll();
            } catch (Exception var3) {
            }

            throw var4;
         }

         this.clientReader = new Thread(this);
         this.readyToStop = false;
         this.clientReader.start();
      }
   }

   public final void sendToServer(Object msg) throws IOException {
      if (this.clientSocket != null && this.output != null) {
         this.output.writeObject(msg);
         this.output.reset();
      } else {
         throw new SocketException("socket does not exist");
      }
   }

   public final void closeConnection() throws IOException {
      this.readyToStop = true;

      try {
         this.closeAll();
      } finally {
         this.connectionClosed();
      }

   }

   public final boolean isConnected() {
      return this.clientReader != null && this.clientReader.isAlive();
   }

   public final int getPort() {
      return this.port;
   }

   public final void setPort(int port) {
      this.port = port;
   }

   public final String getHost() {
      return this.host;
   }

   public final void setHost(String host) {
      this.host = host;
   }

   public final InetAddress getInetAddress() {
      return this.clientSocket.getInetAddress();
   }

   public final void run() {
      this.connectionEstablished();

      try {
         while(!this.readyToStop) {
            Object msg = this.input.readObject();
            this.handleMessageFromServer(msg);
         }
      } catch (Exception var9) {
         if (!this.readyToStop) {
            try {
               this.closeAll();
            } catch (Exception var8) {
            }

            this.connectionException(var9);
         }
      } finally {
         this.clientReader = null;
      }

   }

   protected void connectionClosed() {
   }

   protected void connectionException(Exception exception) {
   }

   protected void connectionEstablished() {
   }

   protected abstract void handleMessageFromServer(Object var1);

   private void closeAll() throws IOException {
      try {
         if (this.clientSocket != null) {
            this.clientSocket.close();
         }

         if (this.output != null) {
            this.output.close();
         }

         if (this.input != null) {
            this.input.close();
         }
      } finally {
         this.output = null;
         this.input = null;
         this.clientSocket = null;
      }

   }
}