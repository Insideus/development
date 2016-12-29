package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.LoginResponse;
import com.davivienda.billetera.model.LoginSteps;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

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
        Session.getCurrentSession(this).setPendingStep(LoginSteps.COMMUNICATION_PERMISSIONS.getStep());
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
                    new AcceptPolicyTask(PolicyPickerActivity.this, smsSend.isChecked(), phoneContact.isChecked(),
                            emailSend.isChecked(), termsAndConditions.isChecked(), privacyPolicy.isChecked()).execute();
                }
            }
        });
        findViewById(R.id.terms_and_conditions_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTermsAndConditionsPage(getString(R.string.policy_picker_check_box_terms_and_conditions));
            }
        });
        findViewById(R.id.privacy_policy_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPrivacyPolicyPage(getString(R.string.policy_picker_check_box_privacy_policy));
            }
        });
    }

    public class AcceptPolicyTask extends DaviPayTask<LoginResponse> {

        Boolean sms;
        Boolean phone;
        Boolean email;
        Boolean acceptTerms;
        Boolean acceptPrivacy;

        public AcceptPolicyTask(BaseActivity activity, Boolean sms, Boolean phone, Boolean email,
                                Boolean acceptTerms, Boolean acceptPrivacy) {
            super(activity);
            this.sms = sms;
            this.phone = phone;
            this.email = email;
            this.acceptTerms = acceptTerms;
            this.acceptPrivacy = acceptPrivacy;
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
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.acceptPolicy(sid, sms, phone, email, acceptTerms, acceptPrivacy);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(!processedError) {
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid(), response.getAccountStatus());
                if (step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                goToRegistrationStep(step);
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

    /*
    private void goToMainActivity() {
        Session.getCurrentSession(this).setPendingStep(null);
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    private void goToEcardActivity() {
        startActivity(new Intent(this, EcardLoginActivity.class));
        this.finish();
    }
    */

}
