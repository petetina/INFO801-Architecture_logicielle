package info801.tp;

import info801.tp.gui.DesignAndWorkshopGUI;
import info801.tp.models.Specification;

public class DesignAndWorkShopAgent extends Thread{
    private int id;
    private String name;
    private DesignAndWorkshopGUI frame;

    public DesignAndWorkShopAgent(int id){
        this.id = id;
        this.name = "DesignAndWorkShop"+id;
        frame = new DesignAndWorkshopGUI(this);

    }

    @Override
    public void run() {
        super.run();

        while(true){
            Specification specification = waitForSpecification();
            frame.addSpecification(specification);
        }
    }

    @Override
    public long getId() {
        return id;
    }

    public Specification waitForSpecification(){
        Log.write(name," waiting for specification from manufacturer " + getId() + " !");
        String specInString = OpenJMS.getInstance().receiveMessageFromTopic(name,"specificationManufacturer"+getId());
        Specification specification = Specification.parse(specInString);
        Log.write(name," receive the specification : " + specInString);
        return specification;
    }

    public void makeACounterProposal(Specification counterProposal){
        Log.write(name," make a counter proposal for modified spec " + counterProposal + " to manufacturer"+getId());
        OpenJMS.getInstance().postMessageInQueue(counterProposal.toString(), "counterRFPManufacturer" + getId());
    }
}
