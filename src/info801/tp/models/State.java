package info801.tp.models;

public enum State {
    EN_ATTENTE, ACCEPTE, REJETE, EN_PRODUCTION;


    @Override
    public String toString() {
        return name();
    }
}
