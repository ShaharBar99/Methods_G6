package logic;

import java.io.Serializable;
import java.util.List;

public class subscriber  extends user implements Serializable{
	    private List<Parkingsession> history;
	    //private RFID tag;
	    private int code;

	    public subscriber(int id, String name, String phone, String email, Role role,
	                      List<Parkingsession> history,  int code){
	    	super(id, name, phone, email, role);
	        this.history = history;
	        //this.tag = tag;
	        this.code = code;
	    }

	    // Getters
	    public List<Parkingsession> getHistory() { return history; }
	    //public RFID getTag() { return tag; }
	    public int getCode() { return code; }

	    // Setters
	    public void setHistory(List<Parkingsession> history) { this.history = history; }
	    //public void setTag(RFID tag) { this.tag = tag; }
	    public void setCode(int code) { this.code = code; }
	
}
