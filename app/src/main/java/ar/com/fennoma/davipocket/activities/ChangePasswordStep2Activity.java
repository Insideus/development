package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class ChangePasswordStep2Activity extends BaseActivity {

    private EditText lastCardDigits;
    private EditText atmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_step_2_layout);
        setActionBar(getString(R.string.change_password_title), true);
        findFields();
        setContinueButton();
    }

    private void findFields() {
        lastCardDigits = (EditText) findViewById(R.id.last_card_digits);
        atmPassword = (EditText) findViewById(R.id.atm_password);
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.continue_button);
        if (continueButton == null) {
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    startActivity(new Intent(ChangePasswordStep2Activity.this, ChangePasswordStep3Activity.class));
                }
            }
        });
    }

    private boolean validateFields() {
        List<String> errorList = new ArrayList<>();
        if (TextUtils.isEmpty(lastCardDigits.getText())) {
            errorList.add(getString(R.string.change_password_step_2_empty_card_digits_error));
        } else {
            if (lastCardDigits.getText().length() < 4) {
                errorList.add(getString(R.string.change_password_step_2_incomplete_card_digits_error));
            }
        }
        if (TextUtils.isEmpty(atmPassword.getText())) {
            errorList.add(getString(R.string.change_password_step_2_empty_atm_password_error));
        }
        if (!errorList.isEmpty()) {
            DialogUtil.toast(this, DialogUtil.concatMessages(errorList));
            return false;
        }
        return true;
    }
}
