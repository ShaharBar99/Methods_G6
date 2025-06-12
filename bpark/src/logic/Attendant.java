package logic;

import java.io.Serializable;

public class Attendant extends user implements Serializable{

	public Attendant(int id, String name, String phone, String email, Role role) {
		super(id, name, phone, email, role);
	}

}
