package ar.com.fennoma.davipocket.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
    private List<Transaction> transactions;
    private ICardDetailAdapterOwner owner;

    public interface ICardDetailAdapterOwner{
        Card getCard();
        TransactionDetails getTransactionDetails();
        boolean doLoadMore();
        void loadMore();
    }

    public CardDetailAdapter(Activity activity, ICardDetailAdapterOwner owner) {
        this.activity = activity;
        transactions = new ArrayList<>();
        this.owner = owner;
    }

    public void setList(List<Transaction> transactions) {
        if (this.transactions.size() > 0) {
            this.transactions.remove(this.transactions.size() - 1);
            if (this.transactions.get(this.transactions.size() - 1).getDate().equals(transactions.get(1).getDate())) {
                transactions.remove(0);
            }
        }
        this.transactions.addAll(transactions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return BUTTON;
        }
        Transaction transaction = transactions.get(position);
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
                Transaction transaction = transactions.get(position);
                TransactionHolder holder = (TransactionHolder) genericHolder;
                //holder.daviPoints.setText(String.valueOf(transaction.getDavipoints()));
                holder.price.setText(String.valueOf(transaction.getPrice()));
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
                Transaction transaction = transactions.get(position + 1);
                if (transaction != null) {
                    DateTitleHolder holder = (DateTitleHolder) genericHolder;
                    holder.title.setText(DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMMYY_FORMAT, transaction.getDate()));
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
        boolean somethingOnTop = position > 0 && transactions.get(position - 1) != null;
        boolean somethingOnBottom = transactions.get(position + 1) != null;
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
        return transactions.size();
    }
}
