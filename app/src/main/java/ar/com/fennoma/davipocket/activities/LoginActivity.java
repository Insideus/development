package ar.com.fennoma.davipocket.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class LoginActivity extends BaseActivity {

    private Spinner idTypeSpinner;
    private TextView selectedIdTypeText;
    private TextView virtualPasswordText;
    private TextView personIdNumber;
    private PersonIdType selectedIdType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setActionToButtons();
    }

    private void setActionToButtons() {
        CardView loginButton = (CardView) findViewById(R.id.login_button);
        View forgotPassword = findViewById(R.id.forgot_password_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ChangePasswordStep1Activity.class));
            }
        });

        idTypeSpinner = (Spinner) findViewById(R.id.login_id_type_spinner);
        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        virtualPasswordText = (TextView) findViewById(R.id.login_virtual_password);
        personIdNumber = (TextView) findViewById(R.id.login_person_id);

        selectedIdTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idTypeSpinner.performClick();
            }
        });
        idTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIdType = (PersonIdType) parent.getItemAtPosition(position);
                if(selectedIdType != null)
                selectedIdTypeText.setText(selectedIdType.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setIdTypesSpinner();
    }

    private void setIdTypesSpinner() {
        ArrayAdapter<PersonIdType> adapter = new ArrayAdapter<PersonIdType>(this, android.R.layout.simple_spinner_item,
                Session.getCurrentSession(this).getPersonIdTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idTypeSpinner.setAdapter(adapter);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void doLogin() {
        ArrayList<String> errors = validate();
        if(errors != null && errors.size() > 0) {
            DialogUtil.toast(this, errors);
        } else {
            new LoginTask().execute(personIdNumber.getText().toString(),
                    String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString());
        }
    }

    private ArrayList<String> validate() {
        ArrayList<String> errors =  new ArrayList<>();
        if(selectedIdType == null) {
            errors.add(getString(R.string.person_id_type_error_text));
        }
        if(TextUtils.isEmpty(personIdNumber.getText())) {
            errors.add(getString(R.string.person_in_number_error_text));
        }
        if(TextUtils.isEmpty(virtualPasswordText.getText())) {
            errors.add(getString(R.string.virtual_password_error_text));
        }
        return errors;
    }

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
                response = Service.login(params[0], params[1], params[2], "");
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
                goToRegistrationStep(step);
            }
            hideLoading();
        }
    }

    private void processErrorAndContinue(ErrorMessages error) {
        if(error != null) {
            switch(error) {
                case INVALID_TOKEN:
                    startActivity(new Intent(LoginActivity.this, LoginTokenActivity.class));
                    break;
                case LOGIN_ERROR:
                    DialogUtil.toast(this, getString(R.string.login_error_message_text));
                    break;
                case TOKEN_NEEDED_ERROR:
                    startActivity(new Intent(LoginActivity.this, LoginTokenActivity.class));
                    break;
                default:
                    showServiceGenericError();
            }
        } else {
            showServiceGenericError();
        }
    }

    private void goToRegistrationStep(LoginSteps step) {
        if(step != null) {
            switch(step) {
                case FACEBOOK:
                    startActivity(new Intent(LoginActivity.this, FacebookLoginActivity.class));
                    break;
                case ADDITIONAL_INFO:
                    startActivity(new Intent(LoginActivity.this, LoginConfirmationActivity.class));
                    break;
                case ACCOUNT_VERIFICATION:
                    startActivity(new Intent(LoginActivity.this, AccountActivationActivity.class));
                    break;
                case COMMUNICATION_PERMISSIONS:
                    startActivity(new Intent(LoginActivity.this, PolicyPickerActivity.class));
                    break;
                case REGISTRATION_COMPLETED:
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    break;
                default:
                    showServiceGenericError();
            }
        } else {
            showServiceGenericError();
        }
    }

}
