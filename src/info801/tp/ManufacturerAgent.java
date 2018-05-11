package info801.tp;

import info801.tp.gui.ManufacturerAgentGUI;
import info801.tp.models.Specification;

import java.util.ArrayList;
import java.util.List;

public class ManufacturerAgent extends Thread {
    private ManufacturerAgentGUI frame;
    private String name;
    private int id;
    private List<LogisticAgent> logisticAgents;

    public ManufacturerAgent(int id){
        this.id = id;
        this.name = "Manufacturer"+id;
        frame = new ManufacturerAgentGUI(this);
        logisticAgents = new ArrayList<>();

        OpenJMS.getInstance().createTopic("specification"+name);
        OpenJMS.getInstance().createQueue("counterRFP"+name);
        OpenJMS.getInstance().createQueue("opinionProposals"+name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        super.run();

        //For each logistic, we are waiting for RFP
        for(LogisticAgent logisticAgent : logisticAgents) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (true) {

                        //We search for request for proposal
                        Specification spec = searchForRFP(logisticAgent.getId());
                        frame.addSpecification(spec);

                    }
                }
            }.start();
        }

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

        Thread opinionProposalThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true) {
                    listenOpinionProposal();
                }
            }
        };

        getCounterProposalsThread.start();
        opinionProposalThread.start();

    }

    public void addLogistic(LogisticAgent logisticAgent){
        logisticAgents.add(logisticAgent);
    }

    public Specification searchForRFP(long idLogistic) {
        Log.write(name, "waiting for requests for proposal !");
        String result = OpenJMS.getInstance().receiveMessageFromTopic(name,"requestsForProposalLogistic"+idLogistic);
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

    private void listenOpinionProposal() {
        String opinionAndProposal = OpenJMS.getInstance().receiveMessageFromQueue("opinionProposals"+name);
        String array[] = opinionAndProposal.split(";;");
        String projectId = array[0];
        boolean opinion = Boolean.parseBoolean(array[1]);
        if(opinion){

        }else {
            frame.removeProposals(projectId);
            frame.removeCounterProposals(projectId);
            OpenJMS.getInstance().postMessageInQueue(projectId+";;false", "opinionProposalsDesignAndWorkShop" + getId());
        }
    }
}
