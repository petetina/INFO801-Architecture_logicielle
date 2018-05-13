package info801.tp.gui;

import info801.tp.ManufacturerAgent;
import info801.tp.gui.adapters.SpecificationsWithStateModel;
import info801.tp.models.Specification;
import info801.tp.models.State;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManufacturerAgentGUI extends JFrame implements ActionListener {
    private ManufacturerAgent manufacturerAgent;
    private JPanel mainPanel;
    private JTable specificationsTable;
    private JTable counterProposalsTable;
    private SpecificationsWithStateModel specificationsModel;
    private SpecificationsWithStateModel counterProposalsModel;
    private JMenuItem menuItemAnalyse;
    private JMenuItem menuItemSendCounterProposalToLogistic;
    private JMenuItem menuItemFinishedProduction;

    public ManufacturerAgentGUI(ManufacturerAgent manufacturerAgent){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        specificationsModel = new SpecificationsWithStateModel();
        specificationsTable.setModel(specificationsModel);
        specificationsTable.setRowHeight(100);

        setMenuSpecifications();
        specificationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = specificationsTable.rowAtPoint(me.getPoint());
                specificationsTable.clearSelection();
                specificationsTable.setRowSelectionInterval(row,row);

            }
        });

        //For counter proposals
        counterProposalsModel = new SpecificationsWithStateModel();
        counterProposalsTable.setModel(counterProposalsModel);
        counterProposalsTable.setRowHeight(100);

        setMenuCounterProposals();
        counterProposalsTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                int row = counterProposalsTable.rowAtPoint(me.getPoint());
                counterProposalsTable.clearSelection();
                counterProposalsTable.setRowSelectionInterval(row,row);
                State state = counterProposalsModel.data.get(row).getState();
                if(state.equals(State.EN_PRODUCTION)){
                    setMenuCounterProposalsProductionState();
                }else if(state.equals(State.EN_ATTENTE))
                    setMenuCounterProposals();
                else
                    counterProposalsTable.setComponentPopupMenu(null);

            }
        });
    }

    private void setMenuCounterProposalsProductionState(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemFinishedProduction = new JMenuItem("Le produit est réalisé !");
        menuItemFinishedProduction.addActionListener(this);
        popupMenu.add(menuItemFinishedProduction);

        // sets the popup menu for the table
        counterProposalsTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenuSpecifications(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemAnalyse = new JMenuItem("Analyser");
        menuItemAnalyse.addActionListener(this);
        popupMenu.add(menuItemAnalyse);

        // sets the popup menu for the table
        specificationsTable.setComponentPopupMenu(popupMenu);
    }

    private void setMenuCounterProposals(){
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemSendCounterProposalToLogistic = new JMenuItem("Envoyer contre-proposition au maitre d'oeuvre");
        menuItemSendCounterProposalToLogistic.addActionListener(this);
        popupMenu.add(menuItemSendCounterProposalToLogistic);

        // sets the popup menu for the table
        counterProposalsTable.setComponentPopupMenu(popupMenu);
    }

    public void removeProposals(String projectId) {
        specificationsModel.removeSpecificationsFromProjectId(projectId);
    }

    public void removeCounterProposals(String projectId) {
        counterProposalsModel.removeSpecificationsFromProjectId(projectId);
    }

    public void updateCounterProposalState(Specification proposal, State accepte) {
        counterProposalsModel.changeState(proposal,accepte);
    }

    public void updateCounterProposalStateFromProjectId(String projectId, State accepte) {
        counterProposalsModel.changeStateFromProjectId(projectId,accepte);
    }

    public void removeOtherCounterProposals(Specification proposal) {
        counterProposalsModel.removeOtherCounterProposals(proposal);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemAnalyse) {
            manufacturerAgent.askToDesignAndWorkshop(specificationsModel.data.get(specificationsTable.getSelectedRow()));
            JOptionPane.showMessageDialog(null, "Envoyé pour l'analyse !", "", JOptionPane.INFORMATION_MESSAGE);
        }else if(menu == menuItemSendCounterProposalToLogistic){
            Specification counterProposal = counterProposalsModel.data.get(counterProposalsTable.getSelectedRow());
            manufacturerAgent.transmitCounterProposalToLogistic(counterProposal);
            JOptionPane.showMessageDialog(null, "Contre-proposition transmise au maitre d'oeuvre " + counterProposal.getLogisticName() + "!", "", JOptionPane.INFORMATION_MESSAGE);
        }else if(menu == menuItemFinishedProduction){
            Specification counterProposal = counterProposalsModel.data.get(counterProposalsTable.getSelectedRow());
            manufacturerAgent.notifyFinishedProduction(counterProposal);
            removeOtherCounterProposals(counterProposal);
            updateCounterProposalState(counterProposal,State.PRODUCTION_FINI);

        }
    }
}
