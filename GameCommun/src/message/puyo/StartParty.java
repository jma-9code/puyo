package message.puyo;

import commun.puyo.Puyo_PartyInfo;


public class StartParty implements IMsgPuyo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Puyo_PartyInfo pinfo;
	private boolean started = false;
	
	public StartParty(){
		
	}
	
	public StartParty(boolean start, Puyo_PartyInfo _pinfo){
		setStarted(start);
		pinfo = _pinfo;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public Puyo_PartyInfo getPinfo() {
		return pinfo;
	}

	public void setPinfo(Puyo_PartyInfo pinfo) {
		this.pinfo = pinfo;
	}

}
