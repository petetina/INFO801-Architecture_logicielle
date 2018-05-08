package info801.tp.gui;

import info801.tp.CustomerAgent;
import info801.tp.gui.adapters.SpecificationsModel;
import info801.tp.models.Specification;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerAgentGUI extends JFrame implements ActionListener{
    private CustomerAgent customerAgent;
    private JPanel mainPanel;
    private JTextArea needTxt;
    private JTextField logisticNameTxt;
    private JButton sendNeedBtn;
    private JSpinner txtQuantity;
    private JTable proposalsTable;
    private SpecificationsModel proposalsModel;
    private JMenuItem menuItemAcceptProposal;
    private JMenuItem menuItemRejectProposal;

    public CustomerAgentGUI(CustomerAgent customerAgent){
        this.customerAgent = customerAgent;
        txtQuantity.setModel(new SpinnerNumberModel(1,1,9999,1));
        setSize(500,250);
        setContentPane(mainPanel);
        //setLocationRelativeTo(null);
        setLocation(-8,0);
        setTitle("Customer "+customerAgent.getId());
        setVisible(true);

        /*addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                System.out.println(getTitle() + " size : w= " + getWidth() + " h = " + getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                System.out.println(getTitle() + " position : x= " + getX() + " y = " + getY());
            }
        });
        */

        sendNeedBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!needTxt.getText().isEmpty() && !logisticNameTxt.getText().isEmpty()) {
                    try {
                        customerAgent.askToLogistic(Integer.valueOf(logisticNameTxt.getText()), needTxt.getText(), (int)txtQuantity.getValue());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Le maitre d'oeuvre n'existe pas !", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }else
                    JOptionPane.showMessageDialog(null, "On n'envoie pas de demande en chocolat ;) !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        populate();
    }

    public void populate(){
        proposalsModel = new SpecificationsModel();
        proposalsTable.setModel(proposalsModel);
        addMenu();
        proposalsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                int row = proposalsTable.rowAtPoint(me.getPoint());
                proposalsTable.clearSelection();
                proposalsTable.setRowSelectionInterval(row,row);

            }
        });

    }

    private void addMenu() {
        // constructs the popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        menuItemAcceptProposal = new JMenuItem("Accept");
        menuItemAcceptProposal.addActionListener(this);
        popupMenu.add(menuItemAcceptProposal);

        menuItemRejectProposal = new JMenuItem("Reject");
        menuItemRejectProposal.addActionListener(this);
        popupMenu.add(menuItemRejectProposal);

        // sets the popup menu for the table
        proposalsTable.setComponentPopupMenu(popupMenu);
    }

    public void addAll(List<Specification> specificationList){
        for(Specification specification : specificationList)
            proposalsModel.add(specification);
    }

    private void rejectProposal(int rowIndex){
        Specification proposal = proposalsModel.data.get(rowIndex);
        String id = proposal.getId();

        //Remove only this proposal
        proposalsModel.remove(rowIndex);
        customerAgent.notifyRejectedProposal(proposal);
        //If there is no more proposal,
        //tell logistic to send again a rfp
        List<Specification> proposalsForTheSameProject = proposalsModel.data.stream().filter(specification -> specification.getId().equals(id) ).collect(Collectors.toList());
        if(proposalsForTheSameProject.isEmpty()){

        }
    }

    private void acceptProposal(int rowIndex){
        String id = proposalsModel.data.get(rowIndex).getId();
        //Delete all other proposals linked to the project id
        int i = 0;

            while(i<proposalsModel.size()){

                if(proposalsModel.data.get(i).getId().equals(id))
                    proposalsTable.remove(i);
                else
                    i++;
        }
        //Remove this proposal of the list
        //Tell logistic that proposal i is accepted
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        int rowIndex = proposalsTable.getSelectedRow();
        if (menu == menuItemAcceptProposal) {
            acceptProposal(rowIndex);
        } else if (menu == menuItemRejectProposal) {
           rejectProposal(rowIndex);
        }
    }
}
