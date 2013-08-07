package model.players;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import network.NetworkClient;

import message.party.EStatutPlayer;
import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import model.Movement;
import model.Plateau;

import commun.Player;

public class RemotePlayer extends Player {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Plateau plateau = null;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean playing = false;
	private LinkedBlockingQueue<ActionPuyo> actionQueue = new LinkedBlockingQueue<ActionPuyo>();

	public RemotePlayer (Player p) {
		name = p.getName();
		score = p.getScore();
	}

	public void stopPlay() {
		playing = false;
		executor.shutdownNow();
	}

	public void putAction(ActionPuyo action) {
		try {
			actionQueue.put(action);
		} catch (InterruptedException e) {}
	}

	public void startPlay() {
		playing = true;
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while ( playing ) {
					ActionPuyo amove = null;
					try {
						amove = actionQueue.take();
						boolean movement = false;
						if (amove.getSerial() != null){
							plateau.unSerial(amove.getSerial());
						}
						/*switch(amove.getMove()){
							//Avant upadate
							case DROPPED:
								if (!amove.getSerial().equals(plateau.getSerial())){
									System.out.println("resync");
									plateau.unSerial(amove.getSerial());
								}
								plateau.update_plateau();
								break;
							//apres update
							case UPDATEPLAT:
								if (!amove.getSerial().equals(plateau.getSerial())){
									System.out.println("resync");
									plateau.unSerial(amove.getSerial());
								}
								break;
							case GARBADGE:
								plateau.setReceiv_block(amove.getNbGarbadge());
								break;	
								
							case HAVELOSE:
								plateau.setStatut(EStatutPlayer.LOSE);
								plateau.updateObservers(plateau.getStatut());
								break;
							case HAVEWIN:
								plateau.setStatut(EStatutPlayer.WIN);
								plateau.updateObservers(plateau.getStatut());
								break;
								
							//MOVEMENTS
							case DOWN:
								movement = true;
								Movement.move_down(plateau, false);
							break;
							case LEFT:
								movement = true;
								Movement.move_left(plateau);
							break;
							case RIGHT:
								movement = true;
								Movement.move_right(plateau);
							break;
							case ROT1:
								movement = true;
								Movement.move_rot1(plateau);
							break;
							case ROT2:
								movement = true;
								Movement.move_rot2(plateau);
							case DOWNAUTO:
								movement = true;
								Movement.move_down(plateau, true);
							break;
							default : break;
						}
						
						if (movement){
							if (!amove.getSerial().equals(plateau.getSerial())){
								System.out.println("resync");
								plateau.unSerial(amove.getSerial());
							}
						}
						*/
					}catch (Exception e) {}
				}
			}
		});
	}

	public Plateau getPlateau() {
		return plateau;
	}

	public void setPlateau(Plateau plateau) {
		this.plateau = plateau;
	}

}
