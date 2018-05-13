package info801.tp.gui;

import info801.tp.TransporterAgent;
import info801.tp.gui.adapters.SpecificationsWithStateModel;
import info801.tp.gui.adapters.TransporterNeedsModel;
import info801.tp.models.Specification;
import info801.tp.models.State;
import info801.tp.models.StateTransporterNeed;
import info801.tp.models.TransporterNeed;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TransporterAgentGUI extends JFrame implements ActionListener {
    private TransporterAgent transporterAgent;
    private TransporterNeedsModel proposalModel;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTable proposalsTable;
    private JTable counterProposalsTable;
    private TransporterNeedsModel counterProposalsModel;
    private JMenuItem menuItemOK;
    private JMenuItem menuItemPasOK;
    private JMenuItem menuItemTransport;

    public TransporterAgentGUI(TransporterAgent transporterAgent) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.transporterAgent = transporterAgent;

        setSize(890,360);
        setLocation(480,(int)(transporterAgent.getId()-1)*getHeight());
        setContentPane(mainPanel);
        //setLocationRelativeTo(null);
        setTitle("Transporter " + transporterAgent.getId());
        setVisible(true);

        populate();

    }

    public void populate(){
        //For specifications
        proposalModel = new TransporterNeedsModel();
        proposalsTable.setModel(proposalModel);
        proposalsTable.setRowHeight(100);

        setMenuProposal();
        proposalsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = proposalsTable.rowAtPoint(me.getPoint());
                proposalsTable.clearSelection();
                proposalsTable.setRowSelectionInterval(row,row);
                TransporterNeed proposal = proposalModel.data.get(row);
                if(proposal.getState().equals(StateTransporterNeed.EN_ATTENTE))
                    setMenuProposal();
                else
                    proposalsTable.setComponentPopupMenu(null);

            }
        });

        counterProposalsModel = new TransporterNeedsModel();
        counterProposalsTable.setModel(counterProposalsModel);
        counterProposalsTable.setRowHeight(100);

        setMenuCounterProposals();
        counterProposalsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = counterProposalsTable.rowAtPoint(me.getPoint());
                counterProposalsTable.clearSelection();
                counterProposalsTable.setRowSelectionInterval(row,row);
                TransporterNeed counterProposal = counterProposalsModel.data.get(row);
                /*if(counterProposal.getState().equals(StateTransporterNeed))
                    setMenuCounterProposals();
                else
                    counterProposalsTable.setComponentPopupMenu(null);*/

            }
        });
    }

    private void setMenuCounterProposals() {
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemTransport = new JMenuItem("Transporter");
        menuItemTransport.addActionListener(this);
        popupMenu.add(menuItemTransport);

        // sets the popup menu for the table
        counterProposalsTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenuProposal() {
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemOK = new JMenuItem("OK");
        menuItemOK.addActionListener(this);
        popupMenu.add(menuItemOK);

        menuItemPasOK = new JMenuItem("Pas OK");
        menuItemPasOK.addActionListener(this);
        popupMenu.add(menuItemPasOK);

        // sets the popup menu for the table
        proposalsTable.setComponentPopupMenu(popupMenu);
    }

    public void addProposal(TransporterNeed transporterNeed) {
        proposalModel.add(transporterNeed);
    }

    public void addCounterProposal(TransporterNeed transporterNeed) {
        counterProposalsModel.add(transporterNeed);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();

        if(menu == menuItemOK)
            ;
        else if(menu == menuItemPasOK)
            ;
    }
}
