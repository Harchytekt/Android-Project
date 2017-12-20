package be.heh.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.HashMap;

import be.heh.database.AutomatonAccessDB;
import be.heh.models.Session;

public class HomeActivity extends Activity {

    private Session session;
    FloatingActionButton fab_home_logout;
    TextView tv_home_email;
    TextView tv_home_automatons;
    Button btn_home_seeAutomatons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new Session(getApplicationContext());

        tv_home_email = findViewById(R.id.tv_home_email);
        tv_home_automatons = findViewById(R.id.tv_home_automatons);
        btn_home_seeAutomatons = findViewById(R.id.btn_home_seeAutomatons);

        fab_home_logout = findViewById(R.id.fab_home_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        HashMap<String, String> user = session.getUserDetails();

        AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
        automatonDB.openForWrite();
        int nbAutomatons = automatonDB.getNumberOfAutomatons();
        String nbPills = automatonDB.getPills();
        String nbLiquids = automatonDB.getLiquids();
        automatonDB.Close();

        tv_home_email.setText(Html.fromHtml(getString(R.string.connected_as) + " '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));
        tv_home_automatons.setText(Html.fromHtml(getString(R.string.there_is) + " <b>"+ nbAutomatons +
                "</b> " + getString(R.string.home_users_text1) + "<br><br>" +
                "<b>"+ nbPills + "</b> " + getString(R.string.home_users_text2) + " <i>" + getString(R.string.pills) + "</i>;<br>" +
                "<b>" + nbLiquids + "</b> " + getString(R.string.home_users_text3) + "<i>" + getString(R.string.liquid) + "</i>."));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
        automatonDB.openForWrite();
        int nbAutomatons = automatonDB.getNumberOfAutomatons();
        String nbPills = automatonDB.getPills();
        String nbLiquids = automatonDB.getLiquids();
        automatonDB.Close();

        tv_home_automatons.setText(Html.fromHtml(getString(R.string.there_is) + " <b>"+ nbAutomatons +
                "</b> " + getString(R.string.home_users_text1) + "<br><br>" +
                "<b>"+ nbPills + "</b> " + getString(R.string.home_users_text2) + " <i>" + getString(R.string.pills) + "</i>;<br>" +
                "<b>" + nbLiquids + "</b> " + getString(R.string.home_users_text3) + "<i>" + getString(R.string.liquid) + "</i>."));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onHomeClickManager(View v) {

        switch (v.getId()) {
            case R.id.btn_home_seeAutomatons:
                Intent intentListAutomatons = new Intent(this, ListAutomatonsActivity.class);
                startActivity(intentListAutomatons);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.fab_home_logout:

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

