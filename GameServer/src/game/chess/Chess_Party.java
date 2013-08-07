package game.chess;

import game.AbsParty;

import java.util.HashMap;

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
import commun.chess.Chess_PartyInfo;
import commun.puyo.Puyo_PartyInfo;
import commun.puyo.Puyo_PartyOption;

public class Chess_Party extends AbsParty {
	private HashMap<Client, EStatutPlayer> relClientStatut = new HashMap<Client, EStatutPlayer>(2);
	private final Object lock = new Object();
	private final Object lockGame = new Object();
	
	public Chess_Party(Client _owner){
		id = nextid++;
		option = new Puyo_PartyOption();
		owner = _owner;
		addClient(owner);
	}
	
	public AbsPartyInfo getPartyInfo(){
		synchronized (lock) {
			Chess_PartyInfo info = new Chess_PartyInfo();
			for (Client c : clients){
				info.getPlayers().add(c.getPlayer());
			}
			info.setOption(option);
			info.setId(id);
			info.setStatut(statut);
			return info;
		}
	}
	
	public void setPlayerScore(Client c, int newScore){
		if (Config.DB_USE)
			DataBase.get().updatePlayerScore(c.getPlayer().getName(), newScore, EGames.GAME_PUYO);
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
				
				int cactuelScore = DataBase.get().getPlayerScore(c.getPlayer().getName(), EGames.GAME_CHESS);
				int oactuelScore = DataBase.get().getPlayerScore(opponent.getPlayer().getName(), EGames.GAME_CHESS);
				int cNewScore = Tools.getNewScore(cactuelScore, oactuelScore, false);
				int oNewScore = Tools.getNewScore(oactuelScore, cactuelScore, true);
				if (cNewScore <= 0 || oNewScore <= 0){
					System.out.println("MAJ score erreur");
					return;
				}
				setPlayerScore(c, cNewScore);
				setPlayerScore(opponent, oNewScore);
				
				//On previent les collegues
				/*ActionPuyo am = new ActionPuyo(EMovePlateau.HAVELOSE, -1, "");
				am.setOwner(c.getPlayer().getName());
				for (Client cli : clients){
					if ( c != cli)
						cli.sendObject(am);
				}*/
			}
		}
	}
	
	private Client getOpponent(Client c) {
		Client opponent = null;
		for (Client cli : clients){
			if (cli == c) continue;
			opponent = cli;
		}
		return opponent;
	}

	public void checkPlayerHaveWin(){
		synchronized (lock) {
			/*if (relClientStatut.get(c) != EStatutPlayer.WIN){
				relClientStatut.put(c, EStatutPlayer.WIN);
				stopParty();
			}*/
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
			if (clients.size() == 2) return false;
			if (!clients.contains(c)){
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
				//TODO INFORMER CLIENT 
			}
			relClientStatut.remove(c);
			return clients.remove(c);
		}
	}

}
