package com.davivienda.billetera.tasks;

import com.davivienda.billetera.activities.BaseActivity;
import com.davivienda.billetera.model.LoginResponse;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;

public class LoginWithTokenTask extends DaviPayTask<LoginResponse> {

    private BaseActivity act;
    public String personId;
    public String personIdType;
    public String password;
    public String token;

    public LoginWithTokenTask(BaseActivity activity, String personId, String personIdType, String password, String token) {
        super(activity);
        this.act = activity;
        this.personId = personId;
        this.personIdType = personIdType;
        this.password = password;
        this.token = token;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        act.showLoading();
    }

    @Override
    protected LoginResponse doInBackground(Void... params) {
        LoginResponse response = null;
        try {
            response = Service.loginWithToken(personId, personIdType, password, token, act.getTodo1Data());
        }  catch (ServiceException e) {
            errorCode = e.getErrorCode();
            additionalData = e.getAdditionalData();
        }
        return response;
    }

}
