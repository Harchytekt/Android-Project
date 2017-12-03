package be.heh.myproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import BDD.User;
import BDD.UserAccessBDD;

public class RegisterActivity extends Activity {

    EditText et_register_lastname;
    EditText et_register_firstname;
    EditText et_register_email;
    EditText et_register_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_register_lastname = (EditText)findViewById(R.id.et_register_lastname);
        et_register_firstname = (EditText)findViewById(R.id.et_register_firstname);
        et_register_email = (EditText)findViewById(R.id.et_register_email);
        et_register_pwd = (EditText)findViewById(R.id.et_register_pwd);
    }

    public void onRegisterClickManager(View v) {
        // Récupérer la vue et accéder au bouton
        switch (v.getId()) {
            case R.id.btn_register_login:

                Intent intentMain = new Intent(this, LoginActivity.class); startActivity(intentMain);

                break;
            case R.id.btn_register_register:
                /*Toast.makeText(getApplicationContext()
                        ,"Lastname : " + et_register_lastname.getText() + "\nFirstname : " + et_register_firstname.getText() + "\nEmail : " + et_register_email.getText() + "\nPassword : " + et_register_pwd.getText()
                        , Toast.LENGTH_SHORT)
                        .show();*/

                String str = et_register_lastname.getText().toString() + "#" +
                        et_register_firstname.getText().toString() + "#" +
                        et_register_pwd.getText().toString() + "#" +
                        et_register_email.getText().toString()+ "#";

                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();

                User user1 = new User(et_register_lastname.getText().toString(),
                        et_register_firstname.getText().toString(),
                        et_register_pwd.getText().toString(), et_register_email.getText().toString());

                UserAccessBDD userDB = new UserAccessBDD(this);
                userDB.openForWrite();
                userDB.insertUser(user1);
                userDB.Close();

                break;
        }
    }
}
