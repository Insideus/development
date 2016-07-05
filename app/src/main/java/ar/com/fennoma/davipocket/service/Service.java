package ar.com.fennoma.davipocket.service;

import android.os.Build;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Julian Vega on 04/07/2016.
 */
public class Service {

    private static String BASE_URL = "http://davivienda.fennoma.com.ar/api";
    private static int SUCCESS_CODE = 200;

    private static String GET_ID_TYPES = "/person_id_types";
    private static String LOGIN = "/user/login";

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
                    response = json.getJSONObject("data");
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

    private static boolean isValidStatusLineCode(int statusCode) {
        return statusCode == SUCCESS_CODE;
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

}
