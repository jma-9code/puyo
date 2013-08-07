package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import main.Config;
import message.party.EStatutPlayer;
import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import model.Puyo.PuyoColor;
import model.players.RemoteLanPlayer;
import model.players.RemotePlayer;

import commun.KeyEncrypt;
import commun.Player;

public class Plateau extends Observable {
	public enum EInfoPlateau {
		NEWPIECE, GARBADGE, CHAIN0, CHAIN1, CHAIN2, CHAIN3, CHAIN4, CHAIN5, CHAIN6
	}

	private ArrayList<EInfoPlateau> chains = new ArrayList<Plateau.EInfoPlateau>(
			Arrays.asList(EInfoPlateau.CHAIN0, EInfoPlateau.CHAIN1, EInfoPlateau.CHAIN2,
					EInfoPlateau.CHAIN3, EInfoPlateau.CHAIN4, EInfoPlateau.CHAIN5, EInfoPlateau.CHAIN6));
	
	private EStatutPlayer statut = EStatutPlayer.WAITING;
	private int id;

	private Case[][] plateau = new Case[Config.NB_LINE][Config.NB_COL];

	private int numPiece = 0;
	private Piece curPiece, nextPiece;
	private Stat stat = new Stat();
	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Plateau himSelf;
	private Party party;
	private boolean checkPlateau = false;
	private boolean waitDrop = false;
	private boolean inMovement = false;
	private int receiv_block = 0;
	private final Object lockMovement = new Object();

	private Random r = new Random();
	private long seed = 0;

	public Plateau ( int _id, Party _party ) {
		himSelf = this;
		id = _id;
		party = _party;
		initPlateau();
		statut = EStatutPlayer.WAITING;
		updateObservers(statut);
	}
	
	//tableau + / + receiv_block + / + stat.getScore()
	public String getSerial(){
		synchronized (lockMovement) {
			//tableau
			StringBuffer ret = new StringBuffer();
			for (int i=0; i<plateau.length; i++){
				for (int j=0; j<plateau[0].length; j++){
					ret.append(((plateau[i][j].getPuyo() == null) ? 9 : plateau[i][j].getPuyo().getColor().ordinal()));
				}
			}
			ret.append('/');
			/*ret.append(receiv_block);
			ret.append('/');
			ret.append(stat.get_score());*/
			return ret.toString();
		}
	}
	
	public void unSerial(String serial){
		synchronized (lockMovement) {
			String[] serialData = serial.split("/");
			if(serialData.length != 1) return;
			int index = 0;
			for (int i=0; i<plateau.length; i++){
				for (int j=0; j<plateau[0].length; j++){
					char c = serialData[0].charAt(index);
					if (c != '9'){
						plateau[i][j].setPuyo(new Puyo(PuyoColor.values()[Integer.parseInt(""+c)]));
					}else{
						plateau[i][j].setPuyo(null);
					}
					index++;
				}
			}
			/*setReceiv_block(Integer.parseInt(serialData[1]));
			stat.set_score(Integer.parseInt(serialData[2]));*/
		}
	}
	
	public void introduce_firstPieces(Piece first, Piece next) {
		nextPiece = first;
		introduce_newpiece(next);
	}

	/**
	 * Verifie qu'il n"y a pas de trou dans le tableau entre deux pieces.
	 * 
	 * @return
	 */
	public static boolean checkDropAllPuyo(Case[][] plateau) {
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				if ( plateau[i][j].getPuyo() != null ) {
					if ( i + 1 < Config.NB_LINE && plateau[i + 1][j].getPuyo() == null ) { return false; }
				}
			}
		}
		return true;
	}

	public void dropAllPuyo() {
		if ( !checkDropAllPuyo(plateau) && curPiece == null ) {
			boolean drop = false;
			for ( int i = 0; i < Config.NB_LINE; i++ ) {
				drop = false;
				for ( int j = 0; j < Config.NB_COL; j++ ) {
					if ( plateau[i][j].getPuyo() != null ) {
						if ( Movement.try_down(plateau, plateau[i][j]) ) {
							Movement.replacePuyo(plateau[i][j], plateau[i + 1][j]);
							drop = true;
						}
					}
				}
				if ( drop ) {
					updateObservers(EMovePlateau.DROPALL);
					try {
						Thread.sleep(Config.TIMETODOWN);
					} catch (InterruptedException e) {
					}
				}
			}
			dropAllPuyo();
		}
	}

	public static ArrayList<Case> countRelation(Case[][] plateau, Case c, ArrayList<Case> cases) {
		if ( c.getPuyo() == null || c.getPuyo().getColor() == PuyoColor.BLACK ) { return cases; }
		cases.add(c);

		// bas
		if ( c.getX() + 1 < Config.NB_LINE && plateau[c.getX() + 1][c.getY()].getPuyo() != null && plateau[c.getX() + 1][c.getY()].getPuyo().equals(c.getPuyo()) ) {
			if ( !cases.contains(plateau[c.getX() + 1][c.getY()]) ) {
				countRelation(plateau, plateau[c.getX() + 1][c.getY()], cases);
			}
		}

		// Haut
		if ( c.getX() - 1 >= 0 && plateau[c.getX() - 1][c.getY()].getPuyo() != null && plateau[c.getX() - 1][c.getY()].getPuyo().equals(c.getPuyo()) ) {
			if ( !cases.contains(plateau[c.getX() - 1][c.getY()]) ) {
				countRelation(plateau, plateau[c.getX() - 1][c.getY()], cases);
			}
		}

		// droite
		if ( c.getY() + 1 < Config.NB_COL && plateau[c.getX()][c.getY() + 1].getPuyo() != null && plateau[c.getX()][c.getY() + 1].getPuyo().equals(c.getPuyo()) ) {
			if ( !cases.contains(plateau[c.getX()][c.getY() + 1]) ) {
				countRelation(plateau, plateau[c.getX()][c.getY() + 1], cases);
			}
		}

		// gauche
		if ( c.getY() - 1 >= 0 && plateau[c.getX()][c.getY() - 1].getPuyo() != null && plateau[c.getX()][c.getY() - 1].getPuyo().equals(c.getPuyo()) ) {
			if ( !cases.contains(plateau[c.getX()][c.getY() - 1]) ) {
				countRelation(plateau, plateau[c.getX()][c.getY() - 1], cases);
			}
		}
		return cases;
	}

	private void update_stat(int nb_puyo_explo, int nb_combo, int nb_pierre) {
		// update stats
		if ( nb_puyo_explo > stat.get_explo_max() ) {
			stat.set_explo_max(nb_puyo_explo);
		}
		if ( nb_combo > 1 ) {
			if ( nb_combo > stat.get_combo_max() ) {
				stat.set_combo_max(nb_combo - 1);
			}
			stat.set_combo_total(stat.get_combo_total() + nb_combo - 1);
			stat.set_combo_last(nb_combo - 1);
		}
		if ( nb_pierre > stat.get_pierre_max() ) {
			stat.set_pierre_max(nb_pierre);
		}

		stat.set_explo_total(stat.get_explo_total() + nb_puyo_explo);
		stat.set_pierre_total(stat.get_pierre_total() + nb_pierre);
		stat.set_score(stat.get_score() + nb_puyo_explo * nb_combo * 3);
		stat.set_explo_last(nb_puyo_explo);
		stat.set_pierre_last(nb_pierre);
	}

	public static boolean checkPuyoRelations(Case[][] plateau) {
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				ArrayList<Case> cases = countRelation(plateau, plateau[i][j], new ArrayList<Case>());
				if ( cases.size() >= 4 ) { return true; }
			}
		}
		return false;
	}

	public void update_plateau() {
		if (checkPlateau) return;
		updateObservers(EMovePlateau.DROPPED);
		checkPlateau = true;
		curPiece = null;
		// Enleve les trou
		dropAllPuyo();
		int nb_puyo_explo = 0;
		int nb_combo = 0;
		do {
			boolean destroy = false;
			for ( int i = 0; i < Config.NB_LINE; i++ ) {
				for ( int j = 0; j < Config.NB_COL; j++ ) {
					ArrayList<Case> cases = countRelation(plateau, plateau[i][j], new ArrayList<Case>());
					if ( cases.size() >= 4 ) {
						destroy = true;
						for ( Case c : cases ) {
							updateObservers(new Puyo2Destroy(c.getX(), c.getY(), c.getPuyo().getColor()));
							c.setPuyo(null);
							// destruction des pierres
							if ( c.getX() + 1 < Config.NB_LINE && plateau[c.getX() + 1][c.getY()].getPuyo() != null && plateau[c.getX() + 1][c.getY()].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
								plateau[c.getX() + 1][c.getY()].setPuyo(null);
								updateObservers(new Puyo2Destroy(c.getX() + 1, c.getY(), Puyo.PuyoColor.BLACK));
							}

							if ( c.getX() - 1 >= 0 && plateau[c.getX() - 1][c.getY()].getPuyo() != null && plateau[c.getX() - 1][c.getY()].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
								plateau[c.getX() - 1][c.getY()].setPuyo(null);
								updateObservers(new Puyo2Destroy(c.getX() - 1, c.getY(), Puyo.PuyoColor.BLACK));
							}

							if ( c.getY() + 1 < Config.NB_COL && plateau[c.getX()][c.getY() + 1].getPuyo() != null && plateau[c.getX()][c.getY() + 1].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
								plateau[c.getX()][c.getY() + 1].setPuyo(null);
								updateObservers(new Puyo2Destroy(c.getX(), c.getY() + 1, Puyo.PuyoColor.BLACK));
							}

							if ( c.getY() - 1 >= 0 && plateau[c.getX()][c.getY() - 1].getPuyo() != null && plateau[c.getX()][c.getY() - 1].getPuyo().getColor() == Puyo.PuyoColor.BLACK ) {
								plateau[c.getX()][c.getY() - 1].setPuyo(null);
								updateObservers(new Puyo2Destroy(c.getX(), c.getY() - 1, Puyo.PuyoColor.BLACK));
							}
						}
						nb_puyo_explo += cases.size();
						nb_combo += 1;
						updateObservers(chains.get((nb_combo>6) ? 6 : nb_combo));
					}
				}
			}
			if ( destroy ) {
				dropAllPuyo();
				try {
					Thread.sleep(Config.TIMEBETWEENCOMBO);
				} catch (InterruptedException e) {
				}
			}
		} while (checkPuyoRelations(plateau));
		// Pierre a envoyé
		int nbblocks = (int) (Math.floor((nb_puyo_explo) * (nb_puyo_explo - 3) / 10f + 0.5));
		update_stat(nb_puyo_explo, nb_combo, nbblocks);

		// Pierre a recevoir
		int nbBlockCompense = receiv_block - nbblocks;
		if (! (getPlayer() instanceof RemotePlayer)){
			receive_block(nbBlockCompense);
		}

		if (isLosing(plateau)) {
			if (!(getPlayer() instanceof RemotePlayer)){
				statut = EStatutPlayer.LOSE;
				updateObservers(statut);
				stopGame();
			}
		} else {
			if (nbBlockCompense < 0) {
				if (!(getPlayer() instanceof RemotePlayer)){
					party.sendBlock(this, Math.abs(nbBlockCompense));
				}
			}
			// Nouvelle piece
			try {
				Thread.sleep(Config.TIMEBEFORENEWPIECE);
			} catch (InterruptedException e) {
			}
			introduce_newpiece(party.getPiece(himSelf));
		}
		checkPlateau = false;
		updateObservers(EMovePlateau.UPDATEPLAT);
	}

	public void startGame() {
		introduce_firstPieces(party.getPiece(this), party.getPiece(this));
		statut = EStatutPlayer.INGAME;
		updateObservers(statut);
		
		//Si joueur distant pas de timer
		if ( getPlayer() instanceof RemotePlayer) return;
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Movement.move_down(himSelf, true);
			}
		}, party.getOption().getDownspeed(), party.getOption().getDownspeed(), TimeUnit.MILLISECONDS);
	}

	public void stopGame() {
		try {
			scheduler.shutdownNow();
			executor.shutdownNow();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Verification qu'on peut lancer une nouvelle piece.
	 * 
	 * @return
	 */
	public boolean checkEndStroke() {
		Case c_puyo1 = null;
		try {
			c_puyo1 = caseWithPuyo1();
		} catch (Exception e) {}
		Case c_puyo2 = null;
		try {
			c_puyo2 = caseWithPuyo2();
		} catch (Exception e) {}
		
		if ( c_puyo2 == null || c_puyo1 == null) return false;
		
		switch ( place_puyo2() ) {
		// haut
			case 1:
				if ( c_puyo1.getX() + 1 < Config.NB_LINE ) {
					if ( plateau[c_puyo1.getX() + 1][c_puyo1.getY()].getPuyo() != null ) { return true; }
				} else {
					return true;
				}
			break;
			// bas
			case 3:
				if ( c_puyo2.getX() + 1 < Config.NB_LINE ) {
					if ( plateau[c_puyo2.getX() + 1][c_puyo2.getY()].getPuyo() != null ) { return true; }
				} else {
					return true;
				}
			break;
			// g ou d
			case 2:
			case 4:
				if ( c_puyo1.getX() + 1 < Config.NB_LINE ) {
					if ( plateau[c_puyo1.getX() + 1][c_puyo1.getY()].getPuyo() != null ) { return true; }
				} else {
					return true;
				}

				if ( c_puyo2.getX() + 1 < Config.NB_LINE ) {
					if ( plateau[c_puyo2.getX() + 1][c_puyo2.getY()].getPuyo() != null ) { return true; }
				} else {
					return true;
				}
			break;

		}
		return false;
	}

	/**
	 * Initialisation du tableau, on remplie le tableau.
	 */
	public void initPlateau() {
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				plateau[i][j] = new Case(null, i, j);
			}
		}
	}

	public Case caseWithPuyo1() throws Exception {
		if ( curPiece == null ) { return null; }
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				if ( plateau[i][j].getPuyo() == null ) {
					continue;
				}
				if ( plateau[i][j].getPuyo() == curPiece.getPuyo1() ) { return plateau[i][j]; }
			}
		}

		return null;
	}

	public Case caseWithPuyo2()  throws Exception {
		if ( curPiece == null ) { return null; }
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				if ( plateau[i][j].getPuyo() == null ) {
					continue;
				}
				if ( plateau[i][j].getPuyo() == curPiece.getPuyo2() ) { return plateau[i][j]; }
			}
		}
		return null;
	}

	/**
	 * Cette fonction permet de retourner la place du puyo2 en fonction du
	 * puyo1. 1 : Haut 2 : Droite 3 : Bas 4 : Gauche
	 * 
	 * @return
	 */
	public int place_puyo2() {
		Case c_puyo1 = null;
		try {
			c_puyo1 = caseWithPuyo1();
		} catch (Exception e) {}
		Case c_puyo2 = null;
		try {
			c_puyo2 = caseWithPuyo2();
		} catch (Exception e) {}
		if ( c_puyo1 != null && c_puyo2 != null ) {
			
			int x = c_puyo1.getX() - c_puyo2.getX();
			int y = c_puyo1.getY() - c_puyo2.getY();
			if ( x == 1 ) {
				return 1;
			} else if ( x == -1 ) {
				return 3;
			} else if ( y == 1 ) {
				return 4;
			} else if ( y == -1 ) { return 2; }
		}
		return 0;
	}

	public static boolean isLosing(Case[][] plateau) {
		int cpt = 0;
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			if ( plateau[i][Config.NB_COL / 2 - 1].getPuyo() != null ) {
				cpt += 1;
			}
		}

		if ( cpt >= Config.NB_LINE - 1 ) { return true; }
		return false;
	}

	/**
	 * Permet de r�cup�rer une nouvelle pi�ce, et de l'introduire dans le
	 * tableau
	 */
	private void introduce_newpiece(Piece _nextPiece) {
		if ( curPiece != null ) { return; }
		if ( plateau[0][Config.NB_COL / 2 - 1].getPuyo() != null || plateau[1][Config.NB_COL / 2 - 1].getPuyo() != null ) { return; }

		curPiece = nextPiece;
		plateau[1][Config.NB_COL / 2 - 1].setPuyo(curPiece.getPuyo1());
		plateau[0][Config.NB_COL / 2 - 1].setPuyo(curPiece.getPuyo2());
		nextPiece = _nextPiece;
		updateObservers(EInfoPlateau.NEWPIECE);
	}

	public void receive_block(int nb) {
		if ( nb <= 0 ) return;
		setReceiv_block(nb);
		//updateObservers(new ActionMove((String)null, nb));
		int nbLine = nb / Config.NB_COL;
		int rest = nb % Config.NB_COL;
		Random rand = new Random(seed);
		// Ajout des restes
		boolean tab_bool[] = new boolean[Config.NB_COL];
		for ( int i = 0; i < Config.NB_COL; i++ )
			tab_bool[i] = true;

		int restDrop = 0;
		ArrayList<Integer> listInt = new ArrayList<Integer>();
		while ( restDrop < rest ) {
			listInt.clear();
			for ( int i = 0; i < Config.NB_COL; i++ ) {
				if ( tab_bool[i] ) {
					listInt.add(i);
				}
			}
			int choice = rand.nextInt(listInt.size());
			if ( plateau[0][listInt.get(choice)].getPuyo() == null ) {
				plateau[0][listInt.get(choice)].setPuyo(new Puyo(Puyo.PuyoColor.BLACK));
			}

			tab_bool[listInt.get(choice)] = false;
			restDrop++;
		}

		// Ajout des lignes de pierre
		for ( int i = 1; i < nbLine + 1 && i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				if ( plateau[i][j].getPuyo() == null ) {
					plateau[i][j].setPuyo(new Puyo(Puyo.PuyoColor.BLACK));
				}
			}
		}

		dropAllPuyo();
		
		setReceiv_block(0);
	}

	@Override
	public String toString() {
		String ret = "";
		for ( int i = 0; i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				if ( plateau[i][j].getPuyo() != null ) {
					ret += plateau[i][j].getPuyo();
				} else {
					ret += " ";
				}
			}
			ret += "\n";
		}
		return ret;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Case[][] getPlateau() {
		return plateau;
	}

	public void setPlateau(Case[][] plateau) {
		this.plateau = plateau;
	}

	public int getNumPiece() {
		return numPiece;
	}

	public void setNumPiece(int numPiece) {
		this.numPiece = numPiece;
	}

	public Piece getCurPiece() {
		return curPiece;
	}

	public void setCurPiece(Piece curPiece) {
		this.curPiece = curPiece;
	}

	public Piece getNextPiece() {
		return nextPiece;
	}

	public void setNextPiece(Piece nextPiece) {
		this.nextPiece = nextPiece;
	}

	public Stat getStat() {
		return stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public boolean isCheckPlateau() {
		return checkPlateau;
	}

	public void setCheckPlateau(boolean checkPlateau) {
		this.checkPlateau = checkPlateau;
	}

	public EStatutPlayer getStatut() {
		return statut;
	}

	public void setStatut(EStatutPlayer statut) {
		this.statut = statut;
		updateObservers(statut);
	}

	public void updateObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public boolean isWaitDrop() {
		return waitDrop;
	}

	public void setWaitDrop(boolean waitDrop) {
		this.waitDrop = waitDrop;
	}

	public Player getPlayer() {
		return party.getPlayer(this);
	}

	public void injectSeed(long graine) {
		r = new Random(graine);
		seed = graine;
	}

	public boolean isInMovement() {
		return inMovement;
	}

	public void setInMovement(boolean inMovement) {
		this.inMovement = inMovement;
	}

	public int getReceiv_block() {
		return receiv_block;
	}

	public synchronized void setReceiv_block(int receiv_garbadge) {
		int ancVal = receiv_block;
		this.receiv_block = receiv_garbadge;
		if ( receiv_garbadge != ancVal ){
			//TODO updateObservers(EMovePlateau.GARBADGE);
			updateObservers(EInfoPlateau.GARBADGE);
		}
	}

	public Object getLockMovement() {
		return lockMovement;
	}

}
