package info801.tp.models;

public class TransporterNeed {

    private String id;
    private Specification project;
    private String addressFrom;
    private String warehouseDestination;
    private String date;
    private StateTransporterNeed state;
    private String transporterName;

    public TransporterNeed(){
        state = StateTransporterNeed.EN_ATTENTE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Specification getProject() {
        return project;
    }

    public void setProject(Specification project) {
        this.project = project;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getWarehouseDestination() {
        return warehouseDestination;
    }

    public void setWarehouseDestination(String warehouseDestination) {
        this.warehouseDestination = warehouseDestination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public StateTransporterNeed getState() {
        return state;
    }

    public void setState(StateTransporterNeed state) {
        this.state = state;
    }

    public String getTransporterName() {
        return transporterName;
    }

    public void setTransporterName(String transporterName) {
        this.transporterName = transporterName;
    }

    public static TransporterNeed parse(String data){
        TransporterNeed transporterNeed = new TransporterNeed();

        String array[] = data.split(";;");
        transporterNeed.setProject(Specification.parse(array[0]));
        transporterNeed.setId(array[1]);
        transporterNeed.setAddressFrom(array[2]);
        transporterNeed.setWarehouseDestination(array[3]);
        transporterNeed.setDate(array[4]);
        transporterNeed.setState(StateTransporterNeed.valueOf(array[5]));
        transporterNeed.setTransporterName(array[6]);

        return transporterNeed;
    }

    @Override
    public String toString(){
        return project.toString() + ";;" + id + ";;" + addressFrom + ";;" + warehouseDestination + ";;" + date + ";;" + state.toString() + ";;" + transporterName;
    }

    public boolean equals(TransporterNeed other) {
        return id.equals(other.getId())
                && project.equals(other.getProject())
                && addressFrom.equals(other.getAddressFrom())
                && warehouseDestination.equals(other.getWarehouseDestination())
                && date.equals(other.getDate())
                && state.equals(other.getState())
                && (transporterName.equals(other.getTransporterName()) || transporterName.equals(""));
    }
}
