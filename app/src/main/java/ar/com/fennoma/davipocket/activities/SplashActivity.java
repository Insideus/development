package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.session.Session;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        startAnimating();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!Session.getCurrentSession(getApplicationContext()).isValid()) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 1500);

    }

    private void startAnimating() {
        Animation an = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        an.setDuration(1000);
        an.setRepeatCount(-1);
        an.setRepeatMode(Animation.INFINITE);
        an.setFillAfter(false);
        an.setInterpolator(new LinearInterpolator());
        findViewById(R.id.splash_loading).setAnimation(an);
    }

}
