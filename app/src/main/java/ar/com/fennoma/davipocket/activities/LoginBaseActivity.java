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
                response = Service.login(params[0], params[1], params[2], params[3]);
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
                processErrorAndContinue(error);

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

    private void processErrorAndContinue(ErrorMessages error) {
        if(error != null) {
            switch(error) {
                case INVALID_TOKEN:
                    startActivity(new Intent(LoginBaseActivity.this, LoginTokenActivity.class));
                    break;
                case LOGIN_ERROR:
                    DialogUtil.toast(this, getString(R.string.login_error_message_text));
                    break;
                case TOKEN_REQUIRED_ERROR:
                    Intent intent = new Intent(LoginBaseActivity.this, LoginTokenActivity.class);
                    intent.putExtra(LoginTokenActivity.ID_TYPE_KEY, selectedIdType);
                    intent.putExtra(LoginTokenActivity.ID_NUMBER_KEY, personIdNumber.getText().toString());
                    intent.putExtra(LoginTokenActivity.PASSWORD_KEY, virtualPasswordText.getText().toString());
                    startActivity(intent);
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
                    startActivity(new Intent(LoginBaseActivity.this, FacebookLoginActivity.class));
                    break;
                case ADDITIONAL_INFO:
                    startActivity(new Intent(LoginBaseActivity.this, LoginConfirmationActivity.class));
                    break;
                case ACCOUNT_VERIFICATION:
                    startActivity(new Intent(LoginBaseActivity.this, AccountActivationActivity.class));
                    break;
                case COMMUNICATION_PERMISSIONS:
                    startActivity(new Intent(LoginBaseActivity.this, PolicyPickerActivity.class));
                    break;
                case REGISTRATION_COMPLETED:
                    startActivity(new Intent(LoginBaseActivity.this, MainActivity.class));
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
