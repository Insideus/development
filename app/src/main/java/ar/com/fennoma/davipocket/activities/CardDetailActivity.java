package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.ui.adapters.CardDetailAdapter;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;

public class CardDetailActivity extends MovementsShowerActivity implements CardDetailAdapter.ICardDetailAdapterOwner {

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
        } else {
            setToolbar(R.id.toolbar_layout, true, card.getBin().getFranchise().toUpperCase());
        }
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        setRecycler();
        setSearcher();
        new GetCardTransactionDetailsTask().execute();
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

    protected void setDataToShow() {
        adapter.setShowPayButton(transactionDetails.isShowPayButton());
        if (refresh) {
            adapter.setList(transactionDetails.getTransactions());
            linearLayoutManager.scrollToPosition(0);
            refresh = false;
        } else {
            adapter.addToList(transactionDetails.getTransactions());
        }
        if (card.getECard() != null && card.getECard()) {
            TextView ecardBalance = (TextView) findViewById(R.id.ecard_balance);
            ecardBalance.setText("$" + CurrencyUtils.getCurrencyForString(transactionDetails.getAvailableAmount()));
            return;
        }
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
            new GetCardTransactionDetailsTask().execute();
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

    @Override
    public void loadMore() {
        ((EditText) findViewById(R.id.query)).setText("");
        super.loadMore();
    }
}
