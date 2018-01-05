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
    private static final int MESSAGE_VALVE_MA_UPDATE           = 2;
    private static final int MESSAGE_VALVES_12_UPDATE          = 3;
    private static final int MESSAGE_VALVES_34_UPDATE          = 4;
    private static final int MESSAGE_LEVEL_UPDATE              = 5;
    private static final int MESSAGE_CONSIGNES_UPDATE          = 6;
    private static final int MESSAGE_PILOTAGE_VANNE_UPDATE          = 7;
    private static final int MESSAGE_POST_EXECUTE              = 8;

    private final int readingDataBloc = 5;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private View vi_main_ui;
    private TextView tv_plc;
    private TextView tv_valveMA, tv_valves12, tv_valves34;
    private TextView tv_level, tv_consignes, tv_pilotageVanne;

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] valvesPLC = new byte[2];
    private byte[] levelPLC = new byte[2], pilotageVannePLC = new byte[2];
    private byte[] consigneAutoPLC = new byte[2], consigneManuPLC = new byte[2];

    public ReadLiquidS7(View v, TextView[] tvArray) {
        vi_main_ui          = v;
        tv_plc              = tvArray[0];
        tv_valveMA          = tvArray[1];
        tv_valves12         = tvArray[2];
        tv_valves34         = tvArray[3];
        tv_level            = tvArray[4];
        tv_consignes        = tvArray[5];
        tv_pilotageVanne    = tvArray[6];

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

    private void downloadOnValveMAUpdate(int data) {
        tv_valveMA.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valveMA),
                (data == 1 ? "Automatique" : "Manuelle")));
    }

    private void downloadOnValves12Update(int data1, int data2) {
        tv_valves12.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valves12),
                (data1 == 1 ? "Fermée" : "Ouverte"),
                (data2 == 1 ? "Fermée" : "Ouverte")));
    }

    private void downloadOnValves34Update(int data1, int data2) {
        tv_valves34.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valves34),
                (data1 == 1 ? "Fermée" : "Ouverte"),
                (data2 == 1 ? "Fermée" : "Ouverte")));
    }

    private void downloadOnLevelUpdate(int data) {
        tv_level.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_level),
                String.valueOf(data)));
    }

    private void downloadOnConsignesUpdate(int data1, int data2) {
        tv_consignes.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_consignes),
                String.valueOf(data1),
                String.valueOf(data2)));
    }

    private void downloadOnPilotageVanneUpdate(int data) {
        tv_pilotageVanne.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_pilotageVanne),
                String.valueOf(data)));
    }

    private void downloadOnPostExecute() {
        tv_plc.setText(param[0] + "\nPLC : ⚠");

        tv_valveMA.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valveMA), "?"));
        tv_valves12.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valves12), "?", "?"));
        tv_valves34.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_valves34), "?", "?"));
        tv_level.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_level), "?"));
        tv_consignes.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_consignes), "?", "?"));
        tv_pilotageVanne.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_pilotageVanne), "?"));
    }

    private Handler monHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.arg1);
                    break;
                case MESSAGE_VALVE_MA_UPDATE:
                    downloadOnValveMAUpdate(msg.arg1);
                    break;
                case MESSAGE_VALVES_12_UPDATE:
                    downloadOnValves12Update(msg.arg1, msg.arg2);
                    break;
                case MESSAGE_VALVES_34_UPDATE:
                    downloadOnValves34Update(msg.arg1, msg.arg2);
                    break;
                case MESSAGE_LEVEL_UPDATE:
                    downloadOnLevelUpdate(msg.arg1);
                    break;
                case MESSAGE_CONSIGNES_UPDATE:
                    downloadOnConsignesUpdate(msg.arg1, msg.arg2);
                    break;
                case MESSAGE_PILOTAGE_VANNE_UPDATE:
                    downloadOnPilotageVanneUpdate(msg.arg1);
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

        private int[] array = {0, 0, 0, 0};

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

                        int valvesRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,0,2, valvesPLC);
                        int valveMA, valve1, valve2, valve3, valve4;
                        if (valvesRead == 0) {
                            valve1 = S7.GetBitAt(valvesPLC, 0, 1) ? 1 : 0;
                            valve2 = S7.GetBitAt(valvesPLC, 0, 2) ? 1 : 0;
                            valve3 = S7.GetBitAt(valvesPLC, 0, 3) ? 1 : 0;
                            valve4 = S7.GetBitAt(valvesPLC, 0, 4) ? 1 : 0;
                            valveMA = S7.GetBitAt(valvesPLC, 0, 5) ? 1 : 0;

                            sendValves12Message(valve1, valve2);
                            sendValves34Message(valve3, valve4);
                            sendValveMAMessage(valveMA);
                        }

                        int levelRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,16,2, levelPLC),
                                consigneAutoRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,18,2, consigneAutoPLC),
                                consigneManuRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,20,2, consigneManuPLC),
                                pilotageVanneRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,22,2, pilotageVannePLC);

                        if (levelRead == 0) {
                            array[0] = S7.GetWordAt(levelPLC, 0);
                            sendLevelMessage();
                        }
                        if (consigneAutoRead == 0) {
                            array[1] = S7.GetWordAt(consigneAutoPLC, 0);
                            sendConsignesMessage();
                        }
                        if (consigneManuRead == 0) {
                            array[2] = S7.GetWordAt(consigneManuPLC, 0);
                            sendConsignesMessage();
                        }
                        if (pilotageVanneRead == 0) {
                            array[3] = S7.GetWordAt(pilotageVannePLC, 0);
                            sendPilotageVanneMessage();
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

        private void sendValveMAMessage(int i) {
            Message valveMAMsg = new Message();
            valveMAMsg.what = MESSAGE_VALVE_MA_UPDATE;
            valveMAMsg.arg1 = i;
            monHandler.sendMessage(valveMAMsg);
        }

        private void sendValves12Message(int i, int j) {
            Message valves12Msg = new Message();
            valves12Msg.what = MESSAGE_VALVES_12_UPDATE;
            valves12Msg.arg1 = i;
            valves12Msg.arg2 = j;
            monHandler.sendMessage(valves12Msg);
        }

        private void sendValves34Message(int i, int j) {
            Message valves34Msg = new Message();
            valves34Msg.what = MESSAGE_VALVES_34_UPDATE;
            valves34Msg.arg1 = i;
            valves34Msg.arg2 = j;
            monHandler.sendMessage(valves34Msg);
        }

        private void sendLevelMessage() {
            Message levelMsg = new Message();
            levelMsg.what = MESSAGE_LEVEL_UPDATE;
            levelMsg.arg1 = array[0];
            monHandler.sendMessage(levelMsg);
        }

        private void sendConsignesMessage() {
            Message consignesManuMsg = new Message();
            consignesManuMsg.what = MESSAGE_CONSIGNES_UPDATE;
            consignesManuMsg.arg1 = array[1];
            consignesManuMsg.arg2 = array[2];
            monHandler.sendMessage(consignesManuMsg);
        }

        private void sendPilotageVanneMessage() {
            Message pilotageVanneMsg = new Message();
            pilotageVanneMsg.what = MESSAGE_PILOTAGE_VANNE_UPDATE;
            pilotageVanneMsg.arg1 = array[3];
            monHandler.sendMessage(pilotageVanneMsg);
        }

    }

}
