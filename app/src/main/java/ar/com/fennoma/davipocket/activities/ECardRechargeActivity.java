package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class ECardRechargeActivity extends AbstractPayActivity implements BaseActivity.OtpCodeReceived {

    private EditText otherPaymentValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_card_recharge_activity);
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
        }
        priceIndicator = getString(R.string.card_detail_item_transaction_price_indicator);
        setToolbar(R.id.toolbar_layout, true, getString(R.string.e_card_title));
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        setLayouts();
        setOtpCodeReceived(this);
        new GetCardPayDetail().execute();
    }

    @Override
    protected void setBottomLayouts() {
        String availableAmount = null;
        if(detail != null && !TextUtils.isEmpty(detail.getAvailableAmount())){
            availableAmount = detail.getAvailableAmount();
        }
        if(TextUtils.isEmpty(availableAmount)){
            return;
        }
        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText("$" + CurrencyUtils.getCurrencyForString(availableAmount).toUpperCase());
    }

    @Override
    protected void setLayoutData() {
        if (detail.getAccounts() != null && !detail.getAccounts().isEmpty()) {
            selectedAccount = detail.getAccounts().get(0);
            setSelectedIdAccountName();
            selectedAccountText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCombo();
                }
            });
        }
    }

    private void setLayouts() {
        selectedAccountText = (TextView) findViewById(R.id.account_spinner);
        otherPaymentValue = (EditText) findViewById(R.id.other_payment_value);
        View payButton = findViewById(R.id.pay_button);
        if(selectedAccountText == null || otherPaymentValue == null || payButton == null){
            return;
        }
        otherPaymentValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (justDeletedOtherPaymentText) {
                    justDeletedOtherPaymentText = false;
                    return;
                }
                if (s.toString().equals(priceIndicator)) {
                    justDeletedOtherPaymentText = true;
                    otherPaymentValue.setText(priceIndicator.concat(" "));
                    Selection.setSelection(otherPaymentValue.getText(), otherPaymentValue.getText().length());
                    return;
                }
                if (!s.toString().contains(priceIndicator)) {
                    otherPaymentValue.setText(priceIndicator.concat(" ").concat(s.toString()));
                    Selection.setSelection(otherPaymentValue.getText(), otherPaymentValue.getText().length());
                }
            }
        });
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = getAmount();
                if (validateAmount(amount) && isAmountOnRange(amount)) {
                    Intent intent = new Intent(ECardRechargeActivity.this, ActionDialogActivity.class);
                    intent.putExtra(ActionDialogActivity.TITLE_KEY, "CONFIRMAR");
                    intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, "RECARGA DE ECARD");
                    intent.putExtra(ActionDialogActivity.TEXT_KEY, getPayConfirmationText(amount));
                    intent.putExtra(ActionDialogActivity.IS_CARD_PAY, true);
                    intent.putExtra(ActionDialogActivity.CARD_KEY, card);
                    startActivityForResult(intent, PAY_REQUEST);
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                }
            }
        });
    }

    protected String getPayConfirmationText(String amount) {
        return getString(R.string.e_card_pay_confirmation_text_1).concat(" ").concat(card.getLastDigits()).concat(" ")
                .concat(getString(R.string.card_pay_confirmation_text_2)).concat(" ").concat(selectedAccount.getName())
                .concat(" ").concat(getString(R.string.card_pay_confirmation_text_3)).concat(" ")
                .concat(selectedAccount.getLastDigits()).concat(" ").concat(getString(R.string.card_pay_confirmation_text_4))
                .concat(" $ ").concat(CurrencyUtils.getCurrencyForString(amount)).concat("?");
    }

    private boolean isAmountOnRange(String amount) {
        if(Integer.valueOf(amount) < Integer.valueOf(getString(R.string.e_card_recharge_min_value))
                || Integer.valueOf(amount) > Integer.valueOf(getString(R.string.e_card_recharge_max_value))){
            showErrorDialog(getString(R.string.e_card_recharge_explanation));
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_REQUEST && resultCode == RESULT_OK) {
            new RechargeECardTask(getAmount(), null).execute();
        }
        if (requestCode == ON_CLOSE_REQUEST && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private String getAmount() {
        String value = otherPaymentValue.getText().toString();
        value = value.replace(priceIndicator, "");
        value = value.replace(" ", "");
        return value;
    }

    private class RechargeECardTask extends AsyncTask<Void, Void, Void> {

        private String errorCode;
        private String amount;
        private boolean transactionMade;
        private String otpCode;

        public RechargeECardTask(String amount, String otpCode) {
            this.amount = amount;
            this.otpCode = otpCode;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                transactionMade = Service.rechargeECard(sid, card.getLastDigits(), selectedAccount.getLastDigits(), amount, getTodo1Data(), otpCode);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideLoading();
            if (!transactionMade) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null) {
                    processErrorAndContinue(error, null);
                } else {
                    showServiceGenericError();
                }
            } else {
                DialogUtil.toast(ECardRechargeActivity.this, getString(R.string.card_pay_success_title),
                        getString(R.string.card_pay_success_subtitle),
                        getSuccessText(getString(R.string.e_card_recharge_success_text)), ON_CLOSE_REQUEST);
            }
        }
    }

    @Override
    public void onOtpCodeReceived(String otpCode) {
        new RechargeECardTask(getAmount(), otpCode).execute();
    }

}
