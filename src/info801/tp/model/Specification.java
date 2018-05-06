package info801.tp.model;

import java.util.ArrayList;
import java.util.List;

/***
 * Cahier des charges
 */
public class Specification {

    private String logisticName;
    private String customerName;
    private List<String> requirements;
    private double cost;
    private int productionTimeInDays;
    private int quantity;

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

    @Override
    public String toString() {
        return requirements + ";" + cost + ";" + productionTimeInDays + ";" + quantity + ";" + logisticName + ";" + customerName;
    }

    public static Specification parse(String rfp){
        if(rfp.trim().isEmpty())
            return new Specification("","",new ArrayList<String>(),0,0,0);

        String args[] = rfp.split(";");
        List<String> requirements = new ArrayList<>();
        String req[] = args[0].replace("[","").replace("]","").split(",");
        for(String r : req)
            requirements.add(r.trim());
        double cost = Double.parseDouble(args[1]);
        int productionTimeInDays = Integer.parseInt(args[2]);
        int quantity = Integer.parseInt(args[3]);
        String logisticName = args[4];
        String customerName = args[5];
        return new Specification(customerName,logisticName,requirements,cost,productionTimeInDays,quantity);
    }

    public Specification clone(Specification other){
        Specification specification = new Specification();
        specification.logisticName = other.logisticName;

        //specification
        return null;
    }

    public void addRequirement(String s) {
        requirements.add(s);
    }
}
