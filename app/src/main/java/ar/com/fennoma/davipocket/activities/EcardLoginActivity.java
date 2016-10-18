package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class EcardLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecard_login_layout);
        setActionBar(getString(R.string.ecard_login_activity_title), false);
        setLayouts();
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
                    new EcardCreateTask().execute();
                }
            }
        });
    }

    private class EcardCreateTask extends AsyncTask<Void, Void, Void> {

        private Card response;
        private String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                response = Service.newECard(Session.getCurrentSession(getApplicationContext()).getSid(), getTodo1Data());
            } catch (ServiceException e) {
                e.printStackTrace();
                errorCode = e.getErrorCode();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideLoading();
            if (response == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                showEcardCreatedPopup(response);
            }
        }
    }

    public class SetEcardStepTask extends AsyncTask<String, Void, Boolean> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... params) {
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
            hideLoading();
            if(response == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if(error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else if(!response) {
                //Service error.
                showServiceGenericError();
            } else {
                goToMainActivity();
            }
        }
    }

    private void excecuteEcardStepTask() {
        new SetEcardStepTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_CREATED_ECARD_POPUP) {
            excecuteEcardStepTask();
        }
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

}
