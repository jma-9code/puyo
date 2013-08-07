package network;

import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.Config;
import message.Talk2All;
import message.Talk2Party;
import message.party.EStatutPlayer;
import message.party.EndParty;
import message.party.JoinParty;
import message.party.LeftParty;
import message.party.ManageParty;
import message.party.NewParty;
import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import message.puyo.StartParty;
import model.Party;
import model.Plateau;
import model.players.RemoteLanPlayer;
import model.players.RemotePlayer;

import commun.Player;
import commun.puyo.PartyOption;
import commun.puyo.Puyo_PartyInfo;
import commun.puyo.Puyo_PartyOption;

public class NetworkManager extends Observable {
	public enum NMInfo {
		NEWPARTY, JOINPARTY, LEFTPARTY, UPDATEPLAYERS, UPDATEPARTYS, MSG2PARTY, MSG2ALL, STARTPARTY, ENDPARTY, UPDATEPARTYOPTION
	}

	private static NetworkManager network = new NetworkManager();

	private NetworkManager () {

	}

	public static NetworkManager get() {
		return network;
	}

	public void manageObj(final Object obj) {
		manageObjExecutor(obj);
	}

	private void manageObjExecutor(Object obj) {
		if ( obj instanceof ArrayList<?> ) {
			Object element = null;
			for ( Object o : (ArrayList<?>) obj ) {
				element = o;
				break;
			}
			if ( element instanceof Player ) {
				NetworkClient.get().getPlayers().clear();
				NetworkClient.get().getPlayers().addAll((ArrayList<Player>) obj);
				updateObservers(NMInfo.UPDATEPLAYERS);
			} else if ( element instanceof Puyo_PartyInfo ) {
				NetworkClient.get().getPartysInfo().clear();
				NetworkClient.get().getPartysInfo().addAll((ArrayList<Puyo_PartyInfo>) obj);
				updateObservers(NMInfo.UPDATEPARTYS);
			}
		}
		// Message public
		else if ( obj instanceof Talk2All ) {
			Talk2All msg2all = (Talk2All) obj;
			NetworkClient.get().getMsg2All().append(msg2all.getMessage() + "\n");
			updateObservers(NMInfo.MSG2ALL);
		}
		// Message privee
		else if ( obj instanceof Talk2Party ) {
			Talk2Party msg2party = (Talk2Party) obj;
			NetworkClient.get().getMsg2Party().append(msg2party.getMessage() + "\n");
			updateObservers(NMInfo.MSG2PARTY);
		}
		else if ( obj instanceof NewParty ) {
			if ( ((NewParty) obj).getId() < 0 ) return;
			System.out.println("CREATION DUNE PARTIE");
			Party party = new Party(((NewParty) obj).getId());
			NetworkClient.get().setCurParty(party);
			NetworkClient.get().getMsg2Party().delete(0, NetworkClient.get().getMsg2Party().length());
			updateObservers(NMInfo.NEWPARTY);
		} 
		else if ( obj instanceof JoinParty ) {
			if ( ((JoinParty) obj).getId() < 0 ) return;
			System.out.println("JOIN DUNE PARTIE");
			Party party = new Party(((JoinParty) obj).getId());
			NetworkClient.get().setCurParty(party);
			NetworkClient.get().getMsg2Party().delete(0, NetworkClient.get().getMsg2Party().length());
			updateObservers(NMInfo.JOINPARTY);
		} 
		else if ( obj instanceof LeftParty ) {
			NetworkClient.get().setCurParty(null);
			updateObservers(NMInfo.LEFTPARTY);
		} 
		else if ( obj instanceof EndParty ) {
			updateObservers(NMInfo.ENDPARTY);
		} 
		else if ( obj instanceof StartParty ) {
			if ( ((StartParty) obj).isStarted() ) {
				NetworkClient.get().getCurParty().setOption((Puyo_PartyOption) ((StartParty) obj).getPinfo().getOption());
				for ( Player p : ((StartParty) obj).getPinfo().getPlayers() ) {
					if ( p.getName().equals(Config.NAME) ) {
						RemoteLanPlayer rlplayer = new RemoteLanPlayer(p);
						NetworkClient.get().getCurParty().addPlayer(rlplayer);
					} else {
						RemotePlayer rplayer = new RemotePlayer(p);
						NetworkClient.get().getCurParty().addPlayer(rplayer);
					}
				}
				updateObservers(NMInfo.STARTPARTY);
			} else {
				System.out.println("ERREUR");
			}

		} else if ( obj instanceof ActionPuyo ) {
			ActionPuyo action = (ActionPuyo)obj;
			Party p = NetworkClient.get().getCurParty();
			if ( p == null ) return;
			
			//Auteur du msg
			Player ownerPlayer = p.getPlayer(action.getOwner());
			if ( ownerPlayer == null) return;
			
			switch(action.getMove()){
				case DOWN:
				case DOWNAUTO:
				case LEFT:
				case RIGHT:
				case ROT1:
				case ROT2:
				case HAVELEFT:
				case HAVELOSE:
				case UPDATEPLAT:
				case DROPPED:
					//Envoi sur un tableau ecran
					if (ownerPlayer instanceof RemotePlayer) {
						((RemotePlayer) ownerPlayer).putAction((ActionPuyo) obj);
					}
					break;
				case HAVEWIN:
					//Envoi sur un tableau ecran
					if (ownerPlayer instanceof RemotePlayer) {
						((RemotePlayer) ownerPlayer).putAction((ActionPuyo) obj);
					}else if (ownerPlayer instanceof RemoteLanPlayer){
						Plateau plateau = p.getPlayersMap().get(ownerPlayer);
						plateau.setStatut(EStatutPlayer.WIN);
						plateau.updateObservers(plateau.getStatut());
					}
					break;
				case GARBADGE:
					//Reception de garbadge, renvoi au ecran fantome
					Player receiverPlayer = p.getPlayer(action.getReceiver());
					if (receiverPlayer instanceof RemoteLanPlayer){
						Plateau plateau = p.getPlayersMap().get(receiverPlayer);
						plateau.setReceiv_block(action.getNbGarbadge());
					}
					break;
				default: break;
			}
		} else if ( obj instanceof Puyo_PartyOption ) {
			NetworkClient.get().getCurParty().setOption((Puyo_PartyOption)obj);
			updateObservers(NMInfo.UPDATEPARTYOPTION);
		}
	}

	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}
}
