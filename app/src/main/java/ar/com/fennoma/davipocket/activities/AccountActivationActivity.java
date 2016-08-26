package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
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
        new ResendValidationCodeTask().execute();
    }

    private void setRequestCodeButton() {
        View requestCode = findViewById(R.id.request_code);
        if(requestCode == null){
            return;
        }
        requestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendValidationCodeTask().execute();
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
        new AccountValidationTask().execute(code.getText().toString());
    }

    public class AccountValidationTask extends AsyncTask<String, Void, Boolean> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.accountValidation(sid, params[0]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            hideLoading();
            if(response == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if(error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else if(error != null && error == ErrorMessages.CONFIRMATION_ERROR) {
                    showConfirmationError();
                } else {
                    showServiceGenericError();
                }
            } else if(!response) {
                //Service error.
                showServiceGenericError();
            } else {
                goToConfirmationActivity();
            }
        }
    }

    public class ResendValidationCodeTask extends AsyncTask<String, Void, Boolean> {

        private String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... params) {
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
            hideLoading();
            if(response == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if(error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else if(!response) {
                //Service error.
                showServiceGenericError();
            } else {
               showResendMessage();
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

}
