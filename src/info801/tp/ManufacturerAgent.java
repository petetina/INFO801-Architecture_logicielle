package info801.tp;

import info801.tp.gui.ManufacturerAgentGUI;
import info801.tp.models.Specification;

public class ManufacturerAgent extends Thread {
    private ManufacturerAgentGUI frame;
    private String name;
    private int id;

    public ManufacturerAgent(int id){
        this.id = id;
        this.name = "Manufacturer"+id;
        frame = new ManufacturerAgentGUI(this);

        OpenJMS.getInstance().createTopic("specification"+name);
        OpenJMS.getInstance().createQueue("counterRFP"+name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        super.run();

        Thread getRFPThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){

                    //We search for request for proposal
                    Specification spec = searchForRFP();
                    frame.addSpecification(spec);

                }
            }
        };

        Thread getCounterProposalsThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    Specification counterProposal = waitForCounterRFP();
                    frame.addCounterSpecification(counterProposal);
                }
            }
        };

        getRFPThread.start();
        getCounterProposalsThread.start();

    }

    public Specification searchForRFP() {
        Log.write(name, "waiting for requests for proposal !");
        String result = OpenJMS.getInstance().receiveMessageFromTopic(name,"requestsForProposal");
        Log.write(name,"has received RFP : " + result);
        return Specification.parse(result);
    }

    public void askToDesignAndWorkshop(Specification spec){

        Log.write(name, "ask to design and workshop the specification : " + spec);
        try {
            OpenJMS.getInstance().postMessageInTopic(spec.toString(), "specification" + name);
        }catch (Exception e){
            Log.write(name,"Error while asking to design and workshop !");
        }
    }

    public Specification waitForCounterRFP(){
        Log.write(name," waiting for a counter RFP from manufacturer " + getId() + " !");
        String counterRFP = OpenJMS.getInstance().receiveMessageFromQueue("counterRFP"+name);
        return Specification.parse(counterRFP);
    }

    public void transmitCounterProposalToLogistic(Specification counterRFP) {
        counterRFP.setManufacturer(name);
        Log.write(name," transmit a counter RFP to logistic " + counterRFP.getLogisticName() + " !");
        OpenJMS.getInstance().postMessageInQueue(counterRFP.toString(),"transmitCounterRFPTo"+counterRFP.getLogisticName());
    }
}
