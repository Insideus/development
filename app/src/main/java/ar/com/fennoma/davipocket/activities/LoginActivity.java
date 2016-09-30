package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class LoginActivity extends LoginBaseActivity {

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setActionToButtons();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        String todo1 = getTodo1Data();
        Log.d("Todo1", todo1);
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
        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        ArrayList<PersonIdType> personIdTypes = Session.getCurrentSession(this).getPersonIdTypes();
        if(personIdTypes != null && personIdTypes.size() > 0) {
            selectedIdType = personIdTypes.get(0);
            setSelectedIdTypeName();
        }
        virtualPasswordText = (TextView) findViewById(R.id.login_virtual_password);
        personIdNumber = (TextView) findViewById(R.id.login_person_id);
        selectedIdTypeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DialogUtil.hideKeyboard(LoginActivity.this);
                showCombo();
                return false;
            }
        });
        findViewById(R.id.virtual_help_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog(getString(R.string.login_help_dialog_title),
                        getString(R.string.login_help_dialog_subtitle),
                        getString(R.string.login_help_dialog_text));
            }
        });
    }

    private void doLogin() {
        ArrayList<String> errors = validate();
        if (errors != null && errors.size() > 0) {
            DialogUtil.toast(this, getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle), errors);
        } else {
            new LoginTask().execute(personIdNumber.getText().toString(),
                    String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString());
        }
    }

    private ArrayList<String> validate() {
        ArrayList<String> errors = new ArrayList<>();
        if (selectedIdType == null) {
            errors.add(getString(R.string.person_id_type_error_text));
        }
        if (TextUtils.isEmpty(personIdNumber.getText())) {
            errors.add(getString(R.string.person_in_number_error_text));
        }
        return errors;
    }

}
