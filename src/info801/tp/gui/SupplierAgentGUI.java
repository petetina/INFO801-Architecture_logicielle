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
    private JMenuItem menuItemToPackage;
    private int rowSelectedMaterialNeedsDoing = -1;
    private int rowSelectedMaterialNeedsDone = -1;

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
                rowSelectedMaterialNeedsDoing = row;
                materialNeedsDoingTable.clearSelection();
                materialNeedsDoingTable.setRowSelectionInterval(row,row);
                StateMaterialNeed state = materialNeedsDoingModel.data.get(row).getState();
                if(state.equals(StateMaterialNeed.EN_ATTENTE))
                    setMenuInStateWaiting();
                else
                    materialNeedsDoingTable.setComponentPopupMenu(null);
            }

        });

        materialNeedsDoneModel = new MaterialNeedsModel();
        materialNeedsDoneTable.setModel(materialNeedsDoneModel);
        materialNeedsDoneTable.setRowHeight(100);
        setMenuMaterialNeedsDoneInStateToPackage();
        materialNeedsDoneTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = materialNeedsDoneTable.rowAtPoint(me.getPoint());
                rowSelectedMaterialNeedsDone = row;
                materialNeedsDoneTable.clearSelection();
                materialNeedsDoneTable.setRowSelectionInterval(row,row);
                StateMaterialNeed state = materialNeedsDoneModel.data.get(row).getState();
                if(state.equals(StateMaterialNeed.A_CONDITIONNER))
                    setMenuMaterialNeedsDoneInStateToPackage();
                else
                    materialNeedsDoneTable.setComponentPopupMenu(null);
            }
        });
    }

    private void setMenuMaterialNeedsDoneInStateToPackage() {
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemToPackage = new JMenuItem("Conditionner");
        menuItemToPackage.addActionListener(this);
        popupMenu.add(menuItemToPackage);

        // sets the popup menu for the table
        materialNeedsDoneTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenuInStateWaiting(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemICan = new JMenuItem("OK");
        menuItemICan.addActionListener(this);
        popupMenu.add(menuItemICan);

        menuItemICant = new JMenuItem("Pas OK");
        menuItemICant.addActionListener(this);
        popupMenu.add(menuItemICant);

        // sets the popup menu for the table
        materialNeedsDoingTable.setComponentPopupMenu(popupMenu);
    }

    public void addMaterialNeed(MaterialNeed materialNeed) {
        materialNeedsDoingModel.add(materialNeed);
    }

    public void addMaterialNeedDone(MaterialNeed materialNeed) {
        materialNeedsDoneModel.add(materialNeed);
    }

    public void updateMaterialNeedState(MaterialNeed materialNeed, StateMaterialNeed newState) {
        materialNeedsDoingModel.updateNeedState(materialNeed,newState);
    }

    public void updateMaterialNeedDoneState(String projectId, StateMaterialNeed newState) {
        materialNeedsDoneModel.updateNeedStateByProjectId(projectId,newState);
    }

    public void removeMaterialNeedsDoing(String id) {
        materialNeedsDoingModel.removeMaterialNeeds(id);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemICan) {
            MaterialNeed materialNeed = materialNeedsDoingModel.data.get(rowSelectedMaterialNeedsDoing);
            materialNeed.setState(StateMaterialNeed.ACCEPTE);
            supplierAgent.answerMaterialNeedRFP(materialNeed);
            updateMaterialNeedState(materialNeed,StateMaterialNeed.REPONDU);

        }else if(menu == menuItemICant){
            MaterialNeed materialNeed = materialNeedsDoingModel.data.get(rowSelectedMaterialNeedsDoing);
            materialNeed.setState(StateMaterialNeed.REJETE);
            supplierAgent.answerMaterialNeedRFP(materialNeed);
            updateMaterialNeedState(materialNeed,StateMaterialNeed.REPONDU);
        }else if(menu == menuItemToPackage){
            MaterialNeed materialNeed = materialNeedsDoneModel.data.get(rowSelectedMaterialNeedsDone);
            updateMaterialNeedDoneState(materialNeed.getCustomerProjectId(),StateMaterialNeed.CONDITIONNE);
            supplierAgent.packageMaterialNeed(materialNeed);
        }
    }
}
