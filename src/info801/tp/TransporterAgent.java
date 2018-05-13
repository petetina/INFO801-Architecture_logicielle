package info801.tp;

import info801.tp.gui.TransporterAgentGUI;
import info801.tp.models.TransporterNeed;

public class TransporterAgent extends Thread{
    private TransporterAgentGUI frame;
    private String name;
    private int id;

    public TransporterAgent(int id){
        this.id = id;
        this.name = "Transporter"+id;
        frame = new TransporterAgentGUI(this);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        while(true) {
            listenProposal();
        }
    }

    public void listenProposal(){
        String proposal = OpenJMS.getInstance().receiveMessageFromTopic(name,"transportersProposals");
        frame.addProposal(TransporterNeed.parse(proposal));
    }
}
