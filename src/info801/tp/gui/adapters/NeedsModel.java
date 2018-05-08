package info801.tp.gui.adapters;

import info801.tp.Tools;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class NeedsModel extends AbstractTableModel {
    private String[] headers = {"Date","Numéro de commande","Nom du client", "Besoin du client", "Quantité","Etat"};
    public List<List<Object>> data = new ArrayList<>();

    @Override
    public String getColumnName(int columnIndex) {
        return headers[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }

    public void add(List<Object> row) {
        row.add(0, Tools.getCurrentTime());
        data.add(row);
        fireTableDataChanged();
    }

}