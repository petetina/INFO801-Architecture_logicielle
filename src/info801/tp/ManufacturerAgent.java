package info801.tp;

import info801.tp.gui.ManufacturerAgentGUI;
import info801.tp.model.Specification;

import java.util.ArrayList;
import java.util.List;

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

        while(true){

            //We search for request for proposal
            Specification spec = searchForRFP();
            frame.addSpecification(spec);
            /*
            askToDesignAndWorkshop(spec);
            boolean ok = false;
            Specification counterFRP = waitForCounterRFP();
            transmitCounterFRPToLogistic(counterFRP);*/

        }
    }

    public Specification searchForRFP() {
        Log.write(name, "waiting for requests for proposal !");
        String result = OpenJMS.getInstance().receiveMessageFromTopic(name,"requestsForProposal");
        Log.write(name,"has received RFP : " + result);
        return Specification.parse(result);
    }

    public void askToDesignAndWorkshop(Specification spec){
        if(RandomGenerator.nextBool()){
            List<String> requirements = new ArrayList<>();
            requirements.add("requirement1");
            requirements.add("requirement2");
            requirements.add("requirement3");
            spec.setRequirements(requirements);
            spec = new Specification(spec.getCustomerName(),spec.getLogisticName(),requirements,RandomGenerator.nextInt(0,100),RandomGenerator.nextInt(30,60),RandomGenerator.nextInt(1,10));
            Log.write(name, "ask to design and workshop the specification : " + spec);
            try {
                OpenJMS.getInstance().postMessageInTopic(spec.toString(), "specification" + getName());
            }catch (Exception e){
                Log.write(name,"Error while asking to design and workshop !");
            }
        }else
            Log.write(name,"doesn't want to answer to "+spec.getLogisticName() + " !");
    }

    public Specification waitForCounterRFP(){
        Log.write(name," waiting for a counter RFP from manufacturer " + getId() + " !");
        String counterRFP = OpenJMS.getInstance().receiveMessageFromQueue("counterRFP"+name);
        return Specification.parse(counterRFP);
    }

    public void transmitCounterFRPToLogistic(Specification counterFRP) {
        Log.write(name," transmit a counter RFP to logistic " + counterFRP.getLogisticName() + " !");
        String queueName = "transmitCounterRFPTo"+counterFRP.getLogisticName();

        //We create a queue lazily
        if(!OpenJMS.getInstance().destinationExists(queueName))
            OpenJMS.getInstance().createQueue(queueName);

        OpenJMS.getInstance().postMessageInQueue(getId() + ";;" + counterFRP.toString(),queueName);

    }
}
