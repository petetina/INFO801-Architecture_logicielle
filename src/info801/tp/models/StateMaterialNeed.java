package info801.tp.models;

public enum StateMaterialNeed {
    EN_ATTENTE, REPONDU, ACCEPTE, REJETE, LIVRE, CHOISI, A_CONDITIONNER, CONDITIONNE;


    @Override
    public String toString() {
        return name();
    }
}
