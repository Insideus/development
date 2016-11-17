package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.tasks.DaviPayTask;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class AccountActivationActivity extends BaseActivity{

    private static final int CODE_LENGHT = 4;
    private EditText code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activation_activity_layout);
        setActionBar(getString(R.string.account_activation_activity_title), false);
        findCodeBoxContainer();
        setRequestCodeButton();
        Session.getCurrentSession(this).setPendingStep(LoginSteps.ACCOUNT_VERIFICATION.getStep());
        new ResendValidationCodeTask(this).execute();
    }

    private void setRequestCodeButton() {
        View requestCode = findViewById(R.id.request_code);
        if(requestCode == null){
            return;
        }
        requestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendValidationCodeTask(AccountActivationActivity.this).execute();
            }
        });
    }

    private void findCodeBoxContainer() {
        code = (EditText) findViewById(R.id.code);
        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == CODE_LENGHT){
                    validateCode();
                }
            }
        });
    }

    private void validateCode() {
        if(code.getText().length() != CODE_LENGHT){
            DialogUtil.toast(this,
                    getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle),
                    getString(R.string.account_activation_incomplete_code_error));
            return;
        }
        new AccountValidationTask(this, code.getText().toString()).execute();
    }

    public class AccountValidationTask extends DaviPayTask<Boolean> {

        String activationCode;

        public AccountValidationTask(BaseActivity activity, String activationCode) {
            super(activity);
            this.activationCode = activationCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.accountValidation(sid, activationCode);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    //Service error.
                    showServiceGenericError();
                } else {
                    goToConfirmationActivity();
                }
            }
        }
    }

    public class ResendValidationCodeTask extends DaviPayTask<Boolean> {

        public ResendValidationCodeTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.resendValidationCode(sid);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    //Service error.
                    showServiceGenericError();
                } else {
                    showResendMessage();
                }
            }
        }

    }

    private void showResendMessage() {
        //DialogUtil.toast(this, getString(R.string.resend_validation_code_text));
    }

    public void showConfirmationError() {
        DialogUtil.toast(this,
                getString(R.string.input_data_error_generic_title),
                getString(R.string.input_data_error_generic_subtitle),
                getString(R.string.confirmation_service_error));
    }

    private void goToConfirmationActivity() {
        startActivity(new Intent(this, PolicyPickerActivity.class));
        this.finish();
    }

    public void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if(error != null) {
            switch(error) {
                case CONFIRMATION_ERROR:
                    showConfirmationError();
                    break;
                default:
                    super.processErrorAndContinue(error, additionalParam);
            }
        } else {
            showServiceGenericError();
        }
    }

}
