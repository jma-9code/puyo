package commun.puyo;

import java.io.Serializable;
import java.util.ArrayList;

import commun.Player;

import message.party.EStatutParty;

public class PartyInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private ArrayList<Player> players = new ArrayList<Player>();
	private PartyOption option = new PartyOption();
	private EStatutParty statut;
	
	public PartyInfo(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> player) {
		this.players = player;
	}

	public PartyOption getOption() {
		return option;
	}

	public void setOption(PartyOption option) {
		this.option = option;
	}

	public EStatutParty getStatut() {
		return statut;
	}

	public void setStatut(EStatutParty statut) {
		this.statut = statut;
	}
}
