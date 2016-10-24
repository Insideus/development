package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.tasks.GetInitDataTask;
import ar.com.fennoma.davipocket.tasks.TaskCallback;
import ar.com.fennoma.davipocket.utils.ConnectionUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.Todo1Utils;

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
        checkInternetConnection();
        //String deviceId = EasySolutionsUtils.getDeviceId(this);
        //EasySolutionsUtils.scanDeviceStatus(this);
        //EasySolutionsUtils.scanDeviceHosts(this);
        Todo1Utils.initMobileSdk(this);
    }

    private void checkInternetConnection() {
        if(ConnectionUtils.checkInternetConnection()){
            GetInitDataTask task = new GetInitDataTask(this, new TaskCallback() {
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
                /*
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                */
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RETRY_CONNECTION_REQUEST){
            checkInternetConnection();
        }
    }

}