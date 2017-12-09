package be.heh.session;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import db.User;
import db.UserAccessBDD;
import be.heh.myproject.R;

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

    UserAccessBDD userDB = new UserAccessBDD(this);

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

                if (et_register_lastname.getText().toString().isEmpty() ||
                        et_register_firstname.getText().toString().isEmpty() ||
                        et_register_email.getText().toString().isEmpty() ||
                        et_register_pwd.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "⚠️️ Veuillez compléter tous les champs ! ⚠️ ", Toast.LENGTH_LONG).show();
                else if (!isValid())
                    Toast.makeText(getApplicationContext(), "⚠️️ Veuillez vérifier les champs erronés ! ⚠️ ", Toast.LENGTH_LONG).show();
                else {
                    Password pwd = new Password(et_register_pwd.getText().toString());

                    User user1 = new User(et_register_lastname.getText().toString(),
                            et_register_firstname.getText().toString(),
                            pwd.getGeneratedPassword(), et_register_email.getText().toString());

                    UserAccessBDD userDB = new UserAccessBDD(this);
                    userDB.openForWrite();
                    userDB.insertUser(user1);
                    userDB.Close();

                    Toast.makeText(getApplicationContext(), "✔️ L'utilisateur a bien été créé ! ✔️ ", Toast.LENGTH_LONG).show();

                    Intent intentLogin = new Intent(this, LoginActivity.class); startActivity(intentLogin);
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
                    et_register_lastname.setError("Format incorrect !");
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
                    et_register_firstname.setError("Format incorrect !");
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
                    et_register_email.setError("Format d'email incorrect !");
                    validEmail = false;
                } else {
                    validEmail = true;
                }

                userDB.openForWrite();
                if (userDB.isAlreadyUsed(et_register_email.getText().toString())) {
                    et_register_email.setError("Cette adresse est déjà utilisée !");
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
                    et_register_pwd.setError("Minimum 4 caractères, au moins 1 majuscule, 1 minuscule et 1 chiffre.");
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
