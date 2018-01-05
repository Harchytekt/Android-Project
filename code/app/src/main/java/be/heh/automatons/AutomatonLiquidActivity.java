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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import be.heh.SimaticS7.S7Client;
import be.heh.database.AutomatonAccessDB;
import be.heh.main.R;
import be.heh.models.Automaton;
import be.heh.models.CurrentAutomaton;
import be.heh.models.Session;

public class AutomatonLiquidActivity extends Activity {

    private Session session;
    private CurrentAutomaton currentAutomaton;
    String automatonName;
    Automaton automaton;

    FloatingActionButton fab_automatonLiquid_connect;
    FloatingActionButton fab_automatonLiquid_logout;

    TextView tv_automatonLiquid_connected;
    TextView tv_automatonLiquid_status;
    TextView tv_automatonLiquid_plc;

    TextView tv_automatonLiquid_valveMA;
    TextView tv_automatonLiquid_valves12;
    TextView tv_automatonLiquid_valves34;
    TextView tv_automatonLiquid_level;
    TextView tv_automatonLiquid_consignes;
    TextView tv_automatonLiquid_pilotageVanne;

    Button btn_automatonLiquid_manage;

    LinearLayout ll_automatonLiquid_manage;

    private NetworkInfo network;
    private ConnectivityManager connexStatus;
    private ReadLiquidS7 readS7;
    private WritePillsS7 writeS7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automaton_liquid);

        session = new Session(getApplicationContext());
        currentAutomaton = new CurrentAutomaton(getApplicationContext());

        tv_automatonLiquid_connected = findViewById(R.id.tv_automatonLiquid_connected);
        tv_automatonLiquid_status = findViewById(R.id.tv_automatonLiquid_status);
        tv_automatonLiquid_plc = findViewById(R.id.tv_automatonLiquid_plc);

        tv_automatonLiquid_valveMA = findViewById(R.id.tv_automatonLiquid_valveMA);
        tv_automatonLiquid_valves12 = findViewById(R.id.tv_automatonLiquid_valves12);
        tv_automatonLiquid_valves34 = findViewById(R.id.tv_automatonLiquid_valves34);
        tv_automatonLiquid_level = findViewById(R.id.tv_automatonLiquid_level);
        tv_automatonLiquid_consignes = findViewById(R.id.tv_automatonLiquid_consignes);
        tv_automatonLiquid_pilotageVanne = findViewById(R.id.tv_automatonLiquid_pilotageVanne);


        fab_automatonLiquid_connect = findViewById(R.id.fab_automatonLiquid_connect);
        fab_automatonLiquid_logout = findViewById(R.id.fab_automatonLiquid_logout);

        btn_automatonLiquid_manage = findViewById(R.id.btn_automatonLiquid_manage);

        ll_automatonLiquid_manage = findViewById(R.id.ll_automatonLiquid_manage);

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

        tv_automatonLiquid_connected.setText(Html.fromHtml(getString(R.string.connected_as) + " '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));

        tv_automatonLiquid_plc.setText(Html.fromHtml(automatonName + "<br>" + getString(R.string.not_connected)));
        if (user.get(Session.KEY_RIGHTS).equals("1")) btn_automatonLiquid_manage.setVisibility(View.VISIBLE);

        tv_automatonLiquid_valveMA.setText(String.format(getString(R.string.liquid_valveMA), "?"));
        tv_automatonLiquid_valves12.setText(String.format(getString(R.string.liquid_valves12), "?", "?"));
        tv_automatonLiquid_valves34.setText(String.format(getString(R.string.liquid_valves34), "?", "?"));
        tv_automatonLiquid_level.setText(String.format(getString(R.string.liquid_level), "?"));
        tv_automatonLiquid_consignes.setText(String.format(getString(R.string.liquid_consignes), "?", "?"));
        tv_automatonLiquid_pilotageVanne.setText(String.format(getString(R.string.liquid_pilotageVanne), "?"));

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

    public void onAutomatonLiquidClickManager(View v) {

        switch (v.getId()) {
            case R.id.fab_automatonLiquid_connect:

                connexStatus = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connexStatus != null) {
                    network = connexStatus.getActiveNetworkInfo();
                }

                if (network != null && network.isConnectedOrConnecting()) {
                    if (fab_automatonLiquid_connect.getContentDescription().equals(getString((R.string.login)))) {

                        fab_automatonLiquid_connect.setContentDescription(getString(R.string.logout_title));
                        fab_automatonLiquid_connect.setImageResource(R.drawable.ic_signout);
                        tv_automatonLiquid_status.setText(String.format(getString(R.string.connected_automaton), network.getTypeName()));

                        TextView[] tvArray = {tv_automatonLiquid_plc, tv_automatonLiquid_valveMA,
                                tv_automatonLiquid_valves12, tv_automatonLiquid_valves34,
                                tv_automatonLiquid_level, tv_automatonLiquid_consignes,
                                tv_automatonLiquid_pilotageVanne };

                        readS7 = new ReadLiquidS7(v, tvArray);
                        readS7.Start(automatonName, automaton.getIp(), automaton.getRack(), automaton.getSlot());

                        if (btn_automatonLiquid_manage.getVisibility() == View.VISIBLE) {
                            writeS7 = new WritePillsS7();
                            writeS7.Start(automatonName, automaton.getIp(), automaton.getRack(), automaton.getSlot(), automaton.getDataBloc());
                        }

                    } else {
                        readS7.Stop();

                        fab_automatonLiquid_connect.setContentDescription(getString((R.string.login)));
                        fab_automatonLiquid_connect.setImageResource(R.drawable.ic_signin);
                        tv_automatonLiquid_status.setText(getString((R.string.liquid)));

                        btn_automatonLiquid_manage.setContentDescription("hidden");
                        ll_automatonLiquid_manage.setVisibility(View.GONE);

                        if (btn_automatonLiquid_manage.getVisibility() == View.VISIBLE) writeS7.Stop();

                    }
                } else {
                    Toast.makeText(this, getString((R.string.impossible_connection)), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_automatonLiquid_manage:
                if (btn_automatonLiquid_manage.getContentDescription().equals("hidden") &&
                        fab_automatonLiquid_connect.getContentDescription().equals("Déconnexion")) {
                    btn_automatonLiquid_manage.setContentDescription("visible");
                    ll_automatonLiquid_manage.setVisibility(View.VISIBLE);
                } else {
                    btn_automatonLiquid_manage.setContentDescription("hidden");
                    ll_automatonLiquid_manage.setVisibility(View.GONE);
                }
                break;
            case R.id.fab_automatonLiquid_logout:

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

