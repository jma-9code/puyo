package controller;

import java.util.concurrent.LinkedBlockingQueue;

import controller.ManageKeyboard.Actions;

public class Timer extends Thread {

	private int timing;
	private boolean mustContinue;
	private LinkedBlockingQueue<Actions> actionQueue;
	private Actions action;
	private boolean direct;
	private int tbr; // delai avant repetition
	private boolean firstTop;

	public Timer ( LinkedBlockingQueue<Actions> actionQueue, int timing, Actions action, boolean direct, final int tbr ) {
		this.timing = timing;
		this.actionQueue = actionQueue;
		this.action = action;
		this.direct = direct;
		this.tbr = tbr;
		firstTop = true;
		mustContinue = false;
	}

	public Timer ( LinkedBlockingQueue<Actions> actionQueue, int timing, Actions action, int tbr ) {
		this(actionQueue, timing, action, true, tbr);
	}

	public boolean isRunning() {
		return mustContinue;
	}

	public void myStart() {
		mustContinue = true;
		start();
	}

	public void setTiming(final int timing) {
		this.timing = timing;
	}

	public int getTiming() {
		return timing;
	}

	public void finish() {
		mustContinue = false;
	}

	@Override
	public void run() {
		int t;
		while ( mustContinue ) {
			if ( direct && mustContinue ) {
				try {
					actionQueue.put(action);
				} catch (InterruptedException e) {}
			}
			if ( firstTop ) {
				t = tbr + timing;
				firstTop = false;
			} else {
				t = timing;
			}
			try {
				sleep(t);
			} catch (final InterruptedException e) {
				System.err.println("Erreur : processus interrompu pendant un sleep!");
			}
			if ( !direct && mustContinue ) {
				try {
					actionQueue.put(action);
				} catch (InterruptedException e) {}
			}
		}
	}

}
