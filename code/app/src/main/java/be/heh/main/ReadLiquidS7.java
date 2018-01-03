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
    private static final int MESSAGE_CONSIGNE_AUTO_UPDATE      = 3;
    private static final int MESSAGE_CONSIGNE_MANUELLE_UPDATE  = 4;
    private static final int MESSAGE_PILOTAGE_VANNE_UPDATE     = 5;
    private static final int MESSAGE_BOTTLES_UPDATE            = 6;
    private static final int MESSAGE_POST_EXECUTE              = 7;

    private final int readingDataBloc = 5;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private View vi_main_ui;
    private TextView tv_plc;
    private TextView tv_level;
    private TextView tv_consigneAuto;
    private TextView tv_consigneManuelle;
    private TextView tv_pilotageVanne;

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] levelPLC = new byte[2];
    private byte[] consigneAutoPLC = new byte[2], consigneManuellePLC = new byte[2];
    private byte[] pilotageVannePLC = new byte[2];

    public ReadLiquidS7(View v, TextView[] tvArray) {
        vi_main_ui          = v;
        tv_plc              = tvArray[0];
        tv_level            = tvArray[1];
        tv_consigneAuto     = tvArray[2];
        tv_consigneManuelle = tvArray[3];
        tv_pilotageVanne    = tvArray[4];

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

    private void downloadOnConsigneAutoUpdate(int data) {
        tv_consigneAuto.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_consigneAuto),
                String.valueOf(data)));
    }

    private void downloadOnConsigneManuelleUpdate(int data) {
        tv_consigneManuelle.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_consigneManuelle),
                String.valueOf(data)));
    }

    private void downloadOnPilotageVanneUpdate(int data) {
        tv_pilotageVanne.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_pilotageVanne),
                String.valueOf(data)));
    }

    private void downloadOnPostExecute() {
        tv_plc.setText(param[0] + "\nPLC : ⚠");

        tv_level.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_level), "?"));
        tv_consigneAuto.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_consigneAuto), "?"));
        tv_consigneManuelle.setText(String.format(vi_main_ui.getContext().getString(R.string.liquid_consigneManuelle), "?"));
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
                case MESSAGE_LEVEL_UPDATE:
                    downloadOnLevelUpdate(msg.arg1);
                    break;
                case MESSAGE_CONSIGNE_AUTO_UPDATE:
                    downloadOnConsigneAutoUpdate(msg.arg1);
                    break;
                case MESSAGE_CONSIGNE_MANUELLE_UPDATE:
                    downloadOnConsigneManuelleUpdate(msg.arg1);
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
                else numCPU=0000;

                sendPreExecuteMessage(numCPU);

                while(isRunning.get()) {
                    if (res.equals(0)) {

                        int levelRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,16,2, levelPLC);
                        int level;
                        if (levelRead == 0) {
                            level = S7.GetWordAt(levelPLC, 0);
                            sendLevelMessage(level);
                        }

                        int consigneAutoRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,18,2, consigneAutoPLC);
                        int consigneAuto;
                        if (consigneAutoRead == 0) {
                            consigneAuto = S7.GetWordAt(consigneAutoPLC, 0);
                            sendConsigneAutoMessage(consigneAuto);
                        }

                        int consigneManuelleRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,20,2, consigneManuellePLC);
                        int consigneManuelle;
                        if (consigneManuelleRead == 0) {
                            consigneManuelle = S7.GetWordAt(consigneManuellePLC, 0);
                            sendConsigneManuelleMessage(consigneManuelle);
                        }

                        int pilotageVanneRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,22,2, pilotageVannePLC);
                        int pilotageVanne;
                        if (pilotageVanneRead == 0) {
                            pilotageVanne = S7.GetWordAt(pilotageVannePLC, 0);
                            sendPilotageVanneMessage(pilotageVanne);
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

        private void sendConsigneAutoMessage(int i) {
            Message consigneAutoMsg = new Message();
            consigneAutoMsg.what = MESSAGE_CONSIGNE_AUTO_UPDATE;
            consigneAutoMsg.arg1 = i;
            monHandler.sendMessage(consigneAutoMsg);
        }

        private void sendConsigneManuelleMessage(int i) {
            Message consigneManuelleMsg = new Message();
            consigneManuelleMsg.what = MESSAGE_CONSIGNE_MANUELLE_UPDATE;
            consigneManuelleMsg.arg1 = i;
            monHandler.sendMessage(consigneManuelleMsg);
        }

        private void sendPilotageVanneMessage(int i) {
            Message pilotageVanneMsg = new Message();
            pilotageVanneMsg.what = MESSAGE_PILOTAGE_VANNE_UPDATE;
            pilotageVanneMsg.arg1 = i;
            monHandler.sendMessage(pilotageVanneMsg);
        }

    }

}
