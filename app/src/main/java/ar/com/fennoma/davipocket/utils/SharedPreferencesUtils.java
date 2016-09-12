package ar.com.fennoma.davipocket.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ar.com.fennoma.davipocket.DavipocketApplication;

public class SharedPreferencesUtils {
    public static final String FALSE = "false";

    public static void saveData(String key, String content){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DavipocketApplication.getInstance()).edit();
        editor.putString(key, content);
        editor.apply();
    }

    public static String getData(String key){
        return PreferenceManager.getDefaultSharedPreferences(DavipocketApplication.getInstance()).getString(key, "");
    }
}
