package ar.com.fennoma.davipocket.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class LoginConfirmationActivity extends BaseActivity {

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private EditText birthday;
    private EditText mail;
    private EditText phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_confirmation_activity_layout);
        setActionBar(getString(R.string.login_confirmation_activity_title), false);
        setInputLayouts();
        setBirthdayLayout();
        setContinueButton();
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
        View continueButton = findViewById(R.id.continue_button);
        if (continueButton == null) {
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()) {
                    startActivity(new Intent(LoginConfirmationActivity.this, AccountActivationActivity.class));
                }
            }
        });
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
            DialogUtil.toast(this, DialogUtil.concatMessages(errorList));
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
}
