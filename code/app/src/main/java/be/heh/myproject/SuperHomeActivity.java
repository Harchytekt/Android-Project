package be.heh.myproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import be.heh.session.Session;
import db.UserAccessBDD;

public class SuperHomeActivity extends Activity {

    private Session session;
    Button btn_superHome_logout;
    TextView tv_superHome_title;
    TextView tv_superHome_email;
    TextView tv_superHome_users;
    FloatingActionButton fab_superHome_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_home);

        session = new Session(getApplicationContext());

        tv_superHome_title = findViewById(R.id.tv_superHome_title);
        tv_superHome_email = findViewById(R.id.tv_superHome_email);
        tv_superHome_users = findViewById(R.id.tv_superHome_users);

        fab_superHome_logout = findViewById(R.id.fab_superHome_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.getUserDetails();
        String email = user.get(Session.KEY_EMAIl);
        String rights = user.get(Session.KEY_RIGHTS);

        UserAccessBDD userDB = new UserAccessBDD(this);
        userDB.openForWrite();
        int nbUsers = userDB.getNumberOfUsers();
        String nbRUsers = userDB.getRUsers();
        String nbRWUsers = userDB.getRWUsers();
        userDB.Close();

        tv_superHome_email.setText(Html.fromHtml("<b>" + email + "</b> est connecté !"));
        tv_superHome_users.setText(Html.fromHtml("Il y a <b>"+ nbUsers +
                "</b> utilisateurs dont :<br><br><b>"+ nbRUsers +
                "</b> avec un accès en lecture seule;<br><b>" + nbRWUsers +
                "</b> avec un accès en lecture et écriture."));


        /*fab_superHome_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Déconnexion", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void onSuperHomeClickManager(View v) {

        switch (v.getId()) {
            case R.id.fab_superHome_logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Déconnexion")
                        .setMessage("Voulez-vous vraiment vous déconnecter ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.logoutUser();
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
