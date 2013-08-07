package main;

import gui.LoginGUI;
import gui.SalonGUI;
import gui.config.ConfigGUI;
import gui.config.KeyConfigPnl;
import gui.game.PartyGUI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import message.puyo.ActionPuyo;
import message.puyo.EMovePlateau;
import model.Party;
import model.Puyo.PuyoColor;
import model.players.IAPlayer;
import network.NetworkClient;
import network.NetworkClient.ENetworkClient;

import commun.Player;

import controller.ManageKeyboard;

public class Main extends JApplet implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private JMenu mnGame;
	private JMenuItem mntmPlayerVsIA;
	private JMenu mnOptions;
	private JMenuItem mntmKeyConfig;
	private JLabel lblConnectstatut;
	private JMenu mnNetwork;
	private JMenuItem mntmJoinSalon;
	private SalonGUI salon;
	private ConfigGUI configGUI = new ConfigGUI();
	public static Main himself;
	private Party p = new Party();

	
	public void loadParameter(){
		try {
			Config.NAME = (getParameter("NAME") != null) ? getParameter("NAME")  : Config.NAME;
			Config.PWD = (getParameter("PWD")  != null) ? getParameter("PWD") : Config.PWD;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		loadParameter();
	}

	@Override
	public void destroy() {
		NetworkClient.get().interrupt();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		ActionPuyo ac = new ActionPuyo(EMovePlateau.DOWN, null);
		try{	
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(ac);
			byte[] acBytes = bos.toByteArray(); 
			out.close();
			bos.close();
			FileOutputStream f = new FileOutputStream(new File("serial"));
			f.write(acBytes);
			f.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
		LoginGUI log = new LoginGUI();
	}

	/**
	 * Create the applet.
	 */
	public Main () {
		himself = this;
		setName("Puyo Puyo");
		try {
			UIManager.setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnGame = new JMenu("Jeu");
		menuBar.add(mnGame);

		mntmPlayerVsIA = new JMenuItem("Joueur VS IA");
		mntmPlayerVsIA.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ( p != null ) {
					p.stopGame();
					p = new Party();
					//p.addPlayer(new Player(Config.NAME, 1000));
					p.addPlayer(new IAPlayer());
					p.addPlayer(new IAPlayer());
					p.addPlayer(new IAPlayer());
					p.addPlayer(new IAPlayer());
					p.addPlayer(new IAPlayer());
					p.addPlayer(new IAPlayer());

					startParty(p);
				}
				
			}
		});
		mnGame.add(mntmPlayerVsIA);

		mnNetwork = new JMenu("R\u00E9seau");
		menuBar.add(mnNetwork);

		mntmJoinSalon = new JMenuItem("Rejoindre le salon");
		mntmJoinSalon.setEnabled(false);
		mntmJoinSalon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ( salon == null ) {
					salon = new SalonGUI();
				}
				salon.setVisible(true);
			}
		});
		mnNetwork.add(mntmJoinSalon);

		mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		mntmKeyConfig = new JMenuItem("Configurations");
		mntmKeyConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				configGUI.setVisible(true);
			}
		});
		mnOptions.add(mntmKeyConfig);
		

		lblConnectstatut = new JLabel("");
		lblConnectstatut.setIcon(new ImageIcon(Main.class.getResource("/res/images/serv_OFF.png")));
		menuBar.add(lblConnectstatut);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		NetworkClient.get().addObserver(this);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof ENetworkClient){
			switch ((ENetworkClient)arg1){
				case DISCONNECTED:
					if ( salon != null ) {
						salon.setVisible(false);
					}
					mntmJoinSalon.setEnabled(false);
					lblConnectstatut.setIcon(new ImageIcon(Main.class.getResource("/res/images/serv_OFF.png")));
					break;
				case CONNECTED:
					if ( salon != null ) {
						salon.setVisible(false);
					}
					mntmJoinSalon.setEnabled(true);
					lblConnectstatut.setIcon(new ImageIcon(Main.class.getResource("/res/images/serv_ON.png")));
					break;
			}
		}
	}

	public void startParty(Party p) {
		getContentPane().removeAll();
		Player him = p.getPlayer(Config.NAME);
		if ( him != null ) {
			ManageKeyboard mnk = new ManageKeyboard(p.getPlayersMap().get(him));
			addKeyListener(mnk);
		}
		getContentPane().add(new PartyGUI(p));
		p.startGame();
		validate();
		repaint();
		requestFocus();
		
	}
	
	public static String getParamApplet(String param){
		return himself.getParameter(param);
	}
}
