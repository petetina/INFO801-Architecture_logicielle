package info801.tp.models;

public enum Material {
    CARTON, PLASTIC;


    @Override
    public String toString() {
        return super.name();
    }

    public static Material find(String name){
        name = name.toLowerCase().trim();
        if(name.equals("plastic"))
            return PLASTIC;
        else if(name.equals("carton"))
            return CARTON;
        else
            return null;
    }
}
