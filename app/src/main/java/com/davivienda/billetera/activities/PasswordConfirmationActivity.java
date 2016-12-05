package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.utils.DialogUtil;

public class PasswordConfirmationActivity extends BaseActivity {

    public static String ID_TYPE_KEY = "id_type_key";
    public static String ID_NUMBER_KEY = "id_number_key";
    public static String EXPIRED_PASSWORD_KEY = "expired_password_key";

    private String personIdType;
    private String personId;
    private Boolean expiredPassword;

    private EditText code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_confirmation_activity_layout);

        if (savedInstanceState != null) {
            personIdType = savedInstanceState.getString(ID_TYPE_KEY, "");
            personId = savedInstanceState.getString(ID_NUMBER_KEY, "");
            expiredPassword = savedInstanceState.getBoolean(EXPIRED_PASSWORD_KEY, false);
        } else {
            personIdType = getIntent().getStringExtra(ID_TYPE_KEY);
            personId = getIntent().getStringExtra(ID_NUMBER_KEY);
            expiredPassword = getIntent().getBooleanExtra(EXPIRED_PASSWORD_KEY, false);
        }

        setActionBar(getString(R.string.password_confirmation_title), false);
        findCodeField();
        setSendButton();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putBoolean(EXPIRED_PASSWORD_KEY, expiredPassword);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putBoolean(EXPIRED_PASSWORD_KEY, expiredPassword);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        personIdType = savedInstanceState.getParcelable(ID_TYPE_KEY);
        personId = savedInstanceState.getString(ID_NUMBER_KEY);
        expiredPassword = savedInstanceState.getBoolean(EXPIRED_PASSWORD_KEY, false);
    }

    private void findCodeField() {
        code = (EditText) findViewById(R.id.code);
    }

    private void setSendButton() {
        View sendButton = findViewById(R.id.send_button);
        if(sendButton == null){
            return;
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code != null && !TextUtils.isEmpty(code.getText())) {
                    new ValidateOtpTask(PasswordConfirmationActivity.this, personId, personIdType, code.getText().toString()).execute();
                } else {
                    DialogUtil.toast(PasswordConfirmationActivity.this,
                            getString(R.string.input_data_error_generic_title),
                            getString(R.string.input_data_error_generic_subtitle),
                            getString(R.string.password_confirmation_incomplete_code_error));
                }
            }
        });
    }

    public class ValidateOtpTask extends DaviPayTask<String> {

        String personId;
        String personIdType;
        String pin;

        public ValidateOtpTask(BaseActivity activity, String personId, String personIdType, String pin) {
            super(activity);
            this.personId = personId;
            this.personIdType = personIdType;
            this.pin = pin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            try {
                response = Service.validateOtp(personId, personIdType, pin);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(!processedError) {
                //Success.
                //Change Password Session Token.
                goToSetNewPasswordActivity(response);
            }
        }

    }

    private void goToSetNewPasswordActivity(String passwordToken) {
        Intent intent = new Intent(this, ChangePasswordStep3Activity.class);
        intent.putExtra(ChangePasswordStep3Activity.PASSWORD_TOKEN_KEY, passwordToken);
        intent.putExtra(ChangePasswordStep3Activity.ID_NUMBER_KEY, personId);
        intent.putExtra(ChangePasswordStep3Activity.ID_TYPE_KEY, personIdType);
        intent.putExtra(ChangePasswordStep3Activity.EXPIRED_PASSWORD_KEY, expiredPassword);
        startActivity(intent);
    }

}
