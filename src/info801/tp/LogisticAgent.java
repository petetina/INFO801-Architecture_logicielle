package info801.tp;

import info801.tp.gui.LogisticAgentGUI;
import info801.tp.models.Specification;

import java.util.HashMap;
import java.util.Map;

public class LogisticAgent extends Thread {
    private int id;
    private String name;
    private LogisticAgentGUI frame;

    private Map<String, Specification> rfps;

    public LogisticAgent(int id){
        this.id = id;
        name = "Logistic" + id;
        frame = new LogisticAgentGUI(this);
        OpenJMS.getInstance().createTopic("needsCustomers"+name);
        OpenJMS.getInstance().createQueue("transmitCounterRFPTo"+name);
        OpenJMS.getInstance().createQueue("rejectedProposals"+name);
        rfps = new HashMap<>();
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
        /*
        while(true){
            String need = listenCustomersNeeds();
            frame.addNeed(need);


            List<String> requirements = new ArrayList<>();
            requirements.add("hello");
            requirements.add("it's not a good requirement");
            Specification specification = new Specification(name, requirements,RandomGenerator.nextInt(0,100),RandomGenerator.nextInt(30,60),RandomGenerator.nextInt(1,10));
            makeARequestForProposal(specification.toString());

            //Now we are waiting for manufacturers responses during 5 seconds and send to customer
            //waitForResponsesAndSendToCustomer(5);
        }*/
    }

    @Override
    public long getId() {
        return id;
    }

    public String listenCustomersNeeds(){
        Log.write("Logistic " + id, "listening customers needs !");
        String result = OpenJMS.getInstance().receiveMessageFromTopic(name, "needsCustomers"+name);
        return result;
    }

    public void makeARequestForProposal(Specification rfp){
        Log.write(name," has posted a rfp : " + rfp);
        try {
            OpenJMS.getInstance().postMessageInTopic(rfp.toString(), "requestsForProposal");
        }catch (Exception e){
            Log.write(name,"Error while make a RFP !");
        }
    }

    public void wait(int waitForNSec){
        try {
            Thread.sleep(waitForNSec*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Specification listenCounterProposals(){
        String counterProposalString = OpenJMS.getInstance().receiveMessageFromQueue("transmitCounterRFPTo"+name);
        return Specification.parse(counterProposalString);
    }

}
