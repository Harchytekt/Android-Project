package be.heh.main;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import be.heh.session.Session;
import db.User;
import db.UserAccessBDD;

public class ListUsersActivity extends Activity {

    private Session session;
    ArrayList<User> tabUser;
    TextView tv_listUsers_email;
    UsersAdapter adapter;
    ListView lv_listUsers_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        session = new Session(getApplicationContext());

        tv_listUsers_email = findViewById(R.id.tv_listUsers_email);
        lv_listUsers_list = findViewById(R.id.lv_listUsers_list);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.getUserDetails();

        UserAccessBDD userDB = new UserAccessBDD(this);
        userDB.openForWrite();
        tabUser = userDB.getAllUser();
        userDB.Close();

        tv_listUsers_email.setText(Html.fromHtml("Connecté en tant que '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));

        adapter = new UsersAdapter(this, tabUser);
        lv_listUsers_list.setAdapter(adapter);

    }

    public void onListUsersClickManager(View v) {
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

    /*public void listUsers() {

    }*/
}
