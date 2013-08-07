package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import commun.puyo.PartyOption;
import commun.Player;
import commun.puyo.Puyo_PartyOption;

import main.Config;
import main.Main;
import message.Talk2All;
import message.Talk2Party;
import message.party.JoinParty;
import message.party.LeftParty;
import message.party.NewParty;
import message.puyo.StartParty;
import network.NetworkClient;
import network.NetworkManager;
import network.NetworkManager.NMInfo;
import tools.CountryRenderer;
import tools.TableParty;
import tools.TablePlayer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class SalonGUI extends JDialog implements Observer {

	private JPanel contentPane;
	private JTable jtable_player, jtable2, jtable_playerParty;
	private TablePlayer tablePlayer, tablePlayerParty;
	private TableParty tableParty;
	private JPanel salonPlayer, salonPartys, salonParty;
	private JSplitPane splitPane;
	private JPanel leftpan;
	private JPanel rightpan;
	private JTextField tf_send;
	private JPanel jp_option;
	private JTextArea txtA_salonparty, txtA_salon;
	private JTextField tf_sendParty;
	private JButton bt_startParty;
	private JButton bt_leftParty;
	private JTabbedPane tabbedPane;
	private JButton bt_ready;
	private JPanel jp_btoption;
	private JSlider slider_maxPlayers;
	private JSlider slider_gravity;
	private JPanel jp_conf;
	private JButton bt_result;

	public SalonGUI () {
		this(null, true);
	}

	/**
	 * Create the frame.
	 */
	public SalonGUI ( Dialog dial, boolean modal ) {
		super(dial, modal);
		NetworkManager.get().addObserver(this);
		setTitle("Salon");
		setBounds(100, 100, 647, 392);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		tablePlayer = new TablePlayer(NetworkClient.get().getPlayers());
		tablePlayerParty = new TablePlayer(null);
		tableParty = new TableParty(NetworkClient.get().getPartysInfo());
		salonPlayer = new JPanel();
		tabbedPane.addTab("Salon des joueurs", null, salonPlayer, null);
		salonPlayer.setLayout(new BorderLayout(0, 0));

		splitPane = new JSplitPane();
		salonPlayer.add(splitPane);

		leftpan = new JPanel();
		leftpan.setMinimumSize(new Dimension(200, 10));
		splitPane.setLeftComponent(leftpan);
		leftpan.setLayout(new BorderLayout(0, 0));
		jtable_player = new JTable(tablePlayer);
		jtable_player.setDefaultRenderer(Locale.class, new CountryRenderer());
		jtable_player.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jtable_player.setFillsViewportHeight(true);
		JScrollPane scrollPanePlayers = new JScrollPane(jtable_player);
		leftpan.add(scrollPanePlayers);

		rightpan = new JPanel();
		rightpan.setMinimumSize(new Dimension(200, 10));
		splitPane.setRightComponent(rightpan);
		rightpan.setLayout(new BorderLayout(0, 0));

		txtA_salon = new JTextArea();
		JScrollPane scrollPaneTxtArea = new JScrollPane(txtA_salon);
		rightpan.add(scrollPaneTxtArea, BorderLayout.CENTER);
		txtA_salon.setEditable(false);
		txtA_salon.setWrapStyleWord(true);
		txtA_salon.setLineWrap(true);

		tf_send = new JTextField();
		tf_send.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if ( arg0.getKeyCode() == KeyEvent.VK_ENTER ) {
					NetworkClient.get().sendObject(new Talk2All(tf_send.getText()));
					tf_send.setText("");
				}
			}
		});
		rightpan.add(tf_send, BorderLayout.SOUTH);
		tf_send.setColumns(10);

		salonPartys = new JPanel();
		tabbedPane.addTab("Salon des parties", null, salonPartys, null);
		SpringLayout sl_salonPartys = new SpringLayout();
		salonPartys.setLayout(sl_salonPartys);

		jtable2 = new JTable(tableParty);
		jtable2.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jtable2.setFillsViewportHeight(true);
		JScrollPane scrollPanePartys = new JScrollPane(jtable2);
		sl_salonPartys.putConstraint(SpringLayout.NORTH, scrollPanePartys, 0, SpringLayout.NORTH, salonPartys);
		sl_salonPartys.putConstraint(SpringLayout.WEST, scrollPanePartys, 0, SpringLayout.WEST, salonPartys);
		sl_salonPartys.putConstraint(SpringLayout.SOUTH, scrollPanePartys, -38, SpringLayout.SOUTH, salonPartys);
		sl_salonPartys.putConstraint(SpringLayout.EAST, scrollPanePartys, 0, SpringLayout.EAST, salonPartys);
		salonPartys.add(scrollPanePartys);

		JButton btnJoin = new JButton("Rejoindre");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ( jtable2.getSelectedRow() != -1 ) {
					try {
						int col_id = -1;
						for (int i=0; i<jtable2.getColumnCount(); i++){
							if (jtable2.getColumnName(i).equalsIgnoreCase("id")){
								col_id = i;
							}
						}
						int id = Integer.parseInt(jtable2.getValueAt(jtable2.getSelectedRow(), col_id).toString());
						NetworkClient.get().sendObject(new JoinParty(id));
					} catch (Exception e) {
					}
				}

			}
		});
		sl_salonPartys.putConstraint(SpringLayout.NORTH, btnJoin, 6, SpringLayout.SOUTH, scrollPanePartys);
		sl_salonPartys.putConstraint(SpringLayout.WEST, btnJoin, 10, SpringLayout.WEST, scrollPanePartys);
		salonPartys.add(btnJoin);

		JButton btnNewParty = new JButton("Nouvelle partie");
		btnNewParty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkClient.get().sendObject(new NewParty());
			}
		});
		sl_salonPartys.putConstraint(SpringLayout.NORTH, btnNewParty, 6, SpringLayout.SOUTH, scrollPanePartys);
		sl_salonPartys.putConstraint(SpringLayout.WEST, btnNewParty, -135, SpringLayout.EAST, salonPartys);
		sl_salonPartys.putConstraint(SpringLayout.EAST, btnNewParty, -10, SpringLayout.EAST, salonPartys);
		salonPartys.add(btnNewParty);

		salonParty = new JPanel();
		tabbedPane.addTab("Partie", null, salonParty, null);
		tabbedPane.setEnabledAt(2, false);
		SpringLayout sl_salonParty = new SpringLayout();
		salonParty.setLayout(sl_salonParty);
		jtable_playerParty = new JTable(tablePlayerParty);
		jtable_playerParty.setDefaultRenderer(Locale.class, new CountryRenderer());
		jtable_playerParty.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jtable_playerParty.setFillsViewportHeight(true);
		JScrollPane scrollPanePlayerParty = new JScrollPane(jtable_playerParty);
		sl_salonParty.putConstraint(SpringLayout.NORTH, scrollPanePlayerParty, 0, SpringLayout.NORTH, salonParty);
		sl_salonParty.putConstraint(SpringLayout.WEST, scrollPanePlayerParty, 0, SpringLayout.WEST, salonParty);
		sl_salonParty.putConstraint(SpringLayout.EAST, scrollPanePlayerParty, 200, SpringLayout.WEST, salonParty);
		salonParty.add(scrollPanePlayerParty);

		jp_option = new JPanel();
		jp_option.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sl_salonParty.putConstraint(SpringLayout.NORTH, jp_option, 0, SpringLayout.NORTH, salonParty);
		sl_salonParty.putConstraint(SpringLayout.WEST, jp_option, 0, SpringLayout.EAST, scrollPanePlayerParty);
		sl_salonParty.putConstraint(SpringLayout.EAST, jp_option, 0, SpringLayout.EAST, salonParty);
		salonParty.add(jp_option);

		txtA_salonparty = new JTextArea();
		JScrollPane scrollPaneTxtAreaParty = new JScrollPane(txtA_salonparty);
		sl_salonParty.putConstraint(SpringLayout.WEST, scrollPaneTxtAreaParty, 0, SpringLayout.WEST, salonParty);
		sl_salonParty.putConstraint(SpringLayout.SOUTH, scrollPanePlayerParty, 0, SpringLayout.NORTH, scrollPaneTxtAreaParty);
		sl_salonParty.putConstraint(SpringLayout.SOUTH, jp_option, 0, SpringLayout.NORTH, scrollPaneTxtAreaParty);
		SpringLayout sl_jp_option = new SpringLayout();
		jp_option.setLayout(sl_jp_option);

		jp_btoption = new JPanel();
		sl_jp_option.putConstraint(SpringLayout.WEST, jp_btoption, 0, SpringLayout.WEST, jp_option);
		sl_jp_option.putConstraint(SpringLayout.SOUTH, jp_btoption, 0, SpringLayout.SOUTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.EAST, jp_btoption, 0, SpringLayout.EAST, jp_option);
		jp_option.add(jp_btoption);

		bt_startParty = new JButton("Lancer partie");
		jp_btoption.add(bt_startParty);
		bt_startParty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkClient.get().sendObject(new StartParty());
			}
		});
		sl_jp_option.putConstraint(SpringLayout.NORTH, bt_startParty, 7, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, bt_startParty, 55, SpringLayout.WEST, jp_option);

		bt_ready = new JButton("Pret");
		jp_btoption.add(bt_ready);
		sl_jp_option.putConstraint(SpringLayout.NORTH, bt_ready, 7, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, bt_ready, 160, SpringLayout.WEST, jp_option);
		
		bt_result = new JButton("Classement");
		bt_result.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		jp_btoption.add(bt_result);

		bt_leftParty = new JButton("Quitter partie");
		jp_btoption.add(bt_leftParty);
		sl_jp_option.putConstraint(SpringLayout.NORTH, bt_leftParty, 7, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, bt_leftParty, 215, SpringLayout.WEST, jp_option);

		jp_conf = new JPanel();
		sl_jp_option.putConstraint(SpringLayout.NORTH, jp_conf, 0, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, jp_conf, 0, SpringLayout.WEST, jp_option);
		sl_jp_option.putConstraint(SpringLayout.SOUTH, jp_conf, 0, SpringLayout.NORTH, jp_btoption);
		sl_jp_option.putConstraint(SpringLayout.EAST, jp_conf, 0, SpringLayout.EAST, jp_option);
		jp_option.add(jp_conf);

		slider_maxPlayers = new JSlider(2, 16, 2);
		slider_maxPlayers.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (jtable_playerParty.getRowCount() > slider_maxPlayers.getValue()){
					slider_maxPlayers.setValue(jtable_playerParty.getRowCount());
				}else{
					slider_maxPlayers.setBorder(new TitledBorder(new LineBorder(new Color(41, 41, 41)), "Nombre de joueurs max." + slider_maxPlayers.getValue(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
					NetworkClient.get().sendObject(new Puyo_PartyOption(slider_gravity.getValue(), slider_maxPlayers.getValue()));
				}
			}
		});
		slider_maxPlayers.setMajorTickSpacing(2);
		slider_maxPlayers.setMinorTickSpacing(1);
		slider_maxPlayers.setPaintTicks(true);
		jp_conf.add(slider_maxPlayers);
		sl_jp_option.putConstraint(SpringLayout.NORTH, slider_maxPlayers, 0, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, slider_maxPlayers, 0, SpringLayout.WEST, jp_option);
		slider_maxPlayers.setPaintLabels(true);
		slider_maxPlayers.setBorder(new TitledBorder(new LineBorder(new Color(41, 41, 41)), "Nombre de joueurs max." + slider_maxPlayers.getValue(), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		slider_gravity = new JSlider(300,1500,800);
		slider_gravity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				slider_gravity.setBorder(new TitledBorder(new LineBorder(new Color(41, 41, 41)), "Gravit\u00E9 - " + slider_gravity.getValue(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
				NetworkClient.get().sendObject(new Puyo_PartyOption(slider_gravity.getValue(), slider_maxPlayers.getValue()));
			}
		});
		slider_gravity.setPaintLabels(true);
		slider_gravity.setPaintTicks(true);
		slider_gravity.setMajorTickSpacing(300);
		slider_gravity.setMinorTickSpacing(100);
		
		jp_conf.add(slider_gravity);
		slider_gravity.setBorder(new TitledBorder(new LineBorder(new Color(41, 41, 41)), "Gravit\u00E9 - " + slider_gravity.getValue(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sl_jp_option.putConstraint(SpringLayout.NORTH, slider_gravity, 0, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.EAST, slider_gravity, 0, SpringLayout.EAST, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, slider_gravity, 0, SpringLayout.EAST, slider_maxPlayers);
		sl_jp_option.putConstraint(SpringLayout.SOUTH, slider_gravity, 0, SpringLayout.SOUTH, slider_maxPlayers);
		bt_leftParty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkClient.get().sendObject(new LeftParty());
				tabbedPane.setEnabledAt(2, false);
				tabbedPane.setEnabledAt(1, true);
				tabbedPane.setSelectedIndex(1);
			}
		});
		sl_salonParty.putConstraint(SpringLayout.EAST, scrollPaneTxtAreaParty, 0, SpringLayout.EAST, salonParty);
		salonParty.add(scrollPaneTxtAreaParty);

		tf_sendParty = new JTextField();
		tf_sendParty.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if ( arg0.getKeyCode() == KeyEvent.VK_ENTER ) {
					NetworkClient.get().sendObject(new Talk2Party(tf_sendParty.getText()));
					tf_sendParty.setText("");
				}
			}
		});
		sl_salonParty.putConstraint(SpringLayout.WEST, tf_sendParty, 0, SpringLayout.WEST, salonParty);
		sl_salonParty.putConstraint(SpringLayout.NORTH, scrollPaneTxtAreaParty, -200, SpringLayout.SOUTH, tf_sendParty);
		sl_salonParty.putConstraint(SpringLayout.SOUTH, scrollPaneTxtAreaParty, 0, SpringLayout.NORTH, tf_sendParty);
		sl_salonParty.putConstraint(SpringLayout.SOUTH, tf_sendParty, 0, SpringLayout.SOUTH, salonParty);
		sl_salonParty.putConstraint(SpringLayout.EAST, tf_sendParty, 0, SpringLayout.EAST, salonParty);
		salonParty.add(tf_sendParty);
		tf_sendParty.setColumns(10);
	}

	@Override
	public void update(Observable o, final Object arg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if ( arg instanceof NMInfo ) {
					switch((NMInfo) arg){
						case NEWPARTY:
						case JOINPARTY:
							if ( arg == NMInfo.JOINPARTY ) {
								bt_ready.setVisible(false);
								bt_startParty.setVisible(false);
								bt_result.setVisible(false);
								slider_gravity.setEnabled(false);
								slider_maxPlayers.setEnabled(false);
							} else if ( arg == NMInfo.NEWPARTY ) {
								bt_ready.setVisible(false);
								bt_startParty.setVisible(true);
								bt_result.setVisible(false);
								slider_gravity.setEnabled(true);
								slider_maxPlayers.setEnabled(true);
							}

							// Link player with party
							if ( NetworkClient.get().getCurParty() != null && NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()) != null ) {
								tablePlayerParty.refresh(NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()).getPlayers());
							} else {
								System.out.println("BUG LINK PLAYER PARTY");
							}

							tabbedPane.setEnabledAt(1, false);
							tabbedPane.setEnabledAt(2, true);
							tabbedPane.setSelectedIndex(2);
							break;
							
						case MSG2ALL:
							txtA_salon.setText(NetworkClient.get().getMsg2All().toString());
							txtA_salon.validate();
							txtA_salon.repaint();
							break;
							
						case MSG2PARTY:
							txtA_salonparty.setText(NetworkClient.get().getMsg2Party().toString());
							txtA_salonparty.validate();
							txtA_salonparty.repaint();
							break;
						
						case STARTPARTY:
							setVisible(false);
							bt_result.setVisible(true);
							Main.himself.startParty(NetworkClient.get().getCurParty());
							break;
						
						case ENDPARTY:
							tabbedPane.setEnabledAt(1, false);
							tabbedPane.setEnabledAt(2, true);
							tabbedPane.setSelectedIndex(2);
							bt_ready.setVisible(false);
							bt_startParty.setVisible(false);
							setVisible(true);
							break;
						case LEFTPARTY:
							
							break;
						
						case UPDATEPARTYS:
							if ( NetworkClient.get().getCurParty() != null && NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()) != null ) {
								tablePlayerParty.refresh(NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()).getPlayers());
								ArrayList<Player> players =  NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()).getPlayers();
								
								//Le joueur est le patron de la partie
								if (!players.isEmpty() && players.get(0).getName().equalsIgnoreCase(Config.NAME)){
									bt_ready.setVisible(false);
									bt_startParty.setVisible(true);
									bt_result.setVisible(false);
									slider_gravity.setEnabled(true);
									slider_maxPlayers.setEnabled(true);
								}else{
									bt_ready.setVisible(false);
									bt_startParty.setVisible(false);
									bt_result.setVisible(false);
									slider_gravity.setEnabled(false);
									slider_maxPlayers.setEnabled(false);
								}
							
							}
							
							tableParty.refresh();
							break;
							
						case UPDATEPLAYERS:
							if ( NetworkClient.get().getCurParty() != null && NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()) != null ) {
								tablePlayerParty.refresh(NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()).getPlayers());
								
							}
							tablePlayer.refresh();
							break;
							
						case UPDATEPARTYOPTION:
							refreshPartyOption();
							break;
					}	
				}
			}
		});
	}
	
	public void refreshPartyOption(){
		if ( NetworkClient.get().getCurParty() != null && NetworkClient.get().getPartyInfobyId(NetworkClient.get().getCurParty().getId()) != null ) {
			slider_gravity.setValue(NetworkClient.get().getCurParty().getOption().getDownspeed());
			slider_maxPlayers.setValue(NetworkClient.get().getCurParty().getOption().getNbPlayerMax());
		}
	}
}
