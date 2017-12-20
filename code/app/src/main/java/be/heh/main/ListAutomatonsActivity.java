package be.heh.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import be.heh.models.Automaton;
import be.heh.database.AutomatonAccessDB;
import be.heh.models.CurrentAutomaton;
import be.heh.models.Session;

public class ListAutomatonsActivity extends Activity {

    private Session session;
    private CurrentAutomaton currentAutomaton;

    private ArrayList<Automaton> tabAutomaton;
    private TextView tv_listAutomatons_email;
    private AutomatonsAdapter adapter;
    private ListView lv_listAutomatons_list;

    FloatingActionButton fab_listAutomatons_add;
    FloatingActionButton fab_listAutomatons_logout;

    private Automaton automaton;
    private AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_automatons);

        session = new Session(getApplicationContext());
        currentAutomaton = new CurrentAutomaton(getApplicationContext());

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

        tv_listAutomatons_email.setText(Html.fromHtml(getString(R.string.connected_as) + " '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));

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
            case R.id.btn_automatonItem_seeIcon:

                position = (Integer) v.getTag();

                automaton = adapter.getItem(position);

                currentAutomaton.createCurrentAutomaton(automaton.getName());

                Intent intentAutomaton;

                if (automaton.getType().equals("0"))
                    intentAutomaton = new Intent(this, AutomatonPillsActivity.class);
                else
                    intentAutomaton = new Intent(this, AutomatonLiquidActivity.class);

                startActivity(intentAutomaton);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.btn_automatonItem_editIcon:

                Toast.makeText(getApplicationContext(), "Modifier l'automate", Toast.LENGTH_LONG).show();

                break;
            case R.id.btn_automatonItem_removeIcon:

                position = (Integer) v.getTag();

                automaton = adapter.getItem(position);
                createRemoveAutomatonDialog();

                break;
            case R.id.fab_listAutomatons_add:

                Toast.makeText(getApplicationContext(), "Ajouter un automate", Toast.LENGTH_LONG).show();

                break;
            case R.id.fab_listAutomatons_logout:

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

    public void createRemoveAutomatonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.delete_title)
                .setMessage(R.string.delete_automaton_message)
                .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        automatonDB.openForWrite();
                        automatonDB.removeAutomaton(automaton.getName());
                        tabAutomaton = automatonDB.getAllAutomatons();
                        adapter.clear();
                        adapter.addAll(tabAutomaton);
                        automatonDB.Close();
                    }
                })
                .setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }
}
