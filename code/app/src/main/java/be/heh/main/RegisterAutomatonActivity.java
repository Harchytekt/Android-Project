package be.heh.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.regex.Pattern;

import be.heh.database.AutomatonAccessDB;
import be.heh.models.Automaton;

public class RegisterAutomatonActivity extends Activity {

    EditText et_registerAutomaton_name;
    EditText et_registerAutomaton_ip;
    EditText et_registerAutomaton_rack;
    EditText et_registerAutomaton_slot;
    EditText et_registerAutomaton_databloc;
    Spinner sp_registerAutomaton_type;

    private TextWatcher name;
    private TextWatcher ip;
    private TextWatcher rack;
    private TextWatcher slot;
    private TextWatcher dataBloc;

    private boolean validName;
    private boolean validIp;
    private boolean validRack;
    private boolean validSlot;
    private boolean validDataBloc;

    AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_automaton);

        et_registerAutomaton_name = findViewById(R.id.et_registerAutomaton_name);
        et_registerAutomaton_ip = findViewById(R.id.et_registerAutomaton_ip);
        et_registerAutomaton_rack = findViewById(R.id.et_registerAutomaton_rack);
        et_registerAutomaton_slot = findViewById(R.id.et_registerAutomaton_slot);
        et_registerAutomaton_databloc = findViewById(R.id.et_registerAutomaton_databloc);
        sp_registerAutomaton_type = findViewById(R.id.sp_registerAutomaton_type);

        initValidation();

        et_registerAutomaton_name.addTextChangedListener(name);
        et_registerAutomaton_ip.addTextChangedListener(ip);
        et_registerAutomaton_rack.addTextChangedListener(rack);
        et_registerAutomaton_slot.addTextChangedListener(slot);
        et_registerAutomaton_databloc.addTextChangedListener(dataBloc);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onRegisterAutomatonClickManager(View v) {
        // Récupérer la vue et accéder au bouton
        switch (v.getId()) {
            case R.id.btn_registerAutomaton_cancel:

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.btn_registerAutomaton_register:

                if (et_registerAutomaton_name.getText().toString().isEmpty() ||
                        et_registerAutomaton_ip.getText().toString().isEmpty() ||
                        et_registerAutomaton_rack.getText().toString().isEmpty() ||
                        et_registerAutomaton_slot.getText().toString().isEmpty() ||
                        et_registerAutomaton_databloc.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), R.string.empty_input, Toast.LENGTH_LONG).show();
                else if (!isValid())
                    Toast.makeText(getApplicationContext(), R.string.error_input, Toast.LENGTH_LONG).show();
                else {
                    Automaton automaton = new Automaton(
                            et_registerAutomaton_name.getText().toString(),
                            et_registerAutomaton_ip.getText().toString(),
                            et_registerAutomaton_rack.getText().toString(),
                            et_registerAutomaton_slot.getText().toString(),
                            String.valueOf(sp_registerAutomaton_type.getSelectedItemPosition()),
                            et_registerAutomaton_databloc.getText().toString());

                    AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
                    automatonDB.openForWrite();
                    automatonDB.insertAutomaton(automaton);
                    automatonDB.Close();

                    Toast.makeText(getApplicationContext(), R.string.created_automaton, Toast.LENGTH_LONG).show();

                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                break;
        }
    }

    private void initValidation() {
        name = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.matches("^(([A-z][a-z]+[\\s|-]{1}[A-z][a-z]+)|([A-Z][a-z]+))$", et_registerAutomaton_name.getText().toString())) {
                    et_registerAutomaton_name.setError(getString(R.string.wrong_format));
                    validName = false;
                } else {
                    validName = true;
                }

                automatonDB.openForWrite();
                if (automatonDB.isAlreadyUsed(et_registerAutomaton_name.getText().toString())) {
                    et_registerAutomaton_name.setError(getString(R.string.already_used_name));
                    automatonDB.Close();
                    validName = false;
                } else {
                    validName = true;
                }
            }
        };

        ip = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.matches("^(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))$",
                        et_registerAutomaton_ip.getText().toString())) {
                    et_registerAutomaton_ip.setError(getString(R.string.wrong_ip_format));
                    validIp = false;
                } else {
                    validIp = true;
                }
            }
        };

        rack = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.matches("^[0-3]$",
                        et_registerAutomaton_rack.getText().toString())) {
                    et_registerAutomaton_rack.setError(getString(R.string.wrong_rack_slot_format));
                    validRack = false;
                } else {
                    validRack = true;
                }
            }
        };

        slot = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.matches("^[0-3]$",
                        et_registerAutomaton_slot.getText().toString())) {
                    et_registerAutomaton_slot.setError(getString(R.string.wrong_rack_slot_format));
                    validSlot = false;
                } else {
                    validSlot = true;
                }
            }
        };

        dataBloc = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.matches("^(DB[0-9]{1,2})$", et_registerAutomaton_databloc.getText().toString())) {
                    et_registerAutomaton_databloc.setError(getString(R.string.wrong_databloc_format));
                    validDataBloc = false;
                } else {
                    validDataBloc = true;
                }
            }
        };
    }

    public boolean isValid() {
        return validName && validIp && validRack && validSlot && validDataBloc;
    }
}
