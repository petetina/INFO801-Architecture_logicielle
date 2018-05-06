package info801.tp.gui;

import info801.tp.ManufacturerAgent;
import info801.tp.Tools;
import info801.tp.model.Specification;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerAgentGUI extends JFrame{
    private JPanel mainPanel;
    private JTable specificationsTable;
    private SpecificationsModel specificationsModel;

    public ManufacturerAgentGUI(ManufacturerAgent manufacturerAgent){
        setSize(890,360);
        setLocation(480,(int)(manufacturerAgent.getId()-1)*getHeight());
        setContentPane(mainPanel);
        //setLocationRelativeTo(null);
        setTitle("Manufacturer " + manufacturerAgent.getId());
        setVisible(true);

        populate();

        /*
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                System.out.println(getTitle() + " size : w= " + getWidth() + " h = " + getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                System.out.println(getTitle() + " position : x= " + getX() + " y = " + getY());
            }
        });
        */
    }

    public void addSpecification(Specification spec) {
        specificationsModel.add(spec);
    }

    private void populate(){
        specificationsModel = new SpecificationsModel();
        specificationsTable.setModel(specificationsModel);
        specificationsTable.setRowHeight(100);
    }

    private class SpecificationsModel extends AbstractTableModel {
        private String[] headers = {"Date","Maitre d'oeuvre", "Requirements", "Coût", "Nombre de jours de prod", "Quantité"};
        private List<Specification> data = new ArrayList<>();

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
                case 1 : result = data.get(rowIndex).getLogisticName();
                    break;
                case 2 :
                    result += "<html><body>";
                    for(String requirement : data.get(rowIndex).getRequirements())
                        result += requirement.trim() + "<br/>";
                    result += "</body></html>";
                    break;
                case 3 : result = data.get(rowIndex).getCost() + "€";
                    break;
                case 4 : result = data.get(rowIndex).getProductionTimeInDays()+"";
                    break;
                case 5 : result = data.get(rowIndex).getQuantity()+"";
                    break;
            }
            return result;
        }

        private void add(Specification row) {
            data.add(row);
            fireTableDataChanged();
        }

    }
}
