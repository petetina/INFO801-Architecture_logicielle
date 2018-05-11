package info801.tp.gui.adapters;

import info801.tp.Tools;
import info801.tp.models.State;

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

    private int findNeedByProjectId(String id) {
        int i= 0;
        int res = -1;
        while(i<data.size()){
            String currentId = (String)(data.get(i).get(1));
            if(currentId.equals(id))
                return i;
            i++;
        }
        return -1;
    }

    public void updateNeedState(String id, State newState) {
        int rowIndex = findNeedByProjectId(id);
        System.out.println("updateNeedState"+rowIndex);
        data.get(rowIndex).set(headers.length-1,newState.toString());
        System.out.println("updateNeedState"+data.get(rowIndex).get(headers.length-1));
        fireTableDataChanged();
    }
}