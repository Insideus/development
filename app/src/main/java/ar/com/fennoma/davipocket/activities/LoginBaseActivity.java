package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

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
            }
            return response;
        }

        @Override
        protected void onPostExecute(LoginResponse response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, "");

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
                nextTokenSession = e.getNextTokenSession();
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
                nextTokenSession = e.getNextTokenSession();
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

    private void processErrorAndContinue(ErrorMessages error, String nextTokenSession) {
        if(error != null) {
            switch(error) {
                case INVALID_TOKEN:
                    Intent loginTokenIntent = new Intent(this, LoginTokenActivity.class);
                    loginTokenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginTokenIntent);
                    break;
                case LOGIN_ERROR:
                    DialogUtil.toast(this, getString(R.string.login_error_message_text));
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
                    nextTokenIntent.putExtra(LoginTokenActivity.NEXT_TOKEN_KEY, nextTokenSession);
                    startActivity(nextTokenIntent);
                    break;
                case INVALID_SESSION:
                    handleInvalidSessionError();
                    break;
                default:
                    showServiceGenericError();
            }
        } else {
            showServiceGenericError();
        }
    }

    void goToRegistrationStep(LoginSteps step) {
        if(step != null) {
            switch(step) {
                case FACEBOOK:
                    Intent facebookIntent = new Intent(LoginBaseActivity.this, FacebookLoginActivity.class);
                    facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(facebookIntent);
                    break;
                case ADDITIONAL_INFO:
                    Intent addtionalInfoIntent = new Intent(LoginBaseActivity.this, LoginConfirmationActivity.class);
                    addtionalInfoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(addtionalInfoIntent);
                    break;
                case ACCOUNT_VERIFICATION:
                    Intent accountVerificationIntent = new Intent(LoginBaseActivity.this, AccountActivationActivity.class);
                    accountVerificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(accountVerificationIntent);
                    break;
                case COMMUNICATION_PERMISSIONS:
                    Intent communicationPermissionsActivity = new Intent(LoginBaseActivity.this, PolicyPickerActivity.class);
                    communicationPermissionsActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(communicationPermissionsActivity);
                    break;
                case REGISTRATION_COMPLETED:
                    Intent mainActivityIntent = new Intent(LoginBaseActivity.this, MainActivity.class);
                    mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainActivityIntent);
                    break;
                default:
                    showServiceGenericError();
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
