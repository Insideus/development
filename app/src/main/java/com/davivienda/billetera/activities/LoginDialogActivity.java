package com.davivienda.billetera.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.User;
import com.davivienda.billetera.utils.DialogUtil;

import java.util.ArrayList;

public class LoginDialogActivity extends BaseActivity {

    public static final String NEEDS_TOKEN = "needs_token";
    public static final String USER_KEY = "user";
    public static final int FORGOT_PASSWORD_RESULT = 2;

    private User  user;
    private boolean needsToken;
    private TextView password;
    private TextView tokenCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_activity);
        handleIntent();
    }

    private void handleIntent() {
        if(getIntent() == null || getIntent().getParcelableExtra(USER_KEY) == null){
            finish(RESULT_CANCELED);
            return;
        }
        user = getIntent().getParcelableExtra(USER_KEY);
        needsToken = getIntent().getBooleanExtra(NEEDS_TOKEN, true);
        setContent();
    }

    private void setContent() {
        password = (TextView) findViewById(R.id.password);
        TextView text = (TextView) findViewById(R.id.text);
        if(needsToken){
            tokenCode = (TextView) findViewById(R.id.token_code);
            tokenCode.setVisibility(View.VISIBLE);
            TextView subtitle = (TextView) findViewById(R.id.subtitle);
            subtitle.setText(getString(R.string.login_dialog_token_subtitle));
            text.setText(getString(R.string.login_dialog_token_text));
        }else{
            text.setText(getString(R.string.login_dialog_text));
        }
        findViewById(R.id.ignore_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(RESULT_CANCELED);
            }
        });
        findViewById(R.id.forgot_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(FORGOT_PASSWORD_RESULT);
            }
        });
        findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> errors = validate();
                if (errors != null && errors.size() > 0) {
                    DialogUtil.toast(LoginDialogActivity.this, getString(R.string.login_invalid_inputs_error_title),
                            getString(R.string.login_invalid_inputs_error_subtitle),
                            errors);
                } else {
                    new ReuqestTask().execute();
                }
            }
        });
        findViewById(R.id.container).setOnClickListener(null);
        findViewById(R.id.outside).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(RESULT_CANCELED);
            }
        });
    }

    private ArrayList<String> validate() {
        ArrayList<String> errors = new ArrayList<>();
        if (TextUtils.isEmpty(password.getText())) {
            errors.add(getString(R.string.login_dialog_invalid_password));
        }
        if (needsToken && TextUtils.isEmpty(tokenCode.getText())) {
            errors.add(getString(R.string.login_dialog_invalid_token));
        }
        return errors;
    }

    private void finish(int result){
        setResult(result);
        finish();
    }

    private class ReuqestTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
