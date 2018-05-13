package info801.tp;

import info801.tp.gui.DesignAndWorkshopGUI;
import info801.tp.models.Specification;

public class WarehouseAgent extends Thread{
    private int id;
    private String name;
    private DesignAndWorkshopGUI frame;

    public WarehouseAgent(int id){
        this.id = id;
        this.name = "Warehouse"+id;

        OpenJMS.getInstance().createQueue(name);
        //frame = new DesignAndWorkshopGUI(this);
    }

    @Override
    public void run() {
        super.run();
        while(true){
            listenDeliveries();
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public void listenDeliveries(){
        String delivery = OpenJMS.getInstance().receiveMessageFromQueue(name);
    }
}
