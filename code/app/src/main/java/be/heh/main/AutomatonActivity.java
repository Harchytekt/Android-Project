package be.heh.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import be.heh.database.AutomatonAccessDB;
import be.heh.models.Session;

public class AutomatonActivity extends Activity {

    private Session session;
    FloatingActionButton fab_automaton_connect;
    FloatingActionButton fab_automaton_logout;

    TextView tv_automaton_email;
    TextView tv_automaton_status;
    TextView tv_automaton_plc;

    private NetworkInfo network;
    private ConnectivityManager connexStatus;
    private ReadTaskS7 readS7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automaton);

        session = new Session(getApplicationContext());

        tv_automaton_email = findViewById(R.id.tv_automaton_email);
        tv_automaton_status = findViewById(R.id.tv_automaton_status);
        tv_automaton_plc = findViewById(R.id.tv_automaton_plc);

        fab_automaton_connect = findViewById(R.id.fab_automaton_connect);
        fab_automaton_logout = findViewById(R.id.fab_automaton_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        HashMap<String, String> user = session.getUserDetails();

        tv_automaton_email.setText(Html.fromHtml(getString(R.string.connected_as) + " '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onAutomatonClickManager(View v) {

        switch (v.getId()) {
            case R.id.fab_automaton_connect:

                connexStatus = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connexStatus != null) {
                    network = connexStatus.getActiveNetworkInfo();
                }

                if (network != null && network.isConnectedOrConnecting()) {
                    if (fab_automaton_connect.getContentDescription().equals("Connexion")) {

                        fab_automaton_connect.setContentDescription("Déconnexion");
                        fab_automaton_connect.setImageResource(R.drawable.ic_signout);
                        tv_automaton_status.setText("Connecté par " + network.getTypeName() + " à l'automate.");

                        readS7 = new ReadTaskS7(v, tv_automaton_plc);
                        readS7.Start("192.168.10.130", "0", "2");

                        /*ln_main_ecrireS7.setVisibility(View.VISIBLE);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        writeS7 = new WriteTaskS7();
                        //writeS7.Start("10.1.0.110", "0", "1");*/

                    } else {
                        readS7.Stop();

                        fab_automaton_connect.setContentDescription("Connexion");
                        fab_automaton_connect.setImageResource(R.drawable.ic_signin);
                        tv_automaton_status.setText("Déconnecté de l'automate.");

                        Toast.makeText(getApplication(), "Traitement interrompu par l'utilisateur !!!", Toast.LENGTH_SHORT).show();

                        /*try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        writeS7.Stop();
                        ln_main_ecrireS7.setVisibility(View.INVISIBLE);*/

                    }
                } else {
                    Toast.makeText(this, "! Connexion réseau IMPOSSIBLE !", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.fab_automaton_logout:

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

