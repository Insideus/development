package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.IShowableItem;
import com.davivienda.billetera.model.TransactionByDayBar;
import com.davivienda.billetera.ui.adapters.CardDetailAdapter;
import com.davivienda.billetera.utils.CardsUtils;
import com.davivienda.billetera.utils.CurrencyUtils;
import com.davivienda.billetera.utils.DateUtils;

public class MovementsByDayActivity extends MovementsShowerActivity implements CardDetailAdapter.IByDayBarOwner{

    public static final String LOADED_TRANSACTIONS = "loaded transactions";
    public static final String LOADED_PAGE = "loaded page";
    public static final String LOAD_MORE = "load more";
    private CardDetailAdapter adapter;
    private String dateFrom;
    private String dateTo;
    private boolean replaceList = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movement_by_day_layout);
        ArrayList<IShowableItem> transactions;
        if(savedInstanceState != null){
            card = savedInstanceState.getParcelable(CardDetailActivity.CARD_KEY);
            transactions = savedInstanceState.getParcelableArrayList(LOADED_TRANSACTIONS);
            curPage = savedInstanceState.getInt(LOADED_PAGE);
            transactionDetails = savedInstanceState.getParcelable(CardPayDetailActivity.TRANSACTION_DETAILS);
            loadMore = savedInstanceState.getBoolean(LOAD_MORE);
        }else{
            card = getIntent().getParcelableExtra(CardDetailActivity.CARD_KEY);
            transactions = getIntent().getParcelableArrayListExtra(LOADED_TRANSACTIONS);
            curPage = getIntent().getIntExtra(LOADED_PAGE, -1);
            transactionDetails = getIntent().getParcelableExtra(CardPayDetailActivity.TRANSACTION_DETAILS);
            loadMore = getIntent().getBooleanExtra(LOAD_MORE, true);
        }
        setFooterLayouts();
        if(card != null && card.getECard() != null && card.getECard()){
            setToolbar(R.id.toolbar_layout, true, getString(R.string.e_card_title));
        }else {
            setToolbar(R.id.toolbar_layout, true, card.getBin().getFranchise().toUpperCase());
        }
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        setRecycler();
        setBalanceData();
        adapter.setShowPayButton(transactionDetails.isShowPayButton());
        adapter.justSetList(addByDayBar(transactions));
    }

    private ArrayList<IShowableItem> addByDayBar(ArrayList<IShowableItem> transactions) {
        transactions.add(0, new TransactionByDayBar());
        return transactions;
    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if (recyclerView == null) {
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new CardDetailAdapter(this, this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setDataToShow() {
        adapter.setShowPayButton(transactionDetails.isShowPayButton());
        if(replaceList){
            replaceList = false;
            adapter.setList(transactionDetails.getTransactions());
        }else {
            adapter.addToList(transactionDetails.getTransactions());
        }
        setBalanceData();
    }

    private void setBalanceData(){
        if(transactionDetails == null){
            return;
        }
        if (card.getECard() != null && card.getECard()) {
            TextView ecardBalance = (TextView) findViewById(R.id.ecard_balance);
            ecardBalance.setText("$" + CurrencyUtils.getCurrencyForString(transactionDetails.getAvailableAmount()));
            return;
        }
        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText("$" + CurrencyUtils.getCurrencyForString(transactionDetails.getAvailableAmount()));
        TextView paymentDate = (TextView) findViewById(R.id.payment_date);
        final String date = DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMYY_FORMAT, transactionDetails.getPaymentDate()).toLowerCase();
        if (date.length() > 0) {
            paymentDate.setText(date);
        } else {
            paymentDate.setText(transactionDetails.getPaymentDate());
        }
    }

    @Override
    public void gotDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public void gotDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    @Override
    public void onFilter() {
        if(TextUtils.isEmpty(dateTo) || TextUtils.isEmpty(dateFrom)){
            return;
        }
        replaceList = true;
        new GetCardTransactionDetailsTask(this, dateFrom, dateTo).execute();
    }

}
