package info801.tp;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
        OpenJMS.getInstance().createQueue("materialNeedsResponses"+name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {

        Thread materialNeedsThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    listenMaterialNeeds();
                }
            }
        };
        materialNeedsThread.start();

        Thread acceptedOrRejetedRFPThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    listenAcceptedOrRejectedRFP();
                }
            }
        };
        acceptedOrRejetedRFPThread.start();
    }

    private void listenMaterialNeeds() {
        String materialNeedString = OpenJMS.getInstance().receiveMessageFromQueue("materialNeeds"+name);
        MaterialNeed materialNeed = MaterialNeed.parse(materialNeedString);
        materialNeed.setState(StateMaterialNeed.EN_ATTENTE);
        frame.addMaterialNeed(materialNeed);
    }

    public void answerMaterialNeedRFP(MaterialNeed materialNeed){
        materialNeed.setSupplierName(name);
        OpenJMS.getInstance().postMessageInQueue(materialNeed.toString(),"materialNeeds"+materialNeed.getLogisticName());
    }

    public void listenAcceptedOrRejectedRFP(){
        String responseStringAndAccepted = OpenJMS.getInstance().receiveMessageFromQueue("materialNeedsResponses"+name);
        String array[] = responseStringAndAccepted.split(";;");
        String responseString = array[0];
        boolean accepted = Boolean.valueOf(array[1]);
        MaterialNeed response = MaterialNeed.parse(responseString);
        frame.removeMaterialNeedsDoing(response.getId());
        if(accepted){
            response.setState(StateMaterialNeed.ACCEPTE);
            frame.addMaterialNeedDone(response);
        }else{
            response.setState(StateMaterialNeed.REJETE);
            frame.addMaterialNeedDone(response);
        }
    }

}
