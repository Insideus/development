package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.tasks.GetInitDataTask;
import ar.com.fennoma.davipocket.tasks.TaskCallback;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        startAnimating();
        GetInitDataTask task = new GetInitDataTask(this, new TaskCallback() {
            @Override
            public void execute(Object result) {
                goToLogin();
            }
        });
        task.execute();
    }

    private void goToLogin() {
        //Intent intent = new Intent(SplashActivity.this, LoginTokenActivity.class);
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void startAnimating() {
        Animation an = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        an.setDuration(1000);
        an.setRepeatCount(-1);
        an.setRepeatMode(Animation.INFINITE);
        an.setFillAfter(false);
        an.setInterpolator(new LinearInterpolator());
        View splashLoading = findViewById(R.id.splash_loading);
        if(splashLoading == null){
            return;
        }
        splashLoading.setAnimation(an);
    }

}
