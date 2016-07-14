package ar.com.fennoma.davipocket.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Country;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class LoginConfirmationActivity extends BaseActivity {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private EditText birthday;
    private EditText mail;
    private EditText phone;
    private Spinner countrySpinner;
    private TextView selectedCountryText;

    private Country selectedCountry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_confirmation_activity_layout);
        setActionBar(getString(R.string.login_confirmation_activity_title), false);
        setInputLayouts();
        setBirthdayLayout();
        setContinueButton();
        setIdTypesSpinner();
    }

    private void setInputLayouts() {
        mail = (EditText) findViewById(R.id.mail);
        phone = (EditText) findViewById(R.id.phone);
    }

    private void setBirthdayLayout() {
        birthday = (EditText) findViewById(R.id.birthday);
        if (birthday == null) {
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        birthday.setOnKeyListener(null);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showDatePicker(LoginConfirmationActivity.this, calendar,
                        new DatePickerListener(birthday, calendar));
            }
        });
        birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogUtil.showDatePicker(LoginConfirmationActivity.this, calendar,
                            new DatePickerListener(birthday, calendar));
                }
            }
        });
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.send_button);
        if (continueButton == null) {
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()) {
                    submitData();
                }
            }
        });
    }

    private void submitData() {
        new ConfirmationTask().execute(mail.getText().toString(), phone.getText().toString(),
                String.valueOf(selectedCountry.getId()), birthday.getText().toString());
    }

    private boolean validateFields() {
        List<String> errorList = new ArrayList<>();
        if (TextUtils.isEmpty(mail.getText())) {
            errorList.add(getString(R.string.login_confirmation_error_message_empty_email));
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail.getText()).matches()) {
                errorList.add(getString(R.string.login_confirmation_error_message_invalid_email));
            }
        }
        if (TextUtils.isEmpty(phone.getText())) {
            errorList.add(getString(R.string.login_confirmation_error_message_empty_phone));
        } else {
            if(!Patterns.PHONE.matcher(phone.getText()).matches()){
                errorList.add(getString(R.string.login_confirmation_error_message_wrong_phone));
            }
        }
        if (TextUtils.isEmpty(birthday.getText())) {
            errorList.add(getString(R.string.registration_error_message_empty_birthday));
        }
        if (!errorList.isEmpty()) {
            DialogUtil.toast(this,
                    getString(R.string.input_data_error_generic_title),
                    getString(R.string.input_data_error_generic_subtitle),
                    errorList);
            return false;
        }

        return true;
    }

    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {

        private EditText editText;
        private Calendar calendar;

        DatePickerListener(EditText editText, Calendar calendar) {
            this.editText = editText;
            this.calendar = calendar;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (editText == null) {
                return;
            }
            calendar.set(year, monthOfYear, dayOfMonth);
            editText.setText(new SimpleDateFormat(DATE_FORMAT, new Locale("es", "ES")).format(calendar.getTime()));
        }
    }

    private void setIdTypesSpinner() {
        countrySpinner = (Spinner) findViewById(R.id.country_spinner);
        selectedCountryText = (TextView) findViewById(R.id.country_text);

        selectedCountryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrySpinner.performClick();
            }
        });
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = (Country) parent.getItemAtPosition(position);
                if(selectedCountry != null)
                    selectedCountryText.setText(selectedCountry.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(this, android.R.layout.simple_spinner_item,
                Session.getCurrentSession(this).getCountries());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        if(selectedCountry != null) {
            int position = adapter.getPosition(selectedCountry);
            countrySpinner.setSelection(position);
        }
    }

    public class ConfirmationTask extends AsyncTask<String, Void, Boolean> {

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
                response = Service.updateUserInfo(sid, params[0], params[1], params[2], params[3]);
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
                goToAccountActivationActivity();
            }
        }
    }

    private void goToAccountActivationActivity() {
        startActivity(new Intent(LoginConfirmationActivity.this, AccountActivationActivity.class));
        this.finish();
    }

}
