package com.davivienda.billetera.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.davivienda.billetera.DaviPayApplication;
import com.davivienda.billetera.model.User;

public class SharedPreferencesUtils {

    private static final String SP_USER_DAVI_POINTS = "sharedprefs_user_davipoints";
    private static final String SP_USER_LAST_LOGIN = "sharedprefs_user_lastlogin";
    private static final String SP_USER_NAME = "sharedprefs_user_name";
    public static final String FALSE = "false";
    private static final String SP_USER_CAN_USE_POINTS = "sharedprefs_user_can_use_points";
    private static final String POINTS_EQUIVALENCE = "sharedprefs_points'_equivalence";

    public static void setString(String key, String content){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DaviPayApplication.getInstance()).edit();
        editor.putString(key, content);
        editor.apply();
    }

    public static void setInt(String key, int value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DaviPayApplication.getInstance()).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void setBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DaviPayApplication.getInstance()).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(String key){
        return PreferenceManager.getDefaultSharedPreferences(DaviPayApplication.getInstance()).getBoolean(key, false);
    }

    public static String getString(String key){
        return PreferenceManager.getDefaultSharedPreferences(DaviPayApplication.getInstance()).getString(key, "");
    }

    private static int getInt(String key) {
        return PreferenceManager.getDefaultSharedPreferences(DaviPayApplication.getInstance()).getInt(key, 0);
    }

    public static void setUser(User user) {
        setInt(SP_USER_DAVI_POINTS, user.getPointsInt());
        setString(SP_USER_LAST_LOGIN, user.getLastLogin());
        setString(SP_USER_NAME, user.getName());
        setInt(POINTS_EQUIVALENCE, user.getPointsEquivalence());
        setBoolean(SP_USER_CAN_USE_POINTS, user.getCanUseDavipoints());
    }

    public static User getUser() {
        User user = new User(getInt(SP_USER_DAVI_POINTS),
                getString(SP_USER_LAST_LOGIN),
                getString(SP_USER_NAME),
                getBoolean(SP_USER_CAN_USE_POINTS),
                getInt(POINTS_EQUIVALENCE));
        return user;
    }

    public static void logOut() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DaviPayApplication.getInstance()).edit();
        editor.remove(SP_USER_DAVI_POINTS);
        editor.remove(SP_USER_LAST_LOGIN);
        editor.remove(SP_USER_NAME);
        editor.remove(SP_USER_CAN_USE_POINTS);
        editor.remove(POINTS_EQUIVALENCE);
        editor.apply();
    }

    public static int getPointsEquivalence() {
        return getInt(POINTS_EQUIVALENCE);
    }

}