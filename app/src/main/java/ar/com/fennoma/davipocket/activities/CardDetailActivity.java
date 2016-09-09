package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.DavipocketApplication;
import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.Transaction;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.utils.DateUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class CardDetailActivity extends BaseActivity {

    public static String CARD_KEY = "card_key";

    private CardDetailAdapter adapter;
    private Card card;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
        }
        setToolbar(R.id.toolbar_layout, true, getString(R.string.mocked_master_card_title));
        setRecycler();
        setSearcher();
        getDataToShow();
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

    private void getDataToShow() {
        adapter.setList(addManagableData(Service.getMockedTransactions()));
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
        adapter = new CardDetailAdapter();
        recyclerView.setAdapter(adapter);
    }

    private class CardDetailAdapter extends RecyclerView.Adapter {

        private final static int TOP_ITEM = 0;
        private final static int MIDDLE_ITEM = 1;
        private final static int BOTTOM_ITEM = 2;
        private final static int LONELY_ITEM = 3;

        private final static int TITLE = 0;
        private final static int ITEM = 1;
        private final static int BUTTON = 2;

        private List<Transaction> transactions;

        public CardDetailAdapter() {
            transactions = new ArrayList<>();
        }

        public void setList(List<Transaction> transactions) {
            this.transactions = transactions;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(position == getItemCount() - 1){
                return BUTTON;
            }
            Transaction transaction = transactions.get(position);
            if(transaction == null){
                return TITLE;
            }
            return ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TITLE){
                return new DateTitleHolder(getLayoutInflater().inflate(R.layout.card_detail_title_item, parent, false));
            } else if(viewType == ITEM){
                return new TransactionHolder(getLayoutInflater().inflate(R.layout.item_card_detail_transaction_item, parent, false));
            } else if(viewType == BUTTON){
                return new PayButtonHolder(getLayoutInflater().inflate(R.layout.card_detail_pay_button_item, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
            switch (getItemViewType(position)){
                case ITEM : {
                    Transaction transaction = transactions.get(position);
                    TransactionHolder holder = (TransactionHolder) genericHolder;
                    holder.daviPoints.setText(String.valueOf(transaction.getDavipoints()));
                    holder.price.setText(String.valueOf(transaction.getPrice()));
                    holder.productAmount.setText(String.valueOf(transaction.getProductAmount()));
                    holder.name.setText(transaction.getName());
                    switch (getProperBackground(position)){
                        case LONELY_ITEM :{
                            holder.container.setBackgroundResource(R.drawable.white_rounded_coreners_background);
                            break;
                        }
                        case MIDDLE_ITEM :{
                            holder.container.setBackgroundColor(ContextCompat.getColor(DavipocketApplication.getInstance(), android.R.color.white));
                            break;
                        }
                        case BOTTOM_ITEM : {
                            holder.container.setBackgroundResource(R.drawable.white_shape_rounded_bottom_corners);
                            break;
                        }
                        case TOP_ITEM : {
                            holder.container.setBackgroundResource(R.drawable.white_shape_rounded_top_corners);
                            break;
                        }
                    }
                    break;
                }
                case TITLE : {
                    Transaction transaction = transactions.get(position + 1);
                    DateTitleHolder holder = (DateTitleHolder) genericHolder;
                    holder.title.setText(DateUtils.formatDate(DateUtils.DDMMYY_FORMAT, DateUtils.DOTTED_DDMMMYY_FORMAT, transaction.getDate()));
                    break;
                }
                case BUTTON : {
                    PayButtonHolder holder = (PayButtonHolder) genericHolder;
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(CardDetailActivity.this, CardPayDetailActivity.class));
                        }
                    });
                }
            }
        }

        private int getProperBackground(int position) {
            boolean somethingOnTop = position > 0 && transactions.get(position - 1) != null;
            boolean somethingOnBottom = transactions.get(position + 1) != null;
            if(somethingOnBottom && somethingOnTop){
                return MIDDLE_ITEM;
            }
            if(somethingOnBottom){
                return TOP_ITEM;
            }
            if(somethingOnTop){
                return BOTTOM_ITEM;
            }
            return LONELY_ITEM;
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }
    }

    private class TransactionHolder extends RecyclerView.ViewHolder{

        protected View container;
        protected TextView name;
        protected TextView productAmount;
        protected TextView price;
        protected TextView daviPoints;

        public TransactionHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            name = (TextView) itemView.findViewById(R.id.name);
            productAmount = (TextView) itemView.findViewById(R.id.product_amount);
            price = (TextView) itemView.findViewById(R.id.price);
            daviPoints = (TextView) itemView.findViewById(R.id.davipoints);
        }
    }

    private class DateTitleHolder extends RecyclerView.ViewHolder{

        protected TextView title;
        public DateTitleHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    private class PayButtonHolder extends RecyclerView.ViewHolder{

        protected View button;
        public PayButtonHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.pay_button);
        }
    }

}
