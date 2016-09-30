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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.TransactionDetails;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class CardPayDetailActivity extends AbstractPayActivity {

    public static final String TRANSACTION_DETAILS = "transactions details";

    private CheckBox totalPaymentUsd;
    private CheckBox minimumPaymentUsd;
    private CheckBox totalPayment;
    private CheckBox minimumPayment;
    private CheckBox otherPayment;

    private TransactionDetails transactionDetails;

    private EditText otherPaymentValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
            transactionDetails = savedInstanceState.getParcelable(TRANSACTION_DETAILS);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
            transactionDetails = getIntent().getParcelableExtra(TRANSACTION_DETAILS);
        }
        setContentView(R.layout.activity_card_pay_detail);
        setToolbar(R.id.toolbar_layout, true, card.getBin().getFranchise().toUpperCase());
        setLayouts();
        new GetCardPayDetail().execute();
    }

    @Override
    public void onBackPressed() {
        if (dialogPlus == null || !dialogPlus.isShowing()) {
            super.onBackPressed();
        }
    }

    protected void setLayoutData() {
        TextView totalPaymentLabelUsd = (TextView) findViewById(R.id.total_payment_label_usd);
        TextView minimumPaymentLabelUsd = (TextView) findViewById(R.id.minimum_payment_label_usd);
        totalPaymentLabelUsd.setText(String.format("$%s", CurrencyUtils.getCurrencyForString(detail.getTotalUsd())));
        minimumPaymentLabelUsd.setText(String.format("$%s", CurrencyUtils.getCurrencyForString(detail.getMinimumPaymentUsd())));

        TextView totalPaymentLabel = (TextView) findViewById(R.id.total_payment_label);
        TextView minimumPaymentLabel = (TextView) findViewById(R.id.minimum_payment_label);
        totalPaymentLabel.setText(String.format("$%s", CurrencyUtils.getCurrencyForString(detail.getTotal())));
        minimumPaymentLabel.setText(String.format("$%s", CurrencyUtils.getCurrencyForString(detail.getMinimumPayment())));
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
        setBottomLayouts();
        selectedAccountText = (TextView) findViewById(R.id.account_spinner);
        TextView cardTitle = (TextView) findViewById(R.id.card_title);

        totalPaymentUsd = (CheckBox) findViewById(R.id.total_payment_usd);
        minimumPaymentUsd = (CheckBox) findViewById(R.id.minimum_payment_usd);
        totalPayment = (CheckBox) findViewById(R.id.total_payment);
        minimumPayment = (CheckBox) findViewById(R.id.minimum_payment);
        otherPayment = (CheckBox) findViewById(R.id.other_payment);

        final View otherPaymentContainer = findViewById(R.id.other_payment_container);
        otherPaymentValue = (EditText) findViewById(R.id.other_payment_value);

        View payButton = findViewById(R.id.pay_button);
        if (totalPayment == null || minimumPayment == null || otherPayment == null || otherPaymentContainer == null
                || otherPaymentValue == null || cardTitle == null || payButton == null) {
            return;
        }
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        totalPaymentUsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(minimumPayment, otherPayment, totalPayment, minimumPaymentUsd);
            }
        });
        minimumPaymentUsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(totalPayment, otherPayment, totalPaymentUsd, minimumPayment);
            }
        });
        totalPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(minimumPayment, otherPayment, totalPaymentUsd, minimumPaymentUsd);
            }
        });
        minimumPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(totalPayment, otherPayment, totalPaymentUsd, minimumPaymentUsd);
            }
        });
        otherPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(totalPayment, minimumPayment, totalPaymentUsd, minimumPaymentUsd);
            }
        });
        otherPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    otherPaymentContainer.setVisibility(View.VISIBLE);
                } else {
                    otherPaymentContainer.setVisibility(View.GONE);
                }
            }
        });
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
                if (validateAmount(amount)) {
                    Intent intent = new Intent(CardPayDetailActivity.this, CardActionDialogActivity.class);
                    intent.putExtra(CardActionDialogActivity.TITLE_KEY, "CONFIRMAR");
                    intent.putExtra(CardActionDialogActivity.SUBTITLE_KEY, "PAGO");
                    intent.putExtra(CardActionDialogActivity.TEXT_KEY, getPayConfirmationText(amount));
                    intent.putExtra(CardActionDialogActivity.IS_CARD_PAY, true);
                    intent.putExtra(CardActionDialogActivity.CARD_KEY, card);
                    startActivityForResult(intent, PAY_REQUEST);
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                }
            }
        });
    }

    private Boolean isUsdPayment() {
        if (minimumPaymentUsd.isChecked()) {
            return true;
        }
        if (totalPaymentUsd.isChecked()) {
            return true;
        }
        return false;
    }

    protected String getPayConfirmationText(String amount) {
        return getString(R.string.card_pay_confirmation_text_1).concat(" ").concat(card.getLastDigits()).concat(" ")
                .concat(getString(R.string.card_pay_confirmation_text_2)).concat(" ").concat(selectedAccount.getName())
                .concat(" ").concat(getString(R.string.card_pay_confirmation_text_3)).concat(" ")
                .concat(selectedAccount.getLastDigits()).concat(" ").concat(getString(R.string.card_pay_confirmation_text_4))
                .concat(" $ ").concat(CurrencyUtils.getCurrencyForString(amount)).concat("?");
    }

    private String getAmount() {
        if (totalPayment.isChecked()) {
            return detail.getTotal();
        } else if (minimumPayment.isChecked()) {
            return detail.getMinimumPayment();
        } else if (minimumPaymentUsd.isChecked()) {
            return detail.getMinimumPaymentUsd();
        } else if (totalPaymentUsd.isChecked()) {
            return detail.getTotalUsd();
        } else {
            String value = otherPaymentValue.getText().toString();
            value = value.replace(priceIndicator, "");
            value = value.replace(" ", "");
            return value;
        }
    }

    protected void setBottomLayouts() {
        String paymentDay = null;
        String availableAmount = null;
        if(transactionDetails != null){
            paymentDay = transactionDetails.getPaymentDate();
            availableAmount = transactionDetails.getAvailableAmount();
        }
        if(detail != null && !TextUtils.isEmpty(detail.getAvailableAmount()) && !TextUtils.isEmpty(detail.getPaymentDate())){
            paymentDay = detail.getPaymentDate();
            availableAmount = detail.getAvailableAmount();
        }
        if(TextUtils.isEmpty(paymentDay) || TextUtils.isEmpty(availableAmount)){
            return;
        }

        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText("$" + CurrencyUtils.getCurrencyForString(availableAmount).toUpperCase());
        TextView paymentDate = (TextView) findViewById(R.id.payment_date);
        final String date = DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMMYY_FORMAT, paymentDay).toUpperCase();
        if (date.length() > 0) {
            paymentDate.setText(date);
        } else {
            paymentDate.setText(paymentDay);
        }
    }

    private void unselectCheckboxes(CheckBox checkBox1, CheckBox checkBox2, CheckBox checkBox3, CheckBox checkBox4) {
        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        checkBox4.setChecked(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_REQUEST && resultCode == RESULT_OK) {
            new PayTask(getAmount()).execute();
        }
        if (requestCode == ON_CLOSE_REQUEST && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private class PayTask extends AsyncTask<Void, Void, Void> {

        private String errorCode;
        private String amount;
        private boolean transactionMade;

        public PayTask(String amount) {
            this.amount = amount;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                transactionMade = Service.payCard(sid, card.getLastDigits(), selectedAccount.getLastDigits(),
                        amount, isUsdPayment(), getTodo1Data());;
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
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                DialogUtil.toast(CardPayDetailActivity.this, getString(R.string.card_pay_success_title),
                        getString(R.string.card_pay_success_subtitle),
                        getSuccessText(getString(R.string.card_pay_success_text)), ON_CLOSE_REQUEST);
            }
        }
    }
}
