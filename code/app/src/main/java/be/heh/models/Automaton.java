package be.heh.models;

/**
 * This class creates an automaton.
 *
 * @author DUCOBU Alexandre
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

    /**
     * Constructor of an automaton.
     * It initializes the details of the automaton.
     *
     * @param name
     *      The name of the automaton
     * @param ip
     *      The IP address of the automaton
     * @param rack
     *      The rack used by the automaton
     * @param slot
     *      The slot used by automaton
     * @param type
     *      The type of the automaton
     * @param dataBloc
     *      The databloc used by the user.
     *
     */
    public Automaton(String name, String ip, String rack, String slot, String type, String dataBloc) {
        this.name = name;
        this.ip   = ip;
        this.rack = rack;
        this.slot = slot;
        this.type = type;
        this.dataBloc = dataBloc;
    }

    /**
     * Get the ID of the automaton.
     *
     * @return the ID of the automaton.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID of the automaton.
     *
     * @param id
     *      The ID of the automaton.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the name of the automaton.
     *
     * @return the name of the automaton.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the automaton.
     *
     * @param name
     *      The name of the automaton.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the IP address of the automaton.
     *
     * @return the IP address of the automaton.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set the IP address of the automaton.
     *
     * @param ip
     *      The IP address of the automaton.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get the rack used by the automaton.
     *
     * @return the rack used by the automaton.
     */
    public String getRack() {
        return rack;
    }

    /**
     * Set the rack used by the automaton.
     *
     * @param rack
     *      The rack used by the automaton.
     */
    public void setRack(String rack) {
        this.rack = rack;
    }

    /**
     * Get the slot used by the automaton.
     *
     * @return the slot used by the automaton.
     */
    public String getSlot() {
        return slot;
    }

    /**
     * Set the slot used by the automaton.
     *
     * @param slot
     *      The slot used by the automaton.
     */
    public void setSlot(String slot) {
        this.slot = slot;
    }

    /**
     * Get the type of the automaton.
     *
     * @return the type of the automaton.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of the automaton.
     *
     * @param type
     *      The type of the automaton.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the databloc used by the user.
     *
     * @return the databloc used by the user.
     */
    public String getDataBloc() {
        return dataBloc;
    }

    /**
     * Set the databloc used by the user.
     *
     * @param dataBloc
     *      The databloc used by the user.
     */
    public void setDataBloc(String dataBloc) {
        this.dataBloc = dataBloc;
    }

    /**
     * Return a string representation of the automaton.
     *
     * @return a string representation of the automaton.
     */
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
