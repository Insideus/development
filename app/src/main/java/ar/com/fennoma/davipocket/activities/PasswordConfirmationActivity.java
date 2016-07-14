package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;

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
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putBoolean(EXPIRED_PASSWORD_KEY, expiredPassword);
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
                    //startActivity(new Intent(PasswordConfirmationActivity.this, ActivatedPasswordActivity.class));
                    new ValidateOtpTask().execute(personId, personIdType, code.getText().toString());
                }
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
                showServiceGenericError();
            } else {
                //Success.
                //Change Password Session Token.
                goToSetNewPasswordActivity(response);
            }
            hideLoading();
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
