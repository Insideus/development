package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class ChangePasswordStep1Activity extends BaseActivity{

    private EditText idType;
    private EditText idNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_step_1_layout);
        setActionBar(getString(R.string.change_password_title), true);
        findFields();
        setContinueButton();
    }

    private void findFields() {
        idType = (EditText) findViewById(R.id.id_type);
        idNumber = (EditText) findViewById(R.id.id_number);
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.send_button);
        if(continueButton == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(idNumber.getText())){
                    DialogUtil.toast(ChangePasswordStep1Activity.this, getString(R.string.change_password_step_1_empty_id_number_error));
                    return;
                }
                startActivity(new Intent(ChangePasswordStep1Activity.this, ChangePasswordStep2Activity.class));
            }
        });
    }
}
