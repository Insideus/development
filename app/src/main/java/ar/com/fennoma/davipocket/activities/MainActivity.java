package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.trusteer.tas.TAS_CLIENT_INFO;
import com.trusteer.tas.atas;
import com.trusteer.tas.tas;

import ar.com.fennoma.davipocket.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tasInit();
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

}
