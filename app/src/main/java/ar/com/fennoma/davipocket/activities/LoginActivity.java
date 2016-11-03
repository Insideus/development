package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
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
import ar.com.fennoma.davipocket.tasks.GetInitDataTask;
import ar.com.fennoma.davipocket.tasks.TaskCallback;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.EncryptionUtils;

public class LoginActivity extends LoginBaseActivity {

    private static final int ESSENTIAL_DATA_CHECK = 11;
    private GetInitDataTask initDataTask;
    private boolean finishingUpApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setActionToButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(finishingUpApp){
            finishingUpApp = false;
            return;
        }
        checkForEssentialData();
    }

    private void checkForEssentialData() {
        if(initDataTask != null && initDataTask.isRunning()){
            return;
        }
        if(Session.getCurrentSession(this).getPersonIdTypes().isEmpty()){
            Intent intent = new Intent(this, ActionDialogActivity.class);
            intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.generic_service_error_title));
            intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.login_not_enough_data_text));
            intent.putExtra(ActionDialogActivity.LOGIN_DATA_CHECK, true);
            startActivityForResult(intent, ESSENTIAL_DATA_CHECK);
            overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setActionToButtons() {
        View loginButton = findViewById(R.id.login_button);
        View forgotPassword = findViewById(R.id.resend_sms);
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
        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        ArrayList<PersonIdType> personIdTypes = Session.getCurrentSession(this).getPersonIdTypes();
        if(personIdTypes != null && personIdTypes.size() > 0) {
            selectedIdType = personIdTypes.get(0);
            setSelectedIdTypeName();
        }
        virtualPasswordText = (TextView) findViewById(R.id.login_virtual_password);
        personIdNumber = (TextView) findViewById(R.id.login_person_id);
        selectedIdTypeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DialogUtil.hideKeyboard(LoginActivity.this);
                showCombo();
                return false;
            }
        });
        findViewById(R.id.virtual_help_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog(getString(R.string.login_help_dialog_title),
                        getString(R.string.login_help_dialog_subtitle),
                        getString(R.string.login_help_dialog_text));
            }
        });
    }

    private void doLogin() {
        ArrayList<String> errors = validate();
        if (errors != null && errors.size() > 0) {
            DialogUtil.toast(this, getString(R.string.login_invalid_inputs_error_title),
                    getString(R.string.login_invalid_inputs_error_subtitle),
                    getString(R.string.login_invalid_inputs_error_text));
        } else {
            new LoginTask().execute(personIdNumber.getText().toString(),
                    String.valueOf(selectedIdType.getId()),
                    virtualPasswordText.getText().toString());
        }
    }

    private ArrayList<String> validate() {
        ArrayList<String> errors = new ArrayList<>();
        if (selectedIdType == null) {
            errors.add(getString(R.string.person_id_type_error_text));
        }
        if (TextUtils.isEmpty(personIdNumber.getText())) {
            errors.add(getString(R.string.person_in_number_error_text));
        }
        return errors;
    }

    private void resetLayouts(){
        personIdNumber.setText("");
        virtualPasswordText.setText("");
        ArrayList<PersonIdType> personIdTypes = Session.getCurrentSession(this).getPersonIdTypes();
        if(personIdTypes == null || personIdTypes.isEmpty()){
            return;
        }
        selectedIdType = personIdTypes.get(0);
        selectedIdTypeText.setText(selectedIdType.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ESSENTIAL_DATA_CHECK){
            if(resultCode == RESULT_OK || resultCode == RESULT_CANCELED){
                initDataTask = new GetInitDataTask(this, true, new TaskCallback() {
                    @Override
                    public void execute(Object result) {
                        checkForEssentialData();
                    }
                });
                initDataTask.execute();
            } else {
                finishingUpApp = true;
                onBackPressed();
            }
        }
    }

    public class LoginTask extends AsyncTask<String, Void, LoginResponse> {

        private String errorCode;
        private String additionalData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            LoginResponse response = null;
            try {
                String encryptedPassword = EncryptionUtils.encryptPassword(LoginActivity.this, params[2]);
                response = Service.login(params[0], params[1], encryptedPassword, getTodo1Data());
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
                resetLayouts();
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
                resetLayouts();
            } else {
                //Success login.
                LoginSteps step = LoginSteps.getStep(response.getAccountStatus());
                Session.getCurrentSession(getApplicationContext()).loginUser(response.getSid(),
                        response.getAccountStatus());
                if(step == null) {
                    step = LoginSteps.REGISTRATION_COMPLETED;
                }
                new GetUserTask(response.getSid()).execute();
                goToRegistrationStep(step);
            }
            hideLoading();
        }
    }

}
