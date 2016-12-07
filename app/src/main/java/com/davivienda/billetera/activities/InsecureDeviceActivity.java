package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.davivienda.billetera.R;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.utils.SharedPreferencesUtils;

public class InsecureDeviceActivity extends BaseActivity {

    public static final String PERMISSIONS_KEY = "permissions_key";

    private Boolean permissionsProblem = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            permissionsProblem = savedInstanceState.getBoolean(PERMISSIONS_KEY, false);
        } else {
            permissionsProblem = getIntent().getBooleanExtra(PERMISSIONS_KEY, false);
        }
        setContentView(R.layout.insecure_device_layout);
        setToolbar(R.id.toolbar_layout, true, getString(R.string.app_name).toUpperCase());
        setLayoutText();
        setButton();
    }

    private void setLayoutText() {
        if(permissionsProblem) {
            TextView text = (TextView) findViewById(R.id.text);
            text.setText(getString(R.string.not_permissions_explanation));
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new LogoutAndCloseApp(this).execute();
    }

    private void setButton() {
        View restartButton = findViewById(R.id.restart_app);
        if(restartButton == null){
            return;
        }
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutAndCloseApp(InsecureDeviceActivity.this).execute();
            }
        });
    }

    private class LogoutAndCloseApp extends BaseLogoutTask {

        public LogoutAndCloseApp(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideLoading();
            Session.getCurrentSession(InsecureDeviceActivity.this).logout();
            SharedPreferencesUtils.logOut();
            finish();
        }

    }

}
