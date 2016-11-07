package ar.com.fennoma.davipocket.service;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import org.json.JSONArray;
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

import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.MyCardsResponse;
import ar.com.fennoma.davipocket.model.PaymentDetail;
import ar.com.fennoma.davipocket.model.PreCheckout;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.Store;
import ar.com.fennoma.davipocket.model.StoreCategory;
import ar.com.fennoma.davipocket.model.TransactionDetails;
import ar.com.fennoma.davipocket.model.User;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;

public class Service {

    //private static String BASE_URL = "http://davipocket-stg.paymentez.com/api";
    private final static String BASE_URL = "http://davipocket-dev.paymentez.com/api";
    //private static String BASE_URL = "http://davivienda.fennoma.com.ar/api";
    public final static String IMAGE_BASE_URL = "http://davipocket-dev.paymentez.com";
    private final static int CONNECTION_TIMEOUT = 30000;
    private final static int SUCCESS_CODE = 200;
    private final static String DATA_TAG = "data";
    private final static String ERROR_CODE_TAG = "error_code";
    private final static String ERROR_MESSAGE_TAG = "error_message";
    private final static String METHOD_TAG = "method";
    private final static String NEW_DEVICE_TOKEN_TAG = "new_device_token";
    private final static String NEXT_TOKEN_TAG = "next_token_session";
    private final static String PASSWORD_TOKEN_TAG = "password_token";

    //Init data services
    private final static String GET_ID_TYPES = "/person_id_types";
    private final static String GET_COUNTRIES = "/countries";
    private final static String GET_BANK_PRODUCTS = "/products";
    private final static String GET_USER_INTERESTS = "/categories_of_interest";

    //User services
    private final static String LOGIN = "/user/login";
    private final static String LOGIN_WITH_TOKEN = "/user/login_token";
    private final static String LOGIN_WITH_NEXT_TOKEN = "/user/login_next_token";
    private final static String CONNECT_USER_FACEBOOK = "/user/connect_facebook";
    private final static String UPDATE_USER_INFO = "/user/update_info";
    private final static String USER_ACCOUNT_VALIDATION = "/user/confirm_account_validation";
    private final static String RESEND_USER_VALIDATION_CODE = "/user/validate_account";
    private final static String UPDATE_USER_COMMUNICATIONS_INFO = "/user/update_communications_info";
    private final static String VALIDATE_PRODUCT = "/user/validate_product";
    private final static String VALIDATE_OTP = "/user/validate_otp_pin"; //<<<<
    private final static String SET_PASSWORD = "/user/set_password";
    private final static String FORGOT_PASSWORD = "/user/forgot_password";
    private final static String SET_EXPIRED_PASSWORD = "/user/set_expired_password";
    private final static String LOGOUT = "/user/logout";
    private final static String SET_USER_INTERESTS = "/user/categories_of_interest";
    private final static String GET_USER_CARDS = "/user/cards";
    private final static String GET_USER = "/user";
    private final static String ACCEPT_NEW_DEVICE = "/user/accept_new_device";
    private final static String RESEND_NEW_DEVICE_OTP = "/user/generate_otp_new_device"; //<<<
    private final static String ECARD_STEP = "/user/ecard_step";

    //Card services
    private final static String ACTIVATE_CARD = "/card/activate";
    private final static String ADD_CARD = "/card/add";
    private final static String BLOCK_CARD = "/card/block";
    private final static String SET_FAVOURITE_CARD = "/card/set_default";
    private final static String GET_CARD_MOVEMENTS = "/card/movements";
    private final static String GET_CARD_BALANCE = "/card/balance";
    private static final String PAY_CARD = "/card/pay";
    private static final String NEW_E_CARD = "/card/new";
    private static final String E_CARD_GET_CVV = "/card/get_cvv";
    private static final String E_CARD_SHOW_DATA = "/card/show";
    private static final String RECHARGE_E_CARD = "/card/recharge";

    //Store
    private static final String STORE_LIST = "/store/get_list";
    private static final String STORE_BY_ID = "/store/get_menu";

    //Order
    private static final String PRE_CHECKOUT = "/order/pre_checkout";

    public static JSONObject getPersonIdTypes() {
        HttpURLConnection urlConnection = null;
        JSONObject response = null;
        try {
            urlConnection = getHttpURLConnection(GET_ID_TYPES);
            urlConnection.connect();
            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                if (json.has("error") && !json.getBoolean("error")) {
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
            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                if (json.has("error") && !json.getBoolean("error")) {
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
            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static JSONObject getUserInterests() {
        HttpURLConnection urlConnection = null;
        JSONObject response = null;
        try {
            urlConnection = getHttpURLConnection(GET_USER_INTERESTS);
            urlConnection.connect();
            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static LoginResponse login(String personId, String personIdType, String password, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse loginResponse = null;
        try {
            urlConnection = getHttpURLConnection(LOGIN);
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
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            /*
            Pair<String, String> android = new Pair("android", "true");
            params.add(android);
            */

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String additionalData = null;
                    if (responseJson.has(METHOD_TAG)) {
                        additionalData = responseJson.getString(METHOD_TAG);
                    }
                    if (responseJson.has(PASSWORD_TOKEN_TAG)) {
                        additionalData = responseJson.getString(PASSWORD_TOKEN_TAG);
                    }
                    if (responseJson.has(NEW_DEVICE_TOKEN_TAG)) {
                        additionalData = responseJson.getString(NEW_DEVICE_TOKEN_TAG);
                    }
                    throw new ServiceException(errorCode, additionalData);
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

    public static LoginResponse acceptNewDevice(String newDeviceToken, String pin, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse loginResponse = null;
        try {
            urlConnection = getHttpURLConnection(ACCEPT_NEW_DEVICE);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> deviceToken = new Pair("new_device_token", newDeviceToken);
            params.add(deviceToken);
            if(pin != null && pin.length() > 0) {
                Pair<String, String> pinParam = new Pair("otp_pin", pin);
                params.add(pinParam);
            }
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String additionalData = null;
                    if (responseJson.has(METHOD_TAG)) {
                        additionalData = responseJson.getString(METHOD_TAG);
                    }
                    if (responseJson.has(PASSWORD_TOKEN_TAG)) {
                        additionalData = responseJson.getString(PASSWORD_TOKEN_TAG);
                    }
                    if (responseJson.has(NEW_DEVICE_TOKEN_TAG)) {
                        additionalData = responseJson.getString(NEW_DEVICE_TOKEN_TAG);
                    }
                    throw new ServiceException(errorCode, additionalData);
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

    public static Boolean resendNewDeviceValidationCode(String newDeviceToken) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnection(RESEND_NEW_DEVICE_OTP);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> deviceToken = new Pair("new_device_token", newDeviceToken);
            params.add(deviceToken);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static PaymentDetail balanceDetail(String sid, String fourLastDigits, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        PaymentDetail detail = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(GET_CARD_BALANCE, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitsParam = new Pair("last_digits", fourLastDigits);
            params.add(lastDigitsParam);
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    detail = PaymentDetail.fromJsonObject(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String nextTokenSessionError = "";
                    if (responseJson.has(NEXT_TOKEN_TAG)) {
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
        return detail;
    }

    public static LoginResponse loginWithToken(String personId, String personIdType, String password,
                                               String token, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse loginResponse = null;
        try {
            urlConnection = getHttpURLConnection(LOGIN_WITH_TOKEN);
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
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String additionalData = "";
                    if (responseJson.has(NEXT_TOKEN_TAG)) {
                        additionalData = responseJson.getString(NEXT_TOKEN_TAG);
                    }
                    if (responseJson.has(NEW_DEVICE_TOKEN_TAG)) {
                        additionalData = responseJson.getString(NEW_DEVICE_TOKEN_TAG);
                    }
                    throw new ServiceException(errorCode, additionalData);
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
                                                   String token, String nextTokenSession, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse loginResponse = null;
        try {
            urlConnection = getHttpURLConnection(LOGIN_WITH_NEXT_TOKEN);
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
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String additionalData = "";
                    if (responseJson.has(NEXT_TOKEN_TAG)) {
                        additionalData = responseJson.getString(NEXT_TOKEN_TAG);
                    }
                    if (responseJson.has(NEW_DEVICE_TOKEN_TAG)) {
                        additionalData = responseJson.getString(NEW_DEVICE_TOKEN_TAG);
                    }
                    throw new ServiceException(errorCode, additionalData);
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

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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
                                         String countryId, String birthDate) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(UPDATE_USER_INFO, sid);
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

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static LoginResponse acceptPolicy(String sid, Boolean sms, Boolean phone, Boolean email,
                                       Boolean acceptTerms, Boolean acceptPrivacy) throws ServiceException {
        HttpURLConnection urlConnection = null;
        LoginResponse response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(UPDATE_USER_COMMUNICATIONS_INFO, sid);
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

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if(json.has(DATA_TAG)) {
                    responseJson = json.getJSONObject(DATA_TAG);
                } else {
                    responseJson = json;
                }
                if (json.has("error") && !json.getBoolean("error")) {
                    response = LoginResponse.fromJson(responseJson);
                    response.setSid(sid);
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
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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
                                         String productCode, String productNumber, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        String response = null;
        try {
            urlConnection = getHttpURLConnection(VALIDATE_PRODUCT);
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
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    if (responseJson.has("result")) {
                        response = responseJson.getString("result");
                    } else {
                        if (responseJson.has(PASSWORD_TOKEN_TAG)) {
                            response = responseJson.getString(PASSWORD_TOKEN_TAG);
                        }
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

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
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

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = new JSONObject("{}");
                if (json.has(DATA_TAG)) {
                    responseJson = json.getJSONObject(DATA_TAG);
                }
                if (json.has("error") && !json.getBoolean("error")) {
                    response = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String errorMessage = null;
                    if (responseJson.has(ERROR_MESSAGE_TAG)) {
                        errorMessage = responseJson.getString(ERROR_MESSAGE_TAG);
                    }
                    throw new ServiceException(errorCode, errorMessage);
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
            urlConnection = getHttpURLConnection(SET_EXPIRED_PASSWORD);
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

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = new JSONObject("{}");
                if (json.has(DATA_TAG)) {
                    responseJson = json.getJSONObject(DATA_TAG);
                }
                if (json.has("error") && !json.getBoolean("error")) {
                    response = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    String errorMessage = null;
                    if (responseJson.has(ERROR_MESSAGE_TAG)) {
                        errorMessage = responseJson.getString(ERROR_MESSAGE_TAG);
                    }
                    throw new ServiceException(errorCode, errorMessage);
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

    public static String forgotPassword(String personId, String personIdType, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        String response = null;
        try {
            urlConnection = getHttpURLConnection(FORGOT_PASSWORD);
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
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    if (responseJson.has(METHOD_TAG)) {
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

    public static Boolean setUserInterests(String sid, String interestArray) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(SET_USER_INTERESTS, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> smsParam = new Pair("categories", interestArray);
            params.add(smsParam);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static Boolean setEcardStep(String sid) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(ECARD_STEP, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static MyCardsResponse getUserCards(String sid, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        MyCardsResponse response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(GET_USER_CARDS, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    response = MyCardsResponse.fromJson(responseJson);
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

    public static Boolean logout(String sid) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(LOGOUT, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static Boolean activateCard(String sid, String cardNumber, String todo1, String otpCode,
                                       String lastDigits) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(ACTIVATE_CARD, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> cardNumberParam = new Pair("card_digits", cardNumber);
            params.add(cardNumberParam);
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            addOtpParamIsNeeded(params, otpCode);
            Pair<String, String> lastDigitsParam = new Pair("last_digits", lastDigits);
            params.add(lastDigitsParam);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static Boolean addCard(String sid, String lastDigits, String ccv, String todo1, String otpCode) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(ADD_CARD, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitsParam = new Pair("last_digits", lastDigits);
            params.add(lastDigitsParam);
            Pair<String, String> ccvParam = new Pair("csv", ccv);
            params.add(ccvParam);
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            addOtpParamIsNeeded(params, otpCode);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static Boolean blockCard(String sid, String lastDigits, String todo1, String otpCode) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(BLOCK_CARD, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitsParam = new Pair("last_digits", lastDigits);
            params.add(lastDigitsParam);
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            addOtpParamIsNeeded(params, otpCode);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static Boolean setFavouriteCard(String sid, String cardNumber) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(SET_FAVOURITE_CARD, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));


            if (cardNumber != null && !cardNumber.isEmpty()) {
                List<Pair<String, String>> params = new ArrayList<>();
                Pair<String, String> cardNumberParam = new Pair("last_digits", cardNumber);
                params.add(cardNumberParam);
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
            }

            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
                    response = true;
                } else {
                    responseJson = json.getJSONObject(DATA_TAG);
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    public static TransactionDetails getCardMovementsDetails(String sid, Boolean isECard, String lastDigits,
                                                             int page, String dateFrom,
                                                             String dateTo, String todo1) throws ServiceException {
        HttpURLConnection urlConnection = null;
        TransactionDetails response = null;
        try {
            urlConnection = getHttpURLConnection(sid, GET_CARD_MOVEMENTS);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitParam = new Pair("last_digits", lastDigits);
            params.add(lastDigitParam);
            Pair<String, String> pageParam = new Pair("page_number", String.valueOf(page));
            params.add(pageParam);
            if (!TextUtils.isEmpty(dateFrom)) {
                Pair<String, String> dateFromParam = new Pair("date_from", dateFrom);
                params.add(dateFromParam);
            }
            if (!TextUtils.isEmpty(dateTo)) {
                Pair<String, String> dateToParam = new Pair("date_to", dateTo);
                params.add(dateToParam);
            }
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    response = TransactionDetails.fromJson(isECard, responseJson);
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

    public static User getUser(String sid) throws ServiceException {
        HttpURLConnection urlConnection;
        try {
            urlConnection = getHttpURLConnection(sid, GET_USER);
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    return User.fromJson(responseJson.getJSONObject("user"));
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    throw new ServiceException(errorCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new User();
    }

    public static boolean payCard(String sid, String cardLastDigits, String accountLastDigits,
                                  Double amount, Boolean isUsdPayment, String todo1, String accountCode) throws ServiceException {
        boolean response = false;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(PAY_CARD, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitsParam = new Pair("last_digits", cardLastDigits);
            params.add(lastDigitsParam);
            Pair<String, String> accountNumberParam = new Pair("account_number", accountLastDigits);
            params.add(accountNumberParam);
            Pair<String, String> amountParam = new Pair("amount", CurrencyUtils.getCurrencyForStringWithDecimal(amount));
            params.add(amountParam);
            if(isUsdPayment) {
                Pair<String, String> usdParam = new Pair("is_usd_payment", isUsdPayment.toString());
                params.add(usdParam);
            }
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            Pair<String, String> codeParam = new Pair("account_code", accountCode);
            params.add(codeParam);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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
            if (pair.second == null) {
                result.append(URLEncoder.encode("", "UTF-8"));
            }
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

    public static Card newECard(String sid, String todo1) throws ServiceException {
        Card response = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(NEW_E_CARD, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    response = Card.fromJson(responseJson);
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

    public static Boolean getCVV(String sid, String cardLastDigits, String todo1) throws ServiceException {
        boolean response = false;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(E_CARD_GET_CVV, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitsParam = new Pair("last_digits", cardLastDigits);
            params.add(lastDigitsParam);
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    public static Card getECardData(String sid, String cardLastDigits, String todo1,
                                    String otpCode) throws ServiceException {
        Card response = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(E_CARD_SHOW_DATA, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitsParam = new Pair("last_digits", cardLastDigits);
            params.add(lastDigitsParam);
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            addOtpParamIsNeeded(params, otpCode);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    response = Card.fromJson(responseJson);
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

    public static boolean rechargeECard(String sid, String lastDigits, String accountLastDigits,
                                        Double amount, String todo1, String otpCode, String accountCode) throws ServiceException {
        boolean response = false;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(RECHARGE_E_CARD, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> lastDigitsParam = new Pair("last_digits", lastDigits);
            params.add(lastDigitsParam);
            Pair<String, String> accountNumberParam = new Pair("account_number", accountLastDigits);
            params.add(accountNumberParam);
            Pair<String, String> amountParam = new Pair("amount", CurrencyUtils.getCurrencyForStringWithDecimal(amount));
            params.add(amountParam);
            Pair<String, String> todo1Param = new Pair("todo1", todo1);
            params.add(todo1Param);
            Pair<String, String> codeParam = new Pair("account_code", accountCode);
            params.add(codeParam);
            addOtpParamIsNeeded(params, otpCode);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson;
                if (json.has("error") && !json.getBoolean("error")) {
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

    private static void addOtpParamIsNeeded(List<Pair<String, String>> params, String otpCode) {
        if(otpCode != null && otpCode.length() > 0) {
            Pair<String, String> otpParam = new Pair("otp_pin", otpCode);
            params.add(otpParam);
        }
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String method) throws IOException {
        URL url = new URL(BASE_URL + method);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
            urlConnection.setRequestProperty("Connection", "close");
        }
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
        return urlConnection;
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnectionWithHeader(String method, String token) throws IOException {
        URL url = new URL(BASE_URL + method);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("sid", token);
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
        return urlConnection;
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String sid, String getCardMovements) throws IOException {
        HttpURLConnection urlConnection = getHttpURLConnectionWithHeader(getCardMovements, sid);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        return urlConnection;
    }

    public static List<Store> getStoresWithoutDelivery(String sid, String latitude, String longitude) throws ServiceException {
        List<Store> response = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(STORE_LIST, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            if(!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                List<Pair<String, String>> params = new ArrayList<>();
                Pair<String, String> latitudeParam = new Pair("latitude", latitude);
                params.add(latitudeParam);
                Pair<String, String> longitudeParam = new Pair("longitude", latitude);
                params.add(longitudeParam);

                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
            }
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    response = Store.fromJsonArray(responseJson.getJSONArray("stores"));
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

    public static List<StoreCategory> getStoreById(String sid, String id) throws ServiceException {
        List<StoreCategory> response = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(STORE_BY_ID, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> idParam = new Pair("id", id);
            params.add(idParam);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONArray dataArray = json.getJSONArray(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    JSONObject data = dataArray.getJSONObject(0);
                    response = StoreCategory.fromJSONArray(data.getJSONArray("categories"));
                } else {
                    JSONObject data = dataArray.getJSONObject(0);
                    String errorCode = data.getString(ERROR_CODE_TAG);
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

    public static PreCheckout preCheckout(String sid, String id) throws ServiceException {
        PreCheckout response = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(PRE_CHECKOUT, sid);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            List<Pair<String, String>> params = new ArrayList<>();
            Pair<String, String> idParam = new Pair("id", id);
            params.add(idParam);

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if (isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = json.getJSONObject(DATA_TAG);
                if (json.has("error") && !json.getBoolean("error")) {
                    response = PreCheckout.fromJson(responseJson);
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
}
