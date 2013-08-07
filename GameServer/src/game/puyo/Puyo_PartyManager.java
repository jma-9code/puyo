package game.puyo;

import game.AbsParty;
import game.AbsPartyManager;

import java.util.Observable;

import message.party.EStatutParty;
import client.Client;
import client.ClientManager;

public class Puyo_PartyManager extends AbsPartyManager {
	private static Puyo_PartyManager instance = new Puyo_PartyManager();
	
	
	public static Puyo_PartyManager get(){
		return instance;
	}
	private Puyo_PartyManager() {}

	public boolean addParty(AbsParty p) {
		synchronized (lock) {
			if (isInParty(p.getOwner()) != null){
				return false;
			}
			partys.put(p.getOwner(),p);
			p.addObserver(this);
			updateObservers(EPartyManager.ADDPARTY);
			return true;
		}
	}
	
	public boolean joinParty(int id, Client c) {
		synchronized (lock) {
			if (isInParty(c) != null){
				return false;
			}
			Puyo_Party p = null;
			for ( AbsParty part : partys.values() ) {
				if ( part.getId() == id ){
					p = (Puyo_Party) part;
				}
			}
			if (p != null){
				if (p.addClient(c)){
					updateObservers(EPartyManager.JOINPARTY);
					return true;
				}
			}else {
				System.out.println("PARTIE JOIN INEXISTANTE");
				return false;
			}
			
			System.out.println("PARTIE JOIN ERREUR");
			return false;
		}
	}
	
	public void leftParty(Client c) {
		synchronized (lock) {
			Puyo_Party p = (Puyo_Party) Puyo_PartyManager.get().isInParty(c);
			if (p != null){
				//Le client etait le owner d'une partie, on supprime la partie ou on la relaye au joueur suivant
				if (p.getOwner() == c){
					p.removeClient(c);
					partys.remove(c);
					if (!p.getClients().isEmpty()){
						p.setOwner(p.getClients().get(0));
						partys.put(p.getClients().get(0), p);
						updateObservers(EPartyManager.LEFTPARTY);
					}else{
						updateObservers(EPartyManager.REMOVEPARTY);
					}
				}//Le client avait rejoint une partie
				else {
					p.removeClient(c);
					updateObservers(EPartyManager.LEFTPARTY);
				}
			}
		}
	}

	public Puyo_Party isInParty(Client cli) {
		if (partys.get(cli) != null){
			return (Puyo_Party) partys.get(cli);
		}else{
			for (AbsParty p : partys.values()){
				if (p == null) continue;
				for(Client c : p.getClients()){
					if (cli == c){
						return (Puyo_Party) p;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof Puyo_Party){
			if (arg1 instanceof EStatutParty){
				updateObservers(EPartyManager.STATUTPARTY);
			}
		}
	}
	
	public void updateObservers(Object obj) {
		System.out.println("PartyManager : " + obj);
		ClientManager.get().sendToAll(getAllPartyInfo());
		setChanged();
		notifyObservers(obj);
	}
}
