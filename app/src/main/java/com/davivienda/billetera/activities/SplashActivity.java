package com.davivienda.billetera.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.LoginSteps;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.GetInitDataTask;
import com.davivienda.billetera.tasks.TaskCallback;
import com.davivienda.billetera.utils.ConnectionUtils;
import com.davivienda.billetera.utils.DialogUtil;
import com.davivienda.billetera.utils.risk.EasySolutionsUtils;
import com.davivienda.billetera.utils.risk.RiskUtils;

public class SplashActivity extends BaseActivity {

    private static final int RETRY_CONNECTION_REQUEST = 1;
    private View splashLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        splashLoading = findViewById(R.id.splash_loading);
        startAnimating();
        if(RiskUtils.requestPermissions(this)) {
            checkInternetConnectionAndRisk();
        }
    }

    private void checkInternetConnectionAndRisk() {
        new EvaluateRiskTask().execute();
    }

    private class EvaluateRiskTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return isInsecureDevice();
        }

        @Override
        protected void onPostExecute(Boolean insecureDevice) {
            super.onPostExecute(insecureDevice);
            if(insecureDevice == null || insecureDevice) {
                insecureDevice();
            } else {
                secureDevice();
            }
        }

    }

    private boolean isInsecureDevice() {
        if(!EasySolutionsUtils.isSecure(getApplicationContext())) {
            return true;
        }
        if(!EasySolutionsUtils.isSecureCertificate(getApplicationContext())) {
            return true;
        }
        return false;
    }

    private void secureDevice() {
        if (ConnectionUtils.checkInternetConnection()) {
            GetInitDataTask task = new GetInitDataTask(this, false, new TaskCallback() {
                @Override
                public void execute(Object result) {
                    goToLoginOrHome();
                }
            });
            task.execute();
        } else {
            DialogUtil.noConnectionDialog(this, RETRY_CONNECTION_REQUEST);
        }
    }

    private void goToLoginOrHome() {
        if(Session.getCurrentSession(this).isValid()) {
            if(Session.getCurrentSession(this).hasPengingStep()) {
                LoginSteps step = LoginSteps.getStep(Session.getCurrentSession(this).getPendingStep());
                goToRegistrationStep(step);
            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void startAnimating() {
        Animation an = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        an.setDuration(1000);
        an.setRepeatCount(-1);
        an.setRepeatMode(Animation.INFINITE);
        an.setFillAfter(false);
        an.setInterpolator(new LinearInterpolator());
        splashLoading.setAnimation(an);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RiskUtils.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if(checkPermissionsResult(grantResults)) {
                checkInternetConnectionAndRisk();
            } else {
                noPermissionGranteed();
            }
        }
    }

    private boolean checkPermissionsResult(int[] grantResults) {
        boolean result = false;
        for(int i = 0; i < grantResults.length; i++) {
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                result = true;
            } else {
                return false;
            }
        }
        return result;
    }

    private void noPermissionGranteed() {
        Intent intent = new Intent(this, InsecureDeviceActivity.class);
        intent.putExtra(InsecureDeviceActivity.PERMISSIONS_KEY, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RETRY_CONNECTION_REQUEST){
            checkInternetConnectionAndRisk();
        }
    }

}