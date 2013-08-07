package game.puyo;


import game.AbsParty;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import main.Config;
import message.party.EStatutParty;
import message.party.EStatutPlayer;
import message.party.EndParty;
import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import message.puyo.StartParty;
import server.DataBase;
import client.Client;
import client.Client.EClientStatut;
import client.ClientManager;

import commun.AbsPartyInfo;
import commun.EGames;
import commun.Tools;
import commun.puyo.PartyOption;
import commun.puyo.Puyo_PartyInfo;
import commun.puyo.Puyo_PartyOption;

public class Puyo_Party extends AbsParty{	
	private HashMap<Client, EStatutPlayer> relClientStatut = new HashMap<Client, EStatutPlayer>();
	private long graine = new Random().nextLong();
	private final Object lock = new Object();
	private final Object lockGame = new Object();
	
	
	public Puyo_Party(Client _owner){
		id = nextid++;
		option = new Puyo_PartyOption();
		owner = _owner;
		addClient(owner);
		((Puyo_PartyOption)option).setGraine(graine);
	}
	
	public AbsPartyInfo getPartyInfo(){
		synchronized (lock) {
			Puyo_PartyInfo info = new Puyo_PartyInfo();
			for (Client c : clients){
				info.getPlayers().add(c.getPlayer());
			}
			info.setOption(option);
			info.setId(id);
			info.setStatut(statut);
			return info;
		}
	}
	
	private Client getOpponent(Client c){
		int indexPlayer = clients.indexOf(c);
		for (int i=indexPlayer+1; i<clients.size(); i++){
			if (relClientStatut.get(clients.get(i)) == EStatutPlayer.INGAME){
				return clients.get(i);
			}
		}
		
		for (int i=0; i<indexPlayer; i++){
			if (relClientStatut.get(clients.get(i)) == EStatutPlayer.INGAME){
				return clients.get(i);
			}
		}
		
		return null;
	}
	
	public void setPlayerScore(Client c, int newScore){
		if (Config.DB_USE){
			DataBase.get().updatePlayerScore(c.getPlayer().getName(), newScore, EGames.GAME_PUYO);
		}
		ClientManager.get().update(c, EClientStatut.NEWSCORE);
	}
	
	public void playerHaveLose(Client c){
		synchronized (lockGame) {
			if (relClientStatut.get(c) != EStatutPlayer.LOSE){
				relClientStatut.put(c, EStatutPlayer.LOSE);
				/* recupere le score du client contre qui on a perdu */ 
				Client opponent = getOpponent(c);
				if (opponent == null){
					System.out.println("Err NO opponent");
					return;
				}
				
				/*Mise a jour des score */
				int cactuelScore = DataBase.get().getPlayerScore(c.getPlayer().getName(), EGames.GAME_PUYO);
				int oactuelScore = DataBase.get().getPlayerScore(opponent.getPlayer().getName(), EGames.GAME_PUYO);
				int cNewScore = Tools.getNewScore(cactuelScore, oactuelScore, false);
				int oNewScore = Tools.getNewScore(oactuelScore, cactuelScore, true);
				if (cNewScore <= 0 || oNewScore <= 0){
					System.out.println("MAJ score erreur");
					return;
				}
				setPlayerScore(c, cNewScore);
				setPlayerScore(opponent, oNewScore);
				
				//On previent les collegues
				ActionPuyo am = new ActionPuyo(EMovePlateau.HAVELOSE,"");
				am.setOwner(c.getPlayer().getName());
				for (Client cli : clients){
					if ( c != cli)
						cli.sendObject(am);
				}
				checkPlayerHaveWin();
			}
		}
	}
	
	
	public void checkPlayerHaveWin(){
		synchronized (lock) {
			int countLose = 0;
			Client winner = null;
			for (Map.Entry<Client, EStatutPlayer> e : relClientStatut.entrySet()){
			    if ( e.getValue() == EStatutPlayer.LOSE || e.getValue() == EStatutPlayer.LEFT){
			    	countLose++;
			    }else{
			    	winner = e.getKey();
			    }
			}
			if (relClientStatut.size()-countLose == 1){
				relClientStatut.put(winner, EStatutPlayer.WIN);
				//On previent les collegues
				ActionPuyo am = new ActionPuyo(EMovePlateau.HAVELOSE,"");
				am.setOwner(winner.getPlayer().getName());
				for (Client cli : clients){
					cli.sendObject(am);
				}
				stopParty();
			}
		}
	}
	
	public void startParty(){
		synchronized (lock) {
			if (statut != EStatutParty.INGAME){
				statut = EStatutParty.INGAME;
				for (Client c : clients){
					c.sendObject(new StartParty(true, (Puyo_PartyInfo) getPartyInfo()));
					relClientStatut.put(c, EStatutPlayer.INGAME);
				}
				updateObservers(statut);
			}
		}
	}
	
	public void stopParty(){
		synchronized (lock) {
			if (statut != EStatutParty.END){
				statut = EStatutParty.END;
				for (Client c : clients){
					c.sendObject(new EndParty());
				}
				updateObservers(statut);
			}
		}
	}
	
	public boolean addClient(Client c){
		synchronized (lock) {
			if (!clients.contains(c) && clients.size() <= ((Puyo_PartyOption)option).getNbPlayerMax()){
				clients.add(c);
				relClientStatut.put(c, EStatutPlayer.WAITING);
				return true;
			}
			return false;
		}
	}
	
	public boolean removeClient(Client c){
		synchronized (lock) {
			if (relClientStatut.get(c) == EStatutPlayer.INGAME){
				playerHaveLose(c);
			}
			relClientStatut.remove(c);
			return clients.remove(c);
		}
	}

	public long getGraine() {
		return graine;
	}

	public void setGraine(long graine) {
		this.graine = graine;
	}
}
