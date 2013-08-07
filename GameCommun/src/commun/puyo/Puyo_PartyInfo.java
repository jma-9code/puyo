package commun.puyo;

import commun.AbsPartyInfo;

public class Puyo_PartyInfo extends AbsPartyInfo{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mediumScore;
	
	public Puyo_PartyInfo(){
		option = new Puyo_PartyOption();
	}

	public int getMediumScore() {
		return mediumScore;
	}

	public void setMediumScore(int mediumScore) {
		this.mediumScore = mediumScore;
	}
}
