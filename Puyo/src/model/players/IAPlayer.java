package model.players;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import message.puyo.EMovePlateau;
import model.IA;
import model.Piece;
import model.Plateau;
import model.StrokeIA;

import commun.Player;

public class IAPlayer extends Player implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Plateau plateau;
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public IAPlayer () {
		name = "IA" + new Random().nextInt(100);
	}

	public void runIA() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				ArrayList<Piece> p = new ArrayList<Piece>(plateau.getParty().getFuturePiece(plateau).subList(0, 2));
				try{
					StrokeIA ia = IA.getIAStroke(plateau.getPlateau(), p, plateau.caseWithPuyo1(), plateau.caseWithPuyo2());
					IA.playStrokeIA(plateau, ia);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( arg instanceof EMovePlateau ) {
			if ( (EMovePlateau) arg == EMovePlateau.UPDATEPLAT) {
				runIA();
			}
		}
	}

	public Plateau getPlateau() {
		return plateau;
	}

	public void setPlateau(Plateau plateau) {
		this.plateau = plateau;
	}

}
