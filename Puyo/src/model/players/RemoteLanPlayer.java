package model.players;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import model.IA;
import model.Piece;
import model.Plateau;
import model.StrokeIA;
import network.NetworkClient;

import commun.Player;

public class RemoteLanPlayer extends Player implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	
	public RemoteLanPlayer ( Player p ) {
		name = p.getName();
		score = p.getScore();
	}

	public void runIA(final Plateau plateau) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				ArrayList<Piece> p = new ArrayList<Piece>(plateau.getParty().getFuturePiece(plateau).subList(0, 2));
				try{
					StrokeIA ia = IA.getIAStroke(plateau.getPlateau(), p, plateau.caseWithPuyo1(), plateau.caseWithPuyo2());
					IA.playStrokeIA(plateau, ia);
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	public void update(Observable arg0, Object arg1) {
		if ( arg1 instanceof EMovePlateau ) {
			switch((EMovePlateau)arg1){
				case DOWNAUTO:
				case DOWN:
				case LEFT:
				case RIGHT:
				case ROT1:
				case ROT2:
				case DROPPED : 
					NetworkClient.get().sendObject(new ActionPuyo((EMovePlateau)arg1, ((Plateau)arg0).getSerial()));
					break;
				case UPDATEPLAT : 
					NetworkClient.get().sendObject(new ActionPuyo((EMovePlateau)arg1, ((Plateau)arg0).getSerial()));
					runIA((Plateau)arg0);
					break;
				default: break;
			}
		}else if ( arg1 instanceof ActionPuyo){
			//Validation de la reception des pierres
			NetworkClient.get().sendObject((ActionPuyo)arg1);
		}
	}
}
