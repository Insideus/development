package ar.com.fennoma.davipocket.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Account;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.PaymentDetail;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.TransactionDetails;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.controls.ComboHolder;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;

public class CardPayDetailActivity extends BaseActivity {

    public static final String TRANSACTION_DETAILS = "transactions details";
    public static final int PAY_REQUEST = 12;
    public static String CARD_KEY = "card_key";

    private Card card;
    private String priceIndicator;
    private CheckBox totalPayment;
    private CheckBox minimumPayment;
    private CheckBox otherPayment;
    private TextView selectedAccountText;

    private boolean justDeletedOtherPaymentText = false;
    private PaymentDetail detail;
    private TransactionDetails transactionDetails;
    private Account selectedAccount;
    private DialogPlus dialogPlus;
    private View payButton;

    private TextView totalPaymentLabel;
    private TextView minimumPaymentLabel;
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
        priceIndicator = getString(R.string.card_detail_item_transaction_price_indicator);
        setToolbar(R.id.toolbar_layout, false, card.getBin().getFranchise().toUpperCase());
        setLayouts();
        new GetCardPayDetail().execute();
    }

    @Override
    public void onBackPressed() {
        if (dialogPlus == null || !dialogPlus.isShowing()) {
            super.onBackPressed();
        }
    }

    private void setLayoutData() {
        totalPaymentLabel = (TextView) findViewById(R.id.total_payment_label);
        minimumPaymentLabel = (TextView) findViewById(R.id.minimum_payment_label);
        if (totalPaymentLabel == null || minimumPaymentLabel == null) {
            return;
        }
        totalPaymentLabel.setText(detail.getTotal());
        minimumPaymentLabel.setText(detail.getMinimumPayment());
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
        setTransactionDetails();
        selectedAccountText = (TextView) findViewById(R.id.account_spinner);
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        totalPayment = (CheckBox) findViewById(R.id.total_payment);
        minimumPayment = (CheckBox) findViewById(R.id.minimum_payment);
        otherPayment = (CheckBox) findViewById(R.id.other_payment);
        final View otherPaymentContainer = findViewById(R.id.other_payment_container);
        otherPaymentValue = (EditText) findViewById(R.id.other_payment_value);
        payButton = findViewById(R.id.pay_button);
        if (totalPayment == null || minimumPayment == null || otherPayment == null || otherPaymentContainer == null
                || otherPaymentValue == null || cardTitle == null || payButton == null) {
            return;
        }
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        totalPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(minimumPayment, otherPayment);
            }
        });
        minimumPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(totalPayment, otherPayment);
            }
        });
        otherPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(totalPayment, minimumPayment);
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
                if(!TextUtils.isEmpty(amount)) {
                    //TODO: Acá tenés que levantar el cartel de confirmación
                    new PayTask(amount).execute();
                }
            }
        });
    }

    private String getAmount() {
        if(totalPayment.isChecked()){
            return totalPaymentLabel.getText().toString();
        } else if (minimumPayment.isChecked()){
            return minimumPaymentLabel.getText().toString();
        } else {
            String value = otherPaymentValue.getText().toString();
            value = value.replace(priceIndicator, "");
            value = value.replace(" ", "");
            return value;
        }
    }

    private void setTransactionDetails() {
        if (transactionDetails == null) {
            return;
        }
        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText("$" + transactionDetails.getAvailableAmount());
        TextView paymentDate = (TextView) findViewById(R.id.payment_date);
        final String date = DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMMYY_FORMAT, transactionDetails.getPaymentDate());
        if (date.length() > 0) {
            paymentDate.setText(date);
        } else {
            paymentDate.setText(transactionDetails.getPaymentDate());
        }
    }

    private void unselectCheckboxes(CheckBox checkBox1, CheckBox checkBox2) {
        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
    }

    public void showCombo() {
        final ComboAdapter adapter = new ComboAdapter(detail.getAccounts(), selectedAccount);
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setContentHolder(new ComboHolder())
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
                selectedAccount = adapter.selectedAccount;
                setSelectedIdAccountName();
                dialog.dismiss();
            }
        });
        footerView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        setSelectedIdAccountName();
        dialog.show();
        dialogPlus = dialog;
    }

    public void setSelectedIdAccountName() {
        if (selectedAccount != null) {
            selectedAccountText.setText(getAccountInfoToShow(selectedAccount));
        }
    }

    private class ComboAdapter extends BaseAdapter {

        private List<Account> accounts;
        private Account selectedAccount;

        public ComboAdapter(List<Account> accounts, Account selectedAccount) {
            this.accounts = accounts;
            this.selectedAccount = selectedAccount;
        }

        @Override
        public int getCount() {
            return accounts.size();
        }

        @Override
        public Account getItem(int position) {
            return accounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.valueOf(accounts.get(position).getLastDigits());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            TextView row;
            if (convertView == null) {
                row = (TextView) inflater.inflate(R.layout.combo_item, parent, false);
            } else {
                row = (TextView) convertView;
            }
            final Account account = getItem(position);
            row.setText(getAccountInfoToShow(account));
            if (selectedAccount != null && selectedAccount.getLastDigits() == account.getLastDigits()) {
                row.setTextColor(ContextCompat.getColor(CardPayDetailActivity.this, R.color.combo_item_text_color_selected));
            } else {
                row.setTextColor(ContextCompat.getColor(CardPayDetailActivity.this, R.color.combo_item_text_color));
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedAccount = account;
                    notifyDataSetChanged();
                }
            });
            return row;
        }

    }

    private String getAccountInfoToShow(Account account) {
        String result = "";
        if (!TextUtils.isEmpty(account.getName())) {
            result = result.concat(account.getName()).concat(" ");
        }
        if (!TextUtils.isEmpty(account.getLastDigits())) {
            result = result.concat(account.getLastDigits()).concat(" : ");
        } else if (!TextUtils.isEmpty(result)) {
            result = result.concat(": ");
        }
        if (!TextUtils.isEmpty(account.getBalance())) {
            result = result.concat(account.getBalance());
        }
        return result;
    }

    public class GetCardPayDetail extends AsyncTask<Void, Void, Void> {

        private String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                detail = Service.balanceDetail(sid, card.getLastDigits());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideLoading();
            if (detail == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                setLayoutData();
            }
        }
    }

    private class PayTask extends AsyncTask<Void, Void, Void> {

        private String errorCode;
        private String amount;

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
                Service.payCard(sid, card.getLastDigits(), selectedAccount.getLastDigits(), amount);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideLoading();
            if (amount == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
