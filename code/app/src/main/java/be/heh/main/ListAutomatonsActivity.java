package be.heh.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import be.heh.database.Automaton;
import be.heh.database.AutomatonAccessDB;
import be.heh.database.User;
import be.heh.database.UserAccessDB;
import be.heh.session.Session;

public class ListAutomatonsActivity extends Activity {

    private Session session;

    ArrayList<Automaton> tabAutomaton;
    TextView tv_listAutomatons_email;
    AutomatonsAdapter adapter;
    ListView lv_listAutomatons_list;
    FloatingActionButton fab_listAutomatons_add;
    FloatingActionButton fab_listAutomatons_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_automatons);

        session = new Session(getApplicationContext());

        tv_listAutomatons_email = findViewById(R.id.tv_listAutomatons_email);
        lv_listAutomatons_list = findViewById(R.id.lv_listAutomatons_list);
        fab_listAutomatons_add = findViewById(R.id.fab_listAutomatons_add);
        fab_listAutomatons_logout = findViewById(R.id.fab_listAutomatons_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        HashMap<String, String> user = session.getUserDetails();

        AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
        automatonDB.openForWrite();
        tabAutomaton = automatonDB.getAllAutomatons();
        automatonDB.Close();

        tv_listAutomatons_email.setText(Html.fromHtml("Connecté en tant que '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));

        adapter = new AutomatonsAdapter(this, tabAutomaton);
        lv_listAutomatons_list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onListAutomatonsClickManager(View v) {
        switch (v.getId()) {
            case R.id.fab_listAutomatons_add:

                Toast.makeText(getApplicationContext(), "Ajouter un automate", Toast.LENGTH_LONG).show();

                break;
            case R.id.fab_listAutomatons_logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Déconnexion")
                        .setMessage("Voulez-vous vraiment vous déconnecter ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.logoutUser();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();

                break;
        }
    }
}
