package info801.tp;

import info801.tp.gui.SupplierAgentGUI;
import info801.tp.models.MaterialNeed;
import info801.tp.models.StateMaterialNeed;

public class SupplierAgent extends Thread{
    private SupplierAgentGUI frame;
    private String name;
    private int id;

    public SupplierAgent(int id){
        this.id = id;
        this.name = "Supplier"+id;
        frame = new SupplierAgentGUI(this);

        OpenJMS.getInstance().createQueue("materialNeeds"+name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        while(true){
            listenMaterialNeeds();
        }
    }

    private void listenMaterialNeeds() {
        String materialNeedString = OpenJMS.getInstance().receiveMessageFromQueue("materialNeeds"+name);
        MaterialNeed materialNeed = MaterialNeed.parse(materialNeedString);
        materialNeed.setState(StateMaterialNeed.EN_ATTENTE);
        frame.addMaterialNeed(materialNeed);
    }
}
