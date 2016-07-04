package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;

import ar.com.fennoma.davipocket.R;

public class LoginTokenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_login);
        setActionBar(getString(R.string.login_token_activity_title));
    }

}
