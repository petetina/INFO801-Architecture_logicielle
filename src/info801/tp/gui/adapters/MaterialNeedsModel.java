package info801.tp.gui.adapters;

import info801.tp.Tools;
import info801.tp.models.MaterialNeed;
import info801.tp.models.State;
import info801.tp.models.StateMaterialNeed;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MaterialNeedsModel extends AbstractTableModel {
    protected String[] headers = {"Date","N° de commande","N° commande client","Nom du MO", "Materiels commandés","Etat"};
    public List<MaterialNeed> data = new ArrayList<>();

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
        if(rowIndex < data.size()){
            String result = "";
            MaterialNeed materialNeed = data.get(rowIndex);
            switch (columnIndex){
                case 0 :
                    result += Tools.getCurrentTime();
                    break;
                case 1:
                    result += materialNeed.getId();
                    break;
                case 2:
                    result += materialNeed.getCustomerProjectId();
                    break;
                case 3:
                    result += materialNeed.getLogisticName();
                    break;
                case 4:
                    result += materialNeed.getDatasInString();
                    break;
                case 5:
                    result += materialNeed.getState().toString();
                    break;
            }
            return result;
        }else
            return null;
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

    public void add(MaterialNeed row) {
        data.add(row);
        fireTableDataChanged();
    }

    public void updateNeedState(MaterialNeed materialNeed, StateMaterialNeed newState) {
        int rowIndex = findNeed(materialNeed);
        if(rowIndex != -1) {
            data.get(rowIndex).setState(newState);
            fireTableDataChanged();
        }
    }

    public void removeMaterialNeeds(String id) {
        int i=0;
        while(i<data.size()){
            if(data.get(i).getId().equals(id))
                data.remove(i);
            else
                i++;
        }
    }
}