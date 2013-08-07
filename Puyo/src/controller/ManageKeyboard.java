package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import main.Config;
import message.party.EStatutPlayer;
import model.Movement;
import model.Plateau;

public class ManageKeyboard implements KeyListener, Observer {

	public enum Actions {
		DOWN, LEFT, RIGHT, ROT1, ROT2
	}
	
	private LinkedBlockingQueue<Actions> actionQueue = new LinkedBlockingQueue<Actions>();
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Plateau plateau;
	private boolean started = false;
	private boolean rotation = false;
	private Timer downer = null;
	private Timer lefter = null;
	private Timer rigther = null;

	public ManageKeyboard ( Plateau _plateau ) {
		plateau = _plateau;
		plateau.addObserver(this);
		start();
	}

	public void stop(){
		started = false;
		executor.shutdownNow();
	}
	
	public void start(){
		started = true;
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (started){
					try{
						switch ( actionQueue.take() ) {
							case DOWN:
								Movement.move_down(plateau, false);
							break;
							case LEFT:
								Movement.move_left(plateau);
							break;
							case RIGHT:
								Movement.move_right(plateau);
							break;
							case ROT1:
								Movement.move_rot1(plateau);
							break;
							case ROT2:
								Movement.move_rot2(plateau);
							break;
							default: break;
						}
					}catch (Exception e) {}
				}
			}
		});
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if ( e.getKeyCode() == Config.J0_LEFT ) {
			if ( lefter == null ) {
				lefter = new Timer(actionQueue, Config.KEY_TDP, Actions.LEFT, Config.KEY_REPEAT_DELAY);
			}
			if ( !lefter.isRunning() ) {
				lefter.myStart();
			}
		}
		if ( e.getKeyCode() == Config.J0_RIGHT ) {
			if ( rigther == null ) {
				rigther = new Timer(actionQueue, Config.KEY_TDP, Actions.RIGHT, Config.KEY_REPEAT_DELAY);
			}

			if (!rigther.isRunning()) {
				rigther.myStart();
			}
		}
		if ( e.getKeyCode() == Config.J0_DOWN ) {
			if ( downer == null ) {
				downer = new Timer(actionQueue, Config.KEY_TDP / 3, Actions.DOWN, Config.KEY_REPEAT_DELAY);
			}
			if ( !downer.isRunning() ) {
				downer.myStart();
			}
		}
		if ( e.getKeyCode() == Config.J0_ROT1 ) {
			rotation = true;
			try {
				actionQueue.put(Actions.ROT1);
			} catch (InterruptedException e1) {}
		}
		if ( e.getKeyCode() == Config.J0_ROT2 ) {
			rotation = true;
			try {
				actionQueue.put(Actions.ROT2);
			} catch (InterruptedException e1) {}
		}

	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if ( e.getKeyCode() == Config.J0_LEFT ) {
			lefter.finish();
			lefter = null;
		}
		if ( e.getKeyCode() == Config.J0_RIGHT ) {
			rigther.finish();
			rigther = null;
		}
		if ( e.getKeyCode() == Config.J0_DOWN ) {
			downer.finish();
			downer = null;
		}
		if ( e.getKeyCode() == Config.J0_ROT1 ) {
			rotation = false;
		}
		if ( e.getKeyCode() == Config.J0_ROT2 ) {
			rotation = false;
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		// Permet de gerer la pause avec la touche P, sur les partie locale
	}

	public boolean isRotation() {
		return rotation;
	}

	public void setRotation(boolean rotation) {
		this.rotation = rotation;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof EStatutPlayer){
			switch ((EStatutPlayer)arg){
				case LOSE:
				case WIN:
					stop();
					break;
			}
		}
		
	}
}
