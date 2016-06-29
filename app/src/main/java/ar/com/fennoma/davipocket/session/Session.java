package ar.com.fennoma.davipocket.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Julian Vega on 14/06/2016.
 */
public class Session {

    private static String PREFERENCES = "PreferencesFile";
    private static String PREF_SESSION_KEY = "SessionKey";
    private static String USER_INFORMATION = "UserInformation";

    private static Session instance = null;
    private SharedPreferences sharedPreferences;

    private Session() {

    }

    public static synchronized Session getCurrentSession(Context context) {
        if (instance == null) {
            instance = new Session();
            instance.sharedPreferences = context.getSharedPreferences(PREFERENCES, 0);
            /*
            instance.sid = instance.sharedPreferences.getString(PREF_SESSION_KEY, null);
            try {
                String jsonUser = instance.sharedPreferences.getString(USER_INFORMATION, null);
                if (jsonUser != null) {
                    JSONObject jsonObject = new JSONObject(jsonUser);
                    instance.user = User.fromJson(jsonObject);
                }
                String jsonUserSettings = instance.sharedPreferences.getString(USER_SETTINGS, null);
                if (jsonUserSettings != null) {
                    JSONObject jsonObject = new JSONObject(jsonUserSettings);
                    instance.userSettings = UserSettings.fromJson(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            */
        }
        return instance;
    }

    public boolean isValid() {
        return false;
    }

}
