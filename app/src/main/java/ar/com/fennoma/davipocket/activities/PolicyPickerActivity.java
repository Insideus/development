package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
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
                    startActivity(new Intent(PolicyPickerActivity.this, RateAppDialogActivity.class));
                }
            }
        });
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
            DialogUtil.toast(this, DialogUtil.concatMessages(errorList));
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
}
