package com.davivienda.billetera.tasks;

import android.os.AsyncTask;

import com.davivienda.billetera.activities.BaseActivity;
import com.davivienda.billetera.model.ErrorMessages;

/**
 * Created by Julian Vega on 17/11/2016.
 */

public abstract class DaviPayTask<T> extends AsyncTask<Void, Void, T> {

    public BaseActivity activity;
    public String errorCode;
    public String additionalData;
    public Boolean processedError;

    public DaviPayTask(BaseActivity activity) {
        this.activity = activity;
        processedError = false;
    }

    @Override
    protected abstract T doInBackground(Void... objects);

    @Override
    protected void onPostExecute(T response) {
        super.onPostExecute(response);
        activity.hideLoading();
        if(response == null && errorCode != null) {
            //Expected error.
            ErrorMessages error = ErrorMessages.getError(errorCode);
            activity.processErrorAndContinue(error, additionalData);
            processedError = true;
        } else if(response == null && errorCode == null) {
            //Service error.
            activity.showServiceGenericError();
            processedError = true;
        }
    }



}
