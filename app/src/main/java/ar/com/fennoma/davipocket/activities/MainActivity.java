package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.trusteer.tas.TAS_CLIENT_INFO;
import com.trusteer.tas.atas;
import com.trusteer.tas.tas;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(R.id.toolbar_layout, false);
        //setActionBar(getString(R.string.main_activity_title), false);
        tasInit();
        setButtons();
    }

    private boolean tasInit() {
        int initFlags = tas.TAS_INIT_MANUAL_BG_OPS;
        TAS_CLIENT_INFO client = new TAS_CLIENT_INFO();
        // Replace the following three items with the values from the
        // license file received from Trusteer
        client.setVendorId("YourBankHere.com"); // Replace with Vendor ID
        client.setClientId("YourBankHere.bankingapp"); // Replace with Client ID
        client.setClientKey("ABCD1234..."); //Replace with Client Key
        if (atas.TasInitialize(this, client, initFlags) != tas.TAS_RESULT_SUCCESS) {
            Log.d("TAZ", "TAZ Initialization failed.");
        }
        return true;
    }

    private void setButtons() {
        View logoutButton = findViewById(R.id.logout_button);
        if(logoutButton == null){
            return;
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutTask().execute();
            }
        });
    }

    public class LogoutTask extends AsyncTask<Void, Void, Boolean> {

        String errorCode;

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
                response = Service.logout(sid);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, "");
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                goLoginActivity();
            }
            hideLoading();
        }
    }

    private void goLoginActivity() {
        Intent facebookIntent = new Intent(this, LoginActivity.class);
        facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(facebookIntent);
        finish();
    }

}
