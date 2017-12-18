package be.heh.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import be.heh.models.Password;
import be.heh.models.User;
import be.heh.database.UserAccessDB;
import be.heh.main.R;

public class RegisterActivity extends Activity {

    EditText et_register_lastname;
    EditText et_register_firstname;
    EditText et_register_email;
    EditText et_register_pwd;

    private TextWatcher lastname;
    private TextWatcher firstname;
    private TextWatcher email;
    private TextWatcher password;

    private boolean validLastname;
    private boolean validFirstname;
    private boolean validPassword;
    private boolean validEmail;

    UserAccessDB userDB = new UserAccessDB(this);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onRegisterClickManager(View v) {
        // Récupérer la vue et accéder au bouton
        switch (v.getId()) {
            case R.id.btn_register_login:

                Intent intentMain = new Intent(this, LoginActivity.class);
                startActivity(intentMain);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.btn_register_register:

                if (et_register_lastname.getText().toString().isEmpty() ||
                        et_register_firstname.getText().toString().isEmpty() ||
                        et_register_email.getText().toString().isEmpty() ||
                        et_register_pwd.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), R.string.empty_input, Toast.LENGTH_LONG).show();
                else if (!isValid())
                    Toast.makeText(getApplicationContext(), R.string.error_input, Toast.LENGTH_LONG).show();
                else {
                    Password pwd = new Password(et_register_pwd.getText().toString());

                    User user1 = new User(et_register_lastname.getText().toString(),
                            et_register_firstname.getText().toString(),
                            pwd.getGeneratedPassword(), et_register_email.getText().toString());

                    UserAccessDB userDB = new UserAccessDB(this);
                    userDB.openForWrite();
                    userDB.insertUser(user1);
                    userDB.Close();

                    Toast.makeText(getApplicationContext(), R.string.created_user, Toast.LENGTH_LONG).show();

                    Intent intentLogin = new Intent(this, LoginActivity.class);
                    startActivity(intentLogin);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

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
                if (!Pattern.matches("^(([A-z][a-z]+[\\s|-]{1}[A-z][a-z]+)|([A-Z][a-z]+))$", et_register_lastname.getText().toString())) {
                    et_register_lastname.setError(getString(R.string.wrong_format));
                    validLastname = false;
                } else {
                    validLastname = true;
                }
            }
        };

        firstname = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.matches("^(([A-z][a-z]+[\\s|-]{1}[A-z][a-z]+)|([A-Z][a-z]+))$", et_register_firstname.getText().toString())) {
                    et_register_firstname.setError(getString(R.string.wrong_format));
                    validFirstname = false;
                } else {
                    validFirstname = true;
                }
            }
        };

        email = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidEmail(et_register_email.getText().toString())) {
                    et_register_email.setError(getString(R.string.wrong_email_format));
                    validEmail = false;
                } else {
                    validEmail = true;
                }

                userDB.openForWrite();
                if (userDB.isAlreadyUsed(et_register_email.getText().toString())) {
                    et_register_email.setError(getString(R.string.already_used_email));
                    userDB.Close();
                    validEmail = false;
                } else {
                    validEmail = true;
                }
            }
        };

        password = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{4,16}$", et_register_pwd.getText().toString())) {
                    et_register_pwd.setError(getString(R.string.password_format));
                    validPassword = false;
                } else {
                    validPassword = true;
                }
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

    public boolean isValid() {
        return validLastname && validFirstname && validPassword && validEmail;
    }

}
