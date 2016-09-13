package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import ar.com.fennoma.davipocket.utils.SharedPreferencesUtils;

public class MainActivity extends BaseActivity {

    public static final String OPEN_TOUR = "tour open";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(R.id.toolbar_layout, false);
        tasInit();
        setButtons();
        checkForTour();
    }

    private void checkForTour() {
        if (getIntent() != null && getIntent().getBooleanExtra(OPEN_TOUR, false)) {
            startTour();
            return;
        }
        if (TextUtils.isEmpty(SharedPreferencesUtils.getString(OPEN_TOUR))) {
            startTour();
            SharedPreferencesUtils.setString(OPEN_TOUR, SharedPreferencesUtils.FALSE);
        }
    }

    private void startTour() {
        startActivity(new Intent(this, TourActivity.class));
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void goToHome() {
        startTour();
    }

    private void setButtons() {
        View logoutButton = findViewById(R.id.logout_button);
        if (logoutButton == null) {
            return;
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutTask().execute();
            }
        });
    }
}
