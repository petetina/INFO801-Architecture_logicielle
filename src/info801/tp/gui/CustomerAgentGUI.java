package info801.tp.gui;

import info801.tp.CustomerAgent;
import info801.tp.Log;
import info801.tp.OpenJMS;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CustomerAgentGUI extends JFrame{
    private JPanel mainPanel;
    private JTextArea needTxt;
    private JTextField logisticNameTxt;
    private JButton sendNeedBtn;

    public CustomerAgentGUI(final CustomerAgent customerAgent){

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
                        customerAgent.askToLogistic(Integer.valueOf(logisticNameTxt.getText()), needTxt.getText());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Le maitre d'oeuvre n'existe pas !", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }else
                    JOptionPane.showMessageDialog(null, "On n'envoie pas de demande en chocolat ;) !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


}
