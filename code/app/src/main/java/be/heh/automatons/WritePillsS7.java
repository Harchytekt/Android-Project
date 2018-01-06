package be.heh.automatons;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.SimaticS7.S7;
import be.heh.SimaticS7.S7Client;

/**
 * This class is used to write data to the 'pills" automatons.
 *
 * @author DUCOBU Alexandre
 */
public class WritePillsS7 {

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private int dataBloc;

    private Thread writeThread;
    private AutomateS7 plcS7;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] dbb5 = new byte[2], dbb6 = new byte[2];
    private byte[] dbb7 = new byte[2], dbb8 = new byte[2], dbw18 = new byte[2];

    /**
     * Constructor of the writer.
     */
    public WritePillsS7() {
        comS7 = new S7Client();

        plcS7 = new AutomateS7();
        writeThread = new Thread(plcS7);
    }

    /**
     * Start the connection to the automaton and the writing thread.
     *
     * @param name
     *      The name of the automaton.
     * @param ip
     *      The IP address of the automaton.
     * @param rack
     *      The rack used by the automaton.
     * @param slot
     *      The slot used by the automaton.
     *
     */
    public void Start(String name, String ip, String rack, String slot, String dataBloc) {
        if (!writeThread.isAlive()) {
            param[0]      = name;
            param[1]      = ip;
            param[2]      = rack;
            param[3]      = slot;
            this.dataBloc = Integer.parseInt(dataBloc);

            writeThread.start();
            isRunning.set(true);
        }
    }

    /**
     * Stop the connection to the automaton and the writing thread.
     */
    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }

    /**
     * Private inner class which write the values to the automaton.
     */
    private class AutomateS7 implements Runnable {

        /**
         * Method which connects with the automaton and updates the values in the automaton in live.
         */
        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);

                Integer res = comS7.ConnectTo(param[1],
                        Integer.valueOf(param[2]),
                        Integer.valueOf(param[3]));

                while (isRunning.get() && res.equals(0)) {

                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 5, 2, dbb5);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 6, 2, dbb6);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 7, 2, dbb7);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 8, 2, dbb8);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 18, 2, dbw18);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write the given value to the automaton in 'bool'.
     *
     * @param dbb
     *      The chosen dbb.
     * @param value
     *      The given value to write to the automaton.
     */
    public void setWriteBool(int dbb, String value) {
        char[] array = value.toCharArray();
        int len = array.length;
        byte[] chosenDBB;
        if (dbb == 5) chosenDBB = dbb5;
        else if (dbb == 6) chosenDBB = dbb6;
        else chosenDBB = dbb7;
        for (int i = 0; i < len; i++) {
            S7.SetBitAt(chosenDBB, 0, i, array[len-(i+1)] == '1' ? true : false);
        }
    }

    /**
     * Write the given value to the automaton in 'Byte' in the BCD format.
     *
     * @param value
     *      The given value to write to the automaton.
     */
    public void setWriteByte(String value) {
        dbb8[0] = S7.ByteToBCD(Integer.parseInt(value));
    }

    /**
     * Write the given value to the automaton in 'Integer'.
     *
     * @param value
     *      The given value to write to the automaton.
     */
    public void setWriteInt(String value) {
        S7.SetWordAt(dbw18, 0, Integer.parseInt(value));
    }
}
