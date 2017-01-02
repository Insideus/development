package com.davivienda.billetera;

import android.support.multidex.MultiDexApplication;

import com.davivienda.billetera.activities.BaseActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.splunk.mint.Mint;

import com.davivienda.billetera.utils.risk.EasySolutionsUtils;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public class DaviPayApplication extends MultiDexApplication {

    private static DaviPayApplication instance;
    private static ImageLoader imageManager;
    private static BaseActivity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        initFacebookSdk();
        Mint.initAndStartSession(this, "3cef664b");
        EasySolutionsUtils.initEasySolution(this, getApplicationContext());
        initImageLoader();
        instance = this;
        setActivity(null);
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

    public static DaviPayApplication getInstance(){
        return instance;
    }

    public static ImageLoader getImageManager() {
        return imageManager;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public void setActivity(BaseActivity activity) {
        DaviPayApplication.activity = activity;
    }


}
