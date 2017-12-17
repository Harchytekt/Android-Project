package be.heh.database;

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
    private String mac;

    public Automaton(){}

    public Automaton(String name, String ip, String rack, String slot, String type) {
        this.name = name;
        this.ip   = ip;
        this.rack = rack;
        this.slot = slot;
        this.type = type;
        mac       = "Inconnue";
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
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
                "MAC : " + getMac());
        return sb.toString();
    }

}
