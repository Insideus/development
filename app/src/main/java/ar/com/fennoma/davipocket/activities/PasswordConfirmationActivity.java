package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import ar.com.fennoma.davipocket.R;

public class PasswordConfirmationActivity extends BaseActivity {

    private EditText code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_confirmation_activity_layout);
        setActionBar(getString(R.string.password_confirmation_title), false);
        findCodeField();
        setSendButton();
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
                    startActivity(new Intent(PasswordConfirmationActivity.this, ActivatedPasswordActivity.class));
                }
            }
        });
    }

}
