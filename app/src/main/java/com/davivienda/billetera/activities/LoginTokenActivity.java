package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.LoginResponse;
import com.davivienda.billetera.model.LoginSteps;
import com.davivienda.billetera.model.PersonIdType;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.utils.DialogUtil;

public class LoginTokenActivity extends LoginBaseActivity {

    private static final int NEXT_TOKEN_REQUEST = 13;
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
        if(nextToken) {
            DialogUtil.toast(LoginTokenActivity.this,
                    getString(R.string.login_token_next_token_title),
                    getString(R.string.login_token_next_token_subtitle),
                    getString(R.string.login_token_next_token_text));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(ID_TYPE_KEY, selectedIdType);
        outState.putString(ID_NUMBER_KEY, idNumberStr);
        outState.putString(PASSWORD_KEY, passwordStr);
        outState.putBoolean(NEXT_REQUIRED_TOKEN_KEY, nextToken);
        outState.putString(NEXT_TOKEN_KEY, nextTokenSession);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ID_TYPE_KEY, selectedIdType);
        outState.putString(ID_NUMBER_KEY, idNumberStr);
        outState.putString(PASSWORD_KEY, passwordStr);
        outState.putBoolean(NEXT_REQUIRED_TOKEN_KEY, nextToken);
        outState.putString(NEXT_TOKEN_KEY, nextTokenSession);
        super.onSaveInstanceState(outState);
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

    @Override
    public void onBackPressed() {
        goLoginActivity();
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
                    getString(R.string.login_token_invalid_inputs_title),
                    getString(R.string.login_token_invalid_inputs_subtitle),
                    getString(R.string.login_token_invalid_inputs_text));
        } else {
            new LoginWithTokenTask(this, personIdNumber.getText().toString(), String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString(), token.getText().toString()).execute();
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
            new LoginWithNextTokenTask(this, personIdNumber.getText().toString(), String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString(), token.getText().toString(), nextTokenSession).execute();
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

    public class LoginWithNextTokenTask extends DaviPayTask<LoginResponse> {

        String personId;
        String personIdType;
        String password;
        String token;
        String nextTokenSession;

        public LoginWithNextTokenTask(BaseActivity activity, String personId, String personIdType, String password,
                                      String token, String nextTokenSession) {
            super(activity);
            this.personId = personId;
            this.personIdType = personIdType;
            this.password = password;
            this.token = token;
            this.nextTokenSession = nextTokenSession;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            LoginResponse response = null;
            try {
                response = Service.loginWithNextToken(personId, personIdType, password, token, nextTokenSession, getTodo1Data());
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                additionalData = e.getAdditionalData();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            resetLayoutOnFail();
            super.onPostExecute(response);
            if(!processedError) {
                //Success login.
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid(),
                        response.getAccountStatus());
                if (step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                goToRegistrationStep(step);
            }
        }

    }

    private void resetLayoutOnFail() {
        token.setText("");
    }

    public class LoginWithTokenTask extends DaviPayTask<LoginResponse> {

        String personId;
        String personIdType;
        String password;
        String token;

        public LoginWithTokenTask(BaseActivity activity, String personId, String personIdType, String password, String token) {
            super(activity);
            this.personId = personId;
            this.personIdType = personIdType;
            this.password = password;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            LoginResponse response = null;
            try {
                response = Service.loginWithToken(personId, personIdType, password, token, getTodo1Data());
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                additionalData = e.getAdditionalData();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            resetLayoutOnFail();
            super.onPostExecute(response);
            if(!processedError) {
                //Success login.
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid(),
                        response.getAccountStatus());
                if (step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                goToRegistrationStep(step);
            }
        }
    }

}
