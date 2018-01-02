package be.heh.main;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.SimaticS7.S7;
import be.heh.SimaticS7.S7Client;
import be.heh.SimaticS7.S7OrderCode;

/**
 * Created by alexandre on 16/11/17.
 */

public class ReadTaskS7 {
    private static final int MESSAGE_PRE_EXECUTE = 1;
    private static final int MESSAGE_BOTTLES_UPDATE = 2;
    private static final int MESSAGE_PILLS_UPDATE = 3;
    private static final int MESSAGE_POST_EXECUTE = 4;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private View vi_main_ui;
    private TextView tv_main_plc;
    private TextView tv_bottles;
    private TextView tv_pills;

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] bottlesPLC = new byte[512];
    private byte[] pillsPLC = new byte[512];

    public ReadTaskS7(View v, TextView t, TextView tvBottles, TextView tvPills) {
        vi_main_ui = v;
        tv_main_plc = t;
        tv_bottles = tvBottles;
        tv_pills = tvPills;

        comS7 = new S7Client();
        plcS7 = new AutomateS7();

        readThread = new Thread(plcS7);
    }

    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    public void Start(String name, String ip, String rack, String slot) {
        if (!readThread.isAlive()) {
            param[0] = name;
            param[1] = ip;
            param[2] = rack;
            param[3] = slot;

            readThread.start();
            isRunning.set(true);
        }
    }

    private void downloadOnPreExecute(int t) {
        tv_main_plc.setText(param[0] + "\nPLC : " + String.valueOf(t));
    }

    private void downloadOnBottlesUpdate(int data) {
        tv_bottles.setText("Nombre de bouteilles : ️" + data);
    }

    private void downloadOnPillsUpdate(int data) {
        tv_pills.setText("Nombre de comprimés : ️" + data);
    }

    private void downloadOnPostExecute() {
        tv_main_plc.setText(param[0] + "\nPLC : ⚠");
    }

    private Handler monHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.arg1);
                    break;
                case MESSAGE_BOTTLES_UPDATE:
                    downloadOnBottlesUpdate(msg.arg1);
                    break;
                case MESSAGE_PILLS_UPDATE:
                    downloadOnPillsUpdate(msg.arg1);
                    break;
                case MESSAGE_POST_EXECUTE:
                    downloadOnPostExecute();
                    break;
                default:
                    break;
            }
        }
    };

    private class AutomateS7 implements Runnable {
        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(param[1], Integer.valueOf(param[2]), Integer.valueOf(param[3]));

                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);

                int numCPU;
                if (res.equals(0) && result.equals(0)) {
                    // Quelques exemples :
                    // WinAC : 6ES7 611-4SB00-0YB7
                    // S7-315 2DPP?N : 6ES7 315-4EH13-0AB0
                    // S7-1214C : 6ES7 214-1BG40-0XB0
                    // Récupérer le code CPU → 611 OU 315 OU 214
                    numCPU = Integer.valueOf(orderCode.Code().toString().substring(5, 8));
                } else numCPU=0000;

                sendPreExecuteMessage(numCPU);

                while(isRunning.get()) {
                    if (res.equals(0)) {

                        int bottlesRead = comS7.ReadArea(S7.S7AreaDB,5,16,2, bottlesPLC);
                        int bottles = 0;
                        if (bottlesRead == 0) {
                            bottles = S7.GetWordAt(bottlesPLC, 0);
                            sendBottlesMessage(bottles);
                        }

                        int pillsRead = comS7.ReadArea(S7.S7AreaDB,5,15,2, pillsPLC);
                        int pills = 0;
                        if (pillsRead == 0) {
                            pills = S7.BCDtoByte(pillsPLC[0]);
                            sendPillsMessage(pills);
                        }

                        Log.i("Bottles → ", String.valueOf(bottles)); // OK
                        Log.i("Pills   → ", String.valueOf(pills)); // OK

                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendPostExecuteMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendPostExecuteMessage() {
            Message postExecuteMsg = new Message();
            postExecuteMsg.what = MESSAGE_POST_EXECUTE;
            monHandler.sendMessage(postExecuteMsg);
        }

        private void sendPreExecuteMessage(int v) {
            Message preExecuteMsg = new Message();
            preExecuteMsg.what = MESSAGE_PRE_EXECUTE;
            preExecuteMsg.arg1 = v;
            monHandler.sendMessage(preExecuteMsg);
        }

        private void sendBottlesMessage(int i) {
            Message bottlesMsg = new Message();
            bottlesMsg.what = MESSAGE_BOTTLES_UPDATE;
            bottlesMsg.arg1 = i;
            monHandler.sendMessage(bottlesMsg);
        }

        private void sendPillsMessage(int i) {
            Message pillsMsg = new Message();
            pillsMsg.what = MESSAGE_PILLS_UPDATE;
            pillsMsg.arg1 = i;
            monHandler.sendMessage(pillsMsg);
        }

        private String toBinary(byte[] array, int size) {
            String res = "";
            for (int i = size-1; i >= 0; i--) {
                res += S7.GetBitAt(array, 0, i) ? "1" : "0";
            }
            return res;
        }

    }

}
