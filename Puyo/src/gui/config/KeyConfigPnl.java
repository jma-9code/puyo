package gui.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dialog.ModalityType;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;
import javax.swing.BoxLayout;

import main.Config;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Dimension;

public class KeyConfigPnl extends JPanel {
	private JButton btnBas;
	private JButton btnRotationDroite;
	private JButton btnRotationGauche;
	private JButton btnGauche;
	private JButton btnDroite;

	/**
	 * Create the dialog.
	 */
	public KeyConfigPnl() {
		setSize(new Dimension(350, 250));
		this.setBorder(new TitledBorder(null, "Configuration des touches", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.setLayout(new BorderLayout(0, 0));
		
		btnGauche = new JButton("Gauche");
		btnGauche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnGauche.setText("Gauche - ?");
				btnGauche.addKeyListener(new KeyAdapter(){
					public void keyReleased(KeyEvent e){
						if (e.getKeyCode() != Config.J0_RIGHT && e.getKeyCode() != Config.J0_ROT1 
								&& e.getKeyCode() != Config.J0_ROT2 && e.getKeyCode() != Config.J0_DOWN){
							Config.J0_LEFT = e.getKeyCode();
							refreshKeyText();
							btnGauche.removeKeyListener(this);
						}else{
							refreshKeyText();
							btnGauche.removeKeyListener(this);
						}
					}	
				});
			}
		});
		this.add(btnGauche, BorderLayout.WEST);
		
		btnDroite = new JButton("Droite");
		btnDroite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDroite.setText("Droite - ?");
				btnDroite.addKeyListener(new KeyAdapter(){
					public void keyReleased(KeyEvent e){
						if (e.getKeyCode() != Config.J0_LEFT && e.getKeyCode() != Config.J0_ROT1 
								&& e.getKeyCode() != Config.J0_ROT2 && e.getKeyCode() != Config.J0_DOWN){
							Config.J0_RIGHT = e.getKeyCode();
							refreshKeyText();
							btnDroite.removeKeyListener(this);
						}else{
							refreshKeyText();
							btnDroite.removeKeyListener(this);
						}
					}	
				});
			}
		});
		this.add(btnDroite, BorderLayout.EAST);
		
		btnRotationGauche = new JButton("Rotation Gauche");
		btnRotationGauche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRotationGauche.setText("Rotation Gauche - ?");
				btnRotationGauche.addKeyListener(new KeyAdapter(){
					public void keyReleased(KeyEvent e){
						if (e.getKeyCode() != Config.J0_LEFT && e.getKeyCode() != Config.J0_RIGHT 
								&& e.getKeyCode() != Config.J0_ROT2 && e.getKeyCode() != Config.J0_DOWN){
							Config.J0_ROT1 = e.getKeyCode();
							refreshKeyText();
							btnRotationGauche.removeKeyListener(this);
						}else{
							refreshKeyText();
							btnRotationGauche.removeKeyListener(this);
						}
					}	
				});
			}
		});
		this.add(btnRotationGauche, BorderLayout.NORTH);
		
		btnRotationDroite = new JButton("Rotation Droite");
		btnRotationDroite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRotationDroite.setText("Rotation Droite - ?");
				btnRotationDroite.addKeyListener(new KeyAdapter(){
					public void keyReleased(KeyEvent e){
						if (e.getKeyCode() != Config.J0_LEFT && e.getKeyCode() != Config.J0_RIGHT 
								&& e.getKeyCode() != Config.J0_ROT1 && e.getKeyCode() != Config.J0_DOWN){
							Config.J0_ROT2 = e.getKeyCode();
							refreshKeyText();
							btnRotationDroite.removeKeyListener(this);
						}else{
							refreshKeyText();
							btnRotationDroite.removeKeyListener(this);
						}
					}	
				});
			}
		});
		this.add(btnRotationDroite, BorderLayout.SOUTH);
		
		btnBas = new JButton("Bas");
		btnBas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBas.setText("Bas - ?");
				btnBas.addKeyListener(new KeyAdapter(){
					public void keyReleased(KeyEvent e){
						if (e.getKeyCode() != Config.J0_LEFT && e.getKeyCode() != Config.J0_RIGHT 
								&& e.getKeyCode() != Config.J0_ROT1 && e.getKeyCode() != Config.J0_ROT2){
							Config.J0_DOWN = e.getKeyCode();
							refreshKeyText();
							btnBas.removeKeyListener(this);
						}else{
							refreshKeyText();
							btnBas.removeKeyListener(this);
						}
					}	
				});
			}
		});
		this.add(btnBas, BorderLayout.CENTER);
		refreshKeyText();
	}
	
	public void refreshKeyText(){
		btnBas.setText("Bas - " + KeyEvent.getKeyText(Config.J0_DOWN));
		btnRotationDroite.setText("Rotation Droite - " + KeyEvent.getKeyText(Config.J0_ROT2));
		btnRotationGauche.setText("Rotation Gauche - " + KeyEvent.getKeyText(Config.J0_ROT1));
		btnDroite.setText("Droite - " + KeyEvent.getKeyText(Config.J0_RIGHT));
		btnGauche.setText("Gauche - " + KeyEvent.getKeyText(Config.J0_LEFT));
	}
}
