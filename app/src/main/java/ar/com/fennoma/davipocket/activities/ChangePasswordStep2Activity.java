package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.BankProduct;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.controls.ComboHolder;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.EncryptionUtils;

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
    //Spinner bankProductSpinner;
    TextView bankProductNumberText;
    BankProduct selectedBankProduct;
    TextView bankProductText;
    DialogPlus dialogPlus;

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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putString(PRODUCT_CODE_KEY, productCode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        personId = savedInstanceState.getString(ID_NUMBER_KEY);
        personIdType = savedInstanceState.getString(ID_TYPE_KEY);
        productCode = savedInstanceState.getString(PRODUCT_CODE_KEY);
    }

    @Override
    public void onBackPressed() {
        if(dialogPlus != null && dialogPlus.isShowing()) {

        } else {
            super.onBackPressed();
        }
    }

    private void findFields() {
        if(isTdBankProduct()) {
            showTdFields(true);
            lastCardDigits = (EditText) findViewById(R.id.last_card_digits);
            atmPassword = (EditText) findViewById(R.id.atm_password);
            findViewById(R.id.last_card_digits_help_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHelpDialog("", getString(R.string.change_password_step_2_last_card_digits_help_text));
                }
            });
            findViewById(R.id.atm_password_help_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHelpDialog("", getString(R.string.change_password_step_atm_password_help_text));
                }
            });
        } else {
            showTdFields(false);
            bankProductText = (TextView) findViewById(R.id.product_type_text);
            bankProductNumberText = (TextView) findViewById(R.id.product_number);
            bankProductText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    DialogUtil.hideKeyboard(ChangePasswordStep2Activity.this);
                    showCombo();
                    return false;
                }
            });
            findViewById(R.id.product_number_help_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHelpDialog("", getString(R.string.change_password_step_2_product_help_text));
                }
            });
        }
        ArrayList<BankProduct> bankProducts = Session.getCurrentSession(this).getBankProducts();
        if(bankProducts != null && bankProducts.size() > 0) {
            selectedBankProduct = bankProducts.get(0);
        }
        setSelectedBankProductName();
    }

    public void showCombo() {
        ArrayList<BankProduct> bankProducts = Session.getCurrentSession(this).getBankProducts();
        final ComboAdapter adapter = new ComboAdapter(bankProducts, selectedBankProduct);
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setContentHolder(new ComboHolder())
                .setFooter(R.layout.combo_footer)
                .setExpanded(false)
                .create();
        View footerView = dialog.getFooterView();
        footerView.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBankProduct = adapter.selectedType;
                setSelectedBankProductName();
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

    private void setSelectedBankProductName() {
        if (bankProductText != null) {
            bankProductText.setText(selectedBankProduct.getName());
        }
    }

    private class ComboAdapter extends BaseAdapter {

        ArrayList<BankProduct> types;
        BankProduct selectedType;

        public ComboAdapter(ArrayList<BankProduct> types, BankProduct selectedType) {
            this.types = types;
            this.selectedType = selectedType;
        }

        @Override
        public int getCount() {
            return types.size();
        }

        @Override
        public BankProduct getItem(int position) {
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
            final BankProduct type = getItem(position);
            row.setText(type.getName());
            if (selectedType != null && selectedType.getId() == type.getId()) {
                row.setTextColor(ContextCompat.getColor(ChangePasswordStep2Activity.this, R.color.combo_item_text_color_selected));
            } else {
                row.setTextColor(ContextCompat.getColor(ChangePasswordStep2Activity.this, R.color.combo_item_text_color));
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

    /*
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
    */

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
                String encryptedPin = EncryptionUtils.encryptPin(ChangePasswordStep2Activity.this, params[2]);
                response = Service.validateProduct(params[0], params[1], encryptedPin, params[3], params[4], getTodo1Data());
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
                    goToSetNewPasswordActivity(response);
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

    private void goToSetNewPasswordActivity(String passwordToken) {
        Intent intent = new Intent(this, ChangePasswordStep3Activity.class);
        intent.putExtra(ChangePasswordStep3Activity.PASSWORD_TOKEN_KEY, passwordToken);
        intent.putExtra(ChangePasswordStep3Activity.ID_NUMBER_KEY, personId);
        intent.putExtra(ChangePasswordStep3Activity.ID_TYPE_KEY, personIdType);
        intent.putExtra(ChangePasswordStep3Activity.EXPIRED_PASSWORD_KEY, false);
        startActivity(intent);
    }


}
