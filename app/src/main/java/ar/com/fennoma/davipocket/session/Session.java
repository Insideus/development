package ar.com.fennoma.davipocket.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.model.BankProduct;
import ar.com.fennoma.davipocket.model.Country;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.UserInterest;

public class Session {

    private static String PREFERENCES = "PreferencesFile";
    private static String PERSON_ID_TYPES = "PersonIdTypes";
    private static String COUNTRIES = "Countries";
    private static String BANK_PRODUCTS = "BankProducts";
    private static String USER_INTERESTS = "UserInterests";
    private static String PREF_SESSION_KEY = "SessionKey";
    private static String USER_INFORMATION = "UserInformation";

    private static Session instance = null;
    private SharedPreferences sharedPreferences;
    private ArrayList<PersonIdType> personIdTypes;
    private ArrayList<Country> countries;
    private ArrayList<BankProduct> bankProducts;
    private ArrayList<UserInterest> userInterests;
    private String sid;

    private Session() {

    }

    public static synchronized Session getCurrentSession(Context context) {
        if (instance == null) {
            instance = new Session();
            instance.sharedPreferences = context.getSharedPreferences(PREFERENCES, 0);
            instance.sid = instance.sharedPreferences.getString(PREF_SESSION_KEY, null);
            try {
                String jsonIdTypes = instance.sharedPreferences.getString(PERSON_ID_TYPES, null);
                if (jsonIdTypes != null) {
                    JSONObject jsonObject = new JSONObject(jsonIdTypes);
                    instance.personIdTypes = PersonIdType.fromJsonArray(jsonObject);
                }
                String jsonCountries = instance.sharedPreferences.getString(COUNTRIES, null);
                if (jsonCountries != null) {
                    JSONObject jsonObject = new JSONObject(jsonCountries);
                    instance.countries = Country.fromJsonArray(jsonObject);
                }
                String jsonBankProducts = instance.sharedPreferences.getString(BANK_PRODUCTS, null);
                if (jsonBankProducts != null) {
                    JSONObject jsonObject = new JSONObject(jsonBankProducts);
                    instance.bankProducts = BankProduct.fromJsonArray(jsonObject);
                }
                String jsonUserInterests = instance.sharedPreferences.getString(USER_INTERESTS, null);
                if (jsonUserInterests != null) {
                    JSONObject jsonObject = new JSONObject(jsonUserInterests);
                    instance.userInterests = UserInterest.fromJsonArray(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public String getSid() {
        return instance.sid;
    }

    public void loginUser(String sid) {
        this.sid = sid;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SESSION_KEY, sid);
        editor.commit();
        this.sid = sid;
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_SESSION_KEY);
        editor.commit();
        this.sid = null;
        LoginManager.getInstance().logOut();
    }

    public ArrayList<PersonIdType> getPersonIdTypes() {
        return instance.personIdTypes;
    }

    public void setPersonIdTypes(ArrayList<PersonIdType> types, String typesJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERSON_ID_TYPES, typesJson);
        this.personIdTypes = types;
        editor.commit();
    }

    public ArrayList<Country> getCountries() {
        return instance.countries;
    }

    public void setCountries(ArrayList<Country> countries, String countriesJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COUNTRIES, countriesJson);
        this.countries = countries;
        editor.commit();
    }

    public ArrayList<BankProduct> getBankProducts() {
        return instance.bankProducts;
    }

    public void setBankProducts(ArrayList<BankProduct> bankProducts, String bankProductsJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BANK_PRODUCTS, bankProductsJson);
        this.bankProducts = bankProducts;
        editor.commit();
    }

    public ArrayList<UserInterest> getUserInterests() {
        return instance.userInterests;
    }

    public void setUserInterests(ArrayList<UserInterest> userInterests, String userInterestsJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_INTERESTS, userInterestsJson);
        this.userInterests = userInterests;
        editor.commit();
    }

    public boolean isValid() {
        return this.sid != null;
    }

}
