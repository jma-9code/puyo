package tools;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import commun.Player;
import commun.puyo.Puyo_PartyInfo;

public class TableParty extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Puyo_PartyInfo> partys = new ArrayList<Puyo_PartyInfo>();

	public TableParty ( ArrayList<Puyo_PartyInfo> arrayList ) {
		partys = arrayList;
	}

	public void refresh() {
		fireTableDataChanged();
	}

	public void refresh(Player p) {
		int n = partys.indexOf(p);
		fireTableRowsUpdated(n, n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int col) {
		switch ( col ) {
			case 0:
				return "Id";
			case 1:
				return "Joueurs";
			case 2:
				return "Config";
			case 3:
				return "Statut";
			default:
				return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return partys.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		if ( row > partys.size() - 1 ) return "";
		switch ( col ) {
			case 0:
				return partys.get(row).getId();
			case 1:
				return partys.get(row).getPlayers();
			case 2:
				return partys.get(row).getOption();
			case 3:
				return partys.get(row).getStatut();
			default:
				return null;
		}
	}

	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	public void removeRow(final int row) {

	}

}
