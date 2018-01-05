package be.heh.automatons;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.SimaticS7.S7;
import be.heh.SimaticS7.S7Client;

/**
 * Created by alexandre on 17/11/17.
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

    public WritePillsS7() {
        comS7 = new S7Client();

        plcS7 = new AutomateS7();
        writeThread = new Thread(plcS7);
    }

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

    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }

    private class AutomateS7 implements Runnable {

        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);

                Integer res = comS7.ConnectTo(param[1],
                        Integer.valueOf(param[2]),
                        Integer.valueOf(param[3]));

                while (isRunning.get() && res.equals(0)) {

                    comS7.WriteArea(S7.S7AreaDB, dataBloc,5,2, dbb5);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,6,2, dbb6);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,7,2, dbb7);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,8,2, dbb8);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,18,2, dbw18);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*public void setWriteBool(int b, int v) {
        //Masquage
        if (v == 1) motCommande[0] = (byte)(b | motCommande[0]);
        else motCommande[0] = (byte)(~b & motCommande[0]);
    }*/

    public void setDBBBinary(int dbb, String value) {
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

    public void setDBB8(String value) {
        dbb8[0] = S7.ByteToBCD(Integer.parseInt(value));
    }

    public void setDBW18(String value) {
        S7.SetWordAt(dbw18, 0, Integer.parseInt(value));
    }
}
