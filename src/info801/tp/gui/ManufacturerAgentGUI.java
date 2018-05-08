package info801.tp.gui;

import info801.tp.ManufacturerAgent;
import info801.tp.Tools;
import info801.tp.gui.adapters.SpecificationsModel;
import info801.tp.models.Specification;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerAgentGUI extends JFrame implements ActionListener {
    private ManufacturerAgent manufacturerAgent;
    private JPanel mainPanel;
    private JTable specificationsTable;
    private JTable counterProposalsTable;
    private SpecificationsModel specificationsModel;
    private SpecificationsModel counterProposalsModel;
    private JMenuItem menuItemAnalyse;
    private JMenuItem menuItemSendCounterProposalToLogistic;

    public ManufacturerAgentGUI(ManufacturerAgent manufacturerAgent){
        this.manufacturerAgent = manufacturerAgent;
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
    public void addCounterSpecification(Specification counterProposal) {counterProposalsModel.add(counterProposal);}

    private void populate(){
        //For specifications
        specificationsModel = new SpecificationsModel();
        specificationsTable.setModel(specificationsModel);
        specificationsTable.setRowHeight(100);

        addMenuSpecifications();
        specificationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = specificationsTable.rowAtPoint(me.getPoint());
                specificationsTable.clearSelection();
                specificationsTable.setRowSelectionInterval(row,row);

            }
        });

        //For counter proposals
        counterProposalsModel = new SpecificationsModel();
        counterProposalsTable.setModel(counterProposalsModel);
        counterProposalsTable.setRowHeight(100);

        addMenuCounterProposals();
        counterProposalsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = counterProposalsTable.rowAtPoint(me.getPoint());
                counterProposalsTable.clearSelection();
                counterProposalsTable.setRowSelectionInterval(row,row);
            }
        });
    }

    private void addMenuSpecifications(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemAnalyse = new JMenuItem("Analyse");
        menuItemAnalyse.addActionListener(this);
        popupMenu.add(menuItemAnalyse);

        // sets the popup menu for the table
        specificationsTable.setComponentPopupMenu(popupMenu);
    }

    private void addMenuCounterProposals(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemSendCounterProposalToLogistic = new JMenuItem("Send counter proposal to logistic");
        menuItemSendCounterProposalToLogistic.addActionListener(this);
        popupMenu.add(menuItemSendCounterProposalToLogistic);

        // sets the popup menu for the table
        counterProposalsTable.setComponentPopupMenu(popupMenu);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemAnalyse) {
            manufacturerAgent.askToDesignAndWorkshop(specificationsModel.data.get(specificationsTable.getSelectedRow()));
            JOptionPane.showMessageDialog(null, "Sent for analysing !", "", JOptionPane.INFORMATION_MESSAGE);
        }else if(menu == menuItemSendCounterProposalToLogistic){
            Specification counterProposal = counterProposalsModel.data.get(counterProposalsTable.getSelectedRow());
            manufacturerAgent.transmitCounterProposalToLogistic(counterProposal);
            JOptionPane.showMessageDialog(null, "Counter proposal transmitted to logistic " + counterProposal.getLogisticName() + "!", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
