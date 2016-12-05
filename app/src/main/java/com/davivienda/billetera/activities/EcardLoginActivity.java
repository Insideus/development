package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.Card;
import com.davivienda.billetera.model.LoginSteps;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.utils.DialogUtil;

public class EcardLoginActivity extends BaseActivity {

    private Card eCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecard_login_layout);
        setActionBar(getString(R.string.ecard_login_activity_title), false);
        setLayouts();
        Session.getCurrentSession(this).setPendingStep(LoginSteps.GET_E_CARD.getStep());
    }

    private void setLayouts() {
        View continueButton = findViewById(R.id.continue_button);
        final CheckBox termsAndConditions = (CheckBox) findViewById(R.id.terms_and_conditions);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excecuteEcardStepTask();
            }
        });
        View createEcard = findViewById(R.id.card_image);
        createEcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!termsAndConditions.isChecked()){
                    DialogUtil.toast(EcardLoginActivity.this, getString(R.string.input_data_error_generic_title),
                            "",
                            getString(R.string.assign_password_recommendation_terms_and_conditions));
                } else {
                    new EcardCreateTask(EcardLoginActivity.this).execute();
                }
            }
        });
    }

    private class EcardCreateTask extends DaviPayTask<Card> {

        public EcardCreateTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                showLoading();
        }

        @Override
        protected Card doInBackground(Void... params) {
            Card response = null;
            try {
                response = Service.newECard(Session.getCurrentSession(getApplicationContext()).getSid(), getTodo1Data());
            } catch (ServiceException e) {
                e.printStackTrace();
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Card response) {
            super.onPostExecute(response);
            if(!processedError) {
                showEcardCreatedPopup(response);
            }
        }

    }

    public class SetEcardStepTask extends DaviPayTask<Boolean> {

        public SetEcardStepTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.setEcardStep(sid);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    //Service error.
                    showServiceGenericError();
                } else {
                    goToMainActivity();
                }
            }
        }

    }

    private void excecuteEcardStepTask() {
        new SetEcardStepTask(this).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_CREATED_ECARD_POPUP) {
            if(data != null && data.getParcelableExtra(FIRST_LOGIN_WITH_E_CARD) != null){
                eCard = data.getParcelableExtra(FIRST_LOGIN_WITH_E_CARD);
            }
            excecuteEcardStepTask();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        if(eCard != null){
            intent.putExtra(FIRST_LOGIN_WITH_E_CARD, eCard);
        }
        Session.getCurrentSession(this).setPendingStep(null);
        startActivity(intent);
        this.finish();
    }

}
