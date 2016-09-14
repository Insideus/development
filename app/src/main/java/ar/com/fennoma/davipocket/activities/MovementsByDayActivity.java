package ar.com.fennoma.davipocket.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.ui.adapters.CardDetailAdapter;
import ar.com.fennoma.davipocket.utils.CardsUtils;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class MovementsByDayActivity extends MovementsShowerActivity{

    private CardDetailAdapter adapter;
    private String dateFrom;
    private String dateTo;
    private boolean replaceList = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movement_by_day_layout);
        if(savedInstanceState != null){
            card = savedInstanceState.getParcelable(CardDetailActivity.CARD_KEY);
        }else{
            card = getIntent().getParcelableExtra(CardDetailActivity.CARD_KEY);
        }
        setToolbar(R.id.toolbar_layout, true, card.getBin().getFranchise().toUpperCase());
        TextView cardTitle = (TextView) findViewById(R.id.card_title);
        cardTitle.setText(CardsUtils.getMaskedCardNumber(card.getLastDigits()));
        setRecycler();
        setLayouts();
        new GetCardTransactionDetailsTask().execute();
    }

    private void setLayouts() {
        View dateToFilter = findViewById(R.id.date_to_filter);
        View dateFromFilter = findViewById(R.id.date_from_filter);
        final View dateToContainer = findViewById(R.id.date_to);
        final View dateFromContainer = findViewById(R.id.date_from);
        final TextView dateFromDay = (TextView) findViewById(R.id.date_from_day);
        final TextView dateFromMonth = (TextView) findViewById(R.id.date_from_month);
        final TextView dateFromYear = (TextView) findViewById(R.id.date_from_year);
        final TextView dateToDay = (TextView) findViewById(R.id.date_to_day);
        final TextView dateToMonth = (TextView) findViewById(R.id.date_to_month);
        final TextView dateToYear = (TextView) findViewById(R.id.date_to_year);
        View filterButton = findViewById(R.id.filter_button);
        if(dateToFilter == null || dateFromFilter == null || dateToContainer == null || dateFromContainer == null ||
                dateFromDay == null || dateFromMonth == null || dateFromYear == null || dateToDay == null || dateToMonth == null
                || dateToYear == null || filterButton == null){
            return;
        }
        dateToFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showDatePicker(MovementsByDayActivity.this, Calendar.getInstance(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateToDay.setText(String.valueOf(dayOfMonth));
                        dateToMonth.setText(String.valueOf(monthOfYear));
                        dateToYear.setText(String.valueOf(year));
                        dateToContainer.setVisibility(View.VISIBLE);
                        dateTo = String.format("%s%s%s%s%s", String.valueOf(dayOfMonth), "/", String.valueOf(monthOfYear),
                                "/", String.valueOf(year));
                    }
                });
            }
        });
        dateFromFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showDatePicker(MovementsByDayActivity.this, Calendar.getInstance(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateFromDay.setText(String.valueOf(dayOfMonth));
                        dateFromMonth.setText(String.valueOf(monthOfYear));
                        dateFromYear.setText(String.valueOf(year));
                        dateFromContainer.setVisibility(View.VISIBLE);
                        dateFrom = String.format("%s%s%s%s%s", String.valueOf(dayOfMonth), "/", String.valueOf(monthOfYear),
                                "/", String.valueOf(year));
                    }
                });
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(dateTo) || TextUtils.isEmpty(dateFrom)){
                    return;
                }
                replaceList = true;
                new GetCardTransactionDetailsTask(dateFrom, dateTo).execute();
            }
        });
    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if (recyclerView == null) {
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new CardDetailAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setDataToShow() {
        if(replaceList){
            replaceList = false;
            adapter.setList(transactionDetails.getTransactions());
        }else {
            adapter.addToList(transactionDetails.getTransactions());
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
}
