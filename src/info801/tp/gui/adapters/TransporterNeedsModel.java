package info801.tp.gui.adapters;

import info801.tp.models.MaterialNeed;
import info801.tp.models.TransporterNeed;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TransporterNeedsModel extends AbstractTableModel {
    protected String[] headers = {"N° de commande","N° commande client","Nom du MO", "Materiels commandés","Source", "Destination", "Date de transport","Etat"};
    public List<TransporterNeed> data = new ArrayList<>();

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
        String result = "";

        if(rowIndex < data.size()){
            TransporterNeed transporterNeed = data.get(rowIndex);
            switch (columnIndex){
                case 0:
                    result += transporterNeed.getId();
                    break;
                case 1:
                    result += transporterNeed.getProject().getId();
                    break;
                case 2:
                    result += transporterNeed.getProject().getLogisticName();
                    break;
                case 3 :
                    for(String req : transporterNeed.getProject().getRequirements())
                        result += req + ";";
                    break;
                case 4:
                    result += transporterNeed.getAddressFrom();
                    break;
                case 5:
                    result += transporterNeed.getWarehouseDestination();
                    break;
                case 6:
                    result += transporterNeed.getDate();
                    break;
                case 7:
                    result += transporterNeed.getState().toString();
                    break;
            }

        }
        return result;
    }

    private int findNeed(MaterialNeed materialNeed) {
        int i= 0;
        while(i<data.size()){
            if(data.get(i).equals(materialNeed))
                return i;
            i++;
        }
        return -1;
    }

    public void add(TransporterNeed row) {
        data.add(row);
        fireTableDataChanged();
    }
}