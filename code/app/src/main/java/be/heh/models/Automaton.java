package be.heh.models;

/**
 * Created by alexandre on 1/12/17.
 */

public class Automaton {

    private int id;
    private String name;
    private String ip;
    private String rack;
    private String slot;
    private String type;
    private String dataBloc;

    public Automaton(){}

    public Automaton(String name, String ip, String rack, String slot, String type, String dataBloc) {
        this.name = name;
        this.ip   = ip;
        this.rack = rack;
        this.slot = slot;
        this.type = type;
        this.dataBloc = dataBloc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataBloc() {
        return dataBloc;
    }

    public void setDataBloc(String dataBloc) {
        this.dataBloc = dataBloc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID : "
            + Integer.toString(getId()) + "\n"+
                "Name : " + getName() + "\n" +
                "IP : " + getIp() + "\n" +
                "Rack : " + getRack() + "\n" +
                "Slot : " + getSlot() + "\n" +
                "Type : " + getType() + "\n" +
                "Databloc : " + getDataBloc());
        return sb.toString();
    }

}
