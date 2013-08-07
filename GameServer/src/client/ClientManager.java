package client;

import game.AbsPartyManager;
import game.puyo.Puyo_PartyManager;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import client.Client.EClientStatut;

import commun.Player;

public class ClientManager implements Observer {
	
	private static ClientManager instance = new ClientManager();
	private ArrayList<Client> clients = new ArrayList<Client>();
	private static final Object lock = new Object();

	private ClientManager () {

	}

	public static ClientManager get(){
		return instance;
	}

	public void addClient(Client c) {
		synchronized (lock) {
			System.out.println("Client authentifie : " +c.getPlayer().getName() + " -> " + c.getGame());
			clients.add(c);
			sendToAll(getListPlayer());
			sendToAll(Puyo_PartyManager.get().getAllPartyInfo());
		}
	}

	public void removeClient(Client c) {
		synchronized (lock) {
			if (clients.contains(c) ) {
				Puyo_PartyManager.get().leftParty(c);
				clients.remove(c);
				System.out.println("Client deconnexion : " +c.getPlayer().getName());
				c.interrupt();
				sendToAll(getListPlayer());
			}
		}
	}

	public void removeClients() {
		synchronized (lock) {
			boolean removed = false;
			for ( int i = 0; i < clients.size(); i++ ) {
				Client c = clients.get(i);
				c.interrupt();
				removed = true;
			}
			clients.clear();
			System.out.println("Suppresion de tout les clients du serveur");
		}
	}

	private ArrayList<Player> getListPlayer(){
		synchronized (lock) {
			ArrayList<Player> players = new ArrayList<Player>();
			for (Client c : clients){
				players.add(c.getPlayer());
			}
			return players;
		}
	}
	
	public Client getClientByName(String name){
		synchronized (lock) {
			for (Client c : clients){
				if (c.getPlayer().getName().equalsIgnoreCase(name)){
					return c;
				}
			}
			return null;
		}
	}
	
	public void sendToAll(Object obj) {
		synchronized (lock) {
			for (Client c : clients){
				c.sendObject(obj);
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof EClientStatut){
			switch((EClientStatut)arg1){
				case TRY_CONNECT:
					System.out.println("Tentative de connexion : " + ((Client)arg0).getInetAddress());
					break;
				case AUTH_FAIL:
					System.out.println("Echec lors de l'authentification : " + ((Client)arg0).getInetAddress());
					break;
				case AUTH_SUCCESS:
					addClient((Client)arg0);
					break;
				case DISCONNECT:
					removeClient((Client)arg0);
					break;
				case NEWSCORE:
					System.out.println("MAJ Score : " + ((Client)arg0).getPlayer().getName());
					sendToAll(getListPlayer());
					break;
				default : break;
			}
		}
		
	}

}