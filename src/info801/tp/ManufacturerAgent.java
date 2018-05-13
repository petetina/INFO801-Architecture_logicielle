package info801.tp;

import info801.tp.gui.ManufacturerAgentGUI;
import info801.tp.models.MaterialNeed;
import info801.tp.models.Specification;

import java.util.ArrayList;
import java.util.List;

import static info801.tp.models.State.ACCEPTE;
import static info801.tp.models.State.CONDITIONNE;
import static info801.tp.models.State.EN_PRODUCTION;

public class ManufacturerAgent extends Thread {
    private ManufacturerAgentGUI frame;
    private String name;
    private int id;
    private List<LogisticAgent> logisticAgents;
    private String address;

    public ManufacturerAgent(int id, String address){
        this.id = id;
        this.address = address;
        this.name = "Manufacturer"+id;
        frame = new ManufacturerAgentGUI(this);
        logisticAgents = new ArrayList<>();

        OpenJMS.getInstance().createTopic("specification"+name);
        OpenJMS.getInstance().createQueue("counterRFP"+name);
        OpenJMS.getInstance().createQueue("opinionProposals"+name);
        OpenJMS.getInstance().createQueue("packageMaterialNeed"+name);
        OpenJMS.getInstance().createQueue("getAddress"+name);
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

        Thread packageMaterialNeedThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    listenPackagingRequestsMaterialNeed();
                }
            }
        };

        Thread getAddressThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    getAddress();
                }
            }
        };

        getCounterProposalsThread.start();
        opinionProposalThread.start();
        packageMaterialNeedThread.start();
        getAddressThread.start();

    }

    private void getAddress() {
        OpenJMS.getInstance().receiveMessageFromQueue("getAddress"+name);
        OpenJMS.getInstance().postMessageInQueue(address,"getAddress"+name);
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

    public void notifyFinishedProduction(Specification counterProposal) {
        OpenJMS.getInstance().postMessageInQueue(counterProposal.toString(),"finishedProduction"+counterProposal.getLogisticName());
    }

    private void listenPackagingRequestsMaterialNeed() {
        String materialNeedString = OpenJMS.getInstance().receiveMessageFromQueue("packageMaterialNeed"+name);
        MaterialNeed materialNeed = MaterialNeed.parse(materialNeedString);
        frame.updateCounterProposalStateFromProjectId(materialNeed.getCustomerProjectId(),CONDITIONNE);
    }

    private void listenOpinionProposal() {
        String opinionAndProposal = OpenJMS.getInstance().receiveMessageFromQueue("opinionProposals"+name);
        String array[] = opinionAndProposal.split(";;");
        Specification proposal = Specification.parse(array[0]);
        boolean opinion = Boolean.parseBoolean(array[1]);
        frame.removeProposals(proposal.getId());
        if(opinion){
            frame.updateCounterProposalState(proposal,EN_PRODUCTION);
            frame.removeOtherCounterProposals(proposal);
        }else {
            frame.removeCounterProposals(proposal.getId());
        }
        OpenJMS.getInstance().postMessageInQueue(proposal.getId()+";;false", "opinionProposalsDesignAndWorkShop" + getId());
    }
}
