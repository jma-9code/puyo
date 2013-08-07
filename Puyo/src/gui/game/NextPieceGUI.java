package gui.game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import main.Config;
import message.party.EStatutPlayer;
import model.Piece;
import model.Plateau;

public class NextPieceGUI extends JPanel implements Observer {

	private Piece piece = null;

	/**
	 * Create the panel.
	 */
	public NextPieceGUI () {
		setPreferredSize(new Dimension(50, 70));
	}

	public void updatePiece(Piece _piece) {
		if ( piece != null && piece.equals(_piece) ) { return; }
		piece = _piece;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		/* Lissage du texte et des dessins */
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		updateGUI(g);
	}

	private void updateGUI(Graphics g) {
		if ( piece == null ) { return; }
		g.drawImage(PuyoFactoryGUI.getPuyoGUI(piece.getPuyo2().getColor()), 7, 10, null);
		g.drawImage(PuyoFactoryGUI.getPuyoGUI(piece.getPuyo1().getColor()), 7, 10 + Config.PUYO_SIZE, null);
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( arg instanceof Plateau.EInfoPlateau ) {
			switch ( (Plateau.EInfoPlateau) arg ) {
				case NEWPIECE:
					updatePiece(((Plateau) o).getNextPiece());
				break;
			}
		}else if ( arg instanceof EStatutPlayer ) {
			updatePiece(((Plateau) o).getNextPiece());
		}
	}

}
