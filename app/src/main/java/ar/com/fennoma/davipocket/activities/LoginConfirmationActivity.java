package ar.com.fennoma.davipocket.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Country;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.controls.ComboHolder;
import ar.com.fennoma.davipocket.utils.DateUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class LoginConfirmationActivity extends BaseActivity {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final int AGE_LIMIT = 14;
    private static final String COLOMBIA_CHECKER = "Colombia";
    private static final String[] validNumbers = {"300", "301", "302", "303", "304", "305", "310",
            "311", "312", "313", "314", "315", "316", "317", "318", "319", "320", "321", "350"};

    private EditText birthday;
    private EditText mail;
    private EditText phone;
    private TextView selectedCountryText;

    private Country selectedCountry;
    DialogPlus dialogPlus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_confirmation_activity_layout);
        setActionBar(getString(R.string.login_confirmation_activity_title), false);
        setInputLayouts();
        setBirthdayLayout();
        setContinueButton();
        setCountriesCombo();
    }

    @Override
    public void onBackPressed() {
        if(dialogPlus != null && dialogPlus.isShowing()) {

        } else {
            super.onBackPressed();
        }
    }

    private void setInputLayouts() {
        mail = (EditText) findViewById(R.id.mail);
        phone = (EditText) findViewById(R.id.phone);
        if(isFacebookLoggedIn()) {
            getUserDataFromFacebook();
        }
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
            if(selectedCountryText.getText().toString().contains(COLOMBIA_CHECKER) && !validateColombianPhone(phone.getText().toString())){
                errorList.add(getString(R.string.login_confirmation_error_message_wrong_colombian_phone));
            }
        }
        if (TextUtils.isEmpty(birthday.getText())) {
            errorList.add(getString(R.string.registration_error_message_empty_birthday));
        } else if (DateUtils.getYearsFromDate(DateUtils.getDate(birthday.getText().toString(), DATE_FORMAT)) < AGE_LIMIT){
            errorList.add(getString(R.string.registration_error_message_underage));
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

    private boolean validateColombianPhone(String phone) {
        if(phone.length() != 10) {
            return false;
        }
        String first3Digits = phone.substring(0, 3);
        ArrayList<String> validDigits = new ArrayList<>(Arrays.asList(validNumbers));
        if(!validDigits.contains(first3Digits)) {
            return false;
        }
        String fourthDigit = String.valueOf(phone.charAt(3));
        if(fourthDigit.equals("1") || fourthDigit.equals("0")) {
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

    private void setCountriesCombo() {
        selectedCountryText = (TextView) findViewById(R.id.country_text);
        selectedCountryText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DialogUtil.hideKeyboard(LoginConfirmationActivity.this);
                showCombo();
                return false;
            }
        });
        ArrayList<Country> countries = Session.getCurrentSession(this).getCountries();
        if(countries != null && countries.size() > 0) {
            selectedCountry = countries.get(0);
            setSelectedCountryName();
        }
    }

    public void showCombo() {
        ArrayList<Country> countries = Session.getCurrentSession(this).getCountries();
        final ComboAdapter adapter = new ComboAdapter(countries, selectedCountry);
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ComboHolder())
                .setAdapter(adapter)
                .setFooter(R.layout.combo_footer)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();
        View footerView = dialog.getFooterView();
        footerView.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCountry = adapter.selectedType;
                setSelectedCountryName();
                dialog.dismiss();
            }
        });
        footerView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialogPlus = dialog;
    }

    private void setSelectedCountryName() {
        if (selectedCountryText != null) {
            selectedCountryText.setText(selectedCountry.toString());
        }
    }

    private class ComboAdapter extends BaseAdapter {

        ArrayList<Country> types;
        Country selectedType;

        public ComboAdapter(ArrayList<Country> types, Country selectedType) {
            this.types = types;
            this.selectedType = selectedType;
        }

        @Override
        public int getCount() {
            return types.size();
        }

        @Override
        public Country getItem(int position) {
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
            final Country type = getItem(position);
            row.setText(type.toString());
            if (selectedType != null && selectedType.getId() == type.getId()) {
                row.setTextColor(ContextCompat.getColor(LoginConfirmationActivity.this, R.color.combo_item_text_color_selected));
            } else {
                row.setTextColor(ContextCompat.getColor(LoginConfirmationActivity.this, R.color.combo_item_text_color));
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

    private void getUserDataFromFacebook(){
        showLoading();
        GraphRequest graphRequestAsyncTask = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        String email = "";
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            try {
                                email = response.getJSONObject().get("email").toString();
                                mail.setText(email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            hideLoading();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        graphRequestAsyncTask.setParameters(parameters);
        graphRequestAsyncTask.executeAsync();
    }

    public boolean isFacebookLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void goToAccountActivationActivity() {
        startActivity(new Intent(LoginConfirmationActivity.this, AccountActivationActivity.class));
        this.finish();
    }

}
