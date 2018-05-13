package info801.tp;

import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Vector;

// La classe est finale, car un singleton n'est pas censé avoir d'héritier.
public final class OpenJMS {
    private static OpenJMS instance = null;
    private static String URL = "tcp://localhost:3035/";
    private JmsAdminServerIfc admin;

    private OpenJMS(){
        // La présence d'un constructeur privé supprime le constructeur public par défaut.
        // De plus, seul le singleton peut s'instancier lui-même.
        super();
        String user = "admin";
        String password = "openjms";
        try {
            admin = AdminConnectionFactory.create(URL, user, password);

        }catch (JMSException e){
            Log.write("admin","OpenJMS Exception : " + e.getMessage());
        }catch (MalformedURLException e){
            Log.write("admin","OpenJMS Malformed url exception : "+e.getMessage());
        }


    }

    public void init(){
        try {
            System.out.println("initialisation ...");
            String destinationsToRemove[] = {"transportersProposals","needsCustomersLogistic", "transmitCounterRFPToCustomer", "requestsForProposal", "specificationManufacturer", "counterRFPManufacturer", "materialNeeds", "finishedProduction", "packageMaterialNeed"};
            Vector destinations = admin.getAllDestinations();
            Iterator iterator = destinations.iterator();
            while (iterator.hasNext()) {
                Destination destination = (Destination) iterator.next();
                if (destination instanceof Queue) {
                    Queue queue = (Queue) destination;
                    for (String s : destinationsToRemove) {
                        if (queue.getQueueName().contains(s)) {
                            admin.removeDestination(queue.getQueueName());
                            System.out.println("queue" + s + " deleted !");
                        }
                    }
                } else {
                    Topic topic = (Topic) destination;
                    for (String s : destinationsToRemove) {
                        if (topic.getTopicName().contains(s)) {
                            admin.removeDestination(topic.getTopicName());
                            System.out.println("topic" + s + " deleted !");
                        }
                    }
                }
            }

            createTopic("transportersProposals");
        }catch(JMSException e){
            System.out.println("Failed to initialized OpenJMS");
        }
    }

    public static final OpenJMS getInstance(){
        if(instance == null)
            instance = new OpenJMS();
        return instance;
    }

    public boolean destinationExists(String destinationName){
        boolean result = false;
        try{
            result = admin.destinationExists(destinationName);
        }catch (Exception e){

        }
        return result;
    }

    private boolean addDestination(String name, boolean isQueue){
        try {
            JmsAdminServerIfc admin = AdminConnectionFactory.create(URL);
            return admin.addDestination(name, isQueue);
        }catch (Exception e){
            return false;
        }
    }

    public boolean createTopic(String topicName){
        boolean result = addDestination(topicName,Boolean.FALSE);
        if(!result)
            System.err.println("Failed to create topic " + topicName);
        return result;
    }

    public boolean createQueue(String queueName){
        boolean result = addDestination(queueName,Boolean.TRUE);
        //if(!result)
        //    System.err.println("Failed to create queue " + queueName);
        return result;
    }

    public void postMessageInTopic(String message, String topicName){
        try {
            Context context = new InitialContext();

            TopicConnectionFactory qcf = (TopicConnectionFactory) context.lookup("ConnectionFactory");
            Topic q = (Topic) context.lookup(topicName);
            TopicConnection qc = qcf.createTopicConnection();

            TopicSession qs = qc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            qs.createSubscriber(q);
            MessageProducer mp = qs.createProducer(q);
            qc.start();

            Thread.sleep(1000);
            TextMessage textMessage = qs.createTextMessage();
            textMessage.setText(message);
            mp.send(textMessage);
            Log.write("admin", "Message added in topic " + topicName + " : " + textMessage.getText());
        }catch (Exception e){
            Log.write("admin","Failed to post message in topic");
        }
    }

    public void postMessageInQueue(String message, String queueName){
        try {
            Context context = new InitialContext();

            QueueConnectionFactory qcf = (QueueConnectionFactory) context.lookup("ConnectionFactory");
            Queue q = (Queue) context.lookup(queueName);
            QueueConnection qc = qcf.createQueueConnection();

            QueueSession qs = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer mp = qs.createProducer(q);
            qc.start();

            Thread.sleep(1000);
            TextMessage textMessage = qs.createTextMessage();
            textMessage.setText(message);
            mp.send(textMessage);
            Log.write("admin","Message added in queue " + queueName + " : " + textMessage.getText());

        }catch (Exception e){
            writeException(queueName, e.getMessage());
        }
    }

    public int countMessagesInTopic(String whoIAm, String topicName) {
        int result = 0;
        try {
            Context context = new InitialContext();

            TopicConnectionFactory qcf = (TopicConnectionFactory) context.lookup("ConnectionFactory");

            Topic t = (Topic) context.lookup(topicName);

            TopicConnection qc = qcf.createTopicConnection();

            TopicSession qs = qc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            //qs.createSubscriber(t, whoIAm, Boolean.FALSE);

            qs.createDurableSubscriber(t, whoIAm);

            MessageConsumer mc = qs.createConsumer(t);

            qc.start();

            TextMessage message = (TextMessage) mc.receive();
            Log.write("admin","Message recu: " + result);
        }catch (Exception e){

        }
        return result;
    }

    public String receiveMessageFromTopic(String whoIAm, String topicName){
        String result = "";
        try {
            Context context = new InitialContext();

            TopicConnectionFactory qcf = (TopicConnectionFactory) context.lookup("ConnectionFactory");

            Topic t = (Topic) context.lookup(topicName);
            TopicConnection qc = qcf.createTopicConnection();

            TopicSession qs = qc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            qs.createSubscriber(t,whoIAm,Boolean.FALSE);
            //qs.createDurableSubscriber(t, whoIAm);

            MessageConsumer mc = qs.createConsumer(t);

            qc.start();

            TextMessage message = (TextMessage) mc.receive();
            result = message.getText();
            Log.write(whoIAm,"Message received : " + result);

        }catch (Exception e){
            e.printStackTrace();
            writeException(topicName,e.getMessage());
        }finally {
            return result;
        }
    }

    public String receiveMessageFromQueue(String queueName){
        String result = "";
        try {
            Context context = new InitialContext();

            QueueConnectionFactory qcf = (QueueConnectionFactory) context.lookup("ConnectionFactory");

            Queue q = (Queue) context.lookup(queueName);
            QueueConnection qc = qcf.createQueueConnection();

            QueueSession qs = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer mc = qs.createConsumer(q);

            qc.start();

            TextMessage message = (TextMessage) mc.receive();
            result = message.getText();
            Log.write("admin","Message recu: " + result);
        }catch (Exception e){
            writeException(queueName,e.getMessage());
        }finally {
            return result;
        }

    }

    private void writeException(String topicOrQueue, String message){
        Log.write("admin","Exception on " + topicOrQueue + " : " + message);
    }


    public String receiveAsyncMessageFromTopic(String topicName, MessageListener messageListener){
        String result = "";
        try {
            Context context = new InitialContext();

            TopicConnectionFactory qcf = (TopicConnectionFactory) context.lookup("ConnectionFactory");

            Topic t = (Topic) context.lookup(topicName);
            TopicConnection qc = qcf.createTopicConnection();

            TopicSession qs = qc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer mc = qs.createConsumer(t);
            mc.setMessageListener(messageListener);
            qc.start();

            context.close();

        }catch (Exception e){
            e.printStackTrace();
            writeException(topicName,e.getMessage());
        }finally {
            return result;
        }
    }
}
