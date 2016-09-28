package ar.com.fennoma.davipocket.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Account;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.PaymentDetail;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.controls.ComboHolder;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public abstract class AbstractPayActivity extends BaseActivity{

    public static final String CARD_KEY = "card key";
    public static final int PAY_REQUEST = 12;
    protected static final int ON_CLOSE_REQUEST = 13;

    protected Card card;
    protected String priceIndicator;
    protected boolean justDeletedOtherPaymentText = false;
    protected PaymentDetail detail;
    protected TextView selectedAccountText;
    protected DialogPlus dialogPlus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        priceIndicator = getString(R.string.card_detail_item_transaction_price_indicator);
    }

    protected Account selectedAccount;

    protected boolean validateAmount(String amount) {
        if(TextUtils.isEmpty(amount)){
            showErrorDialog(getString(R.string.card_pay_empty_amount));
            return false;
        }
        if(Double.valueOf(amount) == 0){
            showErrorDialog(getString(R.string.card_pay_zero_amount));
            return false;
        }
        if(Double.valueOf(amount) > Double.valueOf(selectedAccount.getBalance())){
            showErrorDialog(getString(R.string.card_pay_insuficient_balance));
            return false;
        }
        return true;
    }

    private void showErrorDialog(String error){
        DialogUtil.toast(this, getString(R.string.generic_service_error_title), "",
                error);
    }

    protected String getPayConfirmationText(String amount) {
        return getString(R.string.card_pay_confirmation_text_1).concat(" ").concat(card.getLastDigits()).concat(" ")
                .concat(getString(R.string.card_pay_confirmation_text_2)).concat(" ").concat(selectedAccount.getName())
                .concat(" ").concat(getString(R.string.card_pay_confirmation_text_3)).concat(" ")
                .concat(selectedAccount.getLastDigits()).concat(" ").concat(getString(R.string.card_pay_confirmation_text_4))
                .concat(" ").concat(amount).concat("?");
    }

    protected class GetCardPayDetail extends AsyncTask<Void, Void, Void> {

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
                //Handle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                setBottomLayouts();
                setLayoutData();
            }
        }
    }

    protected static String getAccountInfoToShow(Account account) {
        String result = "";
        if (!TextUtils.isEmpty(account.getName())) {
            result = result.concat(account.getName()).concat(" ");
        }
        if (!TextUtils.isEmpty(account.getLastDigits())) {
            result = result.concat(account.getLastDigits()).concat(": ");
        } else if (!TextUtils.isEmpty(result)) {
            result = result.concat(": ");
        }
        if (!TextUtils.isEmpty(account.getBalance())) {
            result = result.concat("$" + CurrencyUtils.getCurrencyForString(account.getBalance()));
        }
        return result;
    }

    public void setSelectedIdAccountName() {
        if (selectedAccount != null) {
            selectedAccountText.setText(getAccountInfoToShow(selectedAccount));
        }
    }

    protected abstract void setBottomLayouts();

    protected abstract void setLayoutData();

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
            if (selectedAccount != null && selectedAccount.getLastDigits().equals(account.getLastDigits())) {
                row.setTextColor(ContextCompat.getColor(AbstractPayActivity.this, R.color.combo_item_text_color_selected));
            } else {
                row.setTextColor(ContextCompat.getColor(AbstractPayActivity.this, R.color.combo_item_text_color));
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

    private String getSuccessText() {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d", Locale.getDefault());
        Date time = Calendar.getInstance().getTime();
        date = date.concat(simpleDateFormat.format(time)).concat(" de ");
        simpleDateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        date = date.concat(simpleDateFormat.format(time).concat(" de "));
        simpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        date = date.concat(simpleDateFormat.format(time)).concat(" a las ");
        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        date = date.concat(simpleDateFormat.format(time));
        return getString(R.string.card_pay_success_text).concat(" ").concat(date);
    }

    protected class PayTask extends AsyncTask<Void, Void, Void> {

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
                transactionMade = callService(sid, amount);
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
                DialogUtil.toast(AbstractPayActivity.this, getString(R.string.card_pay_success_title),
                        getString(R.string.card_pay_success_subtitle),
                        getSuccessText(), ON_CLOSE_REQUEST);
            }
        }
    }

    protected abstract boolean callService(String sid, String amount) throws ServiceException;
}
