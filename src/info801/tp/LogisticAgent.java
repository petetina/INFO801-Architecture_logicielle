package info801.tp;

import info801.tp.gui.LogisticAgentGUI;
import info801.tp.models.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogisticAgent extends Thread {
    private int id;
    private String name;
    private LogisticAgentGUI frame;
    private List<ManufacturerAgent> manufacturerAgents;

    public LogisticAgent(int id){
        this.id = id;
        name = "Logistic" + id;
        frame = new LogisticAgentGUI(this);
        this.manufacturerAgents = new ArrayList<>();
        OpenJMS.getInstance().createTopic("needsCustomers"+name);
        OpenJMS.getInstance().createTopic("requestsForProposal"+name);
        OpenJMS.getInstance().createQueue("transmitCounterRFPTo"+name);
        OpenJMS.getInstance().createQueue("opinionProposals"+name);
    }

    @Override
    public void run() {
        super.run();

        Thread customersNeedsThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    String need = listenCustomersNeeds();
                    frame.addNeed(need);
                }
            }
        };
        customersNeedsThread.start();

        Thread counterProposalsThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    Specification counterProposal = listenCounterProposals();
                    frame.addCounterProposal(counterProposal);
                }
            }
        };

        counterProposalsThread.start();

        Thread opinionsProposalsThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    listenCustomerOpinionProposal();
                }
            }
        };
        opinionsProposalsThread.start();
    }

    @Override
    public long getId() {
        return id;
    }

    public void addManufacturer(ManufacturerAgent manufacturerAgent){
        manufacturerAgents.add(manufacturerAgent);
        manufacturerAgent.addLogistic(this);
    }

    public String listenCustomersNeeds(){
        Log.write("Logistic " + id, "listening customers needs !");
        String result = OpenJMS.getInstance().receiveMessageFromTopic(name, "needsCustomers"+name);
        return result;
    }

    public void makeARequestForProposal(Specification rfp){
        Log.write(name," has posted a rfp : " + rfp);
        try {
                OpenJMS.getInstance().postMessageInTopic(rfp.toString(), "requestsForProposal"+rfp.getLogisticName());
        }catch (Exception e){
            Log.write(name,"Error while make a RFP !");
        }
    }

    public Specification listenCounterProposals(){
        String counterProposalString = OpenJMS.getInstance().receiveMessageFromQueue("transmitCounterRFPTo"+name);
        return Specification.parse(counterProposalString);
    }

    public void listenCustomerOpinionProposal(){
        String opinionAndProposalString = OpenJMS.getInstance().receiveMessageFromQueue("opinionProposals"+name);
        String array[] = opinionAndProposalString.split(";;");
        String proposalString = array[0];
        boolean opinion = Boolean.parseBoolean(array[1]);
        Specification proposal = Specification.parse(proposalString);
        if(opinion){
            frame.updateNeedState(proposal.getId(), info801.tp.models.State.ACCEPTE);
            frame.removeOtherCounterProposals(proposal);
            //Tell to other manufacturer that theirs proposals are rejected
            for(ManufacturerAgent manufacturerAgent : manufacturerAgents)
            {
                String manufacturerName = "Manufacturer"+manufacturerAgent.getId();
                if(!proposal.getManufacturer().equals(manufacturerName))
                    OpenJMS.getInstance().postMessageInQueue(proposal.getId() + ";;false", "opinionProposals"+manufacturerName);
            }
        }else {
            frame.removeCounterProposal(proposal);
            if (frame.hasNoMoreCounterProposals(proposal.getId())) {
                frame.updateNeedState(proposal.getId(), info801.tp.models.State.REJETE);
            }
        }
        OpenJMS.getInstance().postMessageInQueue(proposal.getId() + ";;" + array[1], "opinionProposals" + proposal.getManufacturer());
    }

    public void transmitCounterRFPToCustomer(String allProposals, String customerId){
        OpenJMS.getInstance().postMessageInQueue(allProposals,"transmitCounterRFPTo"+customerId);
    }

}
