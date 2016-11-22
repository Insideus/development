package ar.com.fennoma.davipocket.activities;

import android.view.View;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.TransactionDetails;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.tasks.DaviPayTask;
import ar.com.fennoma.davipocket.ui.adapters.CardDetailAdapter;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public abstract class MovementsShowerActivity extends BaseActivity implements CardDetailAdapter.ICardDetailAdapterOwner{

    protected Boolean loadMore;
    protected Card card;
    protected TransactionDetails transactionDetails;
    protected int curPage = 1;

    protected interface ITransactionDetailListener{
        void onError();
    }

    public class GetCardTransactionDetailsTask extends DaviPayTask<TransactionDetails> {

        private ITransactionDetailListener listener;
        private String dateFrom = null;
        private String dateTo = null;

        GetCardTransactionDetailsTask(BaseActivity activity){
            super(activity);
        }

        GetCardTransactionDetailsTask(BaseActivity activity, ITransactionDetailListener listener) {
            super(activity);
            this.listener = listener;
        }

        GetCardTransactionDetailsTask(BaseActivity activity, String dateFrom, String dateTo) {
            super(activity);
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
                response = Service.getCardMovementsDetails(sid, card.getECard(), card.getLastDigits(), curPage, dateFrom, dateTo, getTodo1Data());
            }  catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(TransactionDetails response) {
            hideLoading();
            if(response == null) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if(error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } if(error != null && error == ErrorMessages.BLOCKED_CARD) {
                    DialogUtil.toast(MovementsShowerActivity.this,
                            getString(R.string.blocked_card_error_title),
                            getString(R.string.blocked_card_error_subtitle),
                            getString(R.string.blocked_card_error_text),
                            CLOSE_ACTIVITY_REQUEST);
                } else {
                    if(listener != null){
                        listener.onError();
                    } else {
                        DialogUtil.toast(MovementsShowerActivity.this, getString(R.string.generic_service_error_title), "",
                                getString(R.string.card_detail_get_transactions_error_text), CLOSE_ACTIVITY_REQUEST);
                    }
                }
            } else {
                if(response.getTransactions() != null) {
                    loadMore = response.isLoadMore();
                } else {
                    loadMore = false;
                }
                transactionDetails = response;
                setDataToShow();
                if(transactionDetails.getTransactions() != null && transactionDetails.getTransactions().size() < 1 && curPage == 1) {
                    DialogUtil.toast(MovementsShowerActivity.this,
                            getString(R.string.card_without_movements_error_title),
                            getString(R.string.card_without_movements_error_subtitle),
                            getString(R.string.card_without_movements_error_text));
                }
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
        new CardDetailActivity.GetCardTransactionDetailsTask(this).execute();
    }
}
