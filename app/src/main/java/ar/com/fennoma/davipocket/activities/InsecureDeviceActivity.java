package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.session.Session;

public class InsecureDeviceActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insecure_device_layout);
        setButton();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Session.getCurrentSession(this).logout();
    }

    private void setButton() {
        View restartButton = findViewById(R.id.restart_app);
        if(restartButton == null){
            return;
        }
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutAndCloseApp().execute();
            }
        });
    }

    private class LogoutAndCloseApp extends BaseLogoutTask{
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideLoading();
            Session.getCurrentSession(InsecureDeviceActivity.this).logout();
            finish();
        }
    }
}
