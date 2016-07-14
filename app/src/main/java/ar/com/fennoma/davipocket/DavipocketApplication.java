package ar.com.fennoma.davipocket;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public class DavipocketApplication extends MultiDexApplication {

    private static DavipocketApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initFacebookSdk();
        instance = this;
    }

    public static DavipocketApplication getInstance(){
        return instance;
    }

    private void initFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

}
