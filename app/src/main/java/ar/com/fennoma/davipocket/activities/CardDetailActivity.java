package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.Transaction;
import ar.com.fennoma.davipocket.model.TransactionDetails;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.CardDetailAdapter;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class CardDetailActivity extends BaseActivity implements CardDetailAdapter.ICardDetailAdapterOwner{

    public static String CARD_KEY = "card_key";

    private CardDetailAdapter adapter;
    private Card card;
    private Boolean loadMore;
    private TransactionDetails transactionDetails;
    private int curPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
        }
        loadMore = true;
        setToolbar(R.id.toolbar_layout, true, card.getBin().getFranchise().toUpperCase());
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        setRecycler();
        setSearcher();
        new GetCardTransactionDetailsTask().execute();
    }

    private void setSearcher() {
        final EditText query = (EditText) findViewById(R.id.query);
        View searchButton = findViewById(R.id.search_button);
        if(query == null || searchButton == null){
            return;
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(query.getText())){
                    return;
                }
                doQuery(query.getText().toString());
            }
        });
        query.clearFocus();
    }

    private void doQuery(String query) {
        DialogUtil.toast(this, "Haciendo búsqueda");
    }

    private void setDataToShow() {
        adapter.setList(addManagableData(transactionDetails.getTransactions()));
        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText("$" + CurrencyUtils.getCurrencyForString(transactionDetails.getAvailableAmount()));
        TextView paymentDate = (TextView) findViewById(R.id.payment_date);
        final String date = DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMMYY_FORMAT, transactionDetails.getPaymentDate()).toUpperCase();
        if (date.length() > 0) {
            paymentDate.setText(date);
        } else {
            paymentDate.setText(transactionDetails.getPaymentDate());
        }
    }

    private List<Transaction> addManagableData(List<Transaction> transactions) {
        if (transactions == null || transactions.size() <= 1) {
            return null;
        }

        transactions.add(0, null);
        Transaction comparator = transactions.get(1);
        for (int i = 2; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            if (!comparator.getDate().equals(transaction.getDate())) {
                transactions.add(i, null);
                i++;
            }
            comparator = transaction;
        }
        transactions.add(null);
        return transactions;
    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if (recyclerView == null) {
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardDetailAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CardPayDetailActivity.PAY_REQUEST && resultCode == RESULT_OK){
            //new GetCardTransactionDetailsTask().
            //TODO: Acá tenés que hacer refresh a la data del recycler (volvela a pedir, porque cambió :D)
        }
    }

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

    public class GetCardTransactionDetailsTask extends AsyncTask<Void, Void, TransactionDetails> {

        String errorCode;

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
                response = Service.getCardMovementsDetails(sid, card.getLastDigits(), curPage, "", "");
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

}
