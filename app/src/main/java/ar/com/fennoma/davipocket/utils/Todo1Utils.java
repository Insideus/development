package ar.com.fennoma.davipocket.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rsa.mobilesdk.sdk.MobileAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Julian Vega on 23/09/2016.
 */
public class Todo1Utils {

    private static String TAG = "MobileSDK";

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 153;//A unique code in this app

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
        requestPermissions(act, MobileAPI.COLLECT_BASIC_DEVICE_DATA_ONLY);
        String mResultStr = mobileAPI.collectInfo();
        return mResultStr;
    }


    public static void doRestartCollection(AppCompatActivity act) {
        MobileAPI mobileAPI = MobileAPI.getInstance(act);
        mobileAPI.destroy();
        requestPermissions(act, MobileAPI.COLLECT_BASIC_DEVICE_DATA_ONLY);
        mobileAPI.initSDK(getSdkProperties());
    }


    private static Properties getSdkProperties() {
        Properties properties = new Properties();

        properties.setProperty(MobileAPI.CONFIGURATION_KEY, "" + MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION);
        properties.setProperty(MobileAPI.TIMEOUT_MINUTES_KEY, "" + MobileAPI.TIMEOUT_DEFAULT_VALUE);
        properties.setProperty(MobileAPI.BEST_LOCATION_AGE_MINUTES_KEY, "" + MobileAPI.BEST_LOCATION_AGE_MINUTES_DEFAULT_VALUE);
        properties.setProperty(MobileAPI.MAX_LOCATION_AGE_DAYS_KEY, "" + MobileAPI.MAX_LOCATION_AGE_DAYS_DEFAULT_VALUE);
        properties.setProperty(MobileAPI.ADD_TIMESTAMP_KEY, "1");
        // override max accuracy - it is 100 meter by default, but we get
        // network location with higher
        // accuracy, we want to force GPS locations
        properties.setProperty(MobileAPI.MAX_ACCURACY_KEY, "" + 50);

        return properties;
    }

    public static void requestPermissions(AppCompatActivity act, int collectionMode){

        List<String> permissionsList = new ArrayList<>();
        List<String>  permissionsNeeded = new ArrayList<>();

        if(collectionMode >= MobileAPI.COLLECT_BASIC_DEVICE_DATA_ONLY){
            if (!addPermission(act, permissionsList, Manifest.permission.READ_PHONE_STATE)){
                permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            }
        }

        if(collectionMode >= MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION){
            if (!addPermission(act, permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)){
                permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (!addPermission(act, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)){
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        if(!permissionsList.isEmpty()){
            ActivityCompat.requestPermissions(act, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }

    }

    private static boolean addPermission(AppCompatActivity act, List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(act, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(act, permission)){
                return false;
            }
        }
        return true;
    }


}
