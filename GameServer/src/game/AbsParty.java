package game;

import java.util.ArrayList;
import java.util.Observable;

import message.party.EStatutParty;
import client.Client;

import commun.AbsPartyInfo;
import commun.AbsPartyOption;

public abstract class AbsParty extends Observable {

	protected static int nextid = 0;
	protected int id;
	protected AbsPartyOption option;
	protected Client owner = null;
	protected ArrayList<Client> clients = new ArrayList<Client>();
	protected EStatutParty statut = EStatutParty.WAITING;
	
	public abstract AbsPartyInfo getPartyInfo();
	
	public abstract void setPlayerScore(Client c, int newScore);
	
	public abstract void playerHaveLose(Client c);
	
	public abstract void checkPlayerHaveWin();
	
	public abstract void startParty();
	
	public abstract void stopParty();
	
	public abstract boolean addClient(Client c);
	
	public abstract boolean removeClient(Client c);
	
	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	public static int getNextid() {
		return nextid;
	}

	public static void setNextid(int nextid) {
		AbsParty.nextid = nextid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AbsPartyOption getOption() {
		return option;
	}

	public void setOption(AbsPartyOption option) {
		this.option = option;
	}

	public Client getOwner() {
		return owner;
	}

	public void setOwner(Client owner) {
		this.owner = owner;
	}

	public ArrayList<Client> getClients() {
		return clients;
	}

	public void setClients(ArrayList<Client> clients) {
		this.clients = clients;
	}

	public EStatutParty getStatut() {
		return statut;
	}

	public void setStatut(EStatutParty statut) {
		this.statut = statut;
	}
	
	
}
