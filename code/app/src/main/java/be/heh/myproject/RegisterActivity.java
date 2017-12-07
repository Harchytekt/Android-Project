package be.heh.myproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private TextWatcher lastname;
    private TextWatcher firstname;
    private TextWatcher email;
    private TextWatcher password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_register_lastname = findViewById(R.id.et_register_lastname);
        et_register_firstname = findViewById(R.id.et_register_firstname);
        et_register_email = findViewById(R.id.et_register_email);
        et_register_pwd = findViewById(R.id.et_register_pwd);

        initValidation();

        et_register_lastname.addTextChangedListener(lastname);
        et_register_firstname.addTextChangedListener(firstname);
        et_register_email.addTextChangedListener(email);
        et_register_pwd.addTextChangedListener(password);

    }

    public void onRegisterClickManager(View v) {
        // Récupérer la vue et accéder au bouton
        switch (v.getId()) {
            case R.id.btn_register_login:

                Intent intentMain = new Intent(this, LoginActivity.class); startActivity(intentMain);

                break;
            case R.id.btn_register_register:

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

    private void initValidation() {
        lastname = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
            if ( et_register_lastname.getText().toString().length() <= 2 )
                et_register_lastname.setError("Taille minimale de 2 caractères !");
            }
        };

        firstname = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if ( et_register_firstname.getText().toString().length() <= 2 )
                    et_register_firstname.setError("Taille minimale de 2 caractères !");
            }
        };

        email = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidEmail(et_register_email.getText().toString()))
                    et_register_email.setError("Mauvais format");
            }
        };

        password = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (et_register_pwd.getText().toString().length() <= 4)
                    et_register_pwd.setError("Longueur minimale de 4 caractères");
            }
        };
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
