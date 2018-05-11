package info801.tp.gui;

import info801.tp.LogisticAgent;
import info801.tp.RandomGenerator;
import info801.tp.gui.adapters.NeedsModel;
import info801.tp.gui.adapters.SpecificationsWithFabricantModel;
import info801.tp.models.Specification;
import info801.tp.models.State;

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
    private SpecificationsWithFabricantModel counterProposalsModel;
    private LogisticAgent logisticAgent;
    private JMenuItem menuItemSendRFP;
    private JMenuItem menuItemAskForMoreDetails;
    private JMenuItem menuItemSendCounterProposalToCustomer;

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

        counterProposalsModel = new SpecificationsWithFabricantModel();
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

    private void addMenuCounterProposals(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemSendCounterProposalToCustomer = new JMenuItem("Send all proposals for this project to customer");
        menuItemSendCounterProposalToCustomer.addActionListener(this);
        popupMenu.add(menuItemSendCounterProposalToCustomer);

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
        JOptionPane.showMessageDialog(null, "Counter proposals has been sent to customer !", "", JOptionPane.INFORMATION_MESSAGE);

    }

    public void removeCounterProposal(Specification proposal) {
        counterProposalsModel.removeSpecification(proposal);
    }

    public void removeOtherCounterProposals(Specification counterProposal) {
        counterProposalsModel.removeOtherCounterProposals(counterProposal);
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
        }
    }

    public boolean hasNoMoreCounterProposals(String id) {
        return counterProposalsModel.countProposalForProject(id) == 0;
    }

    public void updateNeedState(String id, State newState) {
        needsModel.updateNeedState(id,newState);
    }
}
