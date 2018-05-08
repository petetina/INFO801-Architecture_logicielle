package info801.tp.gui.adapters;

import info801.tp.Tools;
import info801.tp.models.Specification;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SpecificationsModel extends AbstractTableModel {
    protected String[] headers = {"Date","Numéro de commande","Requirements", "Coût (en €)", "Nombre de jours de prod", "Quantité", "Fabricant"};
    public List<Specification> data = new ArrayList<>();

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
        switch(columnIndex){
            case 0:
                result = Tools.getCurrentTime();
                break;
            case 1 :
                result = data.get(rowIndex).getId()+"";
                break;
            case 2 :
                result += "<html><body>";
                for(String requirement : data.get(rowIndex).getRequirements())
                    result += requirement.trim() + "<br/>";
                result += "</body></html>";
                break;
            case 3 : result = data.get(rowIndex).getCost()+"";
                break;
            case 4 : result = data.get(rowIndex).getProductionTimeInDays()+"";
                break;
            case 5 : result = data.get(rowIndex).getQuantity()+"";
                break;
            case 6: result = data.get(rowIndex).getManufacturer();
                break;
        }
        return result;
    }

    public void add(Specification row) {
        data.add(row);
        fireTableDataChanged();
    }

    public void remove(int rowIndex){
        if(rowIndex < data.size()) {

            data.remove(rowIndex);
            fireTableDataChanged();
        }
    }

    public int size(){
        return data.size();
    }

}