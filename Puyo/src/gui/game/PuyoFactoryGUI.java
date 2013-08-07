package gui.game;

import java.awt.Image;

import main.Config;
import model.Case;
import model.Plateau;
import model.Puyo;
import model.Puyo.PuyoColor;
import tools.GImage;

public class PuyoFactoryGUI {

	public static Image[][] sprites;

	static {
		// Chargement des images
		sprites = GImage.loadSprite("/res/images/Classic.png");
	}

	public static Image getPuyoGUI(Plateau plat, Case c) {
		int idForme = get_forme(plat, c);
		if ( c == null || c.getPuyo() == null ) { 
			return null; 
		}
		if ( c.getPuyo().getColor() == Puyo.PuyoColor.BLACK ) { return sprites[7][5]; }
		return sprites[idForme][c.getPuyo().getColor().ordinal()];
	}

	public static Image getPuyoGUI(Puyo.PuyoColor color) {
		if ( color == Puyo.PuyoColor.BLACK ) { return sprites[7][5]; }
		return sprites[0][color.ordinal()];
	}

	public static Image getGarbadge(int id) {
		switch ( id ) {
			case 0:
				return sprites[6][5];
			case 1:
				return sprites[7][5];
			case 2:
				return sprites[8][5];
			default:
				return sprites[7][5];
		}
	}

	public static Image getGarbadge(PuyoColor color, int idexplo) {
		switch ( color ) {
			case BLUE:
				return sprites[4 + idexplo][6];
			case GREEN:
				return sprites[2 + idexplo][6];
			case RED:
				return sprites[0 + idexplo][6];
			case VIOLET:
				return sprites[8 + idexplo][6];
			case YELLOW:
				return sprites[6 + idexplo][6];
			default:
				return sprites[idexplo][6];
		}
	}

	/**
	 * Retourne comment doit-�tre la forme du PUYO, cela permettra de renseigner
	 * la vue sur quel sprite utiliser.
	 * 
	 * @param pos
	 * @return
	 */
	private static int get_forme(Plateau plateau, Case c) {
		synchronized (plateau) {
			Case c_puyo1 = null;
			try {
				c_puyo1 = plateau.caseWithPuyo1();
			} catch (Exception e) {}
			Case c_puyo2 = null;
			try {
				c_puyo2 = plateau.caseWithPuyo2();
			} catch (Exception e) {}
			
			if ( c_puyo1 != null && c == c_puyo1 || c_puyo2 != null && c == c_puyo2 ) { return 0; }
			int nbbranche = 0;
			boolean haut = false;
			boolean bas = false;
			boolean gauche = false;
			boolean droite = false;

			// haut?
			if ( c.getX() - 1 >= 0 && plateau.getPlateau()[c.getX() - 1][c.getY()].getPuyo() != null && plateau.getPlateau()[c.getX() - 1][c.getY()].getPuyo().equals(c.getPuyo()) && !plateau
					.getPlateau()[c.getX() - 1][c.getY()].equals(c_puyo1) && !plateau.getPlateau()[c.getX() - 1][c.getY()].equals(c_puyo2) ) {
				nbbranche += 1;
				haut = true;
			}
			// bas?
			if ( c.getX() + 1 < Config.NB_LINE && plateau.getPlateau()[c.getX() + 1][c.getY()].getPuyo() != null && plateau.getPlateau()[c.getX() + 1][c.getY()].getPuyo().equals(c.getPuyo()) && !plateau
					.getPlateau()[c.getX() + 1][c.getY()].equals(c_puyo1) && !plateau.getPlateau()[c.getX() + 1][c.getY()].equals(c_puyo2) ) {
				nbbranche += 1;
				bas = true;
			}
			// gauche?
			if ( c.getY() - 1 >= 0 && plateau.getPlateau()[c.getX()][c.getY() - 1].getPuyo() != null && plateau.getPlateau()[c.getX()][c.getY() - 1].getPuyo().equals(c.getPuyo()) && !plateau
					.getPlateau()[c.getX()][c.getY() - 1].equals(c_puyo1) && !plateau.getPlateau()[c.getX()][c.getY() - 1].equals(c_puyo2) ) {
				nbbranche += 1;
				gauche = true;
			}
			// droite?
			if ( c.getY() + 1 < Config.NB_COL && plateau.getPlateau()[c.getX()][c.getY() + 1].getPuyo() != null && plateau.getPlateau()[c.getX()][c.getY() + 1].getPuyo().equals(c.getPuyo()) && !plateau
					.getPlateau()[c.getX()][c.getY() + 1].equals(c_puyo1) && !plateau.getPlateau()[c.getX()][c.getY() + 1].equals(c_puyo2) ) {
				nbbranche += 1;
				droite = true;
			}

			switch ( nbbranche ) {
				case 0:
					return 0;
				case 1:
					if ( haut ) {
						return 1;
					} else if ( droite ) {
						return 2;
					} else if ( bas ) {
						return 4;
					} else if ( gauche ) { return 8; }
				break;
				case 2:
					if ( gauche && droite ) {
						return 10;
					} else if ( haut && bas ) {
						return 5;
					} else if ( droite && haut ) {
						return 3;
					} else if ( droite && bas ) {
						return 6;
					} else if ( gauche && bas ) {
						return 12;
					} else if ( gauche && haut ) { return 9; }
				break;
				case 3:
					if ( haut && bas && droite ) {
						return 7;
					} else if ( bas && droite && gauche ) {
						return 14;
					} else if ( haut && bas && gauche ) {
						return 13;
					} else if ( haut && gauche && droite ) { return 11; }
				break;
				case 4:
					return 15;
				default:
					return 0;
			}
			return 0;
		}
	}
}
