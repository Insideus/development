package ar.com.fennoma.davipocket.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Account;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.PaymentDetail;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.tasks.DaviPayTask;
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
    protected Account selectedAccount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        priceIndicator = getString(R.string.card_detail_item_transaction_price_indicator);
    }

    protected boolean validateAmount(Double amount) {
        if(amount == null){
            showErrorDialog(getString(R.string.card_pay_empty_amount));
            return false;
        }
        if(amount == 0){
            showErrorDialog(getString(R.string.card_pay_zero_amount));
            return false;
        }
        if(amount > selectedAccount.getBalance()){
            showErrorDialog(getString(R.string.card_pay_insuficient_balance));
            return false;
        }
        return true;
    }

    protected void showErrorDialog(String error){
        DialogUtil.toast(this, getString(R.string.generic_service_error_title), "",
                error);
    }

    protected class GetCardPayDetail extends DaviPayTask<Void> {

        public GetCardPayDetail(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                detail = Service.balanceDetail(sid, card.getLastDigits(), getTodo1Data());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            if(!processedError) {
                setBottomLayouts();
                setLayoutData();
            }
        }
    }

    protected static String getAccountInfoToShow(Activity activity, Account account) {
        String result = "";
        if (TextUtils.isEmpty(account.getName()) || TextUtils.isEmpty(account.getLastDigits()) || TextUtils.isEmpty(CurrencyUtils.getCurrencyForString(account.getBalance()))) {
            return result;
        }
        result = activity.getString(R.string.payment_layout_account_balance_selector, account.getName(),
                account.getLastDigits(), CurrencyUtils.getCurrencyForString(account.getBalance()));
        return result;
    }

    public void setSelectedIdAccountName() {
        if (selectedAccount != null) {
            selectedAccountText.setText(getAccountInfoToShow(this, selectedAccount));
        }
    }

    protected abstract void setBottomLayouts();

    protected abstract void setLayoutData();

    private class ComboAdapter extends BaseAdapter {

        private List<Account> accounts;

        private Account selectedAccount;

        ComboAdapter(List<Account> accounts, Account selectedAccount) {
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
            row.setText(getAccountInfoToShow(AbstractPayActivity.this, account));
            if (selectedAccount != null && selectedAccount.getLastDigits().equals(account.getLastDigits())
                    && selectedAccount.getCode().equals(account.getCode())) {
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

    public void showCombo(){
        final ComboAdapter adapter = new ComboAdapter(detail.getAccounts(), selectedAccount);
        showCombo(adapter, new IComboListener() {
            @Override
            public void onAccept() {
                selectedAccount = adapter.selectedAccount;
                setSelectedIdAccountName();
                dialogPlus.dismiss();
            }

            @Override
            public void setSelectedItem() {
                setSelectedIdAccountName();
            }
        });
    }

    protected String getSuccessText(String startingText) {
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d", new Locale("es", "ES"));
        Date time = Calendar.getInstance().getTime();
        date = date.concat(simpleDateFormat.format(time)).concat(" de ");
        simpleDateFormat = new SimpleDateFormat("MMM", new Locale("es", "ES"));
        date = date.concat(simpleDateFormat.format(time).concat(" de "));
        simpleDateFormat = new SimpleDateFormat("yyyy", new Locale("es", "ES"));
        date = date.concat(simpleDateFormat.format(time)).concat(" a las ");
        simpleDateFormat = new SimpleDateFormat("HH:mm", new Locale("es", "ES"));
        date = date.concat(simpleDateFormat.format(time));
        return startingText.concat(" ").concat(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CLOSE_ACTIVITY_REQUEST){
            finish();
        }
    }
}
