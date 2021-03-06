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

import java.util.ArrayList;
import java.util.HashMap;

import be.heh.automatons.AutomatonLiquidActivity;
import be.heh.automatons.AutomatonPillsActivity;
import be.heh.models.AutomatonsAdapter;
import be.heh.models.Automaton;
import be.heh.database.AutomatonAccessDB;
import be.heh.models.CurrentAutomaton;
import be.heh.models.Session;

/**
 * This class creates the activity listing the automatons.
 *
 * @author DUCOBU Alexandre
 */
public class ListAutomatonsActivity extends Activity {

    private Session session;
    private CurrentAutomaton currentAutomaton;

    private ArrayList<Automaton> tabAutomaton;
    private TextView tv_listAutomatons_connected;
    private AutomatonsAdapter adapter;
    private ListView lv_listAutomatons_list;

    FloatingActionButton fab_listAutomatons_add;
    FloatingActionButton fab_listAutomatons_logout;

    private Automaton automaton;
    private AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
    private int position;

    /**
     * Method called on the activity creation.
     * It initializes all the variable, etc.
     *
     * @param savedInstanceState
     *      The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_automatons);

        session = new Session(getApplicationContext());
        currentAutomaton = new CurrentAutomaton(getApplicationContext());

        tv_listAutomatons_connected = findViewById(R.id.tv_listAutomatons_connected);
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

        tv_listAutomatons_connected.setText(Html.fromHtml(getString(R.string.connected_as) + " '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));

        adapter = new AutomatonsAdapter(this, tabAutomaton);
        lv_listAutomatons_list.setAdapter(adapter);
    }

    /**
     * Method called on resume.
     * It's used to return to the login activity if the user is no longer connected.
     * And updates the displayed values.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
        automatonDB.openForWrite();
        tabAutomaton = automatonDB.getAllAutomatons();
        automatonDB.Close();

        adapter = new AutomatonsAdapter(this, tabAutomaton);
        lv_listAutomatons_list.setAdapter(adapter);

    }

    /**
     * Method called when the 'Back' button is pressed.
     * It's used to change the animation of the activity appearance.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * This is the method managing the actions linked to the buttons of this activity.
     *
     * @param v
     *      The view of the current activity.
     */
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

                position = (Integer) v.getTag();

                automaton = adapter.getItem(position);

                currentAutomaton.createCurrentAutomaton(automaton.getName());

                Intent intentModifyAutomaton = new Intent(this, ModifyAutomatonActivity.class);
                startActivity(intentModifyAutomaton);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //Toast.makeText(getApplicationContext(), "Modifier l'automate", Toast.LENGTH_LONG).show();

                break;
            case R.id.btn_automatonItem_removeIcon:

                position = (Integer) v.getTag();

                automaton = adapter.getItem(position);
                createRemoveAutomatonDialog();

                break;
            case R.id.fab_listAutomatons_add:

                Intent intentRegisterAutomaton = new Intent(this, RegisterAutomatonActivity.class);
                startActivity(intentRegisterAutomaton);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                //Toast.makeText(getApplicationContext(), "Ajouter un automate", Toast.LENGTH_LONG).show();

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

    /**
     * Create an alert dialog to remove the current automaton.
     * It'll ask for a confirmation before removing the automaton.
     */
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
