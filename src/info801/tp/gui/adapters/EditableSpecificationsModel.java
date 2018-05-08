package info801.tp.gui.adapters;

import info801.tp.models.Specification;

import java.util.ArrayList;
import java.util.List;

public class EditableSpecificationsModel extends SpecificationsModel {

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 2 :
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
            case 3 : data.get(rowIndex).setCost(Double.valueOf((String)aValue));
                break;
            case 4 : data.get(rowIndex).setProductionTimeInDays(Integer.valueOf((String)aValue));
                break;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2 || column == 3 || column == 4;
    }

    public void add(Specification row) {
        super.add(row);
    }

}