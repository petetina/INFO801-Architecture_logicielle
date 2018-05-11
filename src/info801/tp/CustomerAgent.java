package info801.tp;

import info801.tp.gui.CustomerAgentGUI;
import info801.tp.models.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerAgent extends Thread{
    private CustomerAgentGUI frame;
    private String name;
    private int id;

    public CustomerAgent(int id){
        this.id = id;
        this.name = "Customer"+id;
        frame = new CustomerAgentGUI(this);

        OpenJMS.getInstance().createQueue("transmitCounterRFPTo"+name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        while(true) {
            List<Specification> propositions = waitForPropositions();
            frame.addAll(propositions);
        }
    }

    public void askToLogistic(int idLogistic, String need, int quantity) throws Exception{
        OpenJMS.getInstance().postMessageInTopic(name + ";" + need + ";" + quantity, "needsCustomersLogistic"+idLogistic);
    }

    public List<Specification> waitForPropositions(){
        List<Specification> results = new ArrayList<>();
        String propositionsString = OpenJMS.getInstance().receiveMessageFromQueue("transmitCounterRFPTo" + name);
        Log.write(name,"debug propositionsString = " + propositionsString);
        String propositions[] = propositionsString.split(";;");
        for(String prop : propositions){
            Log.write(name,"proposal received : " + prop);
            results.add(Specification.parse(prop));
        }

        return results;
    }

    public void notifyRejectedProposal(Specification proposal) {
        OpenJMS.getInstance().postMessageInQueue(proposal.toString()+";;false","opinionProposals"+proposal.getLogisticName());
    }

    public void notifyAcceptedProposal(Specification proposal){
        OpenJMS.getInstance().postMessageInQueue(proposal.toString() + ";;true","opinionProposals"+proposal.getLogisticName());
    }
}
