package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import message.party.EStatutParty;
import message.party.EStatutPlayer;
import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import model.players.IAPlayer;
import model.players.RemoteLanPlayer;
import model.players.RemotePlayer;
import network.NetworkClient;

import commun.Player;
import commun.puyo.Puyo_PartyOption;

public class Party extends Observable implements Observer {
	private int id;
	private HashMap<Player, Plateau> playersMap = new HashMap<Player, Plateau>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<Piece> pieces = new ArrayList<Piece>();

	private Puyo_PartyOption option = new Puyo_PartyOption();
	private EStatutParty statut = EStatutParty.WAITING;
	private final Object lock = new Object();
	

	public Party () {

	}

	public Party ( int _id ) {
		setId(_id);
	}

	public Player getOpponent(Player player) {
		synchronized (lock) {
			int indexPlayer = players.indexOf(player);
			for ( int i = indexPlayer + 1; i < players.size(); i++ ) {
				if ( playersMap.get(players.get(i)).getStatut() == EStatutPlayer.INGAME ) { return playersMap.get(players.get(i)).getPlayer(); }
			}

			for ( int i = 0; i < indexPlayer; i++ ) {
				if ( playersMap.get(players.get(i)).getStatut() == EStatutPlayer.INGAME ) { return playersMap.get(players.get(i)).getPlayer(); }
			}
			return null;
		}
	}

	public Player getPlayer(Plateau p) {
		Map map = playersMap;
		for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			Player key = (Player) entry.getKey();
			Plateau value = (Plateau) entry.getValue();
			if ( value == p ) { return key; }
		}
		return null;
	}

	public Player getPlayer(String name) {
		for ( Player p : players ) {
			if ( p.getName().equals(name) ) return p;
		}
		return null;
	}

	public void stopGame() {
		synchronized (lock) {
			statut = EStatutParty.END;
			for (Player p : players) {
				playersMap.get(p).stopGame();
				if (p instanceof RemotePlayer){
					((RemotePlayer)p).stopPlay();
				}
			}
			updateObservers(statut);
			System.out.println("FIN DE PARTIE");
		}
	}

	public void startGame() {
		synchronized (lock) {
			Piece.setNextid(0);
			Piece.injectGraine(option.getGraine());
			statut = EStatutParty.INGAME;
			for (Plateau p : playersMap.values() ) {
				p.injectSeed(option.getGraine());
				Player player = p.getPlayer();
				if ( player != null){
					if (player instanceof RemotePlayer ) {
						((RemotePlayer) player).startPlay();
					}
					p.startGame();
					if ( player != null && player instanceof IAPlayer ) ((IAPlayer) player).runIA();
				}
			}
			updateObservers(statut);
			System.out.println("LANCEMENT DE PARTIE");
		}
	}

	public void addPlayer(Player p) {
		synchronized (lock) {
			Plateau plateau = new Plateau(playersMap.size(), this);
			plateau.addObserver(this);
			players.add(p);
			playersMap.put(p, plateau);
			if ( p instanceof RemoteLanPlayer ) {
				plateau.addObserver((RemoteLanPlayer) p);
			} else if ( p instanceof RemotePlayer ) {
				((RemotePlayer) p).setPlateau(plateau);
			} else if ( p instanceof IAPlayer ) {
				((IAPlayer) p).setPlateau(plateau);
				plateau.addObserver((IAPlayer) p);
			}
		}
	}

	public void deletePlayer(Player p) {
		synchronized (lock) {
			players.remove(p);
			Plateau plat = playersMap.remove(p);
			plat.stopGame();
		}
	}

	private final Object lockPiece = new Object();
	public Piece getPiece(Plateau plat) {
		synchronized (lockPiece) {
			int numPiece = plat.getNumPiece();
			if ( numPiece >= pieces.size() ) {
				pieces.add(new Piece());
			}
			// Update numPiece of plateau
			plat.setNumPiece(plat.getNumPiece() + 1);
			// GetPiece
			return pieces.get(numPiece).clone();
		}	
	}

	private final Object lockBlock = new Object();
	public final boolean lockNetworkBlock = false;
	public void sendBlock(Plateau plat, int nbBlock) {
		synchronized (lockBlock) {
			Player player = plat.getPlayer();
			Player opponent = getOpponent(plat.getPlayer());
			if ( opponent != null ) {
				if (player instanceof RemoteLanPlayer){
					NetworkClient.get().sendObject(new ActionPuyo(opponent.getName(), nbBlock));
				}else{
					playersMap.get(opponent).setReceiv_block(nbBlock);
				}
				
			}
		}
	}

	public void update(Observable o, Object arg) {
		// System.out.println("PARTYLAN " + arg);
		if ( arg instanceof EStatutPlayer ) {
			switch ( (EStatutPlayer) arg ) {
				case INGAME:

				break;
				case WAITING:

				break;
				case LOSE:
					updateObservers(o);
					Plateau winner = null;
					int nbPlateauAlive = 0;
					for ( Plateau p : playersMap.values() ) {
						if ( p.getStatut() == EStatutPlayer.INGAME ) {
							nbPlateauAlive++;
							winner = p;
						}
					}
					if (((Plateau) o).getPlayer() instanceof RemoteLanPlayer ) {
						NetworkClient.get().sendObject(arg);
					}
					if ( nbPlateauAlive == 1 ) {
						if (((Plateau) o).getPlayer() instanceof RemoteLanPlayer) return;
						winner.setStatut(EStatutPlayer.WIN);
					}
				break;
				case WIN:
					updateObservers(o);
					stopGame();
				break;
					
				case LEFT:
					NetworkClient.get().sendObject(arg);
					updateObservers(o);
					stopGame();
					break;
				default:
				break;
			}
		}

	}

	public synchronized ArrayList<Piece> getFuturePiece(Plateau plat) {
		ArrayList<Piece> p = new ArrayList<Piece>();
		try {
			p.addAll(pieces.subList(plat.getNumPiece() - 2, pieces.size()));
		} catch (Exception e) {
		}
		return p;
	}

	public HashMap<Player, Plateau> getPlayersMap() {
		return playersMap;
	}

	public void setPlayersMap(HashMap<Player, Plateau> playersMap) {
		this.playersMap = playersMap;
	}

	public EStatutParty getStatut() {
		return statut;
	}

	public void setStatut(EStatutParty statut) {
		this.statut = statut;
	}

	public ArrayList<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(ArrayList<Piece> pieces) {
		this.pieces = pieces;
	}

	public Puyo_PartyOption getOption() {
		return option;
	}

	public void setOption(Puyo_PartyOption option) {
		this.option = option;
	}

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

	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}
}
