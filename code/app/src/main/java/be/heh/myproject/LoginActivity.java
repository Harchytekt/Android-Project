package be.heh.myproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import BDD.User;
import BDD.UserAccessBDD;

public class LoginActivity extends Activity {

    EditText et_login_email;
    EditText et_login_pwd;
    boolean isSuper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login_email = findViewById(R.id.et_login_email);
        et_login_pwd   = findViewById(R.id.et_login_pwd);

        /*UserAccessBDD userDB = new UserAccessBDD(this);
        userDB.openForWrite();
        ArrayList<User> tabUser = userDB.getAllUser();
        userDB.Close();

        for (User u: tabUser) {
            //Password pwd = new Password(u.getPassword());
            Toast.makeText(getApplicationContext(), u.getPassword(), Toast.LENGTH_LONG).show();
        }*/

    }

    public void onLoginClickManager(View v) {
        // Récupérer la vue et accéder au bouton
        switch (v.getId()) {
            case R.id.btn_login_register:

                Intent intentRegister = new Intent(this, RegisterActivity.class); startActivity(intentRegister);

                break;
            case R.id.btn_login_login:

                UserAccessBDD userDB = new UserAccessBDD(this);
                userDB.openForWrite();
                ArrayList<User> tabUser = userDB.getAllUser();
                userDB.Close();

                if (login(tabUser)) {
                    Intent intentHome = isSuper ?
                            new Intent(this, SuperHomeActivity.class) :
                            new Intent(this, HomeActivity.class);
                    startActivity(intentHome);
                } else
                    Toast.makeText(getApplicationContext(), "⚠️ L'utilisateur n'existe pas ! ⚠", Toast.LENGTH_LONG).show();

                break;
        }
    }

    /**
     * Log the user in if it's in the database.
     *
     * @param tabUser
     *      The arraylist containing the list of all users of the database.
     * @return true if the user is known, false otherwise.
     */
    public boolean login(ArrayList<User> tabUser) {

        for (User user : tabUser) {
            if (isInBDD(user)) {
                isSuper = isSuper(user);
                return true;
            }
        }

        return false;
    }

    /**
     * Verify if the user is in the database.
     * @param user
     *      The user to test.
     * @return true if the credentials are corrects, false otherwise.
     */
    public boolean isInBDD(User user) {
        Password pwd = new Password(et_login_pwd.getText().toString());
        return (user.getEmail().equals(et_login_email.getText().toString())
                && user.getPassword().equals(pwd.getGeneratedPassword()));
    }

    /**
     * Verify if the user is a superuser.
     * @param user
     *      The user to test.
     * @return true if the user is android, false otherwise.
     */
    public boolean isSuper(User user) {
        return (user.getEmail().equals("android"));
    }

}
