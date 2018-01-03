package be.heh.main;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.SimaticS7.S7;
import be.heh.SimaticS7.S7Client;
import be.heh.SimaticS7.S7OrderCode;

/**
 * Created by alexandre on 16/11/17.
 */

public class ReadLiquidS7 {
    private static final int MESSAGE_PRE_EXECUTE               = 1;
    private static final int MESSAGE_LEVEL_UPDATE              = 2;
    private static final int MESSAGE_VALVE_MA_UPDATE           = 3;
    private static final int MESSAGE_VALVE_1_UPDATE            = 4;
    private static final int MESSAGE_VALVE_2_UPDATE            = 5;
    private static final int MESSAGE_VALVE_3_UPDATE            = 6;
    private static final int MESSAGE_VALVE_4_UPDATE            = 7;
    private static final int MESSAGE_POST_EXECUTE              = 8;

    private final int readingDataBloc = 5;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private View vi_main_ui;
    private TextView tv_plc;
    private TextView tv_level;
    private TextView tv_valveMA, tv_valve1, tv_valve2, tv_valve3, tv_valve4;

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] levelPLC = new byte[2];
    private byte[] valvesPLC = new byte[2];

    public ReadLiquidS7(View v, TextView[] tvArray) {
        vi_main_ui          = v;
        tv_plc              = tvArray[0];
        tv_level            = tvArray[1];
        tv_valveMA          = tvArray[2];
        tv_valve1           = tvArray[3];
        tv_valve2           = tvArray[4];
        tv_valve3           = tvArray[5];
        tv_valve4           = tvArray[6];

        comS7             = new S7Client();
        plcS7             = new AutomateS7();

        readThread        = new Thread(plcS7);
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
        tv_plc.setText(param[0] + "\nPLC : " + String.valueOf(t));
    }

    private void downloadOnLevelUpdate(int data) {
        tv_level.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_level),
                String.valueOf(data)));
    }

    private void downloadOnValveMAUpdate(int data) {
        tv_valveMA.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valveMA),
                (data == 1 ? "Automatique" : "Manuelle")));
    }

    private void downloadOnValve1Update(int data) {
        tv_valve1.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve1),
                (data == 1 ? "Fermée" : "Ouverte")));
    }

    private void downloadOnValve2Update(int data) {
        tv_valve2.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve2),
                (data == 1 ? "Fermée" : "Ouverte")));
    }

    private void downloadOnValve3Update(int data) {
        tv_valve3.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve3),
                (data == 1 ? "Fermée" : "Ouverte")));
    }

    private void downloadOnValve4Update(int data) {
        tv_valve4.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve4),
                (data == 1 ? "Fermée" : "Ouverte")));
    }

    private void downloadOnPostExecute() {
        tv_plc.setText(param[0] + "\nPLC : ⚠");

        tv_level.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_level), "?"));
        tv_valveMA.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valveMA), "?"));
        tv_valve1.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve1), "?"));
        tv_valve2.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve2), "?"));
        tv_valve3.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve3), "?"));
        tv_valve4.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valve4), "?"));
    }

    private Handler monHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.arg1);
                    break;
                case MESSAGE_LEVEL_UPDATE:
                    downloadOnLevelUpdate(msg.arg1);
                    break;
                case MESSAGE_VALVE_MA_UPDATE:
                    downloadOnValveMAUpdate(msg.arg1);
                    break;
                case MESSAGE_VALVE_1_UPDATE:
                    downloadOnValve1Update(msg.arg1);
                    break;
                case MESSAGE_VALVE_2_UPDATE:
                    downloadOnValve2Update(msg.arg1);
                    break;
                case MESSAGE_VALVE_3_UPDATE:
                    downloadOnValve3Update(msg.arg1);
                    break;
                case MESSAGE_VALVE_4_UPDATE:
                    downloadOnValve4Update(msg.arg1);
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
                if (res.equals(0) && result.equals(0))
                    numCPU = Integer.valueOf(orderCode.Code().toString().substring(5, 8));
                else numCPU = 0000;

                sendPreExecuteMessage(numCPU);

                while(isRunning.get()) {
                    if (res.equals(0)) {

                        int levelRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,16,2, levelPLC);
                        int level;
                        if (levelRead == 0) {
                            level = S7.GetWordAt(levelPLC, 0);
                            sendLevelMessage(level);
                        }

                        int valvesRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,0,2, valvesPLC);
                        int valveMA, valve1, valve2, valve3, valve4;
                        if (valvesRead == 0) {
                            valve1 = S7.GetBitAt(valvesPLC, 0, 1) ? 1 : 0;
                            valve2 = S7.GetBitAt(valvesPLC, 0, 2) ? 1 : 0;
                            valve3 = S7.GetBitAt(valvesPLC, 0, 3) ? 1 : 0;
                            valve4 = S7.GetBitAt(valvesPLC, 0, 4) ? 1 : 0;
                            valveMA = S7.GetBitAt(valvesPLC, 0, 5) ? 1 : 0;

                            sendValve1Message(valve1);
                            sendValve2Message(valve2);
                            sendValve3Message(valve3);
                            sendValve4Message(valve4);
                            sendValveMAMessage(valveMA);
                        }

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

        private void sendLevelMessage(int i) {
            Message levelMsg = new Message();
            levelMsg.what = MESSAGE_LEVEL_UPDATE;
            levelMsg.arg1 = i;
            monHandler.sendMessage(levelMsg);
        }

        private void sendValveMAMessage(int i) {
            Message valveMAMsg = new Message();
            valveMAMsg.what = MESSAGE_VALVE_MA_UPDATE;
            valveMAMsg.arg1 = i;
            monHandler.sendMessage(valveMAMsg);
        }

        private void sendValve1Message(int i) {
            Message valve1Msg = new Message();
            valve1Msg.what = MESSAGE_VALVE_1_UPDATE;
            valve1Msg.arg1 = i;
            monHandler.sendMessage(valve1Msg);
        }

        private void sendValve2Message(int i) {
            Message valve2Msg = new Message();
            valve2Msg.what = MESSAGE_VALVE_2_UPDATE;
            valve2Msg.arg1 = i;
            monHandler.sendMessage(valve2Msg);
        }

        private void sendValve3Message(int i) {
            Message valve3Msg = new Message();
            valve3Msg.what = MESSAGE_VALVE_3_UPDATE;
            valve3Msg.arg1 = i;
            monHandler.sendMessage(valve3Msg);
        }

        private void sendValve4Message(int i) {
            Message valve4Msg = new Message();
            valve4Msg.what = MESSAGE_VALVE_4_UPDATE;
            valve4Msg.arg1 = i;
            monHandler.sendMessage(valve4Msg);
        }

    }

}
