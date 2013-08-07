package gui.config;

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ConfigGUI extends JDialog {

	private JPanel configChoice;
	private JPanel configX;
	private JList listChoice;
	private KeyConfigPnl configKey = new KeyConfigPnl();
	private IAConfigPnl configIA = new IAConfigPnl();

	String[] values = new String[] { "Config. Touches", "Config. IA", "Config. Graphiques", "Config. Son",
			"Config. Réseau" };
	private JSplitPane splitPane;

	public ConfigGUI () {
		this(null, true);
	}
	
	/**
	 * Create the dialog.
	 */
	public ConfigGUI ( Dialog dial, boolean modal ) {
		super(dial, modal);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());

		splitPane = new JSplitPane();
		getContentPane().add(splitPane, BorderLayout.CENTER);

		configChoice = new JPanel();
		splitPane.setLeftComponent(configChoice);
		configChoice.setLayout(new BorderLayout(0, 0));

		listChoice = new JList(values);
		listChoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listChoice.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					configX.removeAll();
					switch (listChoice.getSelectedIndex()) {
						case 0:
							configX.add(configKey);
						break;
						case 1:
							configX.add(configIA);
						break;
						case 2:
						break;
						case 3:
						break;
						case 4:
						break;
						default:
						break;
					}
					configX.validate();
					configX.repaint();
				}
			}
		});

		configChoice.add(listChoice);

		configX = new JPanel();
		splitPane.setRightComponent(configX);
		configX.setLayout(null);

	}

}
