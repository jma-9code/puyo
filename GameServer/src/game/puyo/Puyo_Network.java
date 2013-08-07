package game.puyo;

import commun.puyo.PartyInfo;
import commun.puyo.PartyOption;
import commun.puyo.Puyo_PartyOption;

import message.Talk2All;
import message.Talk2Party;
import message.party.EStatutPlayer;
import message.party.JoinParty;
import message.party.LeftParty;
import message.party.ManageParty;
import message.party.NewParty;
import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import message.puyo.StartParty;
import client.Client;
import client.ClientManager;

public class Puyo_Network {

	public static void managePuyoPlayer(Client c, Object obj){
		//Creation d'une partie
		if (obj instanceof NewParty){
			if (Puyo_PartyManager.get().addParty(new Puyo_Party(c))){
				c.sendObject(new NewParty(Puyo_PartyManager.get().isInParty(c).getId()));
			}else{
				c.sendObject(new NewParty(-1));
			}
		}else if (obj instanceof JoinParty){
			if (Puyo_PartyManager.get().joinParty(((JoinParty) obj).getId(), c)){
				c.sendObject(new JoinParty(Puyo_PartyManager.get().isInParty(c).getId()));
			}else{
				c.sendObject(new JoinParty(-1));
			}
		}else if (obj instanceof LeftParty){
			Puyo_PartyManager.get().leftParty(c);
			c.sendObject(new LeftParty());
		}else if (obj instanceof StartParty){
			Puyo_Party p = (Puyo_Party) Puyo_PartyManager.get().isInParty(c);
			if (p != null){
				p.startParty();
			}
		}else if (obj instanceof ActionPuyo){
			Puyo_Party p = (Puyo_Party) Puyo_PartyManager.get().isInParty(c);
			if (p == null) return;
			
			//Renseigne lauteur du msg
			ActionPuyo amove = (ActionPuyo)obj;
			amove.setOwner(c.getPlayer().getName());
			
			if (amove.getMove() == EMovePlateau.GARBADGE){
				if (amove.getReceiver() != null){
					//Envoie de pierre vers X
					for (Client cli : p.getClients()){
						if (cli.getPlayer().getName().equalsIgnoreCase(amove.getReceiver())){
							cli.sendObject(amove);
							break;
						}
					}
				}else{
					//Validation des pierres, envoi vers fantomes
					for (Client cli : p.getClients()){
						if ( cli != c){
							cli.sendObject(amove);
						}
					}
				}
				return;
			}
			
			//Envoi a tlm sauf lui meme
			for (Client cli : p.getClients()){
				if ( cli != c){
					cli.sendObject(amove);
				}
			}
		}else if (obj instanceof Puyo_PartyOption){
			Puyo_Party p = (Puyo_Party) Puyo_PartyManager.get().isInParty(c);
			if (p != null){
				p.setOption((Puyo_PartyOption)obj);
				for (Client cli : p.getClients()){
					if ( cli != c){
						cli.sendObject(obj);
					}
				}
			}
		}
		else if (obj instanceof EStatutPlayer){
			EStatutPlayer statut = (EStatutPlayer)obj;
			Puyo_Party p = (Puyo_Party) Puyo_PartyManager.get().isInParty(c);
			if (p != null){
				if (statut == EStatutPlayer.LOSE){
					p.playerHaveLose(c);
				}
			}
		}
		
		
		//Msg public
		if (obj instanceof Talk2All){
			Talk2All msg2All = (Talk2All)obj;
			msg2All.setMessage(c.getPlayer().getName() + " : " + msg2All.getMessage());
			ClientManager.get().sendToAll(msg2All);
		}
		//msg prive
		else if (obj instanceof Talk2Party){
			Talk2Party msg2party = (Talk2Party)obj;
			msg2party.setMessage(c.getPlayer().getName() + " : " + msg2party.getMessage());
			Puyo_Party party = (game.puyo.Puyo_Party) Puyo_PartyManager.get().isInParty(c);
			if ( party != null){
				for (Client client : party.getClients()){
					client.sendObject(msg2party);
				}
			}
		}
	}
	
}
