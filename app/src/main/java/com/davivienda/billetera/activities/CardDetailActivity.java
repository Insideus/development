package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.interfaces.OnNewUserSession;
import com.davivienda.billetera.ui.adapters.CardDetailAdapter;
import com.davivienda.billetera.utils.CardsUtils;
import com.davivienda.billetera.utils.CurrencyUtils;
import com.davivienda.billetera.utils.DateUtils;
import com.davivienda.billetera.utils.DialogUtil;

public class CardDetailActivity extends MovementsShowerActivity implements CardDetailAdapter.ICardDetailAdapterOwner, OnNewUserSession {

    public static String CARD_KEY = "card_key";
    private boolean refresh = false;

    private CardDetailAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
        }
        setFooterLayouts();
        loadMore = true;
        if (card != null && card.getECard() != null && card.getECard()) {
            setToolbar(R.id.toolbar_layout, true, getString(R.string.e_card_title));
            setECardFooterViews();
        } else {
            setToolbar(R.id.toolbar_layout, true, card.getBin().getFranchise().toUpperCase());
        }
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        setRecycler();
        setSearcher();
        newUserSessionListener = this;
        getCardMovements();
    }

    private void setSearcher() {
        final EditText query = (EditText) findViewById(R.id.query);
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                doQuery(s.toString());
            }
        });
        query.clearFocus();
    }

    private void doQuery(String query) {
        adapter.executeFilter(query);
    }

    private void setECardFooterViews() {
        TextView paymentDate = (TextView) findViewById(R.id.payment_date);
        paymentDate.setVisibility(View.GONE);
        View eCardBalanceTitle = findViewById(R.id.ecard_balance_title);
        eCardBalanceTitle.setVisibility(View.VISIBLE);
        View balanceTitle = findViewById(R.id.balance_title);
        balanceTitle.setVisibility(View.GONE);
        View paymentDateTitle = findViewById(R.id.payment_date_title);
        paymentDateTitle.setVisibility(View.GONE);
    }

    protected void setDataToShow() {
        adapter.setShowPayButton(transactionDetails.isShowPayButton());
        if (refresh) {
            adapter.setList(transactionDetails.getTransactions());
            linearLayoutManager.scrollToPosition(0);
            refresh = false;
        } else {
            adapter.addToList(transactionDetails.getTransactions());
        }
        TextView balance = (TextView) findViewById(R.id.balance);
        balance.setText("$".concat(CurrencyUtils.getCurrencyForString(transactionDetails.getAvailableAmount())));
        if (card.getECard() != null && !card.getECard()) {
            TextView paymentDate = (TextView) findViewById(R.id.payment_date);
            String date = DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMYY_FORMAT, transactionDetails.getPaymentDate()).toLowerCase();
            if (date.length() > 0) {
                paymentDate.setText(date);
            } else {
                paymentDate.setText(transactionDetails.getPaymentDate());
            }
        }

    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if (recyclerView == null) {
            return;
        }
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CardDetailAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CardPayDetailActivity.PAY_REQUEST && resultCode == RESULT_OK) {
            refresh = true;
            new GetCardTransactionDetailsTask(this).execute();
        }
        if(requestCode == CLOSE_ACTIVITY_REQUEST){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_movements, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(CardDetailActivity.CARD_KEY, card);
            bundle.putParcelable(CardPayDetailActivity.TRANSACTION_DETAILS, transactionDetails);
            bundle.putParcelableArrayList(MovementsByDayActivity.LOADED_TRANSACTIONS, adapter.getList());
            bundle.putInt(MovementsByDayActivity.LOADED_PAGE, curPage);
            bundle.putBoolean(MovementsByDayActivity.LOAD_MORE, loadMore);
            startActivity(new Intent(this, MovementsByDayActivity.class).putExtras(bundle));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCardMovements() {
        new GetCardTransactionDetailsTask(this, new ITransactionDetailListener() {
            @Override
            public void onError() {
                DialogUtil.toast(CardDetailActivity.this,
                        getString(R.string.generic_service_error_title),
                        getString(R.string.generic_service_error_subtitle),
                        getString(R.string.generic_service_error),
                        CLOSE_ACTIVITY_REQUEST);
            }
        }).execute();
    }

    @Override
    public void loadMore() {
        ((EditText) findViewById(R.id.query)).setText("");
        super.loadMore();
    }

    @Override
    public void onNewSession() {
        getCardMovements();
    }

}
