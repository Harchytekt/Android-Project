package be.heh.automatons;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.SimaticS7.S7;
import be.heh.SimaticS7.S7Client;

/**
 * Created by alexandre on 17/11/17.
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

    public WriteLiquidS7() {
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

                    comS7.WriteArea(S7.S7AreaDB, dataBloc,2,2, dbb2);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,3,2, dbb3);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,24,2, dbw24);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,26,2, dbw26);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,28,2, dbw28);
                    comS7.WriteArea(S7.S7AreaDB, dataBloc,30,2, dbw30);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setDBBBinary(int dbb, String value) {
        char[] array = value.toCharArray();
        int len = array.length;
        byte[] chosenDBB;
        if (dbb == 2) chosenDBB = dbb2;
        else chosenDBB = dbb3;
        for (int i = 0; i < len; i++) {
            S7.SetBitAt(chosenDBB, 0, i, array[len-(i+1)] == '1' ? true : false);
        }
    }

    public void setDBW(int dbw, String value) {
        byte[] chosenDBW;
        if (dbw == 24) chosenDBW = dbw24;
        else if (dbw == 26) chosenDBW = dbw26;
        else if (dbw == 28) chosenDBW = dbw28;
        else chosenDBW = dbw30;
        S7.SetWordAt(chosenDBW, 0, Integer.parseInt(value));
    }
}
