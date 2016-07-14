package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.PersonIdType;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class ChangePasswordStep1Activity extends LoginBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_step_1_layout);
        setActionBar(getString(R.string.change_password_title), true);
        findFields();
        setContinueButton();
    }

    private void findFields() {
        idTypeSpinner = (Spinner) findViewById(R.id.login_id_type_spinner);
        selectedIdTypeText = (TextView) findViewById(R.id.login_id_type_text);
        personIdNumber = (EditText) findViewById(R.id.id_number);;

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

    private void setContinueButton() {
        View continueButton = findViewById(R.id.send_button);
        if(continueButton == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(personIdNumber.getText())){
                    DialogUtil.toast(ChangePasswordStep1Activity.this,
                            getString(R.string.input_data_error_generic_title),
                            getString(R.string.input_data_error_generic_subtitle),
                            getString(R.string.change_password_step_1_empty_id_number_error));
                    return;
                }
                new ForgotPasswordTask().execute(
                        personIdNumber.getText().toString(),
                        String.valueOf(selectedIdType.getId()));
            }
        });
    }

    public class ForgotPasswordTask extends AsyncTask<String, Void, String> {

        String errorCode;
        String additionalData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;
            try {
                response = Service.forgotPassword(params[0], params[1]);
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, additionalData);
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success.
                goToNextStep(response);
            }
            hideLoading();
        }
    }

    private void goToNextStep(String validationProduct) {
        Intent setVirtualPasswordIntent = new Intent(this, ChangePasswordStep2Activity.class);
        setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_TYPE_KEY, String.valueOf(selectedIdType.getId()));
        setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.ID_NUMBER_KEY, personIdNumber.getText().toString());
        setVirtualPasswordIntent.putExtra(ChangePasswordStep2Activity.PRODUCT_CODE_KEY, validationProduct);
        startActivity(setVirtualPasswordIntent);
    }

}
