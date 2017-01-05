package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.LoginResponse;
import com.davivienda.billetera.model.UserLoginType;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.utils.DialogUtil;

import java.util.ArrayList;

public class LoginDialogActivity extends BaseActivity {

    public static final int FORGOT_PASSWORD_RESULT = 2;

    private boolean needsToken;
    private TextView password;
    private TextView tokenCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_activity);
        UserLoginType type = UserLoginType.getType(Session.getCurrentSession(this).getUserLoginType());
        if(type != null) {
            needsToken = false;
            if(type == UserLoginType.TOKEN) {
                needsToken = true;
            }
        } else {
            finish(RESULT_CANCELED);
        }
        setContent();
    }

    private void setContent() {
        password = (TextView) findViewById(R.id.password);
        TextView text = (TextView) findViewById(R.id.text);
        if(needsToken) {
            tokenCode = (TextView) findViewById(R.id.token_code);
            tokenCode.setVisibility(View.VISIBLE);
            TextView subtitle = (TextView) findViewById(R.id.subtitle);
            subtitle.setText(getString(R.string.login_dialog_token_subtitle));
            text.setText(getString(R.string.login_dialog_token_text));
        } else {
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
                    if(needsToken) {
                        new LoginWithTokenTask(LoginDialogActivity.this,
                                Session.getCurrentSession(getApplicationContext()).getUserId(),
                                Session.getCurrentSession(getApplicationContext()).getUserIdType(),
                                password.getText().toString(),
                                tokenCode.getText().toString()).execute();
                    } else {
                        new LoginTask(LoginDialogActivity.this,
                                Session.getCurrentSession(getApplicationContext()).getUserId(),
                                Session.getCurrentSession(getApplicationContext()).getUserIdType(),
                                password.getText().toString()).execute();
                    }
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
        if(TextUtils.isEmpty(password.getText())) {
            errors.add(getString(R.string.login_dialog_invalid_password));
        }
        if(needsToken && TextUtils.isEmpty(tokenCode.getText())) {
            errors.add(getString(R.string.login_dialog_invalid_token));
        }
        return errors;
    }

    private void finish(int result){
        setResult(result);
        finish();
    }

    public class LoginTask extends com.davivienda.billetera.tasks.LoginTask {

        public LoginTask(BaseActivity activity, String personId, String personIdType, String password) {
            super(activity, personId, personIdType, password);
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            if(response != null && response.getSid() != null && response.getSid().length() > 0) {
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid(), response.getAccountStatus());
                finish(RESULT_OK);
            } else {
                finish(RESULT_CANCELED);
            }
        }

    }

    public class LoginWithTokenTask extends com.davivienda.billetera.tasks.LoginWithTokenTask {

        public LoginWithTokenTask(BaseActivity activity, String personId, String personIdType, String password, String token) {
            super(activity, personId, personIdType, password, token);
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            if(response != null && response.getSid() != null && response.getSid().length() > 0) {
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid(), response.getAccountStatus());
                finish(RESULT_OK);
            } else {
                finish(RESULT_CANCELED);
            }
        }

    }

}
