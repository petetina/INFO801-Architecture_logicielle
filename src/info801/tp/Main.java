package info801.tp;

public class Main {

    public static void main(String[] args) {
        OpenJMS.getInstance().init();
        CustomerAgent customerAgent1 = new CustomerAgent(1);
        //CustomerAgent customerAgent2 = new CustomerAgent(2);

        LogisticAgent logisticAgent1 = new LogisticAgent(1);

        ManufacturerAgent manufacturerAgent1 = new ManufacturerAgent(1);
        ManufacturerAgent manufacturerAgent2 = new ManufacturerAgent(2);

        customerAgent1.start();
        //customerAgent2.start();

        logisticAgent1.start();

        manufacturerAgent1.start();
        manufacturerAgent2.start();
    }
}
