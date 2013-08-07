package gui.game;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import main.Config;
import message.party.EStatutPlayer;
import model.Plateau;
import model.Plateau.EInfoPlateau;

import commun.Player;

public class PlayerGUI extends JPanel implements Observer {
	private PlateauGUI pnl_plateau;
	private NextPieceGUI pnl_nextPiece;
	private JLabel img_avatar;
	private JLabel lblScore;
	private JTextArea txtrInfo;
	private Plateau plateau;
	private Player player;
	private JPanel pnl_garbadge;
	private JLabel lblCombos;
	private JLabel lblPierres;

	/**
	 * Create the panel.
	 */
	public PlayerGUI ( Player _player, Plateau _plateau ) {
		setPreferredSize(new Dimension(320, 370));
		setSize(320, 370);
		setPlayer(_player);
		plateau = _plateau;
		plateau.addObserver(this);

		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		pnl_plateau = new PlateauGUI(plateau);
		plateau.addObserver(pnl_plateau);
		pnl_plateau.setPreferredSize(new Dimension(0, 0));
		pnl_plateau.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, pnl_plateau, 25, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, pnl_plateau, 25, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, pnl_plateau, -45, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, pnl_plateau, -145, SpringLayout.EAST, this);
		add(pnl_plateau);

		pnl_nextPiece = new NextPieceGUI();
		plateau.addObserver(pnl_nextPiece);
		pnl_nextPiece.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, pnl_nextPiece, 25, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, pnl_nextPiece, 24, SpringLayout.EAST, pnl_plateau);
		springLayout.putConstraint(SpringLayout.SOUTH, pnl_nextPiece, -270, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, pnl_nextPiece, -80, SpringLayout.EAST, this);
		add(pnl_nextPiece);

		img_avatar = new JLabel(player.getName());
		img_avatar.setVerticalTextPosition(SwingConstants.TOP);
		img_avatar.setHorizontalTextPosition(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.WEST, img_avatar, 30, SpringLayout.EAST, pnl_plateau);
		springLayout.putConstraint(SpringLayout.SOUTH, img_avatar, 0, SpringLayout.SOUTH, pnl_plateau);
		img_avatar.setIcon(new ImageIcon(PlayerGUI.class.getResource("/res/images/Avatar.png")));
		add(img_avatar);

		lblScore = new JLabel("Score :");
		springLayout.putConstraint(SpringLayout.NORTH, lblScore, 6, SpringLayout.SOUTH, pnl_nextPiece);
		springLayout.putConstraint(SpringLayout.WEST, lblScore, 6, SpringLayout.EAST, pnl_plateau);
		add(lblScore);

		txtrInfo = new JTextArea();
		txtrInfo.setWrapStyleWord(true);
		txtrInfo.setFont(new Font("Arial", Font.PLAIN, 10));
		txtrInfo.setEditable(false);
		txtrInfo.setLineWrap(true);
		springLayout.putConstraint(SpringLayout.WEST, txtrInfo, 0, SpringLayout.WEST, pnl_plateau);
		springLayout.putConstraint(SpringLayout.SOUTH, txtrInfo, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, txtrInfo, 0, SpringLayout.EAST, img_avatar);
		add(txtrInfo);

		pnl_garbadge = new JPanel();
		final FlowLayout fl_pnl_garbadge = (FlowLayout) pnl_garbadge.getLayout();
		fl_pnl_garbadge.setVgap(0);
		fl_pnl_garbadge.setHgap(0);
		fl_pnl_garbadge.setAlignment(FlowLayout.LEFT);
		springLayout.putConstraint(SpringLayout.WEST, pnl_garbadge, 0, SpringLayout.WEST, pnl_plateau);
		springLayout.putConstraint(SpringLayout.SOUTH, pnl_garbadge, 0, SpringLayout.NORTH, pnl_plateau);
		springLayout.putConstraint(SpringLayout.EAST, pnl_garbadge, 0, SpringLayout.EAST, pnl_plateau);
		add(pnl_garbadge);

		lblCombos = new JLabel("Combos :");
		springLayout.putConstraint(SpringLayout.NORTH, lblCombos, 6, SpringLayout.SOUTH, lblScore);
		springLayout.putConstraint(SpringLayout.WEST, lblCombos, 6, SpringLayout.EAST, pnl_plateau);
		add(lblCombos);

		updateStatutPlateau(plateau.getStatut());

		lblPierres = new JLabel("Pierres :");
		springLayout.putConstraint(SpringLayout.NORTH, lblPierres, 6, SpringLayout.SOUTH, lblCombos);
		springLayout.putConstraint(SpringLayout.WEST, lblPierres, 6, SpringLayout.EAST, pnl_plateau);
		add(lblPierres);
	}

	public void updateStatutPlateau(EStatutPlayer arg) {
		switch ( (EStatutPlayer) arg ) {
			case INGAME:
				txtrInfo.setText("En cours...");
			break;
			case WAITING:
				txtrInfo.setText("En attente...");
			break;
			case LOSE:
				txtrInfo.setText("Perdu");
			break;
			case WIN:
				txtrInfo.setText("Victoire");
			break;
			case LEFT:
				txtrInfo.setText("A quitté la partie");
			break;
			default:
			break;
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void update(Observable o, final Object arg) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if ( arg instanceof EStatutPlayer ) {
					updateStatutPlateau((EStatutPlayer) arg);
				} else if ( arg instanceof EInfoPlateau ) {
					switch ( (EInfoPlateau) arg ) {
						case GARBADGE:
							pnl_garbadge.removeAll();
							int nbgarbadge = plateau.getReceiv_block();
							int garbage0 = nbgarbadge % Config.NB_COL;
							int garbage1 = nbgarbadge / Config.NB_COL;
							int garbage2 = 0;
							if ( nbgarbadge >= Config.NB_LINE * Config.NB_COL / 2 ) {
								garbage2 = nbgarbadge / (Config.NB_LINE * Config.NB_COL / 2);
								garbage1 = (nbgarbadge - garbage2 * (Config.NB_LINE * Config.NB_COL / 2)) / Config.NB_COL;
							}

							for ( int i = 0; i < garbage2; i++ )
								pnl_garbadge.add(new JLabel(new ImageIcon(PuyoFactoryGUI.getGarbadge(2))));
							for ( int i = 0; i < garbage1; i++ )
								pnl_garbadge.add(new JLabel(new ImageIcon(PuyoFactoryGUI.getGarbadge(1))));
							for ( int i = 0; i < garbage0; i++ )
								pnl_garbadge.add(new JLabel(new ImageIcon(PuyoFactoryGUI.getGarbadge(0))));

							pnl_garbadge.repaint();
						break;
					}
				}

				// Update stat
				if ( plateau != null ) {
					lblScore.setText("Score : " + plateau.getStat().get_score());
					lblCombos.setText("Combos : " + plateau.getStat().get_combo_total());
					lblPierres.setText("Pierres : " + plateau.getStat().get_pierre_total());
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
