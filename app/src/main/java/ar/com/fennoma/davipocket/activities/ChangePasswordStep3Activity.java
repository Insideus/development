package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class ChangePasswordStep3Activity extends BaseActivity{

    public static String ID_TYPE_KEY = "id_type_key";
    public static String ID_NUMBER_KEY = "id_number_key";
    public static String PASSWORD_TOKEN_KEY = "password_token_key";
    public static String EXPIRED_PASSWORD_KEY = "expired_password_key";

    private String personIdType;
    private String personId;
    private String passwordToken;
    private Boolean expiredPassword;

    private EditText oldPassword;
    private EditText repeatedPassword;
    private EditText virtualPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_step_3_layout);

        if (savedInstanceState != null) {
            personIdType = savedInstanceState.getString(ID_TYPE_KEY, "");
            personId = savedInstanceState.getString(ID_NUMBER_KEY, "");
            passwordToken = savedInstanceState.getString(PASSWORD_TOKEN_KEY, "");
            expiredPassword = savedInstanceState.getBoolean(EXPIRED_PASSWORD_KEY, false);
        } else {
            personIdType = getIntent().getStringExtra(ID_TYPE_KEY);
            personId = getIntent().getStringExtra(ID_NUMBER_KEY);
            passwordToken = getIntent().getStringExtra(PASSWORD_TOKEN_KEY);
            expiredPassword = getIntent().getBooleanExtra(EXPIRED_PASSWORD_KEY, false);
        }

        setActionBar(getString(R.string.change_password_title), false);
        findFields();
        setHelpIcons();
        setContinueButton();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putString(PASSWORD_TOKEN_KEY, passwordToken);
        outState.putBoolean(EXPIRED_PASSWORD_KEY, expiredPassword);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        personId = savedInstanceState.getString(ID_NUMBER_KEY);
        personIdType = savedInstanceState.getString(ID_TYPE_KEY);
        passwordToken = savedInstanceState.getString(PASSWORD_TOKEN_KEY);
        expiredPassword = savedInstanceState.getBoolean(EXPIRED_PASSWORD_KEY, false);
    }

    private void setHelpIcons() {
        View helperIcon1 = findViewById(R.id.help_icon_1);
        View helperIcon2 = findViewById(R.id.help_icon_2);
        if(helperIcon1 == null || helperIcon2 == null){
            return;
        }
        helperIcon1.setOnClickListener(new OnHelpIconClickListener());
        helperIcon2.setOnClickListener(new OnHelpIconClickListener());
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.send_button);
        if(continueButton == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    if(expiredPassword) {
                        new SetExpiredPasswordTask().execute(personId, personIdType, oldPassword.getText().toString(),
                                virtualPassword.getText().toString(), passwordToken);
                    } else {
                        new SetPasswordTask().execute(personId, personIdType,
                                virtualPassword.getText().toString(), passwordToken);
                    }
                }
            }
        });
    }

    private boolean validateFields() {
        List<String> errorList = new ArrayList<>();
        if(expiredPassword) {
            if (TextUtils.isEmpty(oldPassword.getText())) {
                errorList.add(getString(R.string.change_password_step_3_empty_old_virtual_password_error));
            }
        }
        if (TextUtils.isEmpty(virtualPassword.getText())) {
            errorList.add(getString(R.string.change_password_step_3_empty_virtual_password_error));
        }
        if (TextUtils.isEmpty(repeatedPassword.getText())) {
            errorList.add(getString(R.string.change_password_step_3_empty_password_repeat_error));
        }
        if (!errorList.isEmpty()) {
            DialogUtil.toast(this,
                    getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle),
                    errorList);
            return false;
        }
        return true;
    }

    private void findFields() {
        virtualPassword = (EditText) findViewById(R.id.virtual_password);
        repeatedPassword = (EditText) findViewById(R.id.repeated_password);
        oldPassword = (EditText) findViewById(R.id.old_virtual_password);
        if(expiredPassword) {
            findViewById(R.id.old_virtual_password_underline).setVisibility(LinearLayout.VISIBLE);
            oldPassword.setVisibility(LinearLayout.VISIBLE);
        } else {
            findViewById(R.id.old_virtual_password_underline).setVisibility(LinearLayout.GONE);
            oldPassword.setVisibility(LinearLayout.GONE);
        }
    }

    private class OnHelpIconClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ChangePasswordStep3Activity.this, ToastDialogActivity.class);
            intent.putExtra(ToastDialogActivity.TITLE_KEY, getString(R.string.help_dialog_title_1));
            intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, getString(R.string.help_dialog_title_2));
            intent.putExtra(ToastDialogActivity.TEXT_KEY, getString(R.string.help_dialog_activity_text));
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
        }
    }

    public class SetPasswordTask extends AsyncTask<String, Void, LoginResponse> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            LoginResponse response = null;
            try {
                response = Service.setPassword(params[0], params[1], params[2], params[3]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, "");
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                goToActivatedPasswordActivity(response);
            }
            hideLoading();
        }
    }

    public class SetExpiredPasswordTask extends AsyncTask<String, Void, LoginResponse> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            LoginResponse response = null;
            try {
                response = Service.setExpiredPassword(params[0], params[1], params[2], params[3], params[4]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, "");
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                goToActivatedPasswordActivity(response);
            }
            hideLoading();
        }
    }

    private void goToActivatedPasswordActivity(LoginResponse response) {
        Intent accountVerificationIntent = new Intent(this, ActivatedPasswordActivity.class);
        accountVerificationIntent.putExtra(ActivatedPasswordActivity.LOGIN_RESPONSE_KEY, response);
        accountVerificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(accountVerificationIntent);
    }


}
