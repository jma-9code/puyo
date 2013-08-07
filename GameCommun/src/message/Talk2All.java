package message;

public class Talk2All implements IMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message = "";
	
	public Talk2All(String msg){
		setMessage(msg);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
