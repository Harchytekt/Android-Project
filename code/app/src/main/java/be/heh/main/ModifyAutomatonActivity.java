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

public class ModifyAutomatonActivity extends Activity {

    Automaton modifiedAutomaton;

    EditText et_modifyAutomaton_name;
    EditText et_modifyAutomaton_ip;
    EditText et_modifyAutomaton_rack;
    EditText et_modifyAutomaton_slot;
    EditText et_modifyAutomaton_databloc;
    Spinner sp_modifyAutomaton_type;

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
        setContentView(R.layout.activity_modify_automaton);

        et_modifyAutomaton_name = findViewById(R.id.et_modifyAutomaton_name);
        et_modifyAutomaton_ip = findViewById(R.id.et_modifyAutomaton_ip);
        et_modifyAutomaton_rack = findViewById(R.id.et_modifyAutomaton_rack);
        et_modifyAutomaton_slot = findViewById(R.id.et_modifyAutomaton_slot);
        et_modifyAutomaton_databloc = findViewById(R.id.et_modifyAutomaton_databloc);
        sp_modifyAutomaton_type = findViewById(R.id.sp_modifyAutomaton_type);

        AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
        automatonDB.openForWrite();
        // Get Current Automaton
        modifiedAutomaton = automatonDB.getAutomaton("Test Liquide");
        automatonDB.Close();

        et_modifyAutomaton_name.setText(modifiedAutomaton.getName());
        et_modifyAutomaton_ip.setText(modifiedAutomaton.getIp());
        et_modifyAutomaton_rack.setText(modifiedAutomaton.getRack());
        et_modifyAutomaton_slot.setText(modifiedAutomaton.getSlot());
        et_modifyAutomaton_databloc.setText(modifiedAutomaton.getDataBloc());
        sp_modifyAutomaton_type.setSelection(Integer.parseInt(modifiedAutomaton.getType()));

        initValidation();

        et_modifyAutomaton_name.addTextChangedListener(name);
        et_modifyAutomaton_ip.addTextChangedListener(ip);
        et_modifyAutomaton_rack.addTextChangedListener(rack);
        et_modifyAutomaton_slot.addTextChangedListener(slot);
        et_modifyAutomaton_databloc.addTextChangedListener(dataBloc);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onModifyAutomatonClickManager(View v) {
        // Récupérer la vue et accéder au bouton
        switch (v.getId()) {
            case R.id.btn_modifyAutomaton_cancel:

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.btn_modifyAutomaton_register:

                if (et_modifyAutomaton_name.getText().toString().isEmpty() ||
                        et_modifyAutomaton_ip.getText().toString().isEmpty() ||
                        et_modifyAutomaton_rack.getText().toString().isEmpty() ||
                        et_modifyAutomaton_slot.getText().toString().isEmpty() ||
                        et_modifyAutomaton_databloc.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), R.string.empty_input, Toast.LENGTH_LONG).show();
                else if (!isValid())
                    Toast.makeText(getApplicationContext(), R.string.error_input, Toast.LENGTH_LONG).show();
                else {
                    Automaton automaton = new Automaton(
                            et_modifyAutomaton_name.getText().toString(),
                            et_modifyAutomaton_ip.getText().toString(),
                            et_modifyAutomaton_rack.getText().toString(),
                            et_modifyAutomaton_slot.getText().toString(),
                            String.valueOf(sp_modifyAutomaton_type.getSelectedItemPosition()),
                            et_modifyAutomaton_databloc.getText().toString());

                    AutomatonAccessDB automatonDB = new AutomatonAccessDB(this);
                    automatonDB.openForWrite();
                    automatonDB.updateAutomaton(modifiedAutomaton.getId(), automaton);
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
                if (!Pattern.matches("^(([A-z][a-z]+[\\s|-]{1}[A-z][a-z]+)|([A-Z][a-z]+))$", et_modifyAutomaton_name.getText().toString())) {
                    et_modifyAutomaton_name.setError(getString(R.string.wrong_format));
                    validName = false;
                } else {
                    validName = true;
                }

                automatonDB.openForWrite();
                if (automatonDB.isAlreadyUsed(et_modifyAutomaton_name.getText().toString())) {
                    et_modifyAutomaton_name.setError(getString(R.string.already_used_name));
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
                        et_modifyAutomaton_ip.getText().toString())) {
                    et_modifyAutomaton_ip.setError(getString(R.string.wrong_ip_format));
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
                        et_modifyAutomaton_rack.getText().toString())) {
                    et_modifyAutomaton_rack.setError(getString(R.string.wrong_rack_slot_format));
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
                        et_modifyAutomaton_slot.getText().toString())) {
                    et_modifyAutomaton_slot.setError(getString(R.string.wrong_rack_slot_format));
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
                if (!Pattern.matches("^(DB[0-9]{1,2})$", et_modifyAutomaton_databloc.getText().toString())) {
                    et_modifyAutomaton_databloc.setError(getString(R.string.wrong_databloc_format));
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
