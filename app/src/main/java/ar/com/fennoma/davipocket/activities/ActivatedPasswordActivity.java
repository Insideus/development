package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ar.com.fennoma.davipocket.R;

public class ActivatedPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activated_password_activity_layout);
        setActionBar(getString(R.string.activated_password_activity_title), false);

    }
}
