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
        /*List<Specification> propositions = waitForPropositions();
        int num = chooseProposition(propositions);
        if(num == 0){
            //Not agree, so
        }*/
    }

    public void askToLogistic(int idLogistic, String need, int quantity) throws Exception{
        OpenJMS.getInstance().postMessageInTopic(name + ";" + need + ";" + quantity, "needsCustomersLogistic"+idLogistic);
    }

    public List<Specification> waitForPropositions(){
        List<Specification> results = new ArrayList<>();
        String propositionsString = OpenJMS.getInstance().receiveMessageFromTopic(name,"transmitCounterRFPTo" + name);
        String propositions[] = propositionsString.split("|");
        for(String propositionString : propositions){
            results.add(Specification.parse(propositionsString));
        }

        return results;
    }

    public int chooseProposition(List<Specification> specifications){
        int chosenProposition = RandomGenerator.nextInt(0,specifications.size()-1);
        Log.write("Customer "+id, " has chosen proposition "+ chosenProposition);
        return chosenProposition;
    }
}
