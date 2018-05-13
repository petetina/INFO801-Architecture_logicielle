package info801.tp.models;

public enum State {
    EN_ATTENTE, ACCEPTE, REJETE, EN_PRODUCTION, PRODUCTION_FINI, CONDITIONNE;


    @Override
    public String toString() {
        return name();
    }
}
