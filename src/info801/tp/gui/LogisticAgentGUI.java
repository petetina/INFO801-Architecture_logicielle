package info801.tp.gui;

import info801.tp.LogisticAgent;
import info801.tp.Tools;
import info801.tp.model.State;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LogisticAgentGUI extends JFrame implements ActionListener{


    private JPanel mainPanel;
    private JTable needsTable;
    private NeedsModel needsModel;
    private LogisticAgent logisticAgent;
    private JMenuItem menuItemSendRFP;
    private JMenuItem menuItemAskForMoreDetails;

    public LogisticAgentGUI(LogisticAgent logisticAgent){
        this.logisticAgent = logisticAgent;
        setLocation(-7,248);
        setSize(500,484);
        setContentPane(mainPanel);
        //setLocationRelativeTo(null);
        setTitle("Logistic "+logisticAgent.getId());
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

    private void populate(){
        needsModel = new NeedsModel();
        needsTable.setModel(needsModel);

        addMenu();
        needsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = needsTable.rowAtPoint(me.getPoint());
                needsTable.clearSelection();
                needsTable.setRowSelectionInterval(row,row);

            }
        });
    }

    private void addMenu(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemSendRFP = new JMenuItem("Send a request for proposal");
        menuItemSendRFP.addActionListener(this);
        popupMenu.add(menuItemSendRFP);

        menuItemAskForMoreDetails = new JMenuItem("Ask to consumer for more details");
        menuItemAskForMoreDetails.addActionListener(this);
        popupMenu.add(menuItemAskForMoreDetails);

        // sets the popup menu for the table
        needsTable.setComponentPopupMenu(popupMenu);
    }

    public void addNeed(String need){
        String array[] = need.split(";");
        List<Object> data = new ArrayList<>();
        for(String s : array) {
            data.add(s);
        }
        data.add(State.EN_ATTENTE);
        needsModel.add(data);
    }

    private class NeedsModel extends AbstractTableModel {
        private String[] headers = {"Date","Nom du client", "Besoin du client", "Etat"};
        private List<List<Object>> data = new ArrayList<>();

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

        private void add(List<Object> row) {
            row.add(0, Tools.getCurrentTime());
            data.add(row);
            fireTableDataChanged();
        }

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemSendRFP) {
            new CreateRFP(this,logisticAgent, (String)needsTable.getModel().getValueAt(needsTable.getSelectedRow(),1));
        } else if (menu == menuItemAskForMoreDetails) {
            JOptionPane.showMessageDialog(null, "TODO !", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
