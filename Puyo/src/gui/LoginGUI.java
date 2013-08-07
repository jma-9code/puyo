package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import main.Config;
import main.Main;
import network.NetworkClient;

import commun.KeyEncrypt;

public class LoginGUI extends JFrame {

	private JPanel contentPane;
	private JTextField tf_name;
	private JLabel lblPseudo;
	private JLabel lblMotDePasse;
	private JPasswordField tf_pwd;
	private LoginGUI himself;

	/**
	 * Create the frame.
	 */
	public LoginGUI () {
		himself = this;
		setTitle("Puyo - Identification");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 187, 169);
		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(null, "Identification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		tf_name = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.EAST, tf_name, 156, SpringLayout.WEST, contentPane);
		contentPane.add(tf_name);
		tf_name.setColumns(10);
		
		lblPseudo = new JLabel("Pseudo :");
		sl_contentPane.putConstraint(SpringLayout.NORTH, tf_name, 6, SpringLayout.SOUTH, lblPseudo);
		sl_contentPane.putConstraint(SpringLayout.WEST, tf_name, 0, SpringLayout.WEST, lblPseudo);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPseudo, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPseudo, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblPseudo);
		
		lblMotDePasse = new JLabel("Mot de passe :");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMotDePasse, 6, SpringLayout.SOUTH, tf_name);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMotDePasse, 0, SpringLayout.WEST, tf_name);
		contentPane.add(lblMotDePasse);
		
		tf_pwd = new JPasswordField();
		tf_pwd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_ENTER) return;
				Config.NAME = tf_name.getText();
				Config.PWD = KeyEncrypt.sha1(tf_pwd.getText());
				himself.setVisible(false);
				JFrame frame = new JFrame();
				frame.setSize(800, 600);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(new Main());
				frame.setVisible(true);
				frame.addWindowListener(new WindowListener() {

					@Override
					public void windowOpened(WindowEvent arg0) {
					}

					@Override
					public void windowIconified(WindowEvent arg0) {
					}

					@Override
					public void windowDeiconified(WindowEvent arg0) {
					}

					@Override
					public void windowDeactivated(WindowEvent arg0) {
					}

					@Override
					public void windowClosing(WindowEvent arg0) {
						NetworkClient.get().interrupt();
					}

					@Override
					public void windowClosed(WindowEvent arg0) {
					}

					@Override
					public void windowActivated(WindowEvent arg0) {
					}
				});
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, tf_pwd, 6, SpringLayout.SOUTH, lblMotDePasse);
		sl_contentPane.putConstraint(SpringLayout.WEST, tf_pwd, 0, SpringLayout.WEST, tf_name);
		sl_contentPane.putConstraint(SpringLayout.EAST, tf_pwd, 0, SpringLayout.EAST, tf_name);
		contentPane.add(tf_pwd);
		setVisible(true);
	}

	public JLabel getLblPseudo() {
		return lblPseudo;
	}

	public void setLblPseudo(JLabel lblPseudo) {
		this.lblPseudo = lblPseudo;
	}

	public JLabel getLblMotDePasse() {
		return lblMotDePasse;
	}

	public void setLblMotDePasse(JLabel lblMotDePasse) {
		this.lblMotDePasse = lblMotDePasse;
	}

	public JTextField getTf_name() {
		return tf_name;
	}

	public void setTf_name(JTextField tf_name) {
		this.tf_name = tf_name;
	}

	public JPasswordField getTf_pwd() {
		return tf_pwd;
	}

	public void setTf_pwd(JPasswordField tf_pwd) {
		this.tf_pwd = tf_pwd;
	}
}
