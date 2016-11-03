package ar.com.fennoma.davipocket.tasks;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.activities.BaseActivity;
import ar.com.fennoma.davipocket.model.BankProduct;
import ar.com.fennoma.davipocket.model.Country;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.UserInterest;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;

/**
 * Created by Julian Vega on 05/07/2016.
 */
public class GetInitDataTask extends AsyncTask<Void, Void, Boolean> {

    private TaskCallback callback;
    private BaseActivity act;
    private boolean showLoading;
    private boolean running;

    public GetInitDataTask(BaseActivity act, boolean showLoading, TaskCallback callback) {
        this.act = act;
        this.callback = callback;
        this.showLoading = showLoading;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        running = true;
        if(showLoading){
            act.showLoading();
        }
    }

    public boolean isRunning(){
        return running;
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

            JSONObject bankProductsJson = Service.getBankProducts();
            ArrayList<BankProduct> products = BankProduct.fromJsonArray(bankProductsJson);
            Session.getCurrentSession(act).setBankProducts(products, bankProductsJson.toString());

            JSONObject userInterestsJson = Service.getUserInterests();
            ArrayList<UserInterest> userInterests = UserInterest.fromJsonArray(userInterestsJson);
            Session.getCurrentSession(act).setUserInterests(userInterests, userInterestsJson.toString());
        }  catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        running = false;
        if(showLoading){
            act.hideLoading();
        }
        if(callback != null) {
            callback.execute(success);
        }
    }
}
