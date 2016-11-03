package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginResponse;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.User;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.EncryptionUtils;
import ar.com.fennoma.davipocket.utils.SharedPreferencesUtils;

public class LoginBaseActivity extends BaseActivity {

    private static final int NEW_DEVICE_DETECTED = 150;
    private static final int NEW_DEVICE_OTP_VALIDATION = 151;
    private static final int EXPIRED_PASSWORD_TOAST = 101;
    private static final int EXPIRED_OTP_PASSWORD_TOAST = 102;
    private static final int SET_PASSWORD_TOAST = 103;

    TextView selectedIdTypeText;
    TextView virtualPasswordText;
    TextView personIdNumber;
    PersonIdType selectedIdType;
    private String additionalParam;

    @Override
    public void onBackPressed() {
        if (dialogPlus == null || !dialogPlus.isShowing()) {
            super.onBackPressed();
        }
    }

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
                String encryptedPassword = EncryptionUtils.encryptPassword(LoginBaseActivity.this, params[2]);
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
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
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

    public class GetUserTask extends AsyncTask<String, Void, User> {
        private String sid;

        GetUserTask(String sid) {
            this.sid = sid;
        }

        @Override
        protected User doInBackground(String... params) {
            try {
                return Service.getUser(sid);
            }  catch (ServiceException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(User resultingUser) {
            SharedPreferencesUtils.setUser(resultingUser);
        }
    }

    public void showCombo(){
        final ComboAdapter adapter = new ComboAdapter(Session.getCurrentSession(this).getPersonIdTypes(), selectedIdType);
        showCombo(adapter, new IComboListener() {
            @Override
            public void onAccept() {
                selectedIdType = adapter.selectedType;
                setSelectedIdTypeName();
            }

            @Override
            public void setSelectedItem() {
                setSelectedIdTypeName();
            }
        });
    }

    public void setSelectedIdTypeName() {
        if (selectedIdType != null) {
            selectedIdTypeText.setText(selectedIdType.getName());
        }
    }

    private class ComboAdapter extends BaseAdapter {

        ArrayList<PersonIdType> types;
        PersonIdType selectedType;

        ComboAdapter(ArrayList<PersonIdType> types, PersonIdType selectedType) {
            this.types = types;
            this.selectedType = selectedType;
        }

        @Override
        public int getCount() {
            return types.size();
        }

        @Override
        public PersonIdType getItem(int position) {
            return types.get(position);
        }

        @Override
        public long getItemId(int position) {
            return types.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            TextView row;
            if(convertView == null) {
                row = (TextView) inflater.inflate(R.layout.combo_item, parent, false);
            } else {
                row = (TextView) convertView;
            }
            final PersonIdType type = getItem(position);
            row.setText(type.getName());
            if (selectedType != null && selectedType.getId() == type.getId()) {
                row.setTextColor(ContextCompat.getColor(LoginBaseActivity.this, R.color.combo_item_text_color_selected));
            } else {
                row.setTextColor(ContextCompat.getColor(LoginBaseActivity.this, R.color.combo_item_text_color));
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedType = type;
                    notifyDataSetChanged();
                }
            });
            return row;
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
                    this.additionalParam = additionalParam;
                    Intent setVirtualPasswordIntent = new Intent(this, AssignPasswordRecommendationActivity.class);
                    //setVirtualPasswordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    setVirtualPasswordIntent.putExtra(AssignPasswordRecommendationActivity.ID_TYPE_KEY,
                            String.valueOf(selectedIdType.getId()));
                    setVirtualPasswordIntent.putExtra(AssignPasswordRecommendationActivity.ID_NUMBER_KEY,
                            personIdNumber.getText().toString());
                    setVirtualPasswordIntent.putExtra(AssignPasswordRecommendationActivity.PRODUCT_CODE_KEY, additionalParam);
                    startActivity(setVirtualPasswordIntent);
                    break;
                case PASSWORD_EXPIRED:
                    this.additionalParam = additionalParam;
                    Intent toastExpiredIntent = new Intent(this, ToastDialogActivity.class);
                    toastExpiredIntent.putExtra(ToastDialogActivity.TITLE_KEY, getString(R.string.generic_service_error_title));
                    toastExpiredIntent.putExtra(ToastDialogActivity.SUBTITLE_KEY, "");
                    toastExpiredIntent.putExtra(ToastDialogActivity.TEXT_KEY, getString(R.string.expired_password_error_message));
                    startActivityForResult(toastExpiredIntent, EXPIRED_PASSWORD_TOAST);
                    break;
                case PASSWORD_EXPIRED_OTP_VALIDATION_NEEDED:
                    Intent toastOtpExpiredIntent = new Intent(this, ToastDialogActivity.class);
                    toastOtpExpiredIntent.putExtra(ToastDialogActivity.TITLE_KEY, getString(R.string.generic_service_error_title));
                    toastOtpExpiredIntent.putExtra(ToastDialogActivity.SUBTITLE_KEY, "");
                    toastOtpExpiredIntent.putExtra(ToastDialogActivity.TEXT_KEY, getString(R.string.expired_password_error_message));
                    startActivityForResult(toastOtpExpiredIntent, EXPIRED_OTP_PASSWORD_TOAST);
                    break;
                case NEW_DEVICE:
                    this.additionalParam = additionalParam;
                    newDeviceDetectedDialog(ErrorMessages.NEW_DEVICE);
                    break;
                case NEW_DEVICE_EXISTING_DEVICE:
                    this.additionalParam = additionalParam;
                    newDeviceDetectedDialog(ErrorMessages.NEW_DEVICE_EXISTING_DEVICE);
                    break;
                case NEW_DEVICE_OTP_VALIDATION_NEEDED:
                    this.additionalParam = additionalParam;
                    newDeviceDetectedDialog(ErrorMessages.NEW_DEVICE_OTP_VALIDATION_NEEDED);
                    break;
                case NEW_DEVICE_EXISTING_DEVICE_OTP_VALIDATION_NEEDED:
                    this.additionalParam = additionalParam;
                    newDeviceDetectedDialog(ErrorMessages.NEW_DEVICE_EXISTING_DEVICE_OTP_VALIDATION_NEEDED);
                    break;
                default:
                    super.processErrorAndContinue(error, additionalParam);
            }
        } else {
            showServiceGenericError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SET_PASSWORD_TOAST) {
            Intent setVirtualPasswordIntent = new Intent(this, ChangePasswordStep2Activity.class);
            setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
            setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_NUMBER_KEY, personIdNumber.getText().toString());
            setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.PRODUCT_CODE_KEY, additionalParam);
            this.additionalParam = null;
            startActivity(setVirtualPasswordIntent);
        }
        if(requestCode == EXPIRED_PASSWORD_TOAST) {
            Intent expiredPasswordIntent = new Intent(this, ChangePasswordStep3Activity.class);
            expiredPasswordIntent.putExtra(ChangePasswordStep3Activity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
            expiredPasswordIntent.putExtra(ChangePasswordStep3Activity.ID_NUMBER_KEY, personIdNumber.getText().toString());
            if(additionalParam != null) {
                expiredPasswordIntent.putExtra(ChangePasswordStep3Activity.PASSWORD_TOKEN_KEY, additionalParam);
            }
            expiredPasswordIntent.putExtra(ChangePasswordStep3Activity.EXPIRED_PASSWORD_KEY, true);
            startActivity(expiredPasswordIntent);
        }
        if(requestCode == EXPIRED_OTP_PASSWORD_TOAST) {
            Intent expiredOtpPasswordIntent = new Intent(this, PasswordConfirmationActivity.class);
            expiredOtpPasswordIntent.putExtra(PasswordConfirmationActivity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
            expiredOtpPasswordIntent.putExtra(PasswordConfirmationActivity.ID_NUMBER_KEY, personIdNumber.getText().toString());
            expiredOtpPasswordIntent.putExtra(PasswordConfirmationActivity.EXPIRED_PASSWORD_KEY, true);
            startActivity(expiredOtpPasswordIntent);
        }
        if(requestCode == NEW_DEVICE_DETECTED && resultCode == RESULT_OK){
            registerDevice();
        }
        if(requestCode == NEW_DEVICE_OTP_VALIDATION && resultCode == RESULT_OK){
            goToNewDeviceOTPScreen();
        }
    }

    private void registerDevice() {
        new RegisterDeviceTask().execute(additionalParam, null);
    }

    protected void showErrorAndGoToLoginActivity() {
        DialogUtil.showErrorAndGoToLoginActivityToast(this);
    }

    private void newDeviceDetectedDialog(ErrorMessages error){
        Intent intent = new Intent(LoginBaseActivity.this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.new_device_detected_title));
        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.new_device_detected_subtitle));
        intent.putExtra(ActionDialogActivity.NEW_DEVICE_DETECTED, true);
        int rqCode = NEW_DEVICE_DETECTED;
        switch(error) {
            case NEW_DEVICE:
                intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.new_device_detected_text));
                break;
            case NEW_DEVICE_OTP_VALIDATION_NEEDED:
                intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.new_device_detected_text));
                rqCode = NEW_DEVICE_OTP_VALIDATION;
                break;
            case NEW_DEVICE_EXISTING_DEVICE:
                intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.new_device_existing_device_detected_text));
                break;
            case NEW_DEVICE_EXISTING_DEVICE_OTP_VALIDATION_NEEDED:
                intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.new_device_existing_device_detected_text));
                rqCode = NEW_DEVICE_OTP_VALIDATION;
                break;
            default:
                intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.new_device_detected_text));
        }
        startActivityForResult(intent, rqCode);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    private void goToNewDeviceOTPScreen(){
        Intent intent = new Intent(LoginBaseActivity.this, NewDeviceOtpActivity.class);
        intent.putExtra(NewDeviceOtpActivity.NEW_DEVICE_TOKEN, additionalParam);
        startActivity(intent);
    }

    public class RegisterDeviceTask extends AsyncTask<String, Void, LoginResponse> {

        String errorCode;
        String additionalData;
        String pin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected LoginResponse doInBackground(String... params) {
            LoginResponse response = null;
            try {
                pin = params[1];
                response = Service.acceptNewDevice(params[0], pin, getTodo1Data());
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
