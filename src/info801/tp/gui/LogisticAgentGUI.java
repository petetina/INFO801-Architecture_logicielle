package info801.tp.gui;

import info801.tp.LogisticAgent;
import info801.tp.RandomGenerator;
import info801.tp.gui.adapters.MaterialNeedsModel;
import info801.tp.gui.adapters.MaterialNeedsWithSupplierModel;
import info801.tp.gui.adapters.NeedsModel;
import info801.tp.gui.adapters.SpecificationsWithFabricantModel;
import info801.tp.models.MaterialNeed;
import info801.tp.models.Specification;
import info801.tp.models.State;
import info801.tp.models.StateMaterialNeed;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LogisticAgentGUI extends JFrame implements ActionListener{


    private JPanel mainPanel;
    private JTable needsTable;
    private NeedsModel needsModel;
    private JTable counterProposalsTable;
    private JTabbedPane tabbedPane;
    private JTable materialNeedsTable;
    private MaterialNeedsWithSupplierModel materialNeedsModel;
    private JTable materialNeedRFPTable;
    private MaterialNeedsModel materialNeedRFPModel;
    private SpecificationsWithFabricantModel counterProposalsModel;
    public LogisticAgent logisticAgent;
    private JMenuItem menuItemSendRFP;
    private JMenuItem menuItemAskForMoreDetails;
    private JMenuItem menuItemSendCounterProposalToCustomer;
    private JMenuItem menuItemRFPSuppliers;
    private JMenuItem menuItemRFPTransporters;
    private JMenuItem menuItemChooseSupplier;
    private int rowSelectedCounterProposal = -1;
    private int rowSelectedMaterialNeeds = -1;

    public LogisticAgentGUI(LogisticAgent logisticAgent){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        setMenu();
        needsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = needsTable.rowAtPoint(me.getPoint());
                needsTable.clearSelection();
                needsTable.setRowSelectionInterval(row,row);
                if(((State)(needsModel.data.get(row).get(5))).equals(State.EN_ATTENTE))
                    setMenu();
                else
                    needsTable.setComponentPopupMenu(null);

            }
        });

        counterProposalsModel = new SpecificationsWithFabricantModel();
        counterProposalsTable.setModel(counterProposalsModel);
        counterProposalsTable.setRowHeight(100);
        setMenuCounterProposals();
        counterProposalsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = counterProposalsTable.rowAtPoint(me.getPoint());
                rowSelectedCounterProposal = row;
                counterProposalsTable.setRowSelectionInterval(row,row);
                counterProposalsTable.clearSelection();
                State state = counterProposalsModel.data.get(row).getState();
                if(state.equals(State.EN_ATTENTE))
                    setMenuCounterProposals();
                else if(state.equals(State.ACCEPTE))
                    setMenuCounterProposalsAccepted();
                else
                    counterProposalsTable.setComponentPopupMenu(null);
            }
        });

        materialNeedsModel = new MaterialNeedsWithSupplierModel();
        materialNeedsTable.setModel(materialNeedsModel);
        materialNeedsTable.setRowHeight(100);
        setMenuMaterialNeedsInStateAccepted();
        materialNeedsTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                int row = materialNeedsTable.rowAtPoint(me.getPoint());
                rowSelectedMaterialNeeds = row;
                materialNeedsTable.clearSelection();
                materialNeedsTable.setRowSelectionInterval(row,row);
                System.out.println("row = "+row);
                StateMaterialNeed state = materialNeedsModel.data.get(row).getState();
                System.out.println(state.toString());
                if(state.equals(StateMaterialNeed.ACCEPTE))
                    setMenuMaterialNeedsInStateAccepted();
                else
                materialNeedsTable.setComponentPopupMenu(null);

            }
        });

        materialNeedRFPModel = new MaterialNeedsModel();
        materialNeedRFPTable.setModel(materialNeedRFPModel);
    }

    private void setMenuMaterialNeedsInStateAccepted() {
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemChooseSupplier = new JMenuItem("Choisir ce fournisseur");
        menuItemChooseSupplier.addActionListener(this);
        popupMenu.add(menuItemChooseSupplier);

        // sets the popup menu for the table
        materialNeedsTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenu(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemSendRFP = new JMenuItem("Faire un appel d'offre");
        menuItemSendRFP.addActionListener(this);
        popupMenu.add(menuItemSendRFP);

        menuItemAskForMoreDetails = new JMenuItem("Demander au client plus de détails");
        menuItemAskForMoreDetails.addActionListener(this);
        popupMenu.add(menuItemAskForMoreDetails);

        // sets the popup menu for the table
        needsTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenuCounterProposals(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemSendCounterProposalToCustomer = new JMenuItem("Envoyer toutes les propositions du projet au client");
        menuItemSendCounterProposalToCustomer.addActionListener(this);
        popupMenu.add(menuItemSendCounterProposalToCustomer);

        // sets the popup menu for the table
        counterProposalsTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenuCounterProposalsAccepted(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemRFPSuppliers = new JMenuItem("Envoyer un appel d'offre aux fournisseurs");
        menuItemRFPSuppliers.addActionListener(this);
        popupMenu.add(menuItemRFPSuppliers);

        menuItemRFPTransporters = new JMenuItem("Envoyer un appel d'offre aux transporteurs");
        menuItemRFPTransporters.addActionListener(this);
        popupMenu.add(menuItemRFPTransporters);

        // sets the popup menu for the table
        counterProposalsTable.setComponentPopupMenu(popupMenu);
    }

    public void addNeed(String need){
        String array[] = need.split(";");
        List<Object> data = new ArrayList<>();
        data.add(RandomGenerator.generateId());
        for(String s : array) {
            data.add(s);
        }
        data.add(State.EN_ATTENTE);
        needsModel.add(data);
    }

    public void addCounterProposal(Specification counterProposal) {
        counterProposalsModel.add(counterProposal);
    }

    public void sendAllProposalsForProject(String projectId,String customerId){
        String allProposals = "";
        for(Specification proposal : counterProposalsModel.data){
            if(proposal.getId().equals(projectId))
                allProposals += proposal.toString() + ";;";
        }
        if(!allProposals.isEmpty())
            allProposals = allProposals.substring(0,allProposals.length()-2);

        logisticAgent.transmitCounterRFPToCustomer(allProposals,customerId);
        JOptionPane.showMessageDialog(null, "Les contre-propositions ont été envoyées au client !", "", JOptionPane.INFORMATION_MESSAGE);

    }

    public void removeCounterProposal(Specification proposal) {
        counterProposalsModel.removeSpecification(proposal);
    }

    public void removeOtherCounterProposals(Specification counterProposal) {
        counterProposalsModel.removeOtherCounterProposals(counterProposal);
    }

    public boolean hasNoMoreCounterProposals(String id) {
        return counterProposalsModel.countProposalForProject(id) == 0;
    }

    public void updateNeedState(String id, State newState) {
        needsModel.updateNeedState(id,newState);
    }

    public void updateSpecificationState(String id, State newState) {
        counterProposalsModel.updateSpecificationState(id,newState);
    }

    public void addMaterialNeed(MaterialNeed materialNeed) {
        materialNeedsModel.add(materialNeed);
    }

    public void addMaterialNeedRFP(MaterialNeed materialNeed) {
        materialNeedRFPModel.add(materialNeed);
    }

    private void updateMaterialNeed(MaterialNeed materialNeed, StateMaterialNeed newState) {
        materialNeedsModel.removeOthers(materialNeed);
        materialNeedsModel.updateNeedState(materialNeed,newState);
    }

    public void updateMaterialNeedByProjectId(String projectId, StateMaterialNeed newState) {
        MaterialNeed materialNeed = materialNeedsModel.findByProjectId(projectId);
        materialNeedsModel.updateNeedState(materialNeed,newState);
    }

    private void removeMaterialNeedRFP(String id) {
        materialNeedRFPModel.removeMaterialNeeds(id);
    }

    public String findSupplierForProject(String id) {
        MaterialNeed materialNeed = materialNeedsModel.findByProjectId(id);
        if(materialNeed == null)
            return "";
        else
            return materialNeed.getSupplierName();

    }

    public String findManufacturerForProject(String id) {
        int row = counterProposalsModel.findSpecificationByProjectId(id);
        Specification counterProposal = counterProposalsModel.data.get(row);
        if(counterProposal == null)
            return "";
        else
            return counterProposal.getManufacturer();

    }

    @Override
    public void actionPerformed(ActionEvent event) {

        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemSendRFP) {
            String projectId = (String)needsTable.getModel().getValueAt(needsTable.getSelectedRow(),1);
            Integer quantity = Integer.valueOf((String)needsTable.getModel().getValueAt(needsTable.getSelectedRow(),4));
            String customerName = (String)needsTable.getModel().getValueAt(needsTable.getSelectedRow(),2);
            new CreateRFP(this,logisticAgent, projectId,customerName,quantity);
        } else if (menu == menuItemAskForMoreDetails) {
            JOptionPane.showMessageDialog(null, "TODO !", "", JOptionPane.INFORMATION_MESSAGE);
        }else if(menu == menuItemSendCounterProposalToCustomer){
            String projectId = (String)needsTable.getModel().getValueAt(needsTable.getSelectedRow(),1);
            String customerId = (String)needsTable.getModel().getValueAt(needsTable.getSelectedRow(),2);
            sendAllProposalsForProject(projectId,customerId);
        }else if(menu == menuItemRFPSuppliers){
            new CreateRFPMaterials(this, counterProposalsModel.data.get(rowSelectedCounterProposal).getId());
        }else if(menu == menuItemRFPTransporters){

        }else if(menu == menuItemChooseSupplier){
            MaterialNeed materialNeed = materialNeedsModel.data.get(rowSelectedMaterialNeeds);
            logisticAgent.acceptMaterialNeedSupplier(materialNeed);
            updateMaterialNeed(materialNeed,StateMaterialNeed.CHOISI);
            removeMaterialNeedRFP(materialNeed.getId());
        }
    }

}
