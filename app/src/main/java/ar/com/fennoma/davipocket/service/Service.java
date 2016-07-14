package ar.com.fennoma.davipocket.service;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.ServiceException;

/**
 * Created by Julian Vega on 04/07/2016.
 */
public class Service {

    private static String BASE_URL = "http://davivienda.fennoma.com.ar/api";
    private static int SUCCESS_CODE = 200;
    private static String DATA_TAG = "data";
    private static String ERROR_CODE_TAG = "error_code";
    private static String METHOD_TAG = "method";
    private static String NEXT_TOKEN_TAG = "next_token_session";

    private static String GET_ID_TYPES = "/person_id_types";
    private static String GET_COUNTRIES = "/countries";
    private static String GET_BANK_PRODUCTS = "/products";
    private static String LOGIN = "/user/login";
    private static String LOGIN_WITH_TOKEN = "/user/login_token";
    private static String LOGIN_WITH_NEXT_TOKEN = "/user/login_next_token";
    private static String CONNECT_USER_FACEBOOK = "/user/connect_facebook";
    private static String UPDATE_USER_INFO = "/user/update_info";
    private static String USER_ACCOUNT_VALIDATION = "/user/confirm_account_validation";
    private static String RESEND_USER_VALIDATION_CODE = "/user/validate_account";
    private static String UPDATE_USER_COMMUNICATIONS_INFO = "/user/update_communications_info";
    private static String VALIDATE_PRODUCT = "/user/validate_product";
    private static String VALIDATE_OTP = "/user/validate_otp_pin";
    private static String SET_PASSWORD = "/user/set_password";
    private static String FORGOT_PASSWORD = "/user/forgot_password";

    public static JSONObject getPersonIdTypes() {
        HttpURLConnection urlConnection = null;
        JSONObject response = null;
        try {
            urlConnection = getHttpURLConnection(GET_ID_TYPES);
            urlConnection.connect();
            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                if(json.has("error") && !json.getBoolean("error")) {
                    response = json.getJSONObject(DATA_TAG);
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static JSONObject getCountries() {
        HttpURLConnection urlConnection = null;
        JSONObject response = null;
        try {
            urlConnection = getHttpURLConnection(GET_COUNTRIES);
            urlConnection.connect();
            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                if(json.has("error") && !json.getBoolean("error")) {
                    response = json.getJSONObject(DATA_TAG);
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static JSONObject getBankProducts() {
        HttpURLConnection urlConnection = null;
        JSONObject response = null;
        try {
            urlConnection = getHttpURLConnection(GET_BANK_PRODUCTS);
            urlConnection.connect();
            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                if(json.has("error") && !json.getBoolean("error")) {
                    response = json.getJSONObject(DATA_TAG);
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static LoginResponse login(String personId, String personIdType, String password) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse loginResponse = null;
        try {
            urlConnection = getHttpURLConnection(LOGIN);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> personIdTypeParam = new Pair("person_id_type_id", personIdType);
            params.add(personIdTypeParam);
            Pair<String, String> passwordParam = new Pair("password", password);
            params.add(passwordParam);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String method = null;
                    if(responseJson.has(METHOD_TAG)) {
                        method = responseJson.getString(METHOD_TAG);
                    }
                    throw new ServiceException(errorCode, method);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return loginResponse;
    }

    public static LoginResponse loginWithToken(String personId, String personIdType, String password, String token) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse loginResponse = null;
        try {
            urlConnection = getHttpURLConnection(LOGIN_WITH_TOKEN);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> personIdTypeParam = new Pair("person_id_type_id", personIdType);
            params.add(personIdTypeParam);
            Pair<String, String> passwordParam = new Pair("password", password);
            params.add(passwordParam);
            Pair<String, String> tokenParam = new Pair("token", token);
            params.add(tokenParam);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String nextTokenSessionError = "";
                    if(responseJson.has(NEXT_TOKEN_TAG)) {
                        nextTokenSessionError = responseJson.getString(NEXT_TOKEN_TAG);
                    }
                    throw new ServiceException(errorCode, nextTokenSessionError);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return loginResponse;
    }

    public static LoginResponse loginWithNextToken(String personId, String personIdType, String password,
                                                   String token, String nextTokenSession) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse loginResponse = null;
        try {
            urlConnection = getHttpURLConnection(LOGIN_WITH_NEXT_TOKEN);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> personIdTypeParam = new Pair("person_id_type_id", personIdType);
            params.add(personIdTypeParam);
            Pair<String, String> passwordParam = new Pair("password", password);
            params.add(passwordParam);
            Pair<String, String> tokenParam = new Pair("token", token);
            params.add(tokenParam);
            Pair<String, String> nextTokenSessionParam = new Pair("next_token_session", nextTokenSession);
            params.add(nextTokenSessionParam);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String nextTokenSessionError = "";
                    if(responseJson.has(NEXT_TOKEN_TAG)) {
                        nextTokenSessionError = responseJson.getString(NEXT_TOKEN_TAG);
                    }
                    throw new ServiceException(errorCode, nextTokenSessionError);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return loginResponse;
    }

    public static Boolean facebookConnect(String sid, String facebookToken) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(CONNECT_USER_FACEBOOK, sid);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> facebookTokenParam = new Pair("fb_access_token", facebookToken);
            params.add(facebookTokenParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if(json.has("error") && !json.getBoolean("error")) {
                    response = true;
                } else {
                    responseJson = json.getJSONObject(DATA_TAG);
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static Boolean updateUserInfo(String sid, String email, String phone,
                                         String countryId,  String birthDate) throws ServiceException {
        //Parámetros: email, phone, country_id, birth_date (formato dd/MM/yyyy)
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(UPDATE_USER_INFO, sid);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> emailParam = new Pair("email", email);
            params.add(emailParam);
            Pair<String, String> phoneParam = new Pair("phone", phone);
            params.add(phoneParam);
            Pair<String, String> countryParam = new Pair("country_id", countryId);
            params.add(countryParam);
            Pair<String, String> birthDateParam = new Pair("birth_date", birthDate);
            params.add(birthDateParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if(json.has("error") && !json.getBoolean("error")) {
                    response = true;
                } else {
                    responseJson = json.getJSONObject(DATA_TAG);
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static Boolean accountValidation(String sid, String activationCode) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(USER_ACCOUNT_VALIDATION, sid);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> activationCodeParam = new Pair("activation_code", activationCode);
            params.add(activationCodeParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if(json.has("error") && !json.getBoolean("error")) {
                    response = true;
                } else {
                    responseJson = json.getJSONObject(DATA_TAG);
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static Boolean acceptPolicy(String sid, Boolean sms, Boolean phone, Boolean email,
                                       Boolean acceptTerms, Boolean acceptPrivacy) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(UPDATE_USER_COMMUNICATIONS_INFO, sid);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> smsParam = new Pair("allow_sms", sms.toString());
            params.add(smsParam);
            Pair<String, String> phoneParam = new Pair("allow_phone", phone.toString());
            params.add(phoneParam);
            Pair<String, String> emailParam = new Pair("allow_email", email.toString());
            params.add(emailParam);
            Pair<String, String> termsParam = new Pair("accept_terms", acceptTerms.toString());
            params.add(termsParam);
            Pair<String, String> privacyParam = new Pair("accept_privacy", acceptPrivacy.toString());
            params.add(privacyParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if(json.has("error") && !json.getBoolean("error")) {
                    response = true;
                } else {
                    responseJson = json.getJSONObject(DATA_TAG);
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static Boolean resendValidationCode(String sid) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(RESEND_USER_VALIDATION_CODE, sid);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if(json.has("error") && !json.getBoolean("error")) {
                    response = true;
                } else {
                    responseJson = json.getJSONObject(DATA_TAG);
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static String validateProduct(String personId, String personIdType, String productPassword,
                                          String productCode, String productNumber) throws ServiceException {
        HttpURLConnection urlConnection = null;
        String response = null;
        try {
            urlConnection = getHttpURLConnection(VALIDATE_PRODUCT);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> productPasswordParam = new Pair("user_product_password", productPassword);
            params.add(productPasswordParam);
            Pair<String, String> personTypeIdParam = new Pair("person_id_type_id", personIdType);
            params.add(personTypeIdParam);
            Pair<String, String> productCodeParam = new Pair("product_code", productCode);
            params.add(productCodeParam);
            Pair<String, String> userProductParam = new Pair("user_product_number", productNumber);
            params.add(userProductParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    if(responseJson.has("result")) {
                        response = responseJson.getString("result");
                    } else if (responseJson.has("password_token")) {
                        response = responseJson.getString("password_token");
                    }
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static String validateOtp(String personId, String personIdType, String pin) throws ServiceException {
        HttpURLConnection urlConnection = null;
        String response = null;
        try {
            urlConnection = getHttpURLConnection(VALIDATE_OTP);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> personTypeIdParam = new Pair("person_id_type_id", personIdType);
            params.add(personTypeIdParam);
            Pair<String, String> pinParam = new Pair("otp_pin", pin);
            params.add(pinParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    if (responseJson.has("password_token")) {
                        response = responseJson.getString("password_token");
                    }
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static LoginResponse setPassword(String personId, String personIdType, String password,
                                     String passwordToken) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse response = null;
        try {
            urlConnection = getHttpURLConnection(SET_PASSWORD);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> personTypeIdParam = new Pair("person_id_type_id", personIdType);
            params.add(personTypeIdParam);
            Pair<String, String> passwordParam = new Pair("password", password);
            params.add(passwordParam);
            Pair<String, String> passwordTokenParam = new Pair("password_token", passwordToken);
            params.add(passwordTokenParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    response = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String method = null;
                    if(responseJson.has(METHOD_TAG)) {
                        method = responseJson.getString(METHOD_TAG);
                    }
                    throw new ServiceException(errorCode, method);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static LoginResponse setExpiredPassword(String personId, String personIdType,
                           String oldPassword, String password, String passwordToken) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse response = null;
        try {
            urlConnection = getHttpURLConnection(SET_PASSWORD);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> personTypeIdParam = new Pair("person_id_type_id", personIdType);
            params.add(personTypeIdParam);
            Pair<String, String> oldPasswordParam = new Pair("old_password", oldPassword);
            params.add(oldPasswordParam);
            Pair<String, String> passwordParam = new Pair("password", password);
            params.add(passwordParam);
            Pair<String, String> passwordTokenParam = new Pair("password_token", passwordToken);
            params.add(passwordTokenParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    response = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String method = null;
                    if(responseJson.has(METHOD_TAG)) {
                        method = responseJson.getString(METHOD_TAG);
                    }
                    throw new ServiceException(errorCode, method);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static String forgotPassword(String personId, String personIdType) throws ServiceException {
        HttpURLConnection urlConnection = null;
        String response = null;
        try {
            urlConnection = getHttpURLConnection(FORGOT_PASSWORD);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> personIdParam = new Pair("person_id", personId);
            params.add(personIdParam);
            Pair<String, String> personTypeIdParam = new Pair("person_id_type_id", personIdType);
            params.add(personTypeIdParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    if(responseJson.has(METHOD_TAG)) {
                        response = responseJson.getString(METHOD_TAG);
                    }
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    private static boolean isValidStatusLineCode(int statusCode) {
        return statusCode == SUCCESS_CODE;
    }

    private static String getQuery(List<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Pair<String, String> pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }
        return result.toString();
    }

    private static JSONObject getJsonFromResponse(InputStream in) throws IOException, JSONException {
        StringBuilder strBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            strBuilder.append(line);
        }
        String value = strBuilder.toString();
        return new JSONObject(value);
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String method) throws IOException {
        URL url = new URL(BASE_URL + method);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
            urlConnection.setRequestProperty("Connection", "close");
        }
        return urlConnection;
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnectionWithHeader(String method, String token) throws IOException {
        URL url = new URL(BASE_URL + method);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("sid", token);
        return urlConnection;
    }

}
