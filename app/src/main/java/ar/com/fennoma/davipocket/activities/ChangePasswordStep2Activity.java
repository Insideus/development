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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.BankProduct;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class ChangePasswordStep2Activity extends BaseActivity {

    public static String ID_TYPE_KEY = "id_type_key";
    public static String ID_NUMBER_KEY = "id_number_key";
    public static String PRODUCT_CODE_KEY = "product_code_key";

    public static String GO_TO_OTP_KEY = "validate_product.user_otp_validation_sent";
    public static String TD_CODE_KEY = "TD";

    private String personId;
    private String personIdType;
    private String productCode;

    //TD
    private EditText lastCardDigits;
    private EditText atmPassword;
    //OTHER PRODUCT
    Spinner bankProductSpinner;
    TextView bankProductNumberText;
    BankProduct selectedBankProduct;
    TextView bankProductText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_step_2_layout);
        if (savedInstanceState != null) {
            personIdType = savedInstanceState.getString(ID_TYPE_KEY, "");
            personId = savedInstanceState.getString(ID_NUMBER_KEY, "");
            productCode = savedInstanceState.getString(PRODUCT_CODE_KEY, "");
        } else {
            personIdType = getIntent().getStringExtra(ID_TYPE_KEY);
            personId = getIntent().getStringExtra(ID_NUMBER_KEY);
            productCode = getIntent().getStringExtra(PRODUCT_CODE_KEY);
        }
        setActionBar(getString(R.string.change_password_title), true);
        findFields();
        setContinueButton();
    }

    private void findFields() {
        if(isTdBankProduct()) {
            showTdFields(true);
            lastCardDigits = (EditText) findViewById(R.id.last_card_digits);
            atmPassword = (EditText) findViewById(R.id.atm_password);
        } else {
            showTdFields(false);
            bankProductSpinner = (Spinner) findViewById(R.id.product_type_spinner);
            bankProductText = (TextView) findViewById(R.id.product_type_text);
            bankProductNumberText = (TextView) findViewById(R.id.product_number);
            bankProductText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bankProductSpinner.performClick();
                }
            });
            bankProductSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedBankProduct = (BankProduct) parent.getItemAtPosition(position);
                    if(selectedBankProduct != null)
                        bankProductText.setText(selectedBankProduct.getName());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            setIdTypesSpinner();
        }
    }

    private boolean isTdBankProduct() {
        return productCode != null && productCode.equals(TD_CODE_KEY);
    }

    private void showTdFields(boolean showTd) {
        LinearLayout tdLayout = (LinearLayout) findViewById(R.id.validate_td_input_data_layout);
        LinearLayout otherLayout = (LinearLayout) findViewById(R.id.validate_other_input_data_layout);
        if(showTd) {
            tdLayout.setVisibility(LinearLayout.VISIBLE);
            otherLayout.setVisibility(LinearLayout.GONE);
        } else {
            tdLayout.setVisibility(LinearLayout.GONE);
            otherLayout.setVisibility(LinearLayout.VISIBLE);
        }
    }

    void setIdTypesSpinner() {
        ArrayAdapter<BankProduct> adapter = new ArrayAdapter<BankProduct>(this, android.R.layout.simple_spinner_item,
                Session.getCurrentSession(this).getBankProducts());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankProductSpinner.setAdapter(adapter);
        if(selectedBankProduct != null) {
            int position = adapter.getPosition(selectedBankProduct);
            bankProductSpinner.setSelection(position);
        }
    }

    private void setContinueButton() {
        View continueButton = findViewById(R.id.send_button);
        if (continueButton == null) {
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    if(isTdBankProduct()) {
                        new ValidateProductTask().execute(personId, personIdType, atmPassword.getText().toString(),
                                productCode, lastCardDigits.getText().toString());
                    } else {
                        new ValidateProductTask().execute(personId, personIdType, "",
                                selectedBankProduct.getCode(), bankProductNumberText.getText().toString());
                    }
                }
            }
        });
    }

    private boolean validateFields() {
        List<String> errorList = new ArrayList<>();
        if(isTdBankProduct()) {
            if (TextUtils.isEmpty(lastCardDigits.getText())) {
                errorList.add(getString(R.string.change_password_step_2_empty_card_digits_error));
            } else {
                if (lastCardDigits.getText().length() < 4) {
                    errorList.add(getString(R.string.change_password_step_2_incomplete_card_digits_error));
                }
            }
            if (TextUtils.isEmpty(atmPassword.getText())) {
                errorList.add(getString(R.string.change_password_step_2_empty_atm_password_error));
            }
        } else {
            if (TextUtils.isEmpty(bankProductText.getText()) && selectedBankProduct != null) {
                errorList.add(getString(R.string.change_password_step_2_empty_bank_product_error));
            }
            if (TextUtils.isEmpty(bankProductNumberText.getText())) {
                errorList.add(getString(R.string.change_password_step_2_empty_bank_product_number_error));
            }
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

    public class ValidateProductTask extends AsyncTask<String, Void, String> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;
            try {
                response = Service.validateProduct(params[0], params[1], params[2], params[3], params[4]);
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
                processErrorAndContinue(error, "");
            } else if(response == null && errorCode == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success.
                if(response.equals(GO_TO_OTP_KEY)) {
                    goToPasswordConfirmationActivity();
                } else {
                    //Change Password Session Token.
                }
            }
            hideLoading();
        }
    }

    private void goToPasswordConfirmationActivity() {
        Intent intent = new Intent(this, PasswordConfirmationActivity.class);
        intent.putExtra(PasswordConfirmationActivity.ID_NUMBER_KEY, personId);
        intent.putExtra(PasswordConfirmationActivity.ID_TYPE_KEY, personIdType);
        startActivity(intent);
    }


}
