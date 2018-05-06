package info801.tp;

import info801.tp.gui.LogisticAgentGUI;
import info801.tp.model.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        rfps = new HashMap<>();
    }

    @Override
    public void run() {
        super.run();
        while(true){
            String need = listenCustomersNeeds();
            frame.addNeed(need);

            /*
            List<String> requirements = new ArrayList<>();
            requirements.add("hello");
            requirements.add("it's not a good requirement");
            Specification specification = new Specification(name, requirements,RandomGenerator.nextInt(0,100),RandomGenerator.nextInt(30,60),RandomGenerator.nextInt(1,10));
            makeARequestForProposal(specification.toString());
            */
            //Now we are waiting for manufacturers responses during 5 seconds and send to customer
            //waitForResponsesAndSendToCustomer(5);
        }
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
/*
    public void waitForResponsesAndSendToCustomer(int deadLine) {
        List<Specification> result = new ArrayList<>();
        Log.write(name + " wait for responses !");
        OpenJMS.getInstance().receiveAsyncMessageFromTopic("transmitCounterRFPTo"+name, new ResponsesReceiver());
        wait(deadLine);

        String message = "";
        for(String manufacturerId : rfps.keySet())
        {
            message += rfps.get(manufacturerId).toString() + "|";
        }
        message = message.substring(0,message.length()-2);
        Log.write(name + " sending " + rfps.size() + " counterRFP to customer 1");
        OpenJMS.getInstance().postMessageInQueue(message,"transmitCounterRFPToCustomer1");
    }

    private class ResponsesReceiver implements MessageListener {

        public void onMessage(Message message) {
            if (message instanceof TextMessage) {

                TextMessage text = (TextMessage) message;
                try {

                    Log.write(name + "receive a counter rfp " + text.getText());
                    String tab[] = text.getText().split(";;");
                    String idManufacturer = tab[0];
                    Specification counterRFP = Specification.parse(tab[1]);

                    rfps.put(idManufacturer,counterRFP);
                } catch (JMSException exception) {
                    System.err.println("Failed to get message text: " + exception);
                }
            }
        }


    }
    */

}
