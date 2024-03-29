package com.davivienda.billetera.ui.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.davivienda.billetera.DaviPayApplication;
import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.AbstractPayActivity;
import com.davivienda.billetera.activities.CardPayDetailActivity;
import com.davivienda.billetera.activities.ECardRechargeActivity;
import com.davivienda.billetera.model.Card;
import com.davivienda.billetera.model.IShowableItem;
import com.davivienda.billetera.model.Transaction;
import com.davivienda.billetera.model.TransactionByDayBar;
import com.davivienda.billetera.model.TransactionDetails;
import com.davivienda.billetera.model.TransactionPayButton;
import com.davivienda.billetera.model.TransactionTitle;
import com.davivienda.billetera.utils.CurrencyUtils;
import com.davivienda.billetera.utils.DateUtils;
import com.davivienda.billetera.utils.DialogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CardDetailAdapter extends RecyclerView.Adapter {

    private final static int TOP_ITEM = 0;
    private final static int MIDDLE_ITEM = 1;
    private final static int BOTTOM_ITEM = 2;
    private final static int LONELY_ITEM = 3;

    private final static int TITLE = 0;
    private final static int ITEM = 1;
    private final static int BUTTON = 2;
    private static final int BY_DAY_BAR = 3;
    private IByDayBarOwner barOwner;

    private Activity activity;
    private ArrayList<IShowableItem> originalTransactions;
    private List<Transaction> filteredTransactions;
    private List<IShowableItem> transactionsBeingShowed;
    private ICardDetailAdapterOwner owner;
    private Boolean showPayButton;
    private String dateFrom;

    public CardDetailAdapter(Activity activity, ICardDetailAdapterOwner owner) {
        this.activity = activity;
        originalTransactions = new ArrayList<>();
        transactionsBeingShowed = new ArrayList<>();
        this.owner = owner;
        this.showPayButton = true;
    }

    public Boolean getShowPayButton() {
        return showPayButton;
    }

    public void setShowPayButton(Boolean showPayButton) {
        this.showPayButton = showPayButton;
    }

    public interface IByDayBarOwner {
        void gotDateTo(String dateTo);

        void gotDateFrom(String dateFrom);

        void onFilter();
    }

    public CardDetailAdapter(Activity activity, ICardDetailAdapterOwner owner, IByDayBarOwner barOwner) {
        this.activity = activity;
        originalTransactions = new ArrayList<>();
        transactionsBeingShowed = new ArrayList<>();
        this.owner = owner;
        this.barOwner = barOwner;
        this.showPayButton = true;
    }

    public void executeFilter(String query) {
        if (TextUtils.isEmpty(query)) {
            transactionsBeingShowed.clear();
            transactionsBeingShowed.addAll(originalTransactions);
        } else {
            if (filteredTransactions == null) {
                filteredTransactions = new ArrayList<>();
            } else {
                filteredTransactions.clear();
            }

            for (int i = 0; i < originalTransactions.size(); i++) {
                IShowableItem t = originalTransactions.get(i);
                if (t.getKindOfItem() == IShowableItem.TRANSACTION &&
                        ((Transaction) t).getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredTransactions.add((Transaction) t);
                }
            }

            List<IShowableItem> filteredTransactionsToShow = getProcessedTransactionList(filteredTransactions);
            transactionsBeingShowed.clear();
            if (filteredTransactions != null) {
                transactionsBeingShowed.addAll(filteredTransactionsToShow);
            }
        }
        notifyDataSetChanged();
    }

    public void addToList(List<Transaction> receivedTransactions) {
        List<IShowableItem> transactions = getProcessedTransactionList(receivedTransactions);
        if (this.originalTransactions.size() > 0) {
            this.originalTransactions.remove(this.originalTransactions.size() - 1);
            if (((Transaction) this.originalTransactions.get(this.originalTransactions.size() - 1)).getDate().equals(((Transaction) transactions.get(1)).getDate())) {
                receivedTransactions.remove(0);
            }
        }
        this.originalTransactions.addAll(transactions);
        this.transactionsBeingShowed.clear();
        this.transactionsBeingShowed.addAll(originalTransactions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (transactionsBeingShowed.get(position).getKindOfItem() == IShowableItem.BY_DAY_BAR) {
            return BY_DAY_BAR;
        }
        if (transactionsBeingShowed.get(position).getKindOfItem() == IShowableItem.BUTTON) {
            return BUTTON;
        }
        if (transactionsBeingShowed.get(position).getKindOfItem() == IShowableItem.TRANSACTION) {
            return ITEM;
        }
        return TITLE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TITLE) {
            return new DateTitleHolder(activity.getLayoutInflater().inflate(R.layout.card_detail_title_item, parent, false));
        } else if (viewType == ITEM) {
            return new TransactionHolder(activity.getLayoutInflater().inflate(R.layout.item_card_detail_transaction_item, parent, false));
        } else if (viewType == BUTTON) {
            if(owner.getCard().getECard() != null && owner.getCard().getECard()){
                return new PayButtonHolder(activity.getLayoutInflater().inflate(R.layout.card_detail_recharge_card_button_item, parent, false));
            }
            return new PayButtonHolder(activity.getLayoutInflater().inflate(R.layout.card_detail_pay_button_item, parent, false));
        } else if (viewType == BY_DAY_BAR) {
            return new ByDayBarHolder(activity.getLayoutInflater().inflate(R.layout.by_day_bar_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM: {
                Transaction transaction = (Transaction) transactionsBeingShowed.get(position);
                TransactionHolder holder = (TransactionHolder) genericHolder;
                holder.price.setText(CurrencyUtils.getCurrencyForString(transaction.getPrice()));
                holder.name.setText(transaction.getName());
                switch (getProperBackground(position)) {
                    case LONELY_ITEM: {
                        holder.container.setBackgroundResource(R.drawable.white_rounded_corners_background);
                        break;
                    }
                    case MIDDLE_ITEM: {
                        holder.container.setBackgroundColor(ContextCompat.getColor(DaviPayApplication.getInstance(), android.R.color.white));
                        break;
                    }
                    case BOTTOM_ITEM: {
                        holder.container.setBackgroundResource(R.drawable.white_shape_rounded_bottom_corners);
                        break;
                    }
                    case TOP_ITEM: {
                        holder.container.setBackgroundResource(R.drawable.white_shape_rounded_top_corners);
                        break;
                    }
                }
                break;
            }
            case BY_DAY_BAR: {
                if (barOwner == null) {
                    return;
                }
                final ByDayBarHolder holder = (ByDayBarHolder) genericHolder;
                holder.dateFromFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.showDatePicker(activity, Calendar.getInstance(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                holder.dateFromDay.setText(String.valueOf(dayOfMonth));
                                holder.dateFromMonth.setText(String.valueOf(monthOfYear + 1));
                                holder.dateFromYear.setText(String.valueOf(year));
                                holder.dateFromContainer.setVisibility(View.VISIBLE);
                                barOwner.gotDateFrom(DateUtils.formatPickerDate(dayOfMonth, monthOfYear, year));
                                dateFrom = DateUtils.formatPickerDate(dayOfMonth, monthOfYear, year);
                            }
                        }, DialogUtil.SIX_MONTHS_AGO, DialogUtil.TODAY);
                    }
                });
                holder.dateToFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String dateFromFilter = DialogUtil.SIX_MONTHS_AGO;
                        if(dateFrom != null && dateFrom.length() > 0) {
                            dateFromFilter = dateFrom;
                        }
                        DialogUtil.showDatePicker(activity, Calendar.getInstance(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                holder.dateToDay.setText(String.valueOf(dayOfMonth));
                                holder.dateToMonth.setText(String.valueOf(monthOfYear + 1));
                                holder.dateToYear.setText(String.valueOf(year));
                                holder.dateToContainer.setVisibility(View.VISIBLE);
                                barOwner.gotDateTo(DateUtils.formatPickerDate(dayOfMonth, monthOfYear, year));
                            }
                        }, dateFromFilter, DialogUtil.TODAY);
                    }
                });
                holder.filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        barOwner.onFilter();
                    }
                });
            }
            case TITLE: {
                IShowableItem showableItem = transactionsBeingShowed.get(position + 1);
                if (showableItem.getKindOfItem() == IShowableItem.TRANSACTION) {
                    DateTitleHolder holder = (DateTitleHolder) genericHolder;
                    Transaction transaction = (Transaction) showableItem;
                    holder.title.setText(DateUtils.toCamelCase(DateUtils.formatDate(DateUtils.DDMMYY_FORMAT,
                            DateUtils.SPACED_DDMMMMYY_FORMAT, transaction.getDate())));
                }
                break;
            }
            case BUTTON: {
                PayButtonHolder holder = (PayButtonHolder) genericHolder;
                holder.payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AbstractPayActivity.CARD_KEY, owner.getCard());
                        if(owner.getCard().getECard() != null && owner.getCard().getECard()) {
                            intent = new Intent(activity, ECardRechargeActivity.class);
                        } else {
                            intent = new Intent(activity, CardPayDetailActivity.class);
                            bundle.putParcelable(CardPayDetailActivity.TRANSACTION_DETAILS, owner.getTransactionDetails());
                        }
                        activity.startActivityForResult(intent.putExtras(bundle), ECardRechargeActivity.PAY_REQUEST);
                    }
                });
                if(showPayButton) {
                    holder.payButton.setVisibility(View.VISIBLE);
                    holder.payButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(AbstractPayActivity.CARD_KEY, owner.getCard());
                            if(owner.getCard().getECard() != null && owner.getCard().getECard()) {
                                intent = new Intent(activity, ECardRechargeActivity.class);
                            }else {
                                intent = new Intent(activity, CardPayDetailActivity.class);
                                bundle.putParcelable(CardPayDetailActivity.TRANSACTION_DETAILS, owner.getTransactionDetails());
                            }
                            activity.startActivityForResult(intent.putExtras(bundle), CardPayDetailActivity.PAY_REQUEST);
                        }
                    });
                } else {
                    holder.payButton.setVisibility(View.GONE);
                }
                if (!owner.doLoadMore()) {
                    holder.loadMoreButton.setVisibility(View.GONE);
                } else {
                    holder.loadMoreButton.setVisibility(View.VISIBLE);
                    holder.loadMoreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            owner.loadMore();
                        }
                    });
                }
            }
        }
    }

    private int getProperBackground(int position) {
        boolean somethingOnTop = position > 0 && transactionsBeingShowed.get(position - 1).getKindOfItem() == IShowableItem.TRANSACTION;
        boolean somethingOnBottom = false;
        if(transactionsBeingShowed.size() > position + 1) {
            somethingOnBottom = transactionsBeingShowed.get(position + 1).getKindOfItem() == IShowableItem.TRANSACTION;
        }
        if (somethingOnBottom && somethingOnTop) {
            return MIDDLE_ITEM;
        }
        if (somethingOnBottom) {
            return TOP_ITEM;
        }
        if (somethingOnTop) {
            return BOTTOM_ITEM;
        }
        return LONELY_ITEM;
    }

    @Override
    public int getItemCount() {
        return transactionsBeingShowed.size();
    }

    protected List<IShowableItem> getProcessedTransactionList(List<Transaction> transactions) {
        List<IShowableItem> result = new ArrayList<>();
        if (transactions == null || transactions.size() < 1) {
            if (barOwner != null) {
                result.add(new TransactionByDayBar());
            }
            result.add(new TransactionPayButton());
            return result;
        }
        result.addAll(transactions);

        result.add(0, new TransactionTitle());
        for (int i = 2; i < result.size(); i++) {
            Transaction transaction = (Transaction) result.get(i);
            Transaction comparator = (Transaction) result.get(i-1);
            if (!comparator.getDate().equals(transaction.getDate())) {
                result.add(i, new TransactionTitle());
                i++;
            }
        }
        result.add(new TransactionPayButton());
        return result;
    }

    public void justSetList(ArrayList<IShowableItem> transactions) {
        originalTransactions.clear();
        originalTransactions.addAll(transactions);
        transactionsBeingShowed.clear();
        transactionsBeingShowed.addAll(transactions);
        notifyDataSetChanged();
    }

    public ArrayList<IShowableItem> getList() {
        return originalTransactions;
    }

    public void setList(ArrayList<Transaction> transactions) {
        originalTransactions.clear();
        originalTransactions.addAll(getProcessedTransactionList(transactions));
        transactionsBeingShowed.clear();
        transactionsBeingShowed.addAll(originalTransactions);
        notifyDataSetChanged();
    }

    public interface ICardDetailAdapterOwner {
        Card getCard();

        TransactionDetails getTransactionDetails();

        boolean doLoadMore();

        void loadMore();
    }
}
