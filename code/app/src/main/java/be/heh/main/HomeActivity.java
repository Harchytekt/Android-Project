package be.heh.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import java.util.HashMap;

import be.heh.session.Session;

public class HomeActivity extends Activity {

    private Session session;
    FloatingActionButton fab_home_logout;
    TextView tv_home_email;
    TextView tv_home_automatons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new Session(getApplicationContext());

        tv_home_email = findViewById(R.id.tv_home_email);
        tv_home_automatons = findViewById(R.id.tv_home_automatons);

        fab_home_logout = findViewById(R.id.fab_home_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.getUserDetails();

        tv_home_email.setText(Html.fromHtml("Connecté en tant que '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));
        tv_home_automatons.setText(Html.fromHtml("Vous avez <b>"+ "0" +
                "</b> automates dont :<br><br>" +
                "<b>"+ "0" + "</b> pour le <i>conditionnement de comprimés</i>;<br>" +
                "<b>" + "0" + "</b> pour l'<i>asservissement de niveau de liquide</i>."));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session.checkLogin())
            finish();

        /*UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForWrite();
        int nbUsers = userDB.getNumberOfUsers();
        String nbRUsers = userDB.getRUsers();
        String nbRWUsers = userDB.getRWUsers();
        userDB.Close();*/

        tv_home_automatons.setText(Html.fromHtml("Vous avez <b>"+ "0" +
                "</b> automates dont :<br><br>" +
                "<b>"+ "0" + "</b> pour le <i>conditionnement de comprimés</i>;<br>" +
                "<b>" + "0" + "</b> pour l'<i>asservissement de niveau de liquide</i>."));

    }

    public void onHomeClickManager(View v) {

        switch (v.getId()) {
            case R.id.fab_home_logout:

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

