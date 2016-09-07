package ar.com.fennoma.davipocket;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.splunk.mint.Mint;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public class DavipocketApplication extends MultiDexApplication {

    private static DavipocketApplication instance;
    private static ImageLoader imageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initFacebookSdk();
        instance = this;
        Mint.initAndStartSession(this, "3cef664b");
        initImageLoader();
    }

    public static DavipocketApplication getInstance(){
        return instance;
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

    public static ImageLoader getImageManager() {
        return imageManager;
    }

}
