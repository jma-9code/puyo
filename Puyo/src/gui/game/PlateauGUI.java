package gui.game;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Config;
import main.Main;
import message.puyo.EMovePlateau;
import model.Case;
import model.Plateau;
import model.Plateau.EInfoPlateau;
import model.Puyo2Destroy;

public class PlateauGUI extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4678402963147651342L;
	private Plateau plateau = null;
	private PlateauGUI himSelf;
	private ArrayList<Puyo2Destroy> puyo2destroy = new ArrayList<Puyo2Destroy>();
	
	
	//sounds
	private static AudioClip sound_drop = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/drop.wav"));
	private static AudioClip sound_chain0 = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/chain0.wav"));
	private static AudioClip sound_chain1 = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/chain1.wav"));
	private static AudioClip sound_chain2 = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/chain2.wav"));
	private static AudioClip sound_chain3 = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/chain3.wav"));
	private static AudioClip sound_chain4 = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/chain4.wav"));
	private static AudioClip sound_chain5 = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/chain5.wav"));
	private static AudioClip sound_chain6 = Applet.newAudioClip(Main.himself.getClass().getResource("/res/sounds/chain6.wav"));

	/**
	 * Create the panel.
	 */
	public PlateauGUI ( Plateau _plateau ) {
		setPreferredSize(new Dimension(Config.NB_COL * Config.PUYO_SIZE, Config.NB_LINE * Config.PUYO_SIZE));
		setLayout(null);
		plateau = _plateau;
		himSelf = this;
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
		if ( plateau == null ) { return; }
		Case c1 = null;
		Case c2 = null;
		/*if (plateau.getCurPiece() != null){
			for ( int i = 1; i < plateau.getPlateau().length; i++ ) {
				for ( int j = 0; j < plateau.getPlateau()[0].length; j++ ) {
					if ( plateau.getPlateau()[i][j].getPuyo() == plateau.getCurPiece().getPuyo1() ){
						c1 = plateau.getPlateau()[i][j];
					}else if (plateau.getPlateau()[i][j].getPuyo() == plateau.getCurPiece().getPuyo2()){
						c2 = plateau.getPlateau()[i][j];
					}
					if (c1 != null && c2 != null){
						break;
					}
				}
			}
		}*/
		
		for ( int i = 1; i < Config.NB_LINE; i++ ) {
			for ( int j = 0; j < Config.NB_COL; j++ ) {
				if ( plateau.getPlateau()[i][j].getPuyo() == null 
						|| (c1 != null && c1.getX() == i && c1.getY() == j)  
							|| (c2 != null && c2.getX() == i && c2.getY() == j)) continue;
				g.drawImage(PuyoFactoryGUI.getPuyoGUI(plateau, plateau.getPlateau()[i][j]), j * Config.PUYO_SIZE, (i - 1) * Config.PUYO_SIZE, this);
			}
		}

	}
	
	@Override
	public void update(Observable o, final Object arg) {
		if ( arg instanceof EMovePlateau ) {
			switch((EMovePlateau)arg){
				case DROPPED:
				   // sound_drop.play();
					break;
			}
			repaint();
		} else if ( arg instanceof Puyo2Destroy ) {
			final Puyo2Destroy puyo = (Puyo2Destroy) arg;
			new Thread(new Runnable() {
				@Override
				public void run() {
					JLabel explo1 = new JLabel(new ImageIcon(PuyoFactoryGUI.getGarbadge(puyo.getColor(), 0)));
					JLabel explo2 = new JLabel(new ImageIcon(PuyoFactoryGUI.getGarbadge(puyo.getColor(), 1)));
					himSelf.add(explo1);
					explo1.setBounds(puyo.getY() * Config.PUYO_SIZE, (puyo.getX() - 1) * Config.PUYO_SIZE, Config.PUYO_SIZE, Config.PUYO_SIZE);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					himSelf.remove(explo1);

					himSelf.add(explo2);
					explo2.setBounds(puyo.getY() * Config.PUYO_SIZE, (puyo.getX() - 1) * Config.PUYO_SIZE, Config.PUYO_SIZE, Config.PUYO_SIZE);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					himSelf.remove(explo2);
					himSelf.repaint();
				}
			}).start();
		}else if (arg instanceof EInfoPlateau){
			/*switch((EInfoPlateau)arg){
				case CHAIN0:
					sound_chain0.play();
					break;
				case CHAIN1:
					sound_chain1.play();
					break;
				case CHAIN2:
					sound_chain2.play();
					break;
				case CHAIN3:
					sound_chain3.play();
					break;
				case CHAIN4:
					sound_chain4.play();
					break;
				case CHAIN5:
					sound_chain5.play();
					break;
				case CHAIN6:
					sound_chain6.play();
					break;
			}*/
		}
	}

	public ArrayList<Puyo2Destroy> getPuyo2destroy() {
		return puyo2destroy;
	}

	public void setPuyo2destroy(ArrayList<Puyo2Destroy> puyo2destroy) {
		this.puyo2destroy = puyo2destroy;
	}
}
