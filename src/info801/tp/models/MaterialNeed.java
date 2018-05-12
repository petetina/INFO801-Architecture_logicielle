package info801.tp.models;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MaterialNeed {
    private class Data{
        private Pair<Double,Material> data;

        public Data(double quantity, Material material){
            data = new Pair<>(quantity,material);
        }

        public double getQuantity(){
            return data.getKey();
        }

        public Material getMaterial(){
            return data.getValue();
        }

        @Override
        public String toString() {
            return getQuantity() + ";" + getMaterial().toString();
        }
    }

    private String id;
    private String customerProjectId;
    private List<Data> datas;
    private String logisticName;
    private String supplierName;
    private StateMaterialNeed state;

    public MaterialNeed(){
        datas = new ArrayList<>();
        state = StateMaterialNeed.EN_ATTENTE;
    }

    public Data get(int index){
        return datas.get(index);
    }

    public void add(double quantity, Material material){
        datas.add(new Data(quantity,material));
    }

    public String getLogisticName() {
        return logisticName;
    }

    public void setLogisticName(String logisticName) {
        this.logisticName = logisticName;
    }

    public String getCustomerProjectId() {
        return customerProjectId;
    }

    public void setCustomerProjectId(String customerProjectId) {
        this.customerProjectId = customerProjectId;
    }

    public List<Data> getDatas() {
        return datas;
    }

    public String getDatasInString(){
        String result = "";
        for(Data data : datas){
            result += data.toString() + "\n";
        }
        return result.trim();
    }

    public StateMaterialNeed getState() {
        return state;
    }

    public void setState(StateMaterialNeed state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public static MaterialNeed parse(String data){
        MaterialNeed result = new MaterialNeed();
        String lines[] = data.split("\n");

        if(lines.length>=5) {
            System.out.println("lines[0] = " + lines[0]);
            result.setId(lines[0]);
            System.out.println("lines[1] = " + lines[1]);
            result.setLogisticName(lines[1]);
            System.out.println("lines[2] = " + lines[2]);

            if(lines[2].trim().isEmpty())
                result.setState(StateMaterialNeed.EN_ATTENTE);
            else
                result.setState(StateMaterialNeed.valueOf(lines[2]));

            result.setCustomerProjectId(lines[3]);
            result.setSupplierName(lines[4]);
            for (int i = 5; i < lines.length; i++) {
                String array[] = lines[i].split(";");
                Double quantity = Double.valueOf(array[0]);
                Material material = Material.find(array[1]);

                if (quantity <= 0 || material == null)
                    return null;

                result.add(quantity, material);
            }
        }else
            return null;
        return result;
    }

    public boolean equals(MaterialNeed other) {
        boolean result = id.equals(other.getId())
                && customerProjectId.equals(other.getCustomerProjectId())
                && logisticName.equals(other.getLogisticName())
                && (supplierName.equals(other.getSupplierName()) || supplierName.equals(""));

        if(datas.size() == other.datas.size()) {
            for (int i = 0; i < datas.size(); i++) {
                result = result
                        && (datas.get(i).getQuantity() == other.datas.get(i).getQuantity())
                        && (datas.get(i).getMaterial().equals(other.datas.get(i).getMaterial()));
            }
        }else
            return false;
        return result;
    }

    @Override
    public String toString(){
        String result = id + "\n" + logisticName + "\n" + state.toString() + "\n" + customerProjectId + "\n" + supplierName + "\n";
        for(Data data : datas)
            result += data.getQuantity() + ";" + data.getMaterial().toString()+"\n";
        return result.trim();
    }
}
