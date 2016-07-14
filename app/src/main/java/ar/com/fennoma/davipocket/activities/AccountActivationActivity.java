package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class AccountActivationActivity extends BaseActivity{

    private List<EditText> codeEditTextList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activation_activity_layout);
        setActionBar(getString(R.string.account_activation_activity_title), false);
        codeEditTextList = new ArrayList<>();
        findCodeEditTexts();
        setListenersToEditTexts();
        setRequestCodeButton();
        new ResendValidationCodeTask().execute();
    }

    private void setRequestCodeButton() {
        View requestCode = findViewById(R.id.request_code);
        if(requestCode == null){
            return;
        }
        requestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendValidationCodeTask().execute();
            }
        });
    }

    private void setListenersToEditTexts() {
        for(EditText editText : codeEditTextList){
            if(codeEditTextList.indexOf(editText) < codeEditTextList.size() - 1){
                editText.addTextChangedListener(new OnTextChangedGoToNextSpot(codeEditTextList.indexOf(editText)));
            }else{
                editText.addTextChangedListener(new CodeValidator());
                editText.setOnEditorActionListener(new OnSoftKeyboardDoneButtonPressed());
            }
        }
    }

    private void findCodeEditTexts() {
        codeEditTextList.add((EditText) findViewById(R.id.code_number_1));
        codeEditTextList.add((EditText) findViewById(R.id.code_number_2));
        codeEditTextList.add((EditText) findViewById(R.id.code_number_3));
        codeEditTextList.add((EditText) findViewById(R.id.code_number_4));
    }

    private String getCode(){
        String result = "";
        for(EditText codePart : codeEditTextList){
            result = result.concat(String.valueOf(codePart.getText()));
        }
        return result;
    }


    private class OnTextChangedGoToNextSpot implements TextWatcher {
        private int position;

        OnTextChangedGoToNextSpot(int position){
            this.position = position;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count == 1){
                codeEditTextList.get(position + 1).requestFocus();
                return;
            }
            if(position == 0){
                return;
            }
            codeEditTextList.get(position - 1).requestFocus();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private class CodeValidator implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count == 0){
                codeEditTextList.get(codeEditTextList.size() - 2).requestFocus();
                return;
            }
            validateCode();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void validateCode() {
        if(getCode().length() != codeEditTextList.size()){
            DialogUtil.toast(this,
                    getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle),
                    getString(R.string.account_activation_incomplete_code_error));
            return;
        }
        new AccountValidationTask().execute(getCode());
    }

    private class OnSoftKeyboardDoneButtonPressed implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                validateCode();
            }
            return false;
        }
    }

    public class AccountValidationTask extends AsyncTask<String, Void, Boolean> {

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
                response = Service.accountValidation(sid, params[0]);
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
                } else if(error != null && error == ErrorMessages.CONFIRMATION_ERROR) {
                    showConfirmationError();
                } else {
                    showServiceGenericError();
                }
            } else if(!response) {
                //Service error.
                showServiceGenericError();
            } else {
                goToConfirmationActivity();
            }
        }
    }

    public class ResendValidationCodeTask extends AsyncTask<String, Void, Boolean> {

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
                response = Service.resendValidationCode(sid);
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
               showResendMessage();
            }
        }
    }

    private void showResendMessage() {
        //DialogUtil.toast(this, getString(R.string.resend_validation_code_text));
    }

    public void showConfirmationError() {
        DialogUtil.toast(this,
                getString(R.string.input_data_error_generic_title),
                getString(R.string.input_data_error_generic_subtitle),
                getString(R.string.confirmation_service_error));
    }

    private void goToConfirmationActivity() {
        startActivity(new Intent(this, PolicyPickerActivity.class));
        this.finish();
    }

}
