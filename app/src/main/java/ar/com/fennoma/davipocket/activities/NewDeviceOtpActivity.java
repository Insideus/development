package ar.com.fennoma.davipocket.activities;

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

    public static final String NEW_DEVICE_TOKEN = "new_device_token";
    private static final int NEW_DEVICE_DIALOG = 16;

    private EditText code;
    private String newDeviceToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_device_otp_layout);
        setActionBar(getString(R.string.new_device_otp_title), true);
        if (savedInstanceState != null) {
            newDeviceToken = savedInstanceState.getString(NEW_DEVICE_TOKEN, "");
        } else {
            newDeviceToken = getIntent().getStringExtra(NEW_DEVICE_TOKEN);
        }
        new ResendOtpSmsTask().execute();
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
                    new RegisterDeviceTask().execute(newDeviceToken, code.getText().toString());
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

    private void showOtpValidationError() {
        DialogUtil.toast(this,
                getString(R.string.new_device_otp_validation_error_title),
                getString(R.string.new_device_otp_validation_error_subtitle),
                getString(R.string.new_device_otp_validation_error_text));
    }

    private class ResendOtpSmsTask extends AsyncTask<Void, Void, Boolean> {

        String errorCode;
        String additionalData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean success = null;
            try {
                success = Service.resendNewDeviceValidationCode(newDeviceToken);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                additionalData = e.getAdditionalData();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, additionalData);
            } else if(response == null) {
                //Service error.
                showServiceGenericError();
            }
            hideLoading();
        }

    }

}
