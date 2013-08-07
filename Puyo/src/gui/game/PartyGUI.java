package gui.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import main.Config;
import message.party.EStatutParty;
import message.party.EStatutPlayer;
import model.Party;
import model.Plateau;

import commun.Player;

public class PartyGUI extends JPanel implements Observer {
	private JPanel pnl_mainPlateau;
	private JPanel pnl_otherPlateau;
	private Party party;

	private PlayerGUI him;
	private PlayerGUI playerLeft;
	private PlayerGUI playerRight;

	private ArrayList<Player> loosers = new ArrayList<Player>();
	private JPanel pnl_loosers;

	/**
	 * Create the panel.
	 */
	public PartyGUI ( Party _party ) {
		setLayout(new BorderLayout(0, 0));
		party = _party;
		party.addObserver(this);
		pnl_mainPlateau = new JPanel();
		final FlowLayout flowLayout = (FlowLayout) pnl_mainPlateau.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(pnl_mainPlateau);
		pnl_otherPlateau = new JPanel();
		pnl_otherPlateau.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(pnl_otherPlateau, BorderLayout.SOUTH);

		pnl_loosers = new JPanel();
		final FlowLayout flowLayout_1 = (FlowLayout) pnl_loosers.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		pnl_loosers.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(pnl_loosers, BorderLayout.NORTH);
		refreshParty();
	}

	private Player chooseCenterPlayer() {
		Player himself = party.getPlayer(Config.NAME);
		if ( himself == null || party.getPlayersMap().get(himself).getStatut() == EStatutPlayer.LOSE ) {
			for ( int i = 0; i < party.getPlayers().size(); i++ ) {
				if ( party.getPlayersMap().get(party.getPlayers().get(i)).getStatut() != EStatutPlayer.LOSE ) {
					himself = party.getPlayers().get(i);
					break;
				}
			}
		}
		if ( himself == null ) { return null; }
		if ( him == null || him.getPlayer() != himself ) {
			him = new PlayerGUI(himself, party.getPlayersMap().get(himself));
		}
		return himself;
	}

	private Player chooseLeftPlayer() {
		Player himself = chooseCenterPlayer();
		Player left = null;
		if ( himself != null ) {
			// Recherche du joueur de gauche
			for ( Player p : party.getPlayers() ) {
				if ( p != himself && party.getOpponent(p) == himself && party.getPlayersMap().get(p).getStatut() == EStatutPlayer.INGAME ) {
					left = p;
					break;
				}
			}
			if ( left == null ) return left;

			if ( playerLeft == null || playerLeft.getPlayer() != left ) {
				Plateau opponentL = party.getPlayersMap().get(left);
				if ( opponentL != null ) {
					playerLeft = new PlayerGUI(opponentL.getPlayer(), opponentL);
				}
			}
		}
		return left;
	}

	private Player chooseRightPlayer() {
		Player himself = chooseCenterPlayer();
		Player right = party.getOpponent(himself);
		if ( himself != null && right != null ) {
			if ( playerRight == null || playerRight.getPlayer() != party.getOpponent(himself) ) {
				Plateau opponentR = party.getPlayersMap().get(right);
				if ( opponentR != null ) {
					playerRight = new PlayerGUI(opponentR.getPlayer(), opponentR);
				}
			}
		}
		return right;
	}

	private void refreshParty() {
		pnl_mainPlateau.removeAll();
		Player center = chooseCenterPlayer();
		Player left = chooseLeftPlayer();
		Player right = chooseRightPlayer();
		if ( left == null || left == right ) {
			if ( center != null ) pnl_mainPlateau.add(him);
			if ( right != null ) pnl_mainPlateau.add(playerRight);
		} else if ( left != null && right != null && left != right ) {
			pnl_mainPlateau.add(playerLeft);
			if ( center != null ) pnl_mainPlateau.add(him);
			pnl_mainPlateau.add(playerRight);
		} else if ( left == null && right == null ) {
			if ( center != null ) pnl_mainPlateau.add(him);
		}

		pnl_otherPlateau.removeAll();
		for ( Player p : party.getPlayers() ) {
			if ( p != center && p != left && p != right ) {
				if ( party.getPlayersMap().get(p).getStatut() != EStatutPlayer.LOSE ) {
					pnl_otherPlateau.add(new JLabel(p.getName()));
				}
			}
		}
		validate();
		repaint();
	}
	
	@Override
	public void update(Observable arg0, final Object arg1) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if ( arg1 instanceof Plateau ) {
					if ( ((Plateau) arg1).getStatut() == EStatutPlayer.LOSE) {
						loosers.add(((Plateau) arg1).getPlayer());
						if (party.getPlayersMap().size() - loosers.size() > 1){
							refreshParty();
						}
					}
				} else if ( arg1 instanceof EStatutParty ) {
					switch ( (EStatutParty) arg1 ) {
						case WAITING:
						break;
						case INGAME:
							refreshParty();
						break;
						case END:
						break;
					}
				}
			}
		});
	}

	public ArrayList<Player> getLoosers() {
		return loosers;
	}

	public void setLoosers(ArrayList<Player> loosers) {
		this.loosers = loosers;
	}

}
