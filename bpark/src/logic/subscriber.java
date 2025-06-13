package logic;

import java.io.Serializable;
import java.util.List;

public class subscriber  extends user implements Serializable{
	    private List<Parkingsession> history;
	    private String tag;
	    private int code; // Subscriber number - in the login
 
	    public subscriber(int id, String name, String phone, String email, Role role,
	                      List<Parkingsession> history,String tag, int code){
	    	super(id, name, phone, email, role);
	        this.history = history;
	        this.tag = tag;
	        this.code = code;
	    }

	    // Getters
	    public int getId() {
            return super.getId();
	    }

		public String getName() {
			return super.getName();
		}
		
        public String getPhone() {
            return super.getPhone();
        }
        
		public String getEmail() {
			return super.getEmail();
		}
		
        public Role getRole() {
			return super.getRole();
        }
	    
	    public List<Parkingsession> getHistory() { return history; }
	    
	    public int getCode() { return code; }
	    
	    public String getTag() {
			return tag;
		}


		public void setTag(String tag) {
			this.tag = tag;
		}

		// Setters
	    public void setHistory(List<Parkingsession> history) { this.history = history; }
	    //public void setTag(RFID tag) { this.tag = tag; }
	    public void setCode(int code) { this.code = code; }
	
}