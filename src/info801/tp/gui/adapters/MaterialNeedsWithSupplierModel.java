package info801.tp.gui.adapters;

import info801.tp.models.MaterialNeed;

import java.util.ArrayList;
import java.util.List;

public class MaterialNeedsWithSupplierModel extends MaterialNeedsModel {
    protected String[] headers = {"Date","N° de commande","N° commande client","Nom du MO", "Materiels commandés","Transporteur","Etat"};

    @Override
    public String getColumnName(int columnIndex) {
        return headers[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if(rowIndex < data.size()){
            if(columnIndex != 5 && columnIndex != 6)
                return super.getValueAt(rowIndex,columnIndex);
            else {
                MaterialNeed materialNeed = data.get(rowIndex);
                String result = "";
                switch (columnIndex) {
                    case 5:
                        result += materialNeed.getSupplierName();
                        break;
                    case 6:
                        result += materialNeed.getState().toString();
                        break;
                }
                return result;
            }
        }else
            return null;
    }

    public void removeOthers(MaterialNeed materialNeed) {
        int i=0;
        while(i<data.size()){
            if(data.get(i).getId().equals(materialNeed.getId()) && !data.get(i).equals(materialNeed))
                data.remove(i);
            else
                i++;
        }
        fireTableDataChanged();
    }

    public MaterialNeed findByProjectId(String id) {
        int i=0;
        while(i<data.size()){
            if(data.get(i).getCustomerProjectId().equals(id))
                return data.get(i);
            else
                i++;
        }
        return null;
    }
}