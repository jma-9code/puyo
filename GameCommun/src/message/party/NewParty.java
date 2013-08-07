package message.party;

import message.puyo.IMsgPuyo;

public class NewParty implements IMsgPuyo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	
	public NewParty() {
	}
	
	public NewParty(int _id) {
		id = _id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
