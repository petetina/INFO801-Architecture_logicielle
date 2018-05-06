package info801.tp.gui;

import info801.tp.LogisticAgent;
import info801.tp.model.Specification;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class CreateRFP extends JFrame{
    private JTextArea txtRequirements;
    private JPanel mainPanel;
    private JTextField txtCost;
    private JTextField txtQuantity;
    private JTextField txtProductionTime;
    private JButton btnSendRFP;

    public CreateRFP(JFrame parent, LogisticAgent logisticAgent, String customerName){
        setSize(400,250);
        setContentPane(mainPanel);
        setLocationRelativeTo(parent);
        setTitle("Create a RFP - Logistic "+logisticAgent.getId());
        setVisible(true);

        addCheckFieldsListener();

        btnSendRFP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(formIsValid())
                {
                    Double cost = Double.parseDouble(txtCost.getText());
                    int productionTimeInDays = Integer.parseInt(txtProductionTime.getText());
                    int quantity = Integer.parseInt(txtQuantity.getText());
                    Specification specification = new Specification(customerName,"Logistic"+logisticAgent.getId(),getRequirements(),cost,productionTimeInDays,quantity);

                    logisticAgent.makeARequestForProposal(specification);
                    JOptionPane.showMessageDialog(null, "Le cahier des charges a été transmis aux fabricants", "", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }else
                    JOptionPane.showMessageDialog(null, "Merci de vérifier les champs !", "", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private List<String> getRequirements(){
        List<String> results = new ArrayList<>();
        String tab[] = txtRequirements.getText().split(";");
        for(String requirement : tab)
            results.add(requirement);
        return results;
    }

    private boolean formIsValid(){
        return !txtCost.getText().isEmpty()
                && !txtProductionTime.getText().isEmpty()
                && !txtQuantity.getText().isEmpty()
                && txtRequirements.getText().split(";").length >= 1;
    }

    private void addCheckFieldsListener(){
        addNumericCheck(txtCost,false);
        addNumericCheck(txtProductionTime, true);
        addNumericCheck(txtQuantity, true);
    }

    private void addNumericCheck(JTextField jTextField, boolean integerOnly){
        jTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                boolean condition = !Character.isDigit(c);
                if(!integerOnly){
                    condition = condition && c != '.';
                }
                if(condition) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
}
