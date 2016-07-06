package ar.com.fennoma.davipocket.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.model.Country;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;

/**
 * Created by Julian Vega on 05/07/2016.
 */
public class GetInitDataTask extends AsyncTask<Void, Void, Boolean> {

    private TaskCallback callback;
    private Activity act;

    public GetInitDataTask(Activity act, TaskCallback callback) {
        this.act = act;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean success = true;
        try {
            JSONObject typesJson = Service.getPersonIdTypes();
            ArrayList<PersonIdType> types = PersonIdType.fromJsonArray(typesJson);
            Session.getCurrentSession(act).setPersonIdTypes(types, typesJson.toString());

            JSONObject countriesJson = Service.getCountries();
            ArrayList<Country> countries = Country.fromJsonArray(countriesJson);
            Session.getCurrentSession(act).setCountries(countries, countriesJson.toString());
        }  catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(callback != null) {
            callback.execute(success);
        }
    }
}
