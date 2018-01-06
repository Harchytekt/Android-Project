package be.heh.automatons;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.SimaticS7.S7;
import be.heh.SimaticS7.S7Client;

/**
 * This class is used to write data to the 'liquid" automatons.
 *
 * @author DUCOBU Alexandre
 */
public class WriteLiquidS7 {

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private int dataBloc;

    private Thread writeThread;
    private AutomateS7 plcS7;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] dbb2 = new byte[2], dbb3 = new byte[2];
    private byte[] dbw24 = new byte[2], dbw26 = new byte[2], dbw28 = new byte[2], dbw30 = new byte[2];

    /**
     * Constructor of the writer.
     */
    public WriteLiquidS7() {
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

                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 2, 2, dbb2);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 3, 2, dbb3);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 24, 2, dbw24);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 26, 2, dbw26);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 28, 2, dbw28);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc, 30, 2, dbw30);

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
        if (dbb == 2) chosenDBB = dbb2;
        else chosenDBB = dbb3;
        for (int i = 0; i < len; i++) {
            S7.SetBitAt(chosenDBB, 0, i, array[len-(i+1)] == '1' ? true : false);
        }
    }

    /**
     * Write the given value to the automaton in 'Integer'.
     *
     * @param dbw
     *      The chosen dbw.
     * @param value
     *      The given value to write to the automaton.
     */
    public void setWriteInt(int dbw, String value) {
        byte[] chosenDBW;
        if (dbw == 24) chosenDBW = dbw24;
        else if (dbw == 26) chosenDBW = dbw26;
        else if (dbw == 28) chosenDBW = dbw28;
        else chosenDBW = dbw30;
        S7.SetWordAt(chosenDBW, 0, Integer.parseInt(value));
    }
}
