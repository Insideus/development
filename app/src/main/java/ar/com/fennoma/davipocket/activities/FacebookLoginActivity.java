package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;

public class FacebookLoginActivity extends BaseActivity {

    private static String CONTINUE_WITHOUT_LOGIN_TOKEN = "-1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_activity_layout);
        setActionBar(getString(R.string.facebook_login_token_activity_title), false);
        setLoginButtons();
    }

    private void setLoginButtons() {
        View login = findViewById(R.id.send_button);
        View loginWOConnection = findViewById(R.id.do_login_without_connection_button);
        if(login == null || loginWOConnection == null){
            return;
        }
        login.setOnClickListener(new FacebookOnClickListener());
        loginWOConnection.setOnClickListener(new FacebookOnClickListener());
    }

    private class FacebookOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            startActivity(new Intent(FacebookLoginActivity.this, LoginConfirmationActivity.class));
        }
    }

    public class FacebookLoginTask extends AsyncTask<String, Void, Boolean> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean response = null;
            try {
                response = Service.facebookConnect(params[0], params[1]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            /*
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error);

            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid());
                if(step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                goToRegistrationStep(step);
            }
            */
            hideLoading();
        }
    }

}
