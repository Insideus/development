package ar.com.fennoma.davipocket.utils.risk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian Vega on 08/11/2016.
 */

public class RiskUtils {

    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 193;

    public static boolean requestPermissions(AppCompatActivity act) {

        List<String> permissionsList = new ArrayList<>();
        List<String>  permissionsNeeded = new ArrayList<>();
        if (!addPermission(act, permissionsList, Manifest.permission.READ_PHONE_STATE)){
            permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!addPermission(act, permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)){
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!addPermission(act, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!addPermission(act, permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)){
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!addPermission(act, permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!permissionsList.isEmpty()){
            ActivityCompat.requestPermissions(act, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        } else {
            return true;
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
