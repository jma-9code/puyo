package commun.puyo;

import java.io.Serializable;
import java.util.Random;

import commun.AbsPartyOption;

public class Puyo_PartyOption extends AbsPartyOption implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Vitesse de descente AUTO (gravite)
	 */
	private int downspeed = 500;
	
	/**
	 * Graine pour l'aleatoire
	 */
	private long graine = new Random().nextLong();
	
	private int nbPlayerMax = 2;
	
	
	public Puyo_PartyOption(){
		
	}
	
	public Puyo_PartyOption ( int downspeed, int nbPlayerMax ) {
		this.downspeed = downspeed;
		this.nbPlayerMax = nbPlayerMax;
	}

	public long getGraine() {
		return graine;
	}

	public void setGraine(long graine) {
		this.graine = graine;
	}

	public int getDownspeed() {
		return downspeed;
	}

	public void setDownspeed(int downspeed) {
		this.downspeed = downspeed;
	}

	public int getNbPlayerMax() {
		return nbPlayerMax;
	}

	public void setNbPlayerMax(int nbPlayerMax) {
		this.nbPlayerMax = nbPlayerMax;
	}




	@Override
	public String toString() {
		return "Gravite=" + downspeed + ", Nb JoueurMax=" + nbPlayerMax + "]";
	}


}
