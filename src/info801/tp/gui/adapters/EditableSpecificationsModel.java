package info801.tp.gui.adapters;

import info801.tp.Tools;
import info801.tp.models.Specification;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EditableSpecificationsModel extends SpecificationsModel {
    private String[] headers = {"Date","Requirements", "Coût (en €)", "Nombre de jours de prod", "Quantité"};
    public List<Specification> data = new ArrayList<>();

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 1 :
                String requirements = (String) aValue;
                requirements = requirements.replace("<html>","")
                        .replace("<body>","")
                        .replace("<br/>","")
                        .replace("</body>","")
                        .replace("</html>","");
                String req[] = requirements.trim().split(";");

                List<String> result = new ArrayList<>();

                for(String requirement : req)
                    result.add(requirement);

                data.get(rowIndex).setRequirements(result);

                break;
            case 2 : data.get(rowIndex).setCost(Double.valueOf((String)aValue));
                break;
            case 3 : data.get(rowIndex).setProductionTimeInDays(Integer.valueOf((String)aValue));
                break;
            case 4 : data.get(rowIndex).setQuantity(Integer.valueOf((String)aValue));
                break;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1 || column == 2 || column == 3;
    }

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
                result += "<html><body>";
                for(String requirement : data.get(rowIndex).getRequirements())
                    result += requirement.trim() + "<br/>";
                result += "</body></html>";
                break;
            case 2 : result = data.get(rowIndex).getCost()+"";
                break;
            case 3 : result = data.get(rowIndex).getProductionTimeInDays()+"";
                break;
            case 4 : result = data.get(rowIndex).getQuantity()+"";
                break;
        }
        return result;
    }

    public void add(Specification row) {
        data.add(row);
        fireTableDataChanged();
    }

}