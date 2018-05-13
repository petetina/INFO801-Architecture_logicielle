package info801.tp;

import info801.tp.gui.TransporterAgentGUI;
import info801.tp.models.StateTransporterNeed;
import info801.tp.models.TransporterNeed;

public class TransporterAgent extends Thread{
    private TransporterAgentGUI frame;
    public String name;
    private int id;

    public TransporterAgent(int id){
        this.id = id;
        this.name = "Transporter"+id;
        frame = new TransporterAgentGUI(this);

        OpenJMS.getInstance().createQueue("acceptedOrRejectedProposals"+name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {

        Thread listenProposalsThread = new Thread(){
            @Override
            public void run() {
                while(true)
                    listenProposal();
            }
        };
        listenProposalsThread.start();

        Thread listenAcceptedOrRejectedProposalsThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    listenAcceptedOrRejectedProposals();
                }
            }
        };
        listenAcceptedOrRejectedProposalsThread.start();
        while(true) {
            listenProposal();
        }
    }

    private void listenAcceptedOrRejectedProposals() {
        String proposalAndOpinion = OpenJMS.getInstance().receiveMessageFromQueue("acceptedOrRejectedProposals"+name);
        String array[] = proposalAndOpinion.split("!");
        TransporterNeed transporterNeed = TransporterNeed.parse(array[0]);
        boolean opinion = Boolean.valueOf(array[1]);
        frame.removeAllProposals(transporterNeed.getId());
        if(opinion){
            System.out.println(name + " opinion true "+ transporterNeed.toString());
            transporterNeed.setState(StateTransporterNeed.CHOISI);
        }else{
            System.out.println(name + " opinion false "+ transporterNeed.toString());
            transporterNeed.setState(StateTransporterNeed.REJETE);
        }
        frame.addCounterProposal(transporterNeed);
    }

    public void listenProposal(){
        String proposal = OpenJMS.getInstance().receiveMessageFromTopic(name,"transportersProposals");
        frame.addProposal(TransporterNeed.parse(proposal));
    }

    public void acceptProposal(TransporterNeed transporterNeed) {
        OpenJMS.getInstance().postMessageInQueue(transporterNeed.toString() + "::true","counterProposalsTransporters"+transporterNeed.getProject().getLogisticName());
    }

    public void rejectProposal(TransporterNeed transporterNeed) {
        OpenJMS.getInstance().postMessageInQueue(transporterNeed.toString() + "::false","counterProposalsTransporters"+transporterNeed.getProject().getLogisticName());
    }
}
