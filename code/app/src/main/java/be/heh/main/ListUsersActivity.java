package be.heh.main;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import be.heh.database.UserAccessDB;
import be.heh.session.Password;
import be.heh.session.Session;
import be.heh.database.User;

public class ListUsersActivity extends Activity {

    private Session session;

    ArrayList<User> tabUser;
    TextView tv_listUsers_email;
    UsersAdapter adapter;
    ListView lv_listUsers_list;
    FloatingActionButton fab_listUsers_logout;


    CharSequence[] rights = {" Lecture seule "," Lecture-Écriture "};
    AlertDialog rightsDialog;
    User user;
    UserAccessDB userDB = new UserAccessDB(this);
    int position;
    Password pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        session = new Session(getApplicationContext());

        tv_listUsers_email = findViewById(R.id.tv_listUsers_email);
        lv_listUsers_list = findViewById(R.id.lv_listUsers_list);
        fab_listUsers_logout = findViewById(R.id.fab_listUsers_logout);

        // If not logged in, redirection to LoginActivity
        if (session.checkLogin()) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        HashMap<String, String> user = session.getUserDetails();

        UserAccessDB userDB = new UserAccessDB(this);
        userDB.openForWrite();
        tabUser = userDB.getAllUsers();
        userDB.Close();

        tv_listUsers_email.setText(Html.fromHtml("Connecté en tant que '<b>" + user.get(Session.KEY_EMAIl) + "</b>'."));

        adapter = new UsersAdapter(this, tabUser);
        lv_listUsers_list.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onListUsersClickManager(View v) {
        switch (v.getId()) {
            case R.id.btn_userItem_passwordIcon:

                createPasswordDialog();

                break;
            case R.id.btn_userItem_rightsIcon:
                position = (Integer) v.getTag();

                user = adapter.getItem(position);
                createRightsDialog(Integer.parseInt(user.getRights()) % 2);
                break;
            case R.id.btn_userItem_removeIcon:
                position = (Integer) v.getTag();

                user = adapter.getItem(position);

                createRemoveUserDialog();

                break;
            case R.id.fab_listUsers_logout:

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

    public void createPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputPassword = new EditText(this);
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setTitle("Modifier le mot de passe")
                .setMessage("Veuillez entrer votre nouveau mot de passe")
                .setView(inputPassword)
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pwd = new Password(inputPassword.getText().toString());

                        userDB.openForWrite();

                        userDB.updateUserPassword(user.getId(), pwd.getGeneratedPassword());

                        tabUser = userDB.getAllUsers();
                        adapter.clear();
                        adapter.addAll(tabUser);
                        userDB.Close();
                        System.out.println(inputPassword.getText().toString());
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    public void createRightsDialog(int checked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Modifier les droits");
        builder.setSingleChoiceItems(rights, checked, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {


                userDB.openForWrite();

                switch(item) {
                    case 0:
                        userDB.updateUserRights(user.getId(), "2");
                        break;
                    case 1:
                        userDB.updateUserRights(user.getId(), "1");
                        break;
                }
                tabUser = userDB.getAllUsers();
                adapter.clear();
                adapter.addAll(tabUser);
                userDB.Close();
                rightsDialog.dismiss();
            }
        });
        rightsDialog = builder.create();
        rightsDialog.show();
    }

    public void createRemoveUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Suppression")
                .setMessage("Voulez-vous vraiment supprimer l'utilisateur ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userDB.openForWrite();
                        userDB.removeUser(user.getEmail());
                        tabUser = userDB.getAllUsers();
                        adapter.clear();
                        adapter.addAll(tabUser);
                        userDB.Close();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }
}
