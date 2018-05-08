package info801.tp.models;

import java.util.ArrayList;
import java.util.List;

/***
 * Cahier des charges
 */
public class Specification {
    private String id;
    private String logisticName;
    private String customerName;
    private List<String> requirements;
    private double cost;
    private int productionTimeInDays;
    private int quantity;
    private String manufacturer = "Non défini";

    public Specification(){

    }

    public Specification(String customerName, String logisticName, List<String> requirements, double cost, int productionTimeInDays, int quantity) {
        this.customerName = customerName;
        this.logisticName = logisticName;
        this.requirements = requirements;
        this.cost = cost;
        this.productionTimeInDays = productionTimeInDays;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getCost() {
        return cost;
    }

    public int getProductionTimeInDays() {
        return productionTimeInDays;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public String getLogisticName() {
        return logisticName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setLogisticName(String logisticName) {
        this.logisticName = logisticName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setProductionTimeInDays(int productionTimeInDays) {
        this.productionTimeInDays = productionTimeInDays;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return id + ";" + requirements + ";" + cost + ";" + productionTimeInDays + ";" + quantity + ";" + logisticName + ";" + customerName+";"+manufacturer;
    }

    public static Specification parse(String rfp){
        if(rfp.trim().isEmpty())
            return new Specification("","",new ArrayList<String>(),0,0,0);

        String args[] = rfp.split(";");
        List<String> requirements = new ArrayList<>();
        String req[] = args[1].replace("[","").replace("]","").split(",");
        for(String r : req)
            requirements.add(r.trim());
        double cost = Double.parseDouble(args[2]);
        int productionTimeInDays = Integer.parseInt(args[3]);
        int quantity = Integer.parseInt(args[4]);
        String logisticName = args[5];
        String customerName = args[6];
        Specification specification = new Specification(customerName,logisticName,requirements,cost,productionTimeInDays,quantity);
        specification.id = args[0];

        specification.manufacturer = args[7];
        return specification;
    }

    public void addRequirement(String s) {
        requirements.add(s);
    }
}
