package be.heh.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;

import java.util.HashMap;

import be.heh.database.UserAccessDB;
import be.heh.session.Session;

public class SuperHomeActivity extends Activity {

    private Session session;
    TextView tv_superHome_email;
    TextView tv_superHome_users;
    FloatingActionButton fab_superHome_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_home);

        session = new Session(getApplicationContext());

        tv_superHome_email = findViewById(R.id.tv_superHome_email);
        tv_superHome_users = findViewById(R.id.tv_superHome_users);

        fab_superHome_logout = findViewById(R.id.fab_superHome_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        HashMap<String, String> user = session.getUserDetails();

        UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForWrite();
        int nbUsers = userDB.getNumberOfUsers();
        String nbRUsers = userDB.getRUsers();
        String nbRWUsers = userDB.getRWUsers();
        userDB.Close();

        tv_superHome_email.setText(Html.fromHtml(getString(R.string.connected_as) + " '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));
        tv_superHome_users.setText(Html.fromHtml(getString(R.string.there_is) + " <b>"+ nbUsers +
                "</b> " + getString(R.string.superHome_users_text1) + "<br><br>" +
                "<b>"+ nbRUsers + "</b> " + getString(R.string.superHome_users_text2) + "<br>" +
                "<b>" + nbRWUsers + "</b> " + getString(R.string.superHome_users_text3)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForWrite();
        int nbUsers = userDB.getNumberOfUsers();
        String nbRUsers = userDB.getRUsers();
        String nbRWUsers = userDB.getRWUsers();
        userDB.Close();

        tv_superHome_users.setText(Html.fromHtml(getString(R.string.there_is) + " <b>"+ nbUsers +
                "</b> " + getString(R.string.superHome_users_text1) + "<br><br>" +
                "<b>"+ nbRUsers + "</b> " + getString(R.string.superHome_users_text2) + "<br>" +
                "<b>" + nbRWUsers + "</b> " + getString(R.string.superHome_users_text3)));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onSuperHomeClickManager(View v) {

        switch (v.getId()) {
            case R.id.btn_superHome_users:
                Intent intentListUsers = new Intent(this, ListUsersActivity.class);
                startActivity(intentListUsers);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.fab_superHome_logout:

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
