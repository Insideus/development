package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.List;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.LoginSteps;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;

public class FacebookLoginActivity extends BaseActivity {

    private static String CONTINUE_WITHOUT_LOGIN_TOKEN = "-1";

    CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_activity_layout);
        setActionBar(getString(R.string.facebook_login_token_activity_title), false);
        setLoginButtons();
        Session.getCurrentSession(this).setPendingStep(LoginSteps.FACEBOOK.getStep());
        LoginManager.getInstance().logOut();
    }

    private void setLoginButtons() {
        initFacebookLoginApi();
        View login = findViewById(R.id.send_button);
        View loginWOConnection = findViewById(R.id.do_login_without_connection_button);
        if(login == null || loginWOConnection == null){
            return;
        }
        login.setOnClickListener(new FacebookOnClickListener());
        loginWOConnection.setOnClickListener(new WithoutFacebookOnClickListener());
    }

    private void initFacebookLoginApi() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("facebook_login" , loginResult.toString());
                        AccessToken accessToken = loginResult.getAccessToken();
                        new FacebookLoginTask(FacebookLoginActivity.this, accessToken.getToken()).execute();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("facebook_login", "Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("facebook_login" , exception.toString());
                    }
                });
    }

    private class FacebookOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            performFacebookLogin();
        }
    }

    private class WithoutFacebookOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            new FacebookLoginTask(FacebookLoginActivity.this, CONTINUE_WITHOUT_LOGIN_TOKEN).execute();
        }
    }

    private void goToConfirmationActivity() {
        startActivity(new Intent(FacebookLoginActivity.this, LoginConfirmationActivity.class));
        this.finish();
    }

    public class FacebookLoginTask extends DaviPayTask<Boolean> {

        String facebookToken;

        public FacebookLoginTask(BaseActivity activity, String facebookToken) {
            super(activity);
            this.facebookToken = facebookToken;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.facebookConnect(sid, facebookToken);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    //Service error.
                    showServiceGenericError();
                } else {
                    goToConfirmationActivity();
                }
            }
        }

    }

    private void performFacebookLogin() {
        List<String> permissionNeeds= Arrays.asList("email", "user_birthday");
        LoginManager.getInstance().logInWithReadPermissions(this, permissionNeeds);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
