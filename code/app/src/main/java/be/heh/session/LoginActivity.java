package be.heh.session;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

import be.heh.database.User;
import be.heh.database.UserAccessDB;
import be.heh.main.HomeActivity;
import be.heh.main.R;
import be.heh.main.SuperHomeActivity;

public class LoginActivity extends Activity {

    EditText et_login_email;
    EditText et_login_pwd;

    private Session session;

    private boolean isSuper;
    private String rights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login_email = findViewById(R.id.et_login_email);
        et_login_pwd   = findViewById(R.id.et_login_pwd);

        session = new Session(getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onLoginClickManager(View v) {

        switch (v.getId()) {
            case R.id.btn_login_register:

                Intent intentRegister = new Intent(this, RegisterActivity.class);
                startActivity(intentRegister);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.btn_login_login:

                UserAccessDB userDB = new UserAccessDB(this);
                userDB.openForWrite();
                ArrayList<User> tabUser = userDB.getAllUsers();
                userDB.Close();

                if (login(tabUser)) {

                    session.createUserLoginSession(et_login_email.getText().toString(), rights);

                    // Starting home activity
                    Intent intentHome = isSuper ?
                            new Intent(this, SuperHomeActivity.class) :
                            new Intent(this, HomeActivity.class);
                    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentHome);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    finish();
                } else
                    Toast.makeText(getApplicationContext(), R.string.no_user, Toast.LENGTH_LONG).show();

                break;
        }
    }

    /**
     * Log the user in if it's in the database.
     *
     * @param tabUser
     *      The arraylist containing the list of all users of the be.heh.database.
     * @return true if the user is known, false otherwise.
     */
    public boolean login(ArrayList<User> tabUser) {

        for (User user : tabUser) {
            if (isInDB(user)) {
                rights = user.getRights().toString();
                isSuper = user.isSuper();
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
    public boolean isInDB(User user) {
        Password pwd = new Password(et_login_pwd.getText().toString());
        return (user.getEmail().equals(et_login_email.getText().toString())
                && user.getPassword().equals(pwd.getGeneratedPassword()));
    }

}
