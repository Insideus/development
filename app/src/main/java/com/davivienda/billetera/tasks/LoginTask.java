package com.davivienda.billetera.tasks;

import com.davivienda.billetera.activities.BaseActivity;
import com.davivienda.billetera.model.LoginResponse;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.utils.EncryptionUtils;

public class LoginTask extends DaviPayTask<LoginResponse> {

    private BaseActivity act;
    public String personId;
    public String personIdType;
    public String password;

    public LoginTask(BaseActivity activity, String personId, String personIdType, String password) {
        super(activity);
        this.act = activity;
        this.personId = personId;
        this.personIdType = personIdType;
        this.password = password;
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
            String encryptedPassword = EncryptionUtils.encryptPassword(act, password);
            response = Service.login(personId, personIdType, encryptedPassword, act.getTodo1Data());
        } catch (ServiceException e) {
            errorCode = e.getErrorCode();
            additionalData = e.getAdditionalData();
        }
        return response;
    }

}
