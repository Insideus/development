package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.ErrorMessages;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.utils.CardsUtils;
import com.davivienda.billetera.utils.CurrencyUtils;
import com.davivienda.billetera.utils.DialogUtil;

public class ECardRechargeActivity extends AbstractPayActivity implements BaseActivity.OtpCodeReceived {

    private EditText otherPaymentValue;
    private boolean justCreatedECard = false;
    private View payButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_card_recharge_activity);
        handleIntent(savedInstanceState);
        priceIndicator = getString(R.string.card_detail_item_transaction_price_indicator);
        setToolbar(R.id.toolbar_layout, true, getString(R.string.e_card_title));
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        setLayouts();
        setOtpCodeReceived(this);
        new GetCardPayDetail(this).execute();
    }

    private void handleIntent(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
        }
        if (card == null) {
            justCreatedECard = true;
            if(savedInstanceState != null){
                card = savedInstanceState.getParcelable(FIRST_LOGIN_WITH_E_CARD);
            } else {
                card = getIntent().getParcelableExtra(FIRST_LOGIN_WITH_E_CARD);
            }
        }
    }

    @Override
    protected void setBottomLayouts() {
        Double availableAmount;
        if(detail != null && detail.getAvailableAmount() != null){
            availableAmount = detail.getAvailableAmount();
        } else {
            return;
        }
        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText("$".concat(CurrencyUtils.getCurrencyForString(availableAmount).toUpperCase()));
    }

    @Override
    protected void setLayoutData() {
        TextView paymentExplanation = (TextView) findViewById(R.id.payment_description);
        paymentExplanation.setText(getString(R.string.e_card_recharge_explanation,
                CurrencyUtils.getCurrencyForString(detail.getMinimumPayment()),
                CurrencyUtils.getCurrencyForString(detail.getTotal()),
                CurrencyUtils.getCurrencyForString(detail.getTransactionCost())));
        if (detail.getAccounts() == null || detail.getAccounts().isEmpty()) {
            disableScreen();
            return;
        }
        selectedAccount = detail.getAccounts().get(0);
        setSelectedIdAccountName();
        selectedAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCombo();
            }
        });
    }

    private void disableScreen() {
        selectedAccountText.setText(getString(R.string.card_pay_detail_no_accounts_label));
        payButton.setVisibility(View.GONE);
        otherPaymentValue.setEnabled(false);
    }

    private void setLayouts() {
        selectedAccountText = (TextView) findViewById(R.id.account_spinner);
        otherPaymentValue = (EditText) findViewById(R.id.other_payment_value);
        payButton = findViewById(R.id.pay_button);
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
                if(justDeletedOtherPaymentText){
                    justDeletedOtherPaymentText = false;
                    return;
                }
                String price = s.toString();
                price = price.replace(".", "");
                price = price.replace(",", "");
                try {
                    price = CurrencyUtils.getCurrencyForStringWithOutDecimal(Double.valueOf(price));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                justDeletedOtherPaymentText = true;
                otherPaymentValue.setText(price);
                Selection.setSelection(otherPaymentValue.getText(), otherPaymentValue.getText().length());
            }
        });
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double amount = getAmount();
                if (validateAmount(amount) && isAmountOnRange(amount)) {
                    Intent intent = new Intent(ECardRechargeActivity.this, ActionDialogActivity.class);
                    intent.putExtra(ActionDialogActivity.TITLE_KEY, "CONFIRMAR");
                    intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, "RECARGA");
                    intent.putExtra(ActionDialogActivity.TEXT_KEY, getPayConfirmationText(amount));
                    intent.putExtra(ActionDialogActivity.IS_CARD_PAY, true);
                    intent.putExtra(ActionDialogActivity.CARD_KEY, card);
                    startActivityForResult(intent, PAY_REQUEST);
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                }
            }
        });
    }

    protected String getPayConfirmationText(Double amount) {
        return getString(R.string.card_pay_confirmation_text, card.getLastDigits(), selectedAccount.getLastDigits(),
                CurrencyUtils.getCurrencyForString(amount), CurrencyUtils.getCurrencyForString(detail.getTransactionCost()));
    }

    private boolean isAmountOnRange(Double amount) {
        if(amount < detail.getMinimumPayment() || amount > detail.getTotal()){
            showErrorDialog(getString(R.string.e_card_recharge_explanation,
                    CurrencyUtils.getCurrencyForString(detail.getMinimumPayment()),
                    CurrencyUtils.getCurrencyForString(detail.getTotal()),
                    CurrencyUtils.getCurrencyForString(detail.getTransactionCost())));
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_REQUEST && resultCode == RESULT_OK) {
            new RechargeECardTask(this, getAmount(), null).execute();
        }
        if (requestCode == ON_CLOSE_REQUEST && resultCode == RESULT_OK) {
            if(justCreatedECard){
                finish();
                goToHome();
                return;
            }
            setResult(RESULT_OK);
            finish();
        }
    }

    private Double getAmount() {
        String value = otherPaymentValue.getText().toString();
        if(TextUtils.isEmpty(value)){
            return null;
        }
        value = value.replace(priceIndicator, "");
        value = value.replace(",", "");
        value = value.replace(".", "");
        return Double.valueOf(value);
    }

    private class RechargeECardTask extends DaviPayTask<Boolean> {

        private Double amount;
        private String otpCode;

        RechargeECardTask(BaseActivity activity, Double amount, String otpCode) {
            super(activity);
            this.amount = amount;
            this.otpCode = otpCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.rechargeECard(sid, card.getLastDigits(), selectedAccount.getLastDigits(),
                        amount, getTodo1Data(), otpCode, selectedAccount.getCode());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (response) {
                    DialogUtil.toast(ECardRechargeActivity.this,
                            getString(R.string.card_recharge_success_title),
                            getString(R.string.card_recharge_success_subtitle),
                            getSuccessText(getString(R.string.e_card_recharge_success_text)),
                            ON_CLOSE_REQUEST);
                }
            }
        }
    }

    @Override
    public void onOtpCodeReceived(String otpCode) {
        new RechargeECardTask(this, getAmount(), otpCode).execute();
    }

    public void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if(error != null) {
            switch(error) {
                case ACCOUNT_BLOCKED:
                    DialogUtil.toast(this,
                            getString(R.string.recharge_ecard_account_blocked_error_title),
                            getString(R.string.recharge_ecard_account_blocked_error_subtitle),
                            getSuccessText(getString(R.string.recharge_ecard_account_blocked_error_text)));
                    break;
                default:
                    super.processErrorAndContinue(error, additionalParam);
            }
        } else {
            showServiceGenericError();
        }
    }

}
