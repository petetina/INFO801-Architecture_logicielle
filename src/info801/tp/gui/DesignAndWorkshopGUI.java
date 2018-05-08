package info801.tp.gui;

import info801.tp.DesignAndWorkShopAgent;
import info801.tp.gui.adapters.EditableSpecificationsModel;
import info801.tp.gui.adapters.SpecificationsModel;
import info801.tp.models.Specification;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DesignAndWorkshopGUI extends JFrame implements ActionListener {
    private DesignAndWorkShopAgent designAndWorkShopAgent;
    private JTable specificationsTable;
    private JPanel mainPanel;
    private JLabel lblLinkedLogistic;
    private JMenuItem menuItemMakeACounterRFP;
    private EditableSpecificationsModel specificationsModel;

    public DesignAndWorkshopGUI(DesignAndWorkShopAgent designAndWorkShopAgent){
        this.designAndWorkShopAgent = designAndWorkShopAgent;
        setSize(890,360);
        //setLocation(480,(int)(manufacturerAgent.getId()-1)*getHeight());
        setContentPane(mainPanel);
        //setLocationRelativeTo(null);
        lblLinkedLogistic.setText(lblLinkedLogistic.getText() + designAndWorkShopAgent.getId());
        setTitle("DesignAndWorkShop " + designAndWorkShopAgent.getId());
        setVisible(true);

        populate();
    }

    private void populate(){
        specificationsModel = new EditableSpecificationsModel();
        specificationsTable.setModel(specificationsModel);
        specificationsTable.setRowHeight(100);

        addMenu();
        specificationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = specificationsTable.rowAtPoint(me.getPoint());
                specificationsTable.clearSelection();
                specificationsTable.setRowSelectionInterval(row,row);

            }
        });
    }

    private void addMenu(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemMakeACounterRFP = new JMenuItem("Make a counter RFP");
        menuItemMakeACounterRFP.addActionListener(this);
        popupMenu.add(menuItemMakeACounterRFP);

        // sets the popup menu for the table
        specificationsTable.setComponentPopupMenu(popupMenu);
    }

    public void addSpecification(Specification specification){

        specificationsModel.add(specification);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemMakeACounterRFP) {
            designAndWorkShopAgent.makeACounterProposal(specificationsModel.data.get(specificationsTable.getSelectedRow()));
            JOptionPane.showMessageDialog(null,"Counter proposal sent to manufacturer "+designAndWorkShopAgent.getId(), "", JOptionPane.INFORMATION_MESSAGE);
        }
    }


}