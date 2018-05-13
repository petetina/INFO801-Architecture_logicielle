package info801.tp.models;

public enum StateTransporterNeed {
    EN_ATTENTE, REPONDU, ACCEPTE, REJETE, CHOISI;

    @Override
    public String toString() {
        return name();
    }
}
