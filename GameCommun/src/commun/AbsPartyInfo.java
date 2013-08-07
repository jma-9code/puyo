package commun;

import java.io.Serializable;
import java.util.ArrayList;

import message.party.EStatutParty;

public class AbsPartyInfo implements Serializable{
	protected int id;
	protected ArrayList<Player> players = new ArrayList<Player>();
	protected EStatutParty statut;
	protected AbsPartyOption option;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	public EStatutParty getStatut() {
		return statut;
	}
	public void setStatut(EStatutParty statut) {
		this.statut = statut;
	}
	public AbsPartyOption getOption() {
		return option;
	}
	public void setOption(AbsPartyOption option) {
		this.option = option;
	}
	
	
	
}
