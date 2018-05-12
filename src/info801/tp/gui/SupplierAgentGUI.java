package info801.tp.gui;

import info801.tp.SupplierAgent;
import info801.tp.gui.adapters.MaterialNeedsModel;
import info801.tp.models.MaterialNeed;
import info801.tp.models.StateMaterialNeed;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SupplierAgentGUI extends JFrame implements ActionListener {
    private SupplierAgent supplierAgent;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JTable materialNeedsDoingTable;
    private MaterialNeedsModel materialNeedsDoingModel;
    private JTable materialNeedsDoneTable;
    private MaterialNeedsModel materialNeedsDoneModel;
    private JMenuItem menuItemICan;
    private JMenuItem menuItemICant;

    public SupplierAgentGUI(SupplierAgent supplierAgent) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.supplierAgent = supplierAgent;
        setSize(500,250);
        setContentPane(mainPanel);
        //setLocationRelativeTo(null);
        setLocation(-8,0);
        setTitle("Supplier " + supplierAgent.getId());
        setVisible(true);

        populate();
    }

    private void populate(){

        materialNeedsDoingModel = new MaterialNeedsModel();
        materialNeedsDoingTable.setModel(materialNeedsDoingModel);

        setMenuInStateWaiting();
        materialNeedsDoingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = materialNeedsDoingTable.rowAtPoint(me.getPoint());
                materialNeedsDoingTable.clearSelection();
                materialNeedsDoingTable.setRowSelectionInterval(row,row);
                StateMaterialNeed state = materialNeedsDoingModel.data.get(row).getState();
                if(state.equals(StateMaterialNeed.EN_ATTENTE))
                    setMenuInStateWaiting();
                else if(state.equals(StateMaterialNeed.REPONDU))
                    setMenuInStateAnswered();
                else if(state.equals(StateMaterialNeed.ACCEPTE))
                    setMenuInStateAccepted();
                else if(state.equals(StateMaterialNeed.REJETE))
                    setMenuInStateRejected();
            }

        });

        materialNeedsDoneModel = new MaterialNeedsModel();
        materialNeedsDoneTable.setModel(materialNeedsDoneModel);
        materialNeedsDoneTable.setRowHeight(100);
    }

    private void setMenuInStateWaiting(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemICan = new JMenuItem("I can");
        menuItemICan.addActionListener(this);
        popupMenu.add(menuItemICan);

        menuItemICant = new JMenuItem("I can't");
        menuItemICant.addActionListener(this);
        popupMenu.add(menuItemICant);

        // sets the popup menu for the table
        materialNeedsDoingTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenuInStateAnswered() {
    }

    private void setMenuInStateAccepted() {
    }

    private void setMenuInStateRejected() {
    }

    public void addMaterialNeed(MaterialNeed materialNeed) {
        materialNeedsDoingModel.add(materialNeed);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemICan) {
            //String projectId = (String) needsTable.getModel().getValueAt(needsTable.getSelectedRow(), 1);
            //Integer quantity = Integer.val
        }else if(menu == menuItemICant){

        }
    }
}
