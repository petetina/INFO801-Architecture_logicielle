package info801.tp;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
        OpenJMS.getInstance().createQueue("opinionProposals"+name);
    }

    @Override
    public void run() {
        super.run();

        Thread waitForSpecificationsThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    Specification specification = waitForSpecification();
                    frame.addSpecification(specification);
                }
            }
        };
        waitForSpecificationsThread.start();

        Thread opinionProposalsThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    listenOpinionProposal();
                }
            }
        };
        opinionProposalsThread.start();
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

    public void listenOpinionProposal(){
        String opinionAndProjectId = OpenJMS.getInstance().receiveMessageFromQueue("opinionProposals"+name);
        String array[] = opinionAndProjectId.split(";;");
        String projectId = array[0];
        boolean opinion = Boolean.parseBoolean(array[1]);
        if(opinion){

        }else {
            Log.write(name, "Deleted proposal " + projectId);
            frame.removeProposalFromProjectId(projectId);
        }
    }
}
