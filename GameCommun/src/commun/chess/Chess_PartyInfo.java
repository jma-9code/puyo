package commun.chess;

import commun.AbsPartyInfo;

public class Chess_PartyInfo extends AbsPartyInfo{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mediumScore;
	
	public Chess_PartyInfo(){
		option = new Chess_PartyOption();
	}

	public int getMediumScore() {
		return mediumScore;
	}

	public void setMediumScore(int mediumScore) {
		this.mediumScore = mediumScore;
	}
}
