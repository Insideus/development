package ar.com.fennoma.davipocket.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import ar.com.fennoma.davipocket.R;

public class FacebookLoginActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_activity_layout);
        setActionBar(getString(R.string.facebook_login_token_activity_title), false);
        setLoginButtons();
    }

    private void setLoginButtons() {
        View login = findViewById(R.id.continue_button);
        View loginWOConnection = findViewById(R.id.do_login_without_connection_button);
        if(login == null || loginWOConnection == null){
            return;
        }
        login.setOnClickListener(new MockedOnClickListener());
        loginWOConnection.setOnClickListener(new MockedOnClickListener());
    }

    private class MockedOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            startActivity(new Intent(FacebookLoginActivity.this, LoginConfirmationActivity.class));
        }
    }
}
