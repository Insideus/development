package ar.com.fennoma.davipocket;

import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.splunk.mint.Mint;

import net.easysol.dsb.DSB;

import java.util.Locale;

import ar.com.fennoma.davipocket.constants.Constants;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public class DavipocketApplication extends MultiDexApplication {

    private static DavipocketApplication instance;
    private static ImageLoader imageManager;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        newConfig.locale = new Locale("es", "ES");
        super.onConfigurationChanged(newConfig);
        Locale.setDefault(newConfig.locale);
        getBaseContext().getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        onConfigurationChanged(getBaseContext().getResources().getConfiguration());
        initFacebookSdk();
        Mint.initAndStartSession(this, "3cef664b");
        initImageLoader();
        //initEasySolution();
        //initEasySolutionMalwareProtection();
        instance = this;
    }

    private void initFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCacheSize(5 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .build();
        imageManager = ImageLoader.getInstance();
        imageManager.init(config);
    }

    private void initEasySolution() {
        DSB.sdk(this).init(Constants.EASY_SOLUTIONS_DESARROLLO_LICENCIA);
    }

    private void initEasySolutionMalwareProtection() {
        DSB.sdk(this).MALWARE_PROTECTOR_API.startOverlappingProtection(this);
    }

    public static DavipocketApplication getInstance(){
        return instance;
    }

    public static ImageLoader getImageManager() {
        return imageManager;
    }

}
