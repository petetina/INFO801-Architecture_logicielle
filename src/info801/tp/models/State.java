package info801.tp.models;

public enum State {
    EN_ATTENTE, ACCEPTE, REJETE;


    @Override
    public String toString() {
        return name();
    }
}
