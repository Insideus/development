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

public class ChangePasswordStep3Activity extends BaseActivity{

    private EditText repeatedPassword;
    private EditText virtualPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_step_3_layout);
        setActionBar(getString(R.string.change_password_title), false);
        findFields();
        setContinueButton();
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.continue_button);
        if(continueButton == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                   startActivity(new Intent(ChangePasswordStep3Activity.this, PasswordConfirmationActivity.class));
                }
            }
        });
    }

    private boolean validateFields() {
        List<String> errorList = new ArrayList<>();
        if (TextUtils.isEmpty(virtualPassword.getText())) {
            errorList.add(getString(R.string.change_password_step_3_empty_virtual_password_error));
        }
        if (TextUtils.isEmpty(repeatedPassword.getText())) {
            errorList.add(getString(R.string.change_password_step_3_empty_password_repeat_error));
        }
        if (!errorList.isEmpty()) {
            DialogUtil.toast(this, DialogUtil.concatMessages(errorList));
            return false;
        }
        return true;
    }

    private void findFields() {
        virtualPassword = (EditText) findViewById(R.id.virtual_password);
        repeatedPassword = (EditText) findViewById(R.id.repeated_password);
    }
}
