package message.party;



public class JoinParty implements ManageParty {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	
	public JoinParty(int _id){
		setId(_id);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
