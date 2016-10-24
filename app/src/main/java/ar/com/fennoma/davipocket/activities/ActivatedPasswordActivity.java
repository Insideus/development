package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.session.Session;

public class ActivatedPasswordActivity extends BaseActivity {

    public static String LOGIN_RESPONSE_KEY = "login_response_key";

    private LoginResponse loginResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activated_password_activity_layout);

        if (savedInstanceState != null) {
            loginResponse = savedInstanceState.getParcelable(LOGIN_RESPONSE_KEY);
        } else {
            loginResponse = getIntent().getParcelableExtra(LOGIN_RESPONSE_KEY);
        }

        setActionBar(getString(R.string.activated_password_activity_title), false);
        setButton();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(LOGIN_RESPONSE_KEY, loginResponse);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loginResponse = savedInstanceState.getParcelable(LOGIN_RESPONSE_KEY);
    }

    private void setButton() {
        View continueButton = findViewById(R.id.continue_button);
        if(continueButton == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginResponse.getSid() != null && loginResponse.getSid().length() > 0) {
                    LoginSteps step = LoginSteps.getStep(loginResponse.getAccountStatus());
                    Session.getCurrentSession(getApplicationContext()).loginUser(loginResponse.getSid(),
                            loginResponse.getAccountStatus());
                    if (step == null) {
                        step = LoginSteps.REGISTRATION_COMPLETED;
                    }
                    goToRegistrationStep(step);
                } else {
                    Intent intent = new Intent(ActivatedPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finishActivity();
                }
            }
        });
    }

    private void finishActivity() {
        this.finish();
    }

}
