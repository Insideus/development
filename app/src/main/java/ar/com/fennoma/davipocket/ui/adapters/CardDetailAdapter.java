package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.DavipocketApplication;
import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.CardDetailActivity;
import ar.com.fennoma.davipocket.activities.CardPayDetailActivity;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.Transaction;
import ar.com.fennoma.davipocket.model.TransactionDetails;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;

public class CardDetailAdapter extends RecyclerView.Adapter {

    private final static int TOP_ITEM = 0;
    private final static int MIDDLE_ITEM = 1;
    private final static int BOTTOM_ITEM = 2;
    private final static int LONELY_ITEM = 3;

    private final static int TITLE = 0;
    private final static int ITEM = 1;
    private final static int BUTTON = 2;

    private Activity activity;
    private List<Transaction> originalTransactions;
    private List<Transaction> filteredTransactions;
    private List<Transaction> transactionsBeingShowed;
    private ICardDetailAdapterOwner owner;

    public CardDetailAdapter(Activity activity, ICardDetailAdapterOwner owner) {
        this.activity = activity;
        originalTransactions = new ArrayList<>();
        transactionsBeingShowed = new ArrayList<>();
        this.owner = owner;
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
                Transaction t = originalTransactions.get(i);
                if (t != null && t.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredTransactions.add(t);
                }
            }

            filteredTransactions = getProcessedTransactionList(filteredTransactions);
            transactionsBeingShowed.clear();
            if (filteredTransactions != null) {
                transactionsBeingShowed.addAll(filteredTransactions);
            }
        }
        notifyDataSetChanged();
    }

    public void addToList(List<Transaction> transactions) {
        transactions = getProcessedTransactionList(transactions);
        if (this.originalTransactions.size() > 0) {
            this.originalTransactions.remove(this.originalTransactions.size() - 1);
            if (this.originalTransactions.get(this.originalTransactions.size() - 1).getDate().equals(transactions.get(1).getDate())) {
                transactions.remove(0);
            }
        }
        this.originalTransactions.addAll(transactions);
        this.transactionsBeingShowed.clear();
        this.transactionsBeingShowed.addAll(originalTransactions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return BUTTON;
        }
        Transaction transaction = transactionsBeingShowed.get(position);
        if (transaction == null) {
            return TITLE;
        }
        return ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TITLE) {
            return new DateTitleHolder(activity.getLayoutInflater().inflate(R.layout.card_detail_title_item, parent, false));
        } else if (viewType == ITEM) {
            return new TransactionHolder(activity.getLayoutInflater().inflate(R.layout.item_card_detail_transaction_item, parent, false));
        } else if (viewType == BUTTON) {
            return new PayButtonHolder(activity.getLayoutInflater().inflate(R.layout.card_detail_pay_button_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM: {
                Transaction transaction = transactionsBeingShowed.get(position);
                TransactionHolder holder = (TransactionHolder) genericHolder;
                //holder.daviPoints.setText(String.valueOf(transaction.getDavipoints()));
                holder.price.setText(CurrencyUtils.getCurrencyForString(transaction.getPrice()));
                //holder.productAmount.setText(String.valueOf(transaction.getProductAmount()));
                holder.name.setText(transaction.getName());
                switch (getProperBackground(position)) {
                    case LONELY_ITEM: {
                        holder.container.setBackgroundResource(R.drawable.white_rounded_coreners_background);
                        break;
                    }
                    case MIDDLE_ITEM: {
                        holder.container.setBackgroundColor(ContextCompat.getColor(DavipocketApplication.getInstance(), android.R.color.white));
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
            case TITLE: {
                Transaction transaction = transactionsBeingShowed.get(position + 1);
                if (transaction != null) {
                    DateTitleHolder holder = (DateTitleHolder) genericHolder;
                    holder.title.setText(DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMMMYY_FORMAT, transaction.getDate()).toUpperCase());
                }
                break;
            }
            case BUTTON: {
                PayButtonHolder holder = (PayButtonHolder) genericHolder;
                holder.payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, CardPayDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(CardDetailActivity.CARD_KEY, owner.getCard());
                        bundle.putParcelable(CardPayDetailActivity.TRANSACTION_DETAILS, owner.getTransactionDetails());
                        activity.startActivityForResult(intent.putExtras(bundle), CardPayDetailActivity.PAY_REQUEST);
                    }
                });
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
        boolean somethingOnTop = position > 0 && transactionsBeingShowed.get(position - 1) != null;
        boolean somethingOnBottom = transactionsBeingShowed.get(position + 1) != null;
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

    protected List<Transaction> getProcessedTransactionList(List<Transaction> transactions) {
        if (transactions == null || transactions.size() <= 1) {
            transactions = new ArrayList<>();
            transactions.add(null);
            return transactions;
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
