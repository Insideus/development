package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class PolicyPickerActivity extends BaseActivity{

    private CheckBox smsSend;
    private CheckBox emailSend;
    private CheckBox phoneContact;
    private CheckBox termsAndConditions;
    private CheckBox privacyPolicy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policy_picker_activity_layout);
        setActionBar(getString(R.string.policy_picker_title), false);
        findCheckBoxes();
        setContinueButton();
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.send_button);
        if(continueButton == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateRequirements()){
                    new AcceptPolicyTask().execute(smsSend.isChecked(), phoneContact.isChecked(), emailSend.isChecked(),
                            termsAndConditions.isChecked(), privacyPolicy.isChecked());
                }
            }
        });
    }

    public class AcceptPolicyTask extends AsyncTask<Boolean, Void, Boolean> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.acceptPolicy(sid, params[0], params[1], params[2], params[3], params[4]);
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
                goToMainActivity();
            }
        }
    }

    private boolean validateRequirements() {
        List<String> errorList = new ArrayList<>();
        if (!termsAndConditions.isChecked()) {
            errorList.add(getString(R.string.policy_picker_terms_and_conditions_not_checked_error));
        }
        if (!privacyPolicy.isChecked()) {
            errorList.add(getString(R.string.policy_picker_privacy_policy_not_checked_error));
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

    private void findCheckBoxes() {
        smsSend = (CheckBox) findViewById(R.id.sms_send);
        emailSend = (CheckBox) findViewById(R.id.email_send);
        phoneContact = (CheckBox) findViewById(R.id.phone_contact);
        termsAndConditions = (CheckBox) findViewById(R.id.terms_and_conditions);
        privacyPolicy = (CheckBox) findViewById(R.id.privacy_policy);
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

}
