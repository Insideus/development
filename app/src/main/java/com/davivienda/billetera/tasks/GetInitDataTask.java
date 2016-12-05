package com.davivienda.billetera.tasks;

import android.os.AsyncTask;

import com.davivienda.billetera.activities.BaseActivity;
import com.davivienda.billetera.model.BankProduct;
import com.davivienda.billetera.model.Country;
import com.davivienda.billetera.model.PersonIdType;
import com.davivienda.billetera.model.UserInterest;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;

import org.json.JSONObject;

import java.util.ArrayList;

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
