package ar.com.fennoma.davipocket.utils.risk;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rsa.mobilesdk.sdk.MobileAPI;

import java.util.Properties;

/**
 * Created by Julian Vega on 23/09/2016.
 */
public class Todo1Utils extends RiskUtils {

    private static String TAG = "MobileSDK";

    public static void initMobileSdk(AppCompatActivity act) {
        MobileAPI mobileAPI = MobileAPI.getInstance(act);
        Log.d(TAG, "MobileSDK - Calling init sdk");
        mobileAPI.initSDK(getSdkProperties());
    }

    public static void destroyMobileSdk(AppCompatActivity act) {
        MobileAPI mobileAPI = MobileAPI.getInstance(act);
        Log.d(TAG, "MobileSDK - Calling destroy");
        mobileAPI.destroy();
    }

    public static String getData(AppCompatActivity act) {
        MobileAPI mobileAPI = MobileAPI.getInstance(act);
        requestPermissions(act);
        String mResultStr = mobileAPI.collectInfo();
        return mResultStr;
    }


    public static void doRestartCollection(AppCompatActivity act) {
        MobileAPI mobileAPI = MobileAPI.getInstance(act);
        mobileAPI.destroy();
        requestPermissions(act);
        mobileAPI.initSDK(getSdkProperties());
    }


    private static Properties getSdkProperties() {
        Properties properties = new Properties();
        properties.setProperty(MobileAPI.CONFIGURATION_KEY, "" + MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION);
        properties.setProperty(MobileAPI.TIMEOUT_MINUTES_KEY, "" + MobileAPI.TIMEOUT_DEFAULT_VALUE);
        properties.setProperty(MobileAPI.BEST_LOCATION_AGE_MINUTES_KEY, "" + MobileAPI.BEST_LOCATION_AGE_MINUTES_DEFAULT_VALUE);
        properties.setProperty(MobileAPI.MAX_LOCATION_AGE_DAYS_KEY, "" + MobileAPI.MAX_LOCATION_AGE_DAYS_DEFAULT_VALUE);
        properties.setProperty(MobileAPI.ADD_TIMESTAMP_KEY, "1");
        properties.setProperty(MobileAPI.MAX_ACCURACY_KEY, "" + 100);
        return properties;
    }

}
