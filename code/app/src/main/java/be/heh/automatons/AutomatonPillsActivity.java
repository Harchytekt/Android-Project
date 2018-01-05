package be.heh.automatons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import be.heh.database.AutomatonAccessDB;
import be.heh.main.R;
import be.heh.models.Automaton;
import be.heh.models.CurrentAutomaton;
import be.heh.models.Session;

public class AutomatonPillsActivity extends Activity {

    private Session session;
    private CurrentAutomaton currentAutomaton;
    String automatonName;
    Automaton automaton;

    FloatingActionButton fab_automatonPills_connect;
    FloatingActionButton fab_automatonPills_logout;

    TextView tv_automatonPills_connected;
    TextView tv_automatonPills_status;
    TextView tv_automatonPills_plc;

    TextView tv_automatonPills_service;
    TextView tv_automatonPills_bottlesComing;
    TextView tv_automatonPills_wantedPills;
    TextView tv_automatonPills_pills;
    TextView tv_automatonPills_bottles;

    Button btn_automatonPills_manage;

    LinearLayout ll_automatonPills_manage;
    EditText et_automatonPills_DBB5;
    EditText et_automatonPills_DBB6;
    EditText et_automatonPills_DBB7;
    EditText et_automatonPills_DBB8;
    EditText et_automatonPills_DBW18;

    private NetworkInfo network;
    private ConnectivityManager connexStatus;
    private ReadPillsS7 readS7;
    private WritePillsS7 writeS7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automaton_pills);

        session = new Session(getApplicationContext());
        currentAutomaton = new CurrentAutomaton(getApplicationContext());

        tv_automatonPills_connected = findViewById(R.id.tv_automatonPills_connected);
        tv_automatonPills_status = findViewById(R.id.tv_automatonPills_status);
        tv_automatonPills_plc = findViewById(R.id.tv_automatonPills_plc);

        tv_automatonPills_service = findViewById(R.id.tv_automatonPills_service);
        tv_automatonPills_bottlesComing = findViewById(R.id.tv_automatonPills_bottlesComing);
        tv_automatonPills_wantedPills = findViewById(R.id.tv_automatonPills_wantedPills);
        tv_automatonPills_pills = findViewById(R.id.tv_automatonPills_pills);
        tv_automatonPills_bottles = findViewById(R.id.tv_automatonPills_bottles);

        fab_automatonPills_connect = findViewById(R.id.fab_automatonPills_connect);
        fab_automatonPills_logout = findViewById(R.id.fab_automatonPills_logout);

        btn_automatonPills_manage = findViewById(R.id.btn_automatonPills_manage);

        ll_automatonPills_manage = findViewById(R.id.ll_automatonPills_manage);
        et_automatonPills_DBB5 = findViewById(R.id.et_automatonPills_DBB5);
        et_automatonPills_DBB6 = findViewById(R.id.et_automatonPills_DBB6);
        et_automatonPills_DBB7 = findViewById(R.id.et_automatonPills_DBB7);
        et_automatonPills_DBB8 = findViewById(R.id.et_automatonPills_DBB8);
        et_automatonPills_DBW18 = findViewById(R.id.et_automatonPills_DBW18);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin() || currentAutomaton.checkCurrent()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        HashMap<String, String> user = session.getUserDetails();
        automatonName = currentAutomaton.getAutomatonName().get(CurrentAutomaton.KEY_NAME);

        AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
        automatonDB.openForWrite();
        automaton = automatonDB.getAutomaton(automatonName);
        automatonDB.Close();

        tv_automatonPills_connected.setText(Html.fromHtml(getString(R.string.connected_as) + " '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));

        tv_automatonPills_plc.setText(Html.fromHtml(automatonName + "<br>" + getString(R.string.not_connected)));
        if (user.get(Session.KEY_RIGHTS).equals("1")) btn_automatonPills_manage.setVisibility(View.VISIBLE);

        tv_automatonPills_service.setText(String.format(getString(R.string.pills_service), "?"));
        tv_automatonPills_bottlesComing.setText(String.format(getString(R.string.pills_bottlesComing), "?"));
        tv_automatonPills_wantedPills.setText(String.format(getString(R.string.pills_wantedPills), "?"));
        tv_automatonPills_pills.setText(String.format(getString(R.string.pills_pills), "?"));
        tv_automatonPills_bottles.setText(String.format(getString(R.string.pills_bottles), "?"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session.checkLogin() || currentAutomaton.checkCurrent()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onAutomatonPillsClickManager(View v) {

        switch (v.getId()) {
            case R.id.fab_automatonPills_connect:

                connexStatus = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connexStatus != null) {
                    network = connexStatus.getActiveNetworkInfo();
                }

                if (network != null && network.isConnectedOrConnecting()) {
                    if (fab_automatonPills_connect.getContentDescription().equals(getString((R.string.login)))) {

                        fab_automatonPills_connect.setContentDescription(getString((R.string.logout_title)));
                        fab_automatonPills_connect.setImageResource(R.drawable.ic_signout);
                        tv_automatonPills_status.setText(String.format(getString(R.string.connected_automaton), network.getTypeName()));

                        TextView[] tvArray = {tv_automatonPills_plc, tv_automatonPills_service,
                                tv_automatonPills_bottlesComing, tv_automatonPills_wantedPills,
                                tv_automatonPills_pills, tv_automatonPills_bottles};

                        readS7 = new ReadPillsS7(v, tvArray);
                        readS7.Start(automatonName, automaton.getIp(), automaton.getRack(), automaton.getSlot());

                        if (btn_automatonPills_manage.getVisibility() == View.VISIBLE) {
                            writeS7 = new WritePillsS7();
                            writeS7.Start(automatonName, automaton.getIp(), automaton.getRack(), automaton.getSlot(), automaton.getDataBloc());
                        }

                    } else {
                        readS7.Stop();

                        fab_automatonPills_connect.setContentDescription(getString((R.string.login)));
                        fab_automatonPills_connect.setImageResource(R.drawable.ic_signin);
                        tv_automatonPills_status.setText(getString((R.string.pills)));

                        btn_automatonPills_manage.setContentDescription("hidden");
                        ll_automatonPills_manage.setVisibility(View.GONE);

                        if (btn_automatonPills_manage.getVisibility() == View.VISIBLE) writeS7.Stop();

                    }
                } else {
                    Toast.makeText(this, getString((R.string.impossible_connection)), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_automatonPills_manage:
                if (btn_automatonPills_manage.getContentDescription().equals("hidden") &&
                        fab_automatonPills_connect.getContentDescription().equals("Déconnexion")) {
                    btn_automatonPills_manage.setContentDescription("visible");
                    ll_automatonPills_manage.setVisibility(View.VISIBLE);
                } else {
                    btn_automatonPills_manage.setContentDescription("hidden");
                    ll_automatonPills_manage.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_modifyAutomaton_registerDBB5:
                if (et_automatonPills_DBB5.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString((R.string.empty_input)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString((R.string.created_user)), Toast.LENGTH_SHORT).show();
                    writeS7.setDBBBinary(5, et_automatonPills_DBB5.getText().toString());
                }
                break;
            case R.id.btn_modifyAutomaton_registerDBB6:
                if (et_automatonPills_DBB6.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString((R.string.empty_input)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString((R.string.created_user)), Toast.LENGTH_SHORT).show();
                    writeS7.setDBBBinary(6, et_automatonPills_DBB6.getText().toString());
                }
                break;
            case R.id.btn_modifyAutomaton_registerDBB7:
                if (et_automatonPills_DBB7.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString((R.string.empty_input)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString((R.string.created_user)), Toast.LENGTH_SHORT).show();
                    writeS7.setDBBBinary(7, et_automatonPills_DBB7.getText().toString());
                }
                break;
            case R.id.btn_modifyAutomaton_registerDBB8:
                if (et_automatonPills_DBB8.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString((R.string.empty_input)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString((R.string.created_user)), Toast.LENGTH_SHORT).show();
                    writeS7.setDBB8(et_automatonPills_DBB8.getText().toString());
                }
                break;
            case R.id.btn_modifyAutomaton_registerDBW18:
                if (et_automatonPills_DBW18.getText().toString().isEmpty()) {
                    Toast.makeText(this, getString((R.string.empty_input)), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString((R.string.created_user)), Toast.LENGTH_SHORT).show();
                    writeS7.setDBW18(et_automatonPills_DBW18.getText().toString());
                }
                break;
            case R.id.fab_automatonPills_logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.logout_title)
                        .setMessage(R.string.logout_message)
                        .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.logoutUser();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        })
                        .setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();

                break;
        }
    }
}

