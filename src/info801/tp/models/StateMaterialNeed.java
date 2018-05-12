package info801.tp.models;

public enum StateMaterialNeed {
    EN_ATTENTE, REPONDU, ACCEPTE, REJETE, LIVRE;


    @Override
    public String toString() {
        return name();
    }
}
