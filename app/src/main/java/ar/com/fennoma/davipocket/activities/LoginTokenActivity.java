package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.session.Session;
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(ID_TYPE_KEY, selectedIdType);
        outState.putString(ID_NUMBER_KEY, idNumberStr);
        outState.putString(PASSWORD_KEY, passwordStr);
        outState.putBoolean(NEXT_REQUIRED_TOKEN_KEY, nextToken);
        outState.putString(NEXT_TOKEN_KEY, nextTokenSession);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedIdType = savedInstanceState.getParcelable(ID_TYPE_KEY);
        idNumberStr = savedInstanceState.getString(ID_NUMBER_KEY);
        passwordStr = savedInstanceState.getString(PASSWORD_KEY);
        nextToken = savedInstanceState.getBoolean(NEXT_REQUIRED_TOKEN_KEY, false);
        nextTokenSession = savedInstanceState.getString(NEXT_TOKEN_KEY);
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

        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        ArrayList<PersonIdType> personIdTypes = Session.getCurrentSession(this).getPersonIdTypes();
        if(personIdTypes != null && personIdTypes.size() > 0) {
            selectedIdType = personIdTypes.get(0);
            setSelectedIdTypeName();
        }
        virtualPasswordText = (TextView) findViewById(R.id.login_virtual_password);
        personIdNumber = (TextView) findViewById(R.id.login_person_id);
        token = (TextView) findViewById(R.id.login_token);

        virtualPasswordText.setText(passwordStr);
        personIdNumber.setText(idNumberStr);

        selectedIdTypeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DialogUtil.hideKeyboard(LoginTokenActivity.this);
                showCombo();
                return false;
            }
        });
    }

    private void doLogin() {
        ArrayList<String> errors = validate();
        if(errors != null && errors.size() > 0) {
            DialogUtil.toast(this,
                    getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle),
                    errors);
        } else {
            new LoginWithTokenTask().execute(personIdNumber.getText().toString(),
                    String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString(), token.getText().toString());
        }
    }

    private void doNextTokenLogin() {
        ArrayList<String> errors = validate();
        if(errors != null && errors.size() > 0) {
            DialogUtil.toast(this,
                    getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle),
                    errors);
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
