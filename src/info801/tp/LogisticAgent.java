package info801.tp;

import info801.tp.gui.LogisticAgentGUI;
import info801.tp.models.*;

import java.util.ArrayList;
import java.util.List;

public class LogisticAgent extends Thread {
    private int id;
    private String name;
    private LogisticAgentGUI frame;
    private List<ManufacturerAgent> manufacturerAgents;
    private List<SupplierAgent> supplierAgents;

    public LogisticAgent(int id){
        this.id = id;
        name = "Logistic" + id;
        frame = new LogisticAgentGUI(this);
        this.manufacturerAgents = new ArrayList<>();
        this.supplierAgents = new ArrayList<>();

        OpenJMS.getInstance().createTopic("needsCustomers"+name);
        OpenJMS.getInstance().createTopic("requestsForProposal"+name);
        OpenJMS.getInstance().createQueue("transmitCounterRFPTo"+name);
        OpenJMS.getInstance().createQueue("opinionProposals"+name);
        OpenJMS.getInstance().createQueue("materialNeeds"+name);
        OpenJMS.getInstance().createQueue("finishedProduction"+name);
        OpenJMS.getInstance().createQueue("packageMaterialNeed"+name);
        OpenJMS.getInstance().createQueue("counterProposalsTransporters"+name);
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

        Thread materialNeedsThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    listenMaterialNeeds();
                }
            }
        };
        materialNeedsThread.start();

        Thread finishedProductionThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    listenFinishedProduction();
                }
            }
        };
        finishedProductionThread.start();

        Thread packageMaterialNeedThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    listenPackagingRequestsMaterialNeed();
                }
            }
        };
        packageMaterialNeedThread.start();

        Thread listenCounterProposalsTransportersThread = new Thread(){
            @Override
            public void run() {
                while(true){
                    listenCounterProposalsTransporters();
                }
            }
        };
        listenCounterProposalsTransportersThread.start();

    }

    @Override
    public long getId() {
        return id;
    }

    public void addManufacturer(ManufacturerAgent manufacturerAgent){
        manufacturerAgents.add(manufacturerAgent);
        manufacturerAgent.addLogistic(this);
    }

    public void addSupplier(SupplierAgent supplierAgent){
        supplierAgents.add(supplierAgent);
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
            frame.updateSpecificationState(proposal.getId(), info801.tp.models.State.ACCEPTE);
            //Tell to other manufacturer that theirs proposals are rejected
            for(ManufacturerAgent manufacturerAgent : manufacturerAgents)
            {
                String manufacturerName = "Manufacturer"+manufacturerAgent.getId();
                if(!proposal.getManufacturer().equals(manufacturerName))
                    OpenJMS.getInstance().postMessageInQueue(proposal + ";;false", "opinionProposals"+manufacturerName);
            }
        }else {
            frame.removeCounterProposal(proposal);
            if (frame.hasNoMoreCounterProposals(proposal.getId())) {
                frame.updateNeedState(proposal.getId(), info801.tp.models.State.REJETE);
            }
        }
        OpenJMS.getInstance().postMessageInQueue(proposal + ";;" + array[1], "opinionProposals" + proposal.getManufacturer());
    }

    public void transmitCounterRFPToCustomer(String allProposals, String customerId){
        OpenJMS.getInstance().postMessageInQueue(allProposals,"transmitCounterRFPTo"+customerId);
    }

    public void makeARFPMaterialToAllSuppliers(MaterialNeed needMaterial, String projectId) {
        needMaterial.setId(RandomGenerator.generateId());
        needMaterial.setLogisticName(name);
        needMaterial.setCustomerProjectId(projectId);
        for(SupplierAgent supplierAgent : supplierAgents)
            OpenJMS.getInstance().postMessageInQueue(needMaterial.toString(),"materialNeedsSupplier"+supplierAgent.getId());
    }

    public void listenMaterialNeeds(){
        String materialNeedString = OpenJMS.getInstance().receiveMessageFromQueue("materialNeeds"+name);
        MaterialNeed materialNeed = MaterialNeed.parse(materialNeedString);
        frame.addMaterialNeed(materialNeed);
    }

    public void acceptMaterialNeedSupplier(MaterialNeed materialNeed){
        for(SupplierAgent supplierAgent : supplierAgents)
        {
            if(!materialNeed.getSupplierName().equals("Supplier"+supplierAgent.getId()))
                OpenJMS.getInstance().postMessageInQueue(materialNeed.toString() + ";;false","materialNeedsResponsesSupplier"+supplierAgent.getId());
            else
                OpenJMS.getInstance().postMessageInQueue(materialNeed.toString() + ";;true","materialNeedsResponsesSupplier"+supplierAgent.getId());
        }
    }

    private void listenFinishedProduction() {
        String proposalString = OpenJMS.getInstance().receiveMessageFromQueue("finishedProduction"+name);
        Specification proposal = Specification.parse(proposalString);
        frame.updateMaterialNeedByProjectId(proposal.getId(),StateMaterialNeed.A_CONDITIONNER);
        String supplierName = frame.findSupplierForProject(proposal.getId());
        OpenJMS.getInstance().postMessageInQueue(proposal.getId(),"finishedProduction"+supplierName);
    }

    private void listenPackagingRequestsMaterialNeed(){
        String materialNeedString = OpenJMS.getInstance().receiveMessageFromQueue("packageMaterialNeed"+name);
        MaterialNeed materialNeed = MaterialNeed.parse(materialNeedString);
        frame.updateMaterialNeedByProjectId(materialNeed.getCustomerProjectId(),StateMaterialNeed.CONDITIONNE);
        String manufacturerName = frame.findManufacturerForProject(materialNeed.getCustomerProjectId());
        OpenJMS.getInstance().postMessageInQueue(materialNeedString,"packageMaterialNeed"+manufacturerName);
    }

    public boolean makeAProposalTransporter(Specification counterProposal, String addressFrom, String warehouse, String date) {
        boolean result = OpenJMS.getInstance().destinationExists(warehouse);
        TransporterNeed transporterNeed = TransporterNeed.parse(counterProposal.toString() + ";;" + RandomGenerator.generateId() + ";;" + addressFrom + ";;" + warehouse + ";;" + date + ";;" + StateTransporterNeed.EN_ATTENTE.toString() + ";; ");
        if(result){
            OpenJMS.getInstance().postMessageInTopic(transporterNeed.toString(),"transportersProposals");
            result = true;
        }
        frame.addProposalTransporter(transporterNeed);
        return result;
    }

    public String getManufacturerAddress(String manufacturerName){
        OpenJMS.getInstance().postMessageInQueue("","getAddress"+manufacturerName);
        String address = OpenJMS.getInstance().receiveMessageFromQueue("getAddress"+manufacturerName);
        return address;
    }

    private void listenCounterProposalsTransporters() {

        String counterProposalAndOpinion = OpenJMS.getInstance().receiveMessageFromQueue("counterProposalsTransporters" + name);
        String array[] = counterProposalAndOpinion.split("::");
        TransporterNeed transporterNeed = TransporterNeed.parse(array[0]);
        boolean opinion = Boolean.valueOf(array[1]);
        if(!frame.isTransporterChosen(transporterNeed.getId())) {
            if (opinion) {
                //Add transporter need to counterProposalTable with State ACCEPTE
                transporterNeed.setState(StateTransporterNeed.ACCEPTE);
                frame.addTransporterNeedFinished(transporterNeed);
            } else {
                //Add transporter need to counterProposalTable with State REJETE
                transporterNeed.setState(StateTransporterNeed.REJETE);
                frame.addTransporterNeedFinished(transporterNeed);
            }
        }
    }

    public void chooseTransporter(TransporterNeed transporterNeed) {
        List<String> transporterAgents = frame.findOthersTransporters(transporterNeed);
        System.out.println("rejeté size : " + transporterAgents.size());
        for(String transporterName : transporterAgents){
            System.out.println("rejeté : "+transporterName);
            OpenJMS.getInstance().postMessageInQueue(transporterNeed.toString() + "!false" ,"acceptedOrRejectedProposals"+transporterName);
        }
        OpenJMS.getInstance().postMessageInQueue(transporterNeed.toString()+"!true","acceptedOrRejectedProposals"+transporterNeed.getTransporterName());
    }
}
