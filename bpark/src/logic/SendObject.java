package logic;
import java.io.Serializable;
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