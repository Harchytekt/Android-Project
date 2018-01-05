package be.heh.automatons;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.SimaticS7.S7;
import be.heh.SimaticS7.S7Client;
import be.heh.SimaticS7.S7OrderCode;
import be.heh.main.R;

/**
 * Created by alexandre on 16/11/17.
 */

public class ReadPillsS7 {
    private static final int MESSAGE_PRE_EXECUTE            = 1;
    private static final int MESSAGE_SERVICE_UPDATE         = 2;
    private static final int MESSAGE_BOTTLES_COMING_UPDATE  = 3;
    private static final int MESSAGE_WANTED_PILLS_UPDATE    = 4;
    private static final int MESSAGE_PILLS_UPDATE           = 5;
    private static final int MESSAGE_BOTTLES_UPDATE         = 6;
    private static final int MESSAGE_POST_EXECUTE           = 7;

    private final int readingDataBloc = 5;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private View vi_main_ui;
    private TextView tv_plc;
    private TextView tv_service;
    private TextView tv_bottlesComing;
    private TextView tv_wantedPills;
    private TextView tv_pills;
    private TextView tv_bottles;

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] servicePLC = new byte[2], bottlesComingPLC = new byte[2];
    private byte[] wantedPillsPLC = new byte[2], pillsPLC = new byte[2];
    private byte[] bottlesPLC = new byte[2];

    public ReadPillsS7(View v, TextView[] tvArray) {
        vi_main_ui        = v;
        tv_plc            = tvArray[0];
        tv_service        = tvArray[1];
        tv_bottlesComing  = tvArray[2];
        tv_wantedPills    = tvArray[3];
        tv_pills          = tvArray[4];
        tv_bottles        = tvArray[5];

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

    private void downloadOnServiceUpdate(int data) {
        tv_service.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_service),
                (data == 1 ? "Oui" : "Non")));
    }

    private void downloadOnBottlesComingUpdate(int data) {
        tv_bottlesComing.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_bottlesComing),
                (data == 1 ? "Oui" : "Non")));
    }

    private void downloadOnWantedPillsUpdate(int data) {
        tv_wantedPills.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_wantedPills), String.valueOf(data)));
    }

    private void downloadOnPillsUpdate(int data) {
        tv_pills.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_pills), String.valueOf(data)));
    }

    private void downloadOnBottlesUpdate(int data) {
        tv_bottles.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_bottles), String.valueOf(data)));
    }

    private void downloadOnPostExecute() {
        tv_plc.setText(param[0] + "\nPLC : ⚠");

        tv_service.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_service), "?"));
        tv_bottlesComing.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_bottlesComing),
                "?"));
        tv_wantedPills.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_wantedPills), "?"));
        tv_pills.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_pills), "?"));
        tv_bottles.setText(String.format(vi_main_ui.getContext().getString(R.string.pills_bottles), "?"));
    }

    private Handler monHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.arg1);
                    break;
                case MESSAGE_SERVICE_UPDATE:
                    downloadOnServiceUpdate(msg.arg1);
                    break;
                case MESSAGE_BOTTLES_COMING_UPDATE:
                    downloadOnBottlesComingUpdate(msg.arg1);
                    break;
                case MESSAGE_WANTED_PILLS_UPDATE:
                    downloadOnWantedPillsUpdate(msg.arg1);
                    break;
                case MESSAGE_PILLS_UPDATE:
                    downloadOnPillsUpdate(msg.arg1);
                    break;
                case MESSAGE_BOTTLES_UPDATE:
                    downloadOnBottlesUpdate(msg.arg1);
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

                        int serviceRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,0,2, servicePLC);
                        int service;
                        if (serviceRead == 0) {
                            service = S7.GetBitAt(servicePLC, 0, 0) ? 1 : 0;

                            sendServiceMessage(service);
                        }

                        int bottlesComingRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,1,2, bottlesComingPLC);
                        int bottlesComing;
                        if (bottlesComingRead == 0) {
                            bottlesComing  = S7.GetBitAt(bottlesComingPLC, 0, 3) ? 1 : 0;

                            sendBottlesComingMessage(bottlesComing);
                        }

                        int wantedPillsRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,4,2, wantedPillsPLC);
                        int wantedPills;
                        boolean[] wantedPillsArray = {false, false, false};
                        if (wantedPillsRead == 0) {
                            wantedPillsArray[0] = S7.GetBitAt(wantedPillsPLC, 0, 3);
                            wantedPillsArray[1] = S7.GetBitAt(wantedPillsPLC, 0, 4);
                            wantedPillsArray[2] = S7.GetBitAt(wantedPillsPLC, 0, 5);
                            wantedPills = wantedPillsNumber(wantedPillsArray);

                            sendWantedPillsMessage(wantedPills);
                        }

                        int pillsRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,15,2, pillsPLC);
                        int pills;
                        if (pillsRead == 0) {
                            pills = S7.BCDtoByte(pillsPLC[0]);
                            sendPillsMessage(pills);
                        }

                        int bottlesRead = comS7.ReadArea(S7.S7AreaDB, readingDataBloc,16,2, bottlesPLC);
                        int bottles;
                        if (bottlesRead == 0) {
                            bottles = S7.GetWordAt(bottlesPLC, 0);
                            sendBottlesMessage(bottles);
                        }

                        //Log.i("Service → ", String.valueOf(service)); // OK
                        //Log.i("Pills   → ", String.valueOf(pills)); // OK
                        //Log.i("Bottles → ", String.valueOf(bottles)); // OK

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

        private void sendServiceMessage(int i) {
            Message serviceMsg = new Message();
            serviceMsg.what = MESSAGE_SERVICE_UPDATE;
            serviceMsg.arg1 = i;
            monHandler.sendMessage(serviceMsg);
        }

        private void sendBottlesComingMessage(int i) {
            Message bottlesComingMsg = new Message();
            bottlesComingMsg.what = MESSAGE_BOTTLES_COMING_UPDATE;
            bottlesComingMsg.arg1 = i;
            monHandler.sendMessage(bottlesComingMsg);
        }

        private void sendWantedPillsMessage(int i) {
            Message wantedPillsMsg = new Message();
            wantedPillsMsg.what = MESSAGE_WANTED_PILLS_UPDATE;
            wantedPillsMsg.arg1 = i;
            monHandler.sendMessage(wantedPillsMsg);
        }

        private void sendPillsMessage(int i) {
            Message pillsMsg = new Message();
            pillsMsg.what = MESSAGE_PILLS_UPDATE;
            pillsMsg.arg1 = i;
            monHandler.sendMessage(pillsMsg);
        }

        private void sendBottlesMessage(int i) {
            Message bottlesMsg = new Message();
            bottlesMsg.what = MESSAGE_BOTTLES_UPDATE;
            bottlesMsg.arg1 = i;
            monHandler.sendMessage(bottlesMsg);
        }

        private int wantedPillsNumber(boolean[] array) {
            if (array[0])
                return 5;
            else if (array[1])
                return 10;
            else if (array[2])
                return 15;
            return 0;
        }

    }

}
