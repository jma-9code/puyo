package model;

import main.Config;
import message.party.EStatutPlayer;
import message.puyo.EMovePlateau;
import model.players.RemotePlayer;

import commun.Player;

public class Movement {

	public static void replacePuyo(Case from, Case to) {
		if ( from.getPuyo() != null && to.getPuyo() == null ) {
			to.setPuyo(from.getPuyo());
			from.setPuyo(null);
		} else {
			System.out.println("DEPLACEMENT IMPOSSIBLE");
		}
	}

	public static boolean try_left(Case[][] plateau, Case c) {
		if ( c.getY() - 1 >= 0 && plateau[c.getX()][c.getY() - 1].getPuyo() == null ) { return true; }
		return false;
	}

	public static boolean try_right(Case[][] plateau, Case c) {
		if ( c.getY() + 1 < Config.NB_COL && plateau[c.getX()][c.getY() + 1].getPuyo() == null ) { return true; }
		return false;
	}

	public static boolean try_down(Case[][] plateau, Case c) {
		if ( c.getX() + 1 < Config.NB_LINE && plateau[c.getX() + 1][c.getY()].getPuyo() == null ) { return true; }
		return false;
	}

	public static boolean try_up(Case[][] plateau, Case c) {
		if ( c.getX() - 1 >= 0 && plateau[c.getX() - 1][c.getY()].getPuyo() == null ) { return true; }
		return false;
	}

	/* DEPLACEMENT */
	public static void move_rot1(Plateau plateau) {
		if ( plateau.isCheckPlateau() || plateau.getStatut() != EStatutPlayer.INGAME 
				|| plateau.isCheckPlateau()) { return; }
		synchronized (plateau.getLockMovement()) {
			boolean moved = false;
			Case c_puyo1 = null;
			try {
				c_puyo1 = plateau.caseWithPuyo1();
			} catch (Exception e) {}
			Case c_puyo2 = null;
			try {
				c_puyo2 = plateau.caseWithPuyo2();
			} catch (Exception e) {}
			if (c_puyo1 == null || c_puyo2 == null) return;
			
			switch ( plateau.place_puyo2() ) {
			// Haut vers droite
				case 1:
					if ( try_right(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() + 1][c_puyo2.getY() + 1]);
						moved = true;
					} else if ( !try_right(plateau.getPlateau(), c_puyo1) && try_left(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() - 1]);
						replacePuyo(c_puyo2, c_puyo1);
						moved = true;
					}
				break;
				// Droite vers bas
				case 2:
					if ( try_down(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() + 1][c_puyo2.getY() - 1]);
						moved = true;
					}
				break;
				// Bas vers gauche
				case 3:
					if ( try_left(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() - 1][c_puyo2.getY() - 1]);
						moved = true;
					} else if ( !try_left(plateau.getPlateau(), c_puyo1) && try_right(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() + 1]);
						replacePuyo(c_puyo2, c_puyo1);
						moved = true;
					}
				break;
				// gauche vers haut
				case 4:
					if ( try_up(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() - 1][c_puyo2.getY() + 1]);
						moved = true;
					}
				break;
				default:
					System.out.println("PLACE0");
				break;
			}
			if ( moved ) {
				plateau.updateObservers(EMovePlateau.ROT1);
			}
		}
		
	}

	public static void move_rot2(Plateau plateau) {
		if ( plateau.isCheckPlateau() || plateau.getStatut() != EStatutPlayer.INGAME 
				|| plateau.isCheckPlateau()) { return; }
		//plateau.setInMovement(true);
		synchronized (plateau.getLockMovement()) {
			Case c_puyo1 = null;
			try {
				c_puyo1 = plateau.caseWithPuyo1();
			} catch (Exception e) {}
			Case c_puyo2 = null;
			try {
				c_puyo2 = plateau.caseWithPuyo2();
			} catch (Exception e) {}
			if (c_puyo1 == null || c_puyo2 == null) return;
			boolean moved = false;
			switch ( plateau.place_puyo2() ) {
			// Haut vers gauche
				case 1:
					if ( try_left(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() + 1][c_puyo2.getY() - 1]);
						moved = true;
					} else if ( !try_left(plateau.getPlateau(), c_puyo1) && try_right(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() + 1]);
						replacePuyo(c_puyo2, c_puyo1);
						moved = true;
					}
				break;
				// gauche vers bas
				case 4:
					if ( try_down(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() + 1][c_puyo2.getY() + 1]);
						moved = true;
					}
				break;
				// Bas vers droite
				case 3:
					if ( try_right(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() - 1][c_puyo2.getY() + 1]);
						moved = true;
					} else if ( !try_right(plateau.getPlateau(), c_puyo1) && try_left(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() - 1]);
						replacePuyo(c_puyo2, c_puyo1);
						moved = true;
					}
				break;
				// droite vers haut
				case 2:
					if ( try_up(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() - 1][c_puyo2.getY() - 1]);
						moved = true;
					}
				break;
				default:
					System.out.println("PLACE0");
				break;
			}
			if ( moved ) {
				plateau.updateObservers(EMovePlateau.ROT2);
			}
		}
		
	}

	public static void move_left(Plateau plateau) {
		if ( plateau.isCheckPlateau() || plateau.getStatut() != EStatutPlayer.INGAME 
				|| plateau.isCheckPlateau()) { return; }
		synchronized (plateau.getLockMovement()) {
			Case c_puyo1 = null;
			try {
				c_puyo1 = plateau.caseWithPuyo1();
			} catch (Exception e) {}
			Case c_puyo2 = null;
			try {
				c_puyo2 = plateau.caseWithPuyo2();
			} catch (Exception e) {}
			if (c_puyo1 == null || c_puyo2 == null) return;
			boolean moved = false;
			switch ( plateau.place_puyo2() ) {
			// Haut ou bas
				case 1:
				case 3:
					if ( try_left(plateau.getPlateau(), c_puyo1) && try_left(plateau.getPlateau(), c_puyo2) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() - 1]);
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX()][c_puyo2.getY() - 1]);
						moved = true;
					}
				break;

				// droite
				case 2:
					if ( try_left(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() - 1]);
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX()][c_puyo2.getY() - 1]);
						moved = true;
					}
				break;
				// gauche
				case 4:
					if ( try_left(plateau.getPlateau(), c_puyo2) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX()][c_puyo2.getY() - 1]);
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() - 1]);
						moved = true;
					}
				break;
				default:
					System.out.println("PLACE0");
				break;
			}
			if ( moved ) {
				plateau.updateObservers(EMovePlateau.LEFT);
			}
		}
		
	}

	public static void move_right(Plateau plateau) {
		if ( plateau.isCheckPlateau() || plateau.getStatut() != EStatutPlayer.INGAME 
				|| plateau.isCheckPlateau()) { return; }
		synchronized (plateau.getLockMovement()) {
			Case c_puyo1 = null;
			try {
				c_puyo1 = plateau.caseWithPuyo1();
			} catch (Exception e) {}
			Case c_puyo2 = null;
			try {
				c_puyo2 = plateau.caseWithPuyo2();
			} catch (Exception e) {}
			if (c_puyo1 == null || c_puyo2 == null) return;
			boolean moved = false;
			switch ( plateau.place_puyo2() ) {
			// Haut ou bas
				case 1:
				case 3:
					if ( try_right(plateau.getPlateau(), c_puyo1) && try_right(plateau.getPlateau(), c_puyo2) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() + 1]);
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX()][c_puyo2.getY() + 1]);
						moved = true;
					}
				break;

				// droite
				case 2:
					if ( try_right(plateau.getPlateau(), c_puyo2) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX()][c_puyo2.getY() + 1]);
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() + 1]);
						moved = true;
					}
				break;
				// gauche
				case 4:
					if ( try_right(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX()][c_puyo1.getY() + 1]);
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX()][c_puyo2.getY() + 1]);
						moved = true;
					}
				break;
				default:
					System.out.println("PLACE0");
				break;
			}
			if ( moved ) {
				plateau.updateObservers(EMovePlateau.RIGHT);
			}
		}
		
	}

	public static void move_down(final Plateau plateau, boolean auto) {
		if ( plateau.isCheckPlateau() || plateau.getStatut() != EStatutPlayer.INGAME || plateau.isCheckPlateau() 
				|| plateau.isWaitDrop()) { return; }
		synchronized (plateau.getLockMovement()) {
			Case c_puyo1 = null;
			try {
				c_puyo1 = plateau.caseWithPuyo1();
			} catch (Exception e) {}
			Case c_puyo2 = null;
			try {
				c_puyo2 = plateau.caseWithPuyo2();
			} catch (Exception e) {}
			if (c_puyo1 == null || c_puyo2 == null) return;
			boolean moved = false;
			switch ( plateau.place_puyo2() ) {
			// Haut
				case 1:
					if ( try_down(plateau.getPlateau(), c_puyo1) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX() + 1][c_puyo1.getY()]);
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() + 1][c_puyo2.getY()]);
						moved = true;
					}
				break;

				// Bas
				case 3:
					if ( try_down(plateau.getPlateau(), c_puyo2) ) {
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() + 1][c_puyo2.getY()]);
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX() + 1][c_puyo1.getY()]);
						moved = true;
					}
				break;

				// droite ou gauche
				case 2:
				case 4:
					if ( try_down(plateau.getPlateau(), c_puyo1) && try_down(plateau.getPlateau(), c_puyo2) ) {
						replacePuyo(c_puyo1, plateau.getPlateau()[c_puyo1.getX() + 1][c_puyo1.getY()]);
						replacePuyo(c_puyo2, plateau.getPlateau()[c_puyo2.getX() + 1][c_puyo2.getY()]);
						moved = true;
					}
				break;
				default:
					System.out.println("PLACE0");
				break;
			}

			if (moved) {
				plateau.updateObservers((auto) ? EMovePlateau.DOWNAUTO : EMovePlateau.DOWN);
				if (!auto){
					plateau.getStat().set_score(plateau.getStat().get_score()+1);
				}
			}
			
			// Fin du cooup
			final Player p = plateau.getPlayer();
			if ( p != null && !(p instanceof RemotePlayer) ) {
				if ( !plateau.isWaitDrop() && plateau.checkEndStroke() ) {
					plateau.getExecutor().execute(new Runnable() {
						@Override
						public void run() {
							plateau.setWaitDrop(true);
							try {
								Thread.sleep(Config.TIMEBEFOREVALIDSTROKE);
							} catch (InterruptedException e) {
							}
							if ( !plateau.isWaitDrop() ) { return; }
							if ( plateau.checkEndStroke() ) {
								plateau.update_plateau();
							}
							plateau.setWaitDrop(false);
						}
					});
				}
			}
		}
	}
}
