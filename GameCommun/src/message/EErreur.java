package message;

public enum EErreur implements IMsg {
	IP_BANNIE("IP Bannie"), 
	AUTH_FAIL("Echec lors de l'authentification");

	/**
	 * Message � transmettre
	 */
	private String msg;

	EErreur ( final String _msg ) {
		this.msg = _msg;
	}

	public String getMsg() {
		return msg;
	}
}
