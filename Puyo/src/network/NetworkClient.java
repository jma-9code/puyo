package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.EventListenerList;

import main.Config;
import message.Identification;
import model.Party;

import commun.EGames;
import commun.Player;
import commun.puyo.Puyo_PartyInfo;

public class NetworkClient extends Observable {
	private static String SERVER_IP = "127.0.0.1";
	static{
		try {
			SERVER_IP = InetAddress.getByName(Config.ADRESS_SERV).getHostAddress();
		} catch (UnknownHostException e) {}
	}
	
	public enum ENetworkClient{
		CONNECTED, DISCONNECTED
	}
	
	private static final Object lock = new Object();
	private Socket socketTCP = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private static NetworkClient networkClient = new NetworkClient();
	private boolean run = false;
	private boolean interrupt = false;
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<Puyo_PartyInfo> partysInfo = new ArrayList<Puyo_PartyInfo>();
	private Player himself = null;
	private Party curParty = null;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private EventListenerList listeners = new EventListenerList();
	private StringBuffer msg2All = new StringBuffer();
	private StringBuffer msg2Party = new StringBuffer();

	private NetworkClient () {
		// Lancement dans un thread
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while ( !interrupt ) {
					if ( connect() ) {
						start();
					} else {
						System.out.println("UNUABLE TO CONNECT RETRY 30s");
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
						}
					}
					updateObservers(ENetworkClient.DISCONNECTED);
				}
				System.out.println("ENDNETWORK");
			}
		});
	}

	public static NetworkClient get() {
		return networkClient;
	}

	private boolean connect() {
		try {
			socketTCP = new Socket(SERVER_IP, Config.SERVER_PORT);
			// Ouverture des flux
			out = new ObjectOutputStream(socketTCP.getOutputStream());
			in = new ObjectInputStream(socketTCP.getInputStream());
			if ( identification() ) {
				updateObservers(ENetworkClient.CONNECTED);
				return true;
			}
		} catch (Exception e) {
			socketTCP = null;
			out = null;
			in = null;
		}
		return false;
	}

	private boolean identification() {
		Object obj;
		try {
			if ( (obj = in.readObject()) instanceof Identification ) {
				Identification ident = (Identification) obj;
				sendObject(new Identification(ident.getKey(), Config.NAME, Config.PWD, Locale.getDefault().getCountry().toLowerCase(),EGames.GAME_PUYO));
				obj = in.readObject();
				if ( obj instanceof Identification ) {
					himself = ((Identification) obj).getPlayer();
					System.out.println("AUTHREUSSI - " + himself);
					return true;
				} else {
					System.out.println("AUTHFAIL");
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

	private void start() {
		run = true;
		while ( run ) {
			try {
				Object obj = in.readObject();
				NetworkManager.get().manageObj(obj);
			} catch (Exception e) {
				e.printStackTrace();
				run = false;
			}
		}
	}

	public Puyo_PartyInfo getPartyInfobyId(int id) {
		synchronized (lock) {
			for ( Puyo_PartyInfo info : partysInfo ) {
				if ( info.getId() == id ) { return info; }
			}
			return null;
		}
	}

	/**
	 * Permet de close le programme, meme si on est dans la boucle principal
	 */
	public void interrupt() {
		synchronized (lock) {
			interrupt = true;
			run = false;
			try {
				in.close(); // Fermeture du flux
			} catch (Exception e) {
			}
			try {
				socketTCP.close(); // Fermeture du flux
			} catch (Exception e) {
			}
			executor.shutdownNow();
		}
	}

	public synchronized void sendObject(Object obj) {
		synchronized (lock) {
			try {
				out.writeObject(obj);
				out.reset();
			} catch (Exception e) {
			}
		}
	}

	public EventListenerList getListeners() {
		return listeners;
	}

	public void setListeners(EventListenerList listeners) {
		this.listeners = listeners;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public ArrayList<Puyo_PartyInfo> getPartysInfo() {
		return partysInfo;
	}

	public void setPartysInfo(ArrayList<Puyo_PartyInfo> partysInfo) {
		this.partysInfo = partysInfo;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public Party getCurParty() {
		return curParty;
	}

	public void setCurParty(Party curParty) {
		this.curParty = curParty;
	}

	public StringBuffer getMsg2All() {
		return msg2All;
	}

	public void setMsg2All(StringBuffer msg2All) {
		this.msg2All = msg2All;
	}

	public StringBuffer getMsg2Party() {
		return msg2Party;
	}

	public void setMsg2Party(StringBuffer msg2Party) {
		this.msg2Party = msg2Party;
	}
	
	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

}
