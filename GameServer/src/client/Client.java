package client;

import game.puyo.Puyo_Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Observable;

import main.Config;
import message.EErreur;
import message.Identification;
import server.BanByIP;
import server.DataBase;
import server.Server;

import commun.EGames;
import commun.KeyEncrypt;
import commun.Player;

public class Client extends Observable implements Runnable {
	
	public enum EClientStatut{
		TRY_CONNECT, AUTH_SUCCESS, AUTH_FAIL, NEWSCORE, DISCONNECT
	}
	
	private static int nextid = 0;
	private Player player;
	private int id;
	private Server server;
	private EGames game;

	// TCP
	private Socket sock;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean run = false;

	public void interrupt() {
		try {
			in.close(); // Fermeture du flux si l'interruption n'a pas
						// fonctionn�.
		} catch (Exception e) {
		}
	}

	public Client ( Server server, Socket _sock) {
		this.addObserver(ClientManager.get());
		this.server = server;
		sock = _sock;
		id = nextid++;
		// Ouverture des flux
		try {
			out = new ObjectOutputStream(sock.getOutputStream());
			in = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			
		}
		updateObservers(EClientStatut.TRY_CONNECT);
	}
	
	private boolean connect(){
		KeyEncrypt key = new KeyEncrypt(5);
		//Envoie de la cle
		sendObject(new Identification(key));
		try {
			Object obj;
			if ((obj = in.readObject()) instanceof Identification){
				Identification ident = (Identification) obj;
				String name = KeyEncrypt.decrypt(key, ident.getEncrypt_name());
				String pwd = KeyEncrypt.decrypt(key, ident.getEncrypt_pwd());
				if (Config.DB_USE){
					// R�cup�ration de l'utilisateur sur la base de donn�es
					ResultSet res =  DataBase.get().getUser(name);
					// R�ponse conditionnelle au client
					if ( res != null && res.getString("password").equals(pwd)) {
						player = new Player(name, -1);
						if (ClientManager.get().getClientByName(player.getName()) == null){
							game = ident.getGame();
							player.setScore(DataBase.get().getPlayerScore(name, game));
							player.setCountry(ident.getCountryCode());
							sendObject(new Identification(player));
							updateObservers(EClientStatut.AUTH_SUCCESS);
							return true;
						}
					}
				}else{
					player = new Player(name, 1000);
					game = ident.getGame();
					player.setCountry(ident.getCountryCode());
					sendObject(new Identification(player));
					updateObservers(EClientStatut.AUTH_SUCCESS);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//ERREUR 
		sendObject(EErreur.AUTH_FAIL);
		BanByIP.addAvert(this);
		updateObservers(EClientStatut.AUTH_FAIL);
		return false;
	}

	public void run() {
		if (connect()){
			run = true;
			while ( run ) {
				Object obj = null;
				try {
					obj = in.readObject();
					managePacket(obj);
				} catch (Exception e) {
					System.out.println(player.getName() + " : " + e.getLocalizedMessage());
					run = false;
					break;
				}
				obj = null;
			}
		}
		destroy();
	}

	public void managePacket(Object obj) {
		//GESTION de partie
		switch (game){
			case GAME_PUYO:
				Puyo_Network.managePuyoPlayer(this, obj);
				break;
			case GAME_CHESS:
				break;
			default : break;
		}
	}
	
	public synchronized void sendObject(Object obj) {
		try {
			out.writeObject(obj);
			out.reset();
		} catch (Exception e) {}
	}

	public InetAddress getInetAddress() {
		return sock.getInetAddress();
	}

	public void closeFlux() {
		try {
			out.close();
		} catch (Exception e) {
		}
		try {
			in.close();
		} catch (Exception e) {
		}
		try {
			sock.close();
		} catch (Exception e) {
		}
	}

	public void destroy() {
		closeFlux();
		updateObservers(EClientStatut.DISCONNECT);
		server = null;
	}

	@Override
	public String toString() {
		return player.getName();
	}

	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}


	public int getUid() {
		return id;
	}

	public void setUid(int uid) {
		this.id = uid;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	public EGames getGame() {
		return game;
	}

	public void setGame(EGames game) {
		this.game = game;
	}

}