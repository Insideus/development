package com.davivienda.billetera.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.davivienda.billetera.DaviPayApplication;

public class ConnectionUtils {

    public static boolean checkInternetConnection() {
        return isOnline();
    }

    private static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) DaviPayApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}