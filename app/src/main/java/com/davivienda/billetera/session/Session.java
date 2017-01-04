package com.davivienda.billetera.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.davivienda.billetera.model.BankProduct;
import com.davivienda.billetera.model.Country;
import com.davivienda.billetera.model.PersonIdType;
import com.davivienda.billetera.model.UserInterest;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Session {

    private static String PREFERENCES = "PreferencesFile";
    private static String PERSON_ID_TYPES = "PersonIdTypes";
    private static String COUNTRIES = "Countries";
    private static String BANK_PRODUCTS = "BankProducts";
    private static String USER_INTERESTS = "UserInterests";
    private static String PREF_SESSION_KEY = "SessionKey";
    private static String PENDING_STEP = "PendingStep";
    private static String PREF_GCM_TOKEN_KEY = "GcmTokenKey";
    private static String PREF_USER_ID_KEY = "UserIdKey";
    private static String PREF_USER_ID_TYPE_KEY = "UserIdTypeKey";
    private static String PREF_USER_LOGIN_TYPE_KEY = "UserLoginTypeKey";

    private static Session instance = null;
    private SharedPreferences sharedPreferences;
    private ArrayList<PersonIdType> personIdTypes;
    private ArrayList<Country> countries;
    private ArrayList<BankProduct> bankProducts;
    private ArrayList<UserInterest> userInterests;
    private String sid;
    private String pendingStep;
    private String gcmToken;
    private String userId;
    private String userIdType;
    private String userLoginType;

    private Session() {

    }

    public static synchronized Session getCurrentSession(Context context) {
        if (instance == null) {
            instance = new Session();
            instance.sharedPreferences = context.getSharedPreferences(PREFERENCES, 0);
            instance.sid = instance.sharedPreferences.getString(PREF_SESSION_KEY, null);
            instance.pendingStep = instance.sharedPreferences.getString(PENDING_STEP, null);
            instance.gcmToken = instance.sharedPreferences.getString(PREF_GCM_TOKEN_KEY, null);
            instance.userId = instance.sharedPreferences.getString(PREF_USER_ID_KEY, null);
            instance.userIdType = instance.sharedPreferences.getString(PREF_USER_ID_TYPE_KEY, null);
            instance.userLoginType = instance.sharedPreferences.getString(PREF_USER_LOGIN_TYPE_KEY, null);
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

    public String getGcmToken() {
        return instance.gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_GCM_TOKEN_KEY, gcmToken);
        editor.apply();
    }

    public void loginUser(String sid, String pendingStep) {
        this.sid = sid;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SESSION_KEY, sid);
        editor.putString(PENDING_STEP, pendingStep);
        editor.apply();
        this.sid = sid;
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_SESSION_KEY);
        editor.remove(PENDING_STEP);
        editor.remove(PREF_GCM_TOKEN_KEY);
        editor.remove(PREF_USER_ID_KEY);
        editor.remove(PREF_USER_ID_TYPE_KEY);
        editor.remove(PREF_USER_LOGIN_TYPE_KEY);
        editor.apply();
        this.sid = null;
        this.pendingStep = null;
        this.gcmToken = null;
        this.userId = null;
        this.userIdType = null;
        this.userLoginType = null;
        LoginManager.getInstance().logOut();
    }

    public ArrayList<PersonIdType> getPersonIdTypes() {
        if(instance.personIdTypes == null){
            return new ArrayList<>();
        }
        return instance.personIdTypes;
    }

    public void setPersonIdTypes(ArrayList<PersonIdType> types, String typesJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PERSON_ID_TYPES, typesJson);
        this.personIdTypes = types;
        editor.apply();
    }

    public ArrayList<Country> getCountries() {
        return instance.countries;
    }

    public void setCountries(ArrayList<Country> countries, String countriesJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COUNTRIES, countriesJson);
        this.countries = countries;
        editor.apply();
    }

    public ArrayList<BankProduct> getBankProducts() {
        return instance.bankProducts;
    }

    public void setBankProducts(ArrayList<BankProduct> bankProducts, String bankProductsJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BANK_PRODUCTS, bankProductsJson);
        this.bankProducts = bankProducts;
        editor.apply();
    }

    public ArrayList<UserInterest> getUserInterests() {
        return instance.userInterests;
    }

    public void setUserInterests(ArrayList<UserInterest> userInterests, String userInterestsJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_INTERESTS, userInterestsJson);
        this.userInterests = userInterests;
        editor.apply();
    }

    public boolean isValid() {
        return this.sid != null;
    }

    public String getPendingStep() {
        return instance.pendingStep;
    }

    public void setPendingStep(String pendingStep) {
        this.pendingStep = pendingStep;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PENDING_STEP, pendingStep);
        editor.apply();
    }

    public boolean hasPengingStep() {
        return instance.pendingStep != null;
    }

    public String getUserId() {
        return instance.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserIdType() {
        return instance.userIdType;
    }

    public void setUserIdType(String userIdType) {
        this.userIdType = userIdType;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USER_ID_TYPE_KEY, userIdType);
        editor.apply();
    }

    public String getUserLoginType() {
        return instance.userLoginType;
    }

    public void setUserLoginType(String userLoginType) {
        this.userLoginType = userLoginType;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USER_LOGIN_TYPE_KEY, userLoginType);
        editor.apply();
    }

    public void setLoginUserData(String userId, String userIdType, String userLoginType) {
        setUserId(userId);
        setUserIdType(userIdType);
        setUserLoginType(userLoginType);
    }

    public void removeUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_USER_ID_KEY);
        editor.remove(PREF_USER_ID_TYPE_KEY);
        editor.remove(PREF_USER_LOGIN_TYPE_KEY);
        editor.apply();
        this.userId = null;
        this.userIdType = null;
        this.userLoginType = null;
    }

}
