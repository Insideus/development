package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.EncryptationUtils;

public class LoginActivity extends LoginBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setActionToButtons();
        String paswordEncripted = EncryptationUtils.encryptPin(this, "2589");
        Log.d("Encriptation", paswordEncripted);
    }

    private void setActionToButtons() {
        View loginButton = findViewById(R.id.login_button);
        View forgotPassword = findViewById(R.id.forgot_password_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ChangePasswordStep1Activity.class));
            }
        });

        idTypeSpinner = (Spinner) findViewById(R.id.login_id_type_spinner);
        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        virtualPasswordText = (TextView) findViewById(R.id.login_virtual_password);
        personIdNumber = (TextView) findViewById(R.id.login_person_id);

        selectedIdTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idTypeSpinner.performClick();
            }
        });
        idTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIdType = (PersonIdType) parent.getItemAtPosition(position);
                if(selectedIdType != null)
                selectedIdTypeText.setText(selectedIdType.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setIdTypesSpinner();
    }

    private void doLogin() {
        ArrayList<String> errors = validate();
        if(errors != null && errors.size() > 0) {
            DialogUtil.toast(this, getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle), errors);
        } else {
            new LoginTask().execute(personIdNumber.getText().toString(),
                    String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString());
        }
    }

    private ArrayList<String> validate() {
        ArrayList<String> errors =  new ArrayList<>();
        if(selectedIdType == null) {
            errors.add(getString(R.string.person_id_type_error_text));
        }
        if(TextUtils.isEmpty(personIdNumber.getText())) {
            errors.add(getString(R.string.person_in_number_error_text));
        }
        if(TextUtils.isEmpty(virtualPasswordText.getText())) {
            errors.add(getString(R.string.virtual_password_error_text));
        }
        return errors;
    }

}
