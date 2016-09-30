package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class NewDeviceOtpActivity extends LoginBaseActivity {

    private static final int NEW_DEVICE_DIALOG = 16;
    private EditText code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_device_otp_layout);
        findCodeField();
        setButtons();
    }

    private void findCodeField() {
        code = (EditText) findViewById(R.id.code);
    }

    private void setButtons() {
        View sendButton = findViewById(R.id.send_button);
        View resend = findViewById(R.id.resend_sms);
        if(sendButton == null || resend == null){
            return;
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code != null && !TextUtils.isEmpty(code.getText())) {
                    //startActivity(new Intent(PasswordConfirmationActivity.this, ActivatedPasswordActivity.class));
                    new ValidateOtpTask().execute(code.getText().toString());
                } else {
                    DialogUtil.toast(NewDeviceOtpActivity.this,
                            getString(R.string.input_data_error_generic_title),
                            getString(R.string.input_data_error_generic_subtitle),
                            getString(R.string.password_confirmation_incomplete_code_error));
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendOtpSmsTask().execute();
            }
        });
    }

    public class ValidateOtpTask extends AsyncTask<String, Void, String> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;
            try {
                response = Service.validateOtp(params[0], params[1], params[2]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, "");
            } else if(response == null && errorCode == null) {
                //Service error.
                showOtpValidationError();
            } else {
                //Success.
                //Change Password Session Token.
                showOtpValidationSuccessDialog();
            }
            hideLoading();
        }
    }

    private void showOtpValidationSuccessDialog() {
        DialogUtil.toastWithResult(this, NEW_DEVICE_DIALOG, getString(R.string.new_device_otp_validation_success_title),
                getString(R.string.new_device_otp_validation_success_subtitle),
                getString(R.string.new_device_otp_validation_success_text));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_DEVICE_DIALOG){
            //TODO: cerrar activity calculo
        }
    }

    private void showOtpValidationError() {
        DialogUtil.toastWithResult(this, NEW_DEVICE_DIALOG, getString(R.string.new_device_otp_validation_error_title),
                getString(R.string.new_device_otp_validation_error_subtitle),
                getString(R.string.new_device_otp_validation_error_text));
    }

    private class ResendOtpSmsTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
