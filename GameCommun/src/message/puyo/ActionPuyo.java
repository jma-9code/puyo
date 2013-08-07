package message.puyo;


public class ActionPuyo implements IMsgPuyo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EMovePlateau move;
	//celui qui envoi le msg
	private String owner = null;
	//celui a qui il est destine
	private String receiver = null;
	
	private int nbGarbadge = -1;
	
	private String serial = null;
	
	public ActionPuyo(EMovePlateau _move, String _serial){
		setMove(_move);
		serial = _serial;
	}
	
	//constructeur pr envoi de pierre
	public ActionPuyo(String _receiver, int _nbGarbadge){
		this(EMovePlateau.GARBADGE, null);
		receiver = _receiver;
		nbGarbadge = _nbGarbadge;
	}
	
	public EMovePlateau getMove() {
		return move;
	}

	public void setMove(EMovePlateau move) {
		this.move = move;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public int getNbGarbadge() {
		return nbGarbadge;
	}

	public void setNbGarbadge(int nbGarbadge) {
		this.nbGarbadge = nbGarbadge;
	}

	@Override
	public String toString() {
		return "ActionMove [move=" + move +", owner=" + owner + ", receiver=" + receiver + ", nbGarbadge=" + nbGarbadge + "]";
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
	
}
