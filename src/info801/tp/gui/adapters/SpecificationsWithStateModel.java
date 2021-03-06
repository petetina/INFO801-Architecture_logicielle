package info801.tp.gui.adapters;

import info801.tp.Tools;
import info801.tp.models.Specification;
import info801.tp.models.State;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SpecificationsWithStateModel extends AbstractTableModel {
    protected String[] headers = {"Date","Numéro de commande","Requirements", "Coût (en €)", "Nombre de jours de prod", "Quantité", "Etat"};
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
            case 6: result = data.get(rowIndex).getState().toString();
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

    public int countProposalForProject(String projectId){
        int i = 0;
        int res = 0;
        while(i<data.size()){
            if(data.get(i).getId().equals(projectId))
                res ++;
            i++;
        }
        return res;
    }

    public void removeSpecificationsFromProjectId(String projectId){
        int i = 0;
        while(i<data.size()){
            if(data.get(i).getId().equals(projectId)) {
                data.remove(i);
            }else
                i++;
        }
        fireTableDataChanged();
    }

    public void removeOtherCounterProposals(Specification specification) {
        int i = 0;
        while(i<data.size()){
            if(data.get(i).getId().equals(specification.getId()) && !data.get(i).equals(specification)) {
                data.remove(i);
            }else
                i++;
        }
        fireTableDataChanged();
    }

    public void removeSpecification(Specification proposal){
        int i = 0;
        while(i<data.size()){
            if(data.get(i).equals(proposal)) {
                String res = "";
                for(String s : data.get(i).getRequirements())
                    res += s + ";";
                System.out.println("Removed : "+res);
                data.remove(i);
                break;
            }else
                i++;
        }
        fireTableDataChanged();
    }

    protected int findSpecificationByProjectId(Specification proposal){
        int i = 0;
        while(i<data.size()){
            if(data.get(i).equals(proposal)) {
                return i;
            }else
                i++;
        }
        return -1;
    }

    public int findSpecificationByProjectId(String projectId){
        int i = 0;
        while(i<data.size()){
            if(data.get(i).getId().equals(projectId)) {
                return i;
            }else
                i++;
        }
        return -1;
    }

    public void changeStateFromProjectId(String projectId, State newState){
        int rowIndex = findSpecificationByProjectId(projectId);
        data.get(rowIndex).setState(newState);
        fireTableDataChanged();
    }

    public void changeState(Specification proposal, State newState){
        int rowIndex = findSpecificationByProjectId(proposal);
        data.get(rowIndex).setState(newState);
        fireTableDataChanged();
    }

    public int size(){
        return data.size();
    }

}