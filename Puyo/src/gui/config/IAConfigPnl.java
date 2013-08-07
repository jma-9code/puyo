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
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JSlider;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class IAConfigPnl extends JPanel {
	private JSlider sld_iaSpeed;

	/**
	 * Create the dialog.
	 */
	public IAConfigPnl() {
		setSize(new Dimension(350, 250));
		this.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Configuration de l'IA", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 70, 213)));
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		sld_iaSpeed = new JSlider();
		
		sld_iaSpeed.setValue(Config.IASPEED);
		sld_iaSpeed.setPaintTicks(true);
		sld_iaSpeed.setPaintLabels(true);
		sld_iaSpeed.setMaximum(1000);
		sld_iaSpeed.setMinimum(10);
		sld_iaSpeed.setBorder(new TitledBorder(null, "Vittesse de l'IA - " + Config.IASPEED + " ms" , TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sld_iaSpeed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Config.IASPEED = sld_iaSpeed.getValue();
				sld_iaSpeed.setBorder(new TitledBorder(null, "Vittesse de l'IA - " + Config.IASPEED + " ms" , TitledBorder.LEADING, TitledBorder.TOP, null, null));
			}
		});
		add(sld_iaSpeed);
	}
	
}
