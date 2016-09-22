package ar.com.fennoma.davipocket.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ar.com.fennoma.davipocket.DavipocketApplication;

public class ConnectionUtils {

    public static boolean checkInternetConnection() {
        return isOnline();
    }

    private static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) DavipocketApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}