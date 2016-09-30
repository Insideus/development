package ar.com.fennoma.davipocket.activities;

import android.os.AsyncTask;
import android.view.View;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.TransactionDetails;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.CardDetailAdapter;

public abstract class MovementsShowerActivity extends BaseActivity implements CardDetailAdapter.ICardDetailAdapterOwner{

    protected Boolean loadMore;
    protected Card card;
    protected TransactionDetails transactionDetails;
    protected int curPage = 1;

    public class GetCardTransactionDetailsTask extends AsyncTask<Void, Void, TransactionDetails> {

        String errorCode;
        private String dateFrom = null;
        private String dateTo = null;

        public GetCardTransactionDetailsTask(){}

        public GetCardTransactionDetailsTask(String dateFrom, String dateTo){
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected TransactionDetails doInBackground(Void... params) {
            TransactionDetails response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.getCardMovementsDetails(sid, card.getLastDigits(), curPage, dateFrom, dateTo, getTodo1Data());
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(TransactionDetails response) {
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
            } else {
                if(response.getTransactions() != null) {
                    loadMore = response.isLoadMore();
                } else {
                    loadMore = false;
                }
                transactionDetails = response;
                setDataToShow();
            }
        }
    }

    protected void setFooterLayouts() {
        View ecardBalance = findViewById(R.id.ecard_balance);
        View ecardBalanceTitle = findViewById(R.id.ecard_balance_title);
        View balanceTitle = findViewById(R.id.balance_title);
        View balance = findViewById(R.id.balance);
        View payment = findViewById(R.id.payment_date);
        View paymentTitle = findViewById(R.id.payment_date_title);
        if(ecardBalance == null || ecardBalanceTitle == null || balance == null || balanceTitle == null ||
                payment == null || paymentTitle == null){
            return;
        }
        if(card.getECard() != null && card.getECard()){
            ecardBalance.setVisibility(View.VISIBLE);
            ecardBalanceTitle.setVisibility(View.VISIBLE);
        }else{
            balance.setVisibility(View.VISIBLE);
            balanceTitle.setVisibility(View.VISIBLE);
            payment.setVisibility(View.VISIBLE);
            paymentTitle.setVisibility(View.VISIBLE);
        }
    }

    protected abstract void setDataToShow();

    @Override
    public Card getCard() {
        return card;
    }

    @Override
    public TransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    @Override
    public boolean doLoadMore() {
        return loadMore;
    }

    @Override
    public void loadMore() {
        curPage++;
        new CardDetailActivity.GetCardTransactionDetailsTask().execute();
    }
}
