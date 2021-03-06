package info801.tp;

public class Main {

    public static void main(String[] args) {
        try {
            OpenJMS.getInstance().init();
            //CustomerAgent customerAgent2 = new CustomerAgent(2);

            LogisticAgent logisticAgent1 = new LogisticAgent(1);

            ManufacturerAgent manufacturerAgent1 = new ManufacturerAgent(1,"Paris");
            ManufacturerAgent manufacturerAgent2 = new ManufacturerAgent(2, "Lyon");
            ManufacturerAgent manufacturerAgent3 = new ManufacturerAgent(3, "Marseille");

            CustomerAgent customerAgent1 = new CustomerAgent(1);

            DesignAndWorkShopAgent designAndWorkShopAgent1 = new DesignAndWorkShopAgent(1);
            DesignAndWorkShopAgent designAndWorkShopAgent2 = new DesignAndWorkShopAgent(2);
            DesignAndWorkShopAgent designAndWorkShopAgent3 = new DesignAndWorkShopAgent(3);

            SupplierAgent supplierAgent = new SupplierAgent(1);
            SupplierAgent supplierAgent2 = new SupplierAgent(2);
            SupplierAgent supplierAgent3 = new SupplierAgent(3);

            TransporterAgent transporterAgent = new TransporterAgent(1);
            TransporterAgent transporterAgent2 = new TransporterAgent(2);

            WarehouseAgent warehouseAgent = new WarehouseAgent(1);
            WarehouseAgent warehouseAgent2 = new WarehouseAgent(2);

            //Add manufacturer 1 and 2 to logistic1 's manufacturerList
            //And logistic to manufacturer
            //It is a biredirectional link !
            logisticAgent1.addManufacturer(manufacturerAgent1);
            logisticAgent1.addManufacturer(manufacturerAgent2);

            //Unidirectional link !
            logisticAgent1.addSupplier(supplierAgent);
            logisticAgent1.addSupplier(supplierAgent2);

            customerAgent1.start();
            //customerAgent2.start();

            logisticAgent1.start();

            manufacturerAgent1.start();
            manufacturerAgent2.start();
            manufacturerAgent3.start();

            designAndWorkShopAgent1.start();
            designAndWorkShopAgent2.start();
            designAndWorkShopAgent3.start();

            supplierAgent.start();
            supplierAgent2.start();
            supplierAgent3.start();

            transporterAgent.start();
            transporterAgent2.start();

            warehouseAgent.start();
            warehouseAgent.start();
        }catch(Exception e){
            System.out.println("Merci d'ouvrir OpenJMS");
        }
    }
}
