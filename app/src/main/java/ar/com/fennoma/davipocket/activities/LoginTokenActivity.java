package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ar.com.fennoma.davipocket.R;

public class LoginTokenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_login);
        setActionBar(getString(R.string.login_token_activity_title), true);
        setLoginButton();
    }

    private void setLoginButton() {
        View loginButton = findViewById(R.id.send_button);
        if(loginButton == null){
            return;
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginTokenActivity.this, FacebookLoginActivity.class));
            }
        });
    }

}
