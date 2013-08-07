package tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class CountryRenderer extends DefaultTableCellRenderer {
	 
    public Component getTableCellRendererComponent(
                            JTable table, Object country,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
    	super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
    	setIcon(new ImageIcon(getClass().getResource("/res/images/country/" + country.toString() + ".png")));
    	setVerticalAlignment(JLabel.CENTER);
    	setHorizontalAlignment(JLabel.CENTER);
        setToolTipText(" Pays - " + ((Locale)country));
        return this;
    }

}
