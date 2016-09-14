package ar.com.fennoma.davipocket.activities;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.Transaction;
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
                response = Service.getCardMovementsDetails(sid, card.getLastDigits(), curPage, dateFrom, dateTo);
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
