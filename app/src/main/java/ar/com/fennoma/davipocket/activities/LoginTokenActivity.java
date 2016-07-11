package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class LoginTokenActivity extends LoginBaseActivity {

    public static String ID_TYPE_KEY = "id_type_key";
    public static String ID_NUMBER_KEY = "id_number_key";
    public static String PASSWORD_KEY = "password_key";
    public static String NEXT_REQUIRED_TOKEN_KEY = "next_required_token_key";
    public static String NEXT_TOKEN_KEY = "next_token_key";

    private TextView token;
    private String idNumberStr;
    private String passwordStr;

    private Boolean nextToken;
    private String nextTokenSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_login);
        setActionBar(getString(R.string.login_token_activity_title), true);

        if (savedInstanceState != null) {
            selectedIdType = savedInstanceState.getParcelable(ID_TYPE_KEY);
            idNumberStr = savedInstanceState.getString(ID_NUMBER_KEY, "");
            passwordStr = savedInstanceState.getString(PASSWORD_KEY, "");
            nextToken = savedInstanceState.getBoolean(NEXT_REQUIRED_TOKEN_KEY, false);
            nextTokenSession = savedInstanceState.getString(NEXT_TOKEN_KEY, "");
        } else {
            selectedIdType = getIntent().getParcelableExtra(ID_TYPE_KEY);
            idNumberStr = getIntent().getStringExtra(ID_NUMBER_KEY);
            passwordStr = getIntent().getStringExtra(PASSWORD_KEY);
            nextToken = getIntent().getBooleanExtra(NEXT_REQUIRED_TOKEN_KEY, false);
            nextTokenSession = getIntent().getStringExtra(NEXT_TOKEN_KEY);
        }

        setActionsToButtons();
    }

    private void setActionsToButtons() {
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextToken == null || !nextToken) {
                    doLogin();
                } else {
                    doNextTokenLogin();
                }
            }
        });

        idTypeSpinner = (Spinner) findViewById(R.id.login_id_type_spinner);
        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        virtualPasswordText = (TextView) findViewById(R.id.login_virtual_password);
        personIdNumber = (TextView) findViewById(R.id.login_person_id);
        token = (TextView) findViewById(R.id.login_token);

        virtualPasswordText.setText(passwordStr);
        personIdNumber.setText(idNumberStr);

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
            DialogUtil.toast(this, errors);
        } else {
            new LoginWithTokenTask().execute(personIdNumber.getText().toString(),
                    String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString(), token.getText().toString());
        }
    }

    private void doNextTokenLogin() {
        ArrayList<String> errors = validate();
        if(errors != null && errors.size() > 0) {
            DialogUtil.toast(this, errors);
        } else {
            new LoginWithNextTokenTask().execute(personIdNumber.getText().toString(),
                    String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString(), token.getText().toString(), nextTokenSession);
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
        if(TextUtils.isEmpty(token.getText())) {
            errors.add(getString(R.string.token_error_text));
        }
        return errors;
    }

}
