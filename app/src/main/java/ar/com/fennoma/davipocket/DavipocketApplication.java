package ar.com.fennoma.davipocket;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public class DavipocketApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initFacebookSdk();
    }

    private void initFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

}
