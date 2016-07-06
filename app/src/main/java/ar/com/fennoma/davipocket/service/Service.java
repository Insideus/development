package ar.com.fennoma.davipocket.service;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

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

    private static String GET_ID_TYPES = "/person_id_types";
    private static String GET_COUNTRIES = "/countries";
    private static String LOGIN = "/user/login";
    private static String CONNECT_FACEBOOK = "/user/connect_facebook";

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

    public static LoginResponse login(String personId, String personIdType, String password, String token) throws ServiceException {
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
            if(!TextUtils.isEmpty(token)) {
                Pair<String, String> tokenParam = new Pair("token", token);
                params.add(tokenParam);
            }

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            if(isValidStatusLineCode(urlConnection.getResponseCode())) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = getJsonFromResponse(in);
                JSONObject responseJson = null;
                responseJson = json.getJSONObject(DATA_TAG);
                if(json.has("error") && !json.getBoolean("error")) {
                    loginResponse = LoginResponse.fromJson(responseJson);
                } else {
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    //errorCode = "error.token_required";
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
        return loginResponse;
    }

    public static Boolean facebookConnect(String sid, String facebookToken) throws ServiceException {
        HttpURLConnection urlConnection = null;
        Boolean response = null;
        try {
            urlConnection = getHttpURLConnectionWithHeader(CONNECT_FACEBOOK, sid);
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
                JSONObject responseJson = null;
                if(json.has("error") && !json.getBoolean("error")) {
                    response = true;
                } else {
                    responseJson = json.getJSONObject(DATA_TAG);
                    String errorCode = responseJson.getString(ERROR_CODE_TAG);
                    //errorCode = "error.token_required";
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
        JSONObject jso = new JSONObject(value);
        return jso;
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
