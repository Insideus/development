package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.LoginResponse;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.utils.DialogUtil;
import com.davivienda.billetera.utils.EncryptionUtils;

public class ChangePasswordStep3Activity extends BaseActivity{

    public static String ID_TYPE_KEY = "id_type_key";
    public static String ID_NUMBER_KEY = "id_number_key";
    public static String PASSWORD_TOKEN_KEY = "password_token_key";
    public static String EXPIRED_PASSWORD_KEY = "expired_password_key";

    private static final int NOT_HAPPENING = 0;
    private static final int GOING_DOWN = 1;
    private static final int GOING_UP = 2;

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

        setActionBar(getString(R.string.change_password_title), true);
        findFields();
        setHelpIcons();
        setContinueButton();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putString(PASSWORD_TOKEN_KEY, passwordToken);
        outState.putBoolean(EXPIRED_PASSWORD_KEY, expiredPassword);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putString(PASSWORD_TOKEN_KEY, passwordToken);
        outState.putBoolean(EXPIRED_PASSWORD_KEY, expiredPassword);
        super.onSaveInstanceState(outState);
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
                        new SetExpiredPasswordTask(ChangePasswordStep3Activity.this, personId, personIdType, oldPassword.getText().toString(),
                                virtualPassword.getText().toString(), passwordToken).execute();
                    } else {
                        new SetPasswordTask(ChangePasswordStep3Activity.this, personId, personIdType, virtualPassword.getText().toString(),
                                passwordToken).execute();
                    }
                }
            }
        });
    }

    private boolean validateFields() {
        String errorText = null;
        if(expiredPassword) {
            if (TextUtils.isEmpty(oldPassword.getText())) {
                errorText = getString(R.string.change_password_step_3_empty_old_virtual_password_error);
            }
        }
        if (TextUtils.isEmpty(errorText) && TextUtils.isEmpty(virtualPassword.getText())) {
            errorText = getString(R.string.change_password_step_3_empty_virtual_password_error);
        }
        if (TextUtils.isEmpty(errorText) && TextUtils.isEmpty(repeatedPassword.getText())) {
            errorText = getString(R.string.change_password_step_3_empty_password_repeat_error);
        }
        if(TextUtils.isEmpty(errorText)) {
            if (virtualPassword.getText().length() < 4 || virtualPassword.getText().length() > 8) {
                errorText = getString(R.string.change_password_step_3_invalid_amount_of_digits);
            } else if (!validatePasswordAscending(virtualPassword.getText().toString())) {
                errorText = getString(R.string.change_password_step_3_invalid_password);
            } else if (!validatePasswordEqualsNumbers(virtualPassword.getText().toString())) {
                errorText = getString(R.string.change_password_step_3_invalid_password_equals_numbers);
            } else
            if (!TextUtils.equals(virtualPassword.getText(), repeatedPassword.getText())) {
                errorText = getString(R.string.change_password_step_3_password_repeat_error);
            }
        }
        if (!TextUtils.isEmpty(errorText)) {
            DialogUtil.toast(this,
                    getString(R.string.change_password_input_error_title),
                    getString(R.string.change_password_input_error_subtitle),
                    errorText);
            return false;
        }
        return true;
    }

    private boolean validatePasswordAscending(String virtualPassword) {
        return !sequenceIsHigherThan(4, virtualPassword);
    }

    private boolean validatePasswordEqualsNumbers(String virtualPassword) {
        return !frequencyIsHigherThan(4, virtualPassword);
    }

    private boolean sequenceIsHigherThan(int max, String word) {
        for(int i = 0; i <= word.length() - max; i++){
            int starter = Integer.valueOf(String.valueOf(word.charAt(i)));
            int next = Integer.valueOf(String.valueOf(word.charAt(i + 1)));
            int sequenceMode;
            if(starter + 1 == next){
                sequenceMode = GOING_UP;
            } else if(starter - 1 == next ){
                sequenceMode = GOING_DOWN;
            } else {
                sequenceMode = NOT_HAPPENING;
            }
            if(sequenceMode != NOT_HAPPENING){
                for(int j = i + 2; j < max + i; j++) {
                    starter = next;
                    next = Integer.valueOf(String.valueOf(word.charAt(j)));
                    if(sequenceMode == GOING_UP){
                        if(starter + 1 != next){
                            break;
                        } else if(j == max + i - 1){
                            return true;
                        }
                    } else {
                        if(starter - 1 != next){
                            break;
                        } else if(j == max + i -1){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean frequencyIsHigherThan(int max, String word) {
        for(int i = 0; i <= word.length() - max; i++){
            int comparator = Integer.valueOf(String.valueOf(word.charAt(i)));
            for(int j = i + 1; j < max + i; j++) {
                int integer = Integer.valueOf(String.valueOf(word.charAt(j)));
                if(comparator != integer){
                    break;
                }else if(j == max + i - 1){
                    return true;
                }
            }
        }
        return false;
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

    public class SetPasswordTask extends DaviPayTask<LoginResponse> {

        String personId;
        String personIdType;
        String password;
        String passwordToken;

        public SetPasswordTask(BaseActivity activity, String personId, String personIdType, String password, String passwordToken) {
            super(activity);
            this.personId = personId;
            this.personIdType = personIdType;
            this.password = password;
            this.passwordToken = passwordToken;
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
                String encryptedPassword = EncryptionUtils.encryptPassword(ChangePasswordStep3Activity.this, password);
                response = Service.setPassword(personId, personIdType, encryptedPassword, passwordToken);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                additionalData = e.getAdditionalData();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(!processedError) {
                //Success login.
                goToActivatedPasswordActivity(response);
            }
        }
    }

    public class SetExpiredPasswordTask extends DaviPayTask<LoginResponse> {

        String personId;
        String personIdType;
        String oldPassword;
        String password;
        String passwordToken;

        public SetExpiredPasswordTask(BaseActivity activity, String personId, String personIdType,
                                      String oldPassword, String password, String passwordToken) {
            super(activity);
            this.personId = personId;
            this.personIdType = personIdType;
            this.oldPassword = oldPassword;
            this.password = password;
            this.passwordToken = passwordToken;
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
                String encryptedOldPassword = EncryptionUtils.encryptPassword(ChangePasswordStep3Activity.this, oldPassword);
                String encryptedPassword = EncryptionUtils.encryptPassword(ChangePasswordStep3Activity.this, password);
                response = Service.setExpiredPassword(personId, personIdType, encryptedOldPassword, encryptedPassword, passwordToken);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                additionalData = e.getAdditionalData();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(!processedError) {
                //Success login.
                goToActivatedPasswordActivity(response);
            }
        }

    }

    private void goToActivatedPasswordActivity(LoginResponse response) {
        Intent accountVerificationIntent = new Intent(this, ActivatedPasswordActivity.class);
        accountVerificationIntent.putExtra(ActivatedPasswordActivity.LOGIN_RESPONSE_KEY, response);
        accountVerificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(accountVerificationIntent);
    }


}
