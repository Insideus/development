package ar.com.fennoma.davipocket.session;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.model.PersonIdType;

/**
 * Created by Julian Vega on 14/06/2016.
 */
public class Session {

    private static String PREFERENCES = "PreferencesFile";
    private static String PREF_SESSION_KEY = "SessionKey";
    private static String USER_INFORMATION = "UserInformation";
    private static String PERSON_ID_TYPES = "PersonIdTypes";

    private static Session instance = null;
    private SharedPreferences sharedPreferences;
    private ArrayList<PersonIdType> personIdTypes;

    private Session() {

    }

    public static synchronized Session getCurrentSession(Context context) {
        if (instance == null) {
            instance = new Session();
            instance.sharedPreferences = context.getSharedPreferences(PREFERENCES, 0);
            //instance.sid = instance.sharedPreferences.getString(PREF_SESSION_KEY, null);
            try {
                String jsonIdTypes = instance.sharedPreferences.getString(PERSON_ID_TYPES, null);
                if (jsonIdTypes != null) {
                    JSONObject jsonObject = new JSONObject(jsonIdTypes);
                    instance.personIdTypes = PersonIdType.fromJsonArray(jsonObject);
                }
                /*
                String jsonUserSettings = instance.sharedPreferences.getString(USER_SETTINGS, null);
                if (jsonUserSettings != null) {
                    JSONObject jsonObject = new JSONObject(jsonUserSettings);
                    instance.userSettings = UserSettings.fromJson(jsonObject);
                }
                */
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public void setPersonIdTypes(ArrayList<PersonIdType> types, String typesJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERSON_ID_TYPES, typesJson);
        this.personIdTypes = types;
        editor.commit();
    }

    public boolean isValid() {
        return false;
    }

    public ArrayList<PersonIdType> getPersonIdTypes() {
        return instance.personIdTypes;
    }

}
