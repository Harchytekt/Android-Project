package be.heh.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import be.heh.databases.User;
import be.heh.databases.UserAccessDB;
import be.heh.session.Session;

public class ListAutomatonsActivity extends Activity {

    private Session session;

    ArrayList<User> tabUser;
    TextView tv_listAutomatons_email;
    FloatingActionButton fab_listAutomatons_add;
    FloatingActionButton fab_listAutomatons_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_automatons);

        session = new Session(getApplicationContext());

        tv_listAutomatons_email = findViewById(R.id.tv_listAutomatons_email);
        fab_listAutomatons_add = findViewById(R.id.fab_listAutomatons_add);
        fab_listAutomatons_logout = findViewById(R.id.fab_listAutomatons_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.getUserDetails();

        UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForWrite();
        tabUser = userDB.getAllUser();
        userDB.Close();

        tv_listAutomatons_email.setText(Html.fromHtml("Connecté en tant que '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));
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
