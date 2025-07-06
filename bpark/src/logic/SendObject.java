package logic;
import java.io.Serializable;
/**
 * 
 * A generic container class used for encapsulating objects and associated messages 
 * for transmission between client and server in a JavaFX-based parking management system.
 * This class facilitates the serialization of data objects along with a message string,
 * enabling structured communication in a distributed environment.
 *
 * @param <T> the type of the object to be sent, which must implement {@link Serializable}
 */
public class SendObject<T extends Serializable> implements Serializable {
    private T obj;
    private String objectMessage;
    public SendObject(String objectMessage, T obj) {
        super();
        this.obj = obj;
        this.objectMessage = objectMessage;
    }  
    public T getObj() {
        return obj;
    }
    public void setObj(T obj) {
        this.obj = obj;
    }
    public String getObjectMessage() {
        return objectMessage;
    }
    public void setObjectMessage(String objectMessage) {
        this.objectMessage = objectMessage;
    }
}