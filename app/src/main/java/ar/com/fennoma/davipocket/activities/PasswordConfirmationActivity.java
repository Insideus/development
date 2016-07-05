package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ar.com.fennoma.davipocket.R;

public class PasswordConfirmationActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_confirmation_activity_layout);
        setActionBar(getString(R.string.password_confirmation_title), false);
    }
}
