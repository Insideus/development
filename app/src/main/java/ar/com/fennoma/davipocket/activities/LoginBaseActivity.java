package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;

/**
 * Created by Julian Vega on 06/07/2016.
 */
public class LoginBaseActivity extends BaseActivity {

    Spinner idTypeSpinner;
    TextView selectedIdTypeText;
    TextView virtualPasswordText;
    TextView personIdNumber;
    PersonIdType selectedIdType;

    public class LoginTask extends AsyncTask<String, Void, LoginResponse> {

        String errorCode;
        String additionalData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            LoginResponse response = null;
            try {
                response = Service.login(params[0], params[1], params[2]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                additionalData = e.getAdditionalData();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, additionalData);

            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid());
                if(step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                goToRegistrationStep(step);
            }
            hideLoading();
        }
    }

    public class LoginWithTokenTask extends AsyncTask<String, Void, LoginResponse> {

        String errorCode;
        String nextTokenSession;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            LoginResponse response = null;
            try {
                response = Service.loginWithToken(params[0], params[1], params[2], params[3]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                nextTokenSession = e.getAdditionalData();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, nextTokenSession);
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid());
                if(step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                goToRegistrationStep(step);
            }
            hideLoading();
        }
    }

    public class LoginWithNextTokenTask extends AsyncTask<String, Void, LoginResponse> {

        String errorCode;
        String nextTokenSession;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            LoginResponse response = null;
            try {
                response = Service.loginWithNextToken(params[0], params[1], params[2], params[3], params[4]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
                nextTokenSession = e.getAdditionalData();
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, nextTokenSession);

            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid());
                if(step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                goToRegistrationStep(step);
            }
            hideLoading();
        }
    }

    void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if(error != null) {
            switch(error) {
                case INVALID_TOKEN:
                    Intent loginTokenIntent = new Intent(this, LoginTokenActivity.class);
                    loginTokenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginTokenIntent);
                    break;
                case TOKEN_REQUIRED_ERROR:
                    Intent intent = new Intent(this, LoginTokenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(LoginTokenActivity.ID_TYPE_KEY, selectedIdType);
                    intent.putExtra(LoginTokenActivity.ID_NUMBER_KEY, personIdNumber.getText().toString());
                    intent.putExtra(LoginTokenActivity.PASSWORD_KEY, virtualPasswordText.getText().toString());
                    startActivity(intent);
                    break;
                case NEXT_TOKEN_ERROR:
                    Intent nextTokenIntent = new Intent(this, LoginTokenActivity.class);
                    nextTokenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    nextTokenIntent.putExtra(LoginTokenActivity.ID_TYPE_KEY, selectedIdType);
                    nextTokenIntent.putExtra(LoginTokenActivity.ID_NUMBER_KEY, personIdNumber.getText().toString());
                    nextTokenIntent.putExtra(LoginTokenActivity.PASSWORD_KEY, virtualPasswordText.getText().toString());
                    nextTokenIntent.putExtra(LoginTokenActivity.NEXT_REQUIRED_TOKEN_KEY, true);
                    nextTokenIntent.putExtra(LoginTokenActivity.NEXT_TOKEN_KEY, additionalParam);
                    startActivity(nextTokenIntent);
                    break;
               case SET_VIRTUAL_PASSWORD:
                    Intent setVirtualPasswordIntent = new Intent(this, ChangePasswordStep2Activity.class);
                    setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
                    setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_NUMBER_KEY, personIdNumber.getText().toString());
                    setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.PRODUCT_CODE_KEY, additionalParam);
                    startActivity(setVirtualPasswordIntent);
                    break;
                case PASSWORD_EXPIRED:
                    Intent expiredPasswordIntent = new Intent(this, ChangePasswordStep3Activity.class);
                    expiredPasswordIntent.putExtra(ChangePasswordStep3Activity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
                    expiredPasswordIntent.putExtra(ChangePasswordStep3Activity.ID_NUMBER_KEY, personIdNumber.getText().toString());
                    expiredPasswordIntent.putExtra(ChangePasswordStep3Activity.EXPIRED_PASSWORD_KEY, true);
                    startActivity(expiredPasswordIntent);
                    break;
                case PASSWORD_EXPIRED_OTP_VALIDATION_NEEDED:
                    Intent expiredOtpPasswordIntent = new Intent(this, PasswordConfirmationActivity.class);
                    expiredOtpPasswordIntent.putExtra(PasswordConfirmationActivity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
                    expiredOtpPasswordIntent.putExtra(PasswordConfirmationActivity.ID_NUMBER_KEY, personIdNumber.getText().toString());
                    expiredOtpPasswordIntent.putExtra(PasswordConfirmationActivity.EXPIRED_PASSWORD_KEY, true);
                    startActivity(expiredOtpPasswordIntent);
                    break;
                default:
                    super.processErrorAndContinue(error, additionalParam);
            }
        } else {
            showServiceGenericError();
        }
    }

    void setIdTypesSpinner() {
        ArrayAdapter<PersonIdType> adapter = new ArrayAdapter<PersonIdType>(this, android.R.layout.simple_spinner_item,
                Session.getCurrentSession(this).getPersonIdTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idTypeSpinner.setAdapter(adapter);
        if(selectedIdType != null) {
            int position = adapter.getPosition(selectedIdType);
            idTypeSpinner.setSelection(position);
        }
    }

}
