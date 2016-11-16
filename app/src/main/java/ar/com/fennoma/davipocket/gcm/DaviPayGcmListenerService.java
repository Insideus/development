package ar.com.fennoma.davipocket.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import ar.com.fennoma.davipocket.session.Session;

public class DaviPayGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if(Session.getCurrentSession(getApplicationContext()).isValid()) {
            String text = data.getString("message");
            if(text != null && text.length() > 2) {
                Log.d("GCM", text);
            }
        }
    }

}
