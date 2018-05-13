package info801.tp.gui;

import info801.tp.LogisticAgent;
import info801.tp.models.MaterialNeed;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateRFPMaterials extends JFrame {
    private JPanel mainPanel;
    private JTextArea materialNeedsTxt;
    private JButton createRFPMaterialsBtn;
    private LogisticAgent logisticAgent;

    public CreateRFPMaterials(LogisticAgentGUI logisticAgentGUI, String projectId){
        this.logisticAgent = logisticAgentGUI.logisticAgent;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSize(200,250);
        setContentPane(mainPanel);
        setTitle("Create a RFP materials "+logisticAgent.getId());
        setVisible(true);

        createRFPMaterialsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String needMaterial = materialNeedsTxt.getText().trim();
                if(!needMaterial.isEmpty()){
                    try{
                        MaterialNeed need = MaterialNeed.parse("\n\n\n\n\n" + needMaterial);
                        if(need != null)
                            logisticAgent.makeARFPMaterialToAllSuppliers(need, projectId);
                        else
                            throw new IllegalArgumentException();

                        logisticAgentGUI.addMaterialNeedRFP(need);
                        dispose();
                    }catch (Exception exception){
                        JOptionPane.showMessageDialog(null, "Veuillez respecter le format pour chaque ligne : quantité; matériel", "Erreur de saisie !", JOptionPane.ERROR_MESSAGE);
                    }
                }else {
                    JOptionPane.showMessageDialog(null, "Veuillez saisir quelque chose !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

}
