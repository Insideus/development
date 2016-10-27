package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.CardBin;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.CategoryItemAdapter;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class StorePaymentActivity extends BaseActivity {

    private List<StoreProduct> selectedProducts;
    private List<String> tips;
    private int tipIndex = 0;
    private int monthlyFeeIndex;
    private List<Integer> monthlyFees;
    private List<Card> cards;
    private Card selectedCard;
    private ImageView cardLogo;
    private TextView fourDigits;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_payment_activity);
        hardcodeData();
        setLayouts();
    }

    private void setLayouts() {
        setRecycler();
        setTipLayouts();
        setMonthlyFleeLayouts();
        findCardLayouts();
    }

    private void findCardLayouts() {
        cardLogo = (ImageView) findViewById(R.id.card_logo);
        fourDigits = (TextView) findViewById(R.id.four_digits);
        View cardContainer = findViewById(R.id.card_container);
        setSelectedCardData();
        cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCombo();
            }
        });
    }

    private void setTipLayouts(){
        final TextView tip = (TextView) findViewById(R.id.tip);
        final View minusTip = findViewById(R.id.minus_tip);
        final View plusTip = findViewById(R.id.plus_tip);
        if(tips == null || tips.isEmpty()){
            return;
        }
        minusTip.setEnabled(false);
        tip.setText(tips.get(tipIndex));
        minusTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipIndex--;
                tip.setText(tips.get(tipIndex));
                if(tipIndex == 0){
                    minusTip.setEnabled(false);
                }
                if(tipIndex + 1 < tips.size()){
                    plusTip.setEnabled(true);
                }
            }
        });
        plusTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipIndex++;
                tip.setText(tips.get(tipIndex));
                if(tipIndex > 0){
                    minusTip.setEnabled(true);
                }
                if(tipIndex + 1 == tips.size()){
                    plusTip.setEnabled(false);
                }
            }
        });
    }

    private void setMonthlyFleeLayouts(){
        final TextView monthlyFee = (TextView) findViewById(R.id.monthly_fee);
        final View plusMonthlyFee = findViewById(R.id.plus_monthly_fee);
        final View minusMonthlyFee = findViewById(R.id.minus_monthly_fee);
        if(monthlyFees == null || monthlyFees.isEmpty()){
            return;
        }
        monthlyFee.setText(String.valueOf(monthlyFees.get(monthlyFeeIndex)));
        plusMonthlyFee.setEnabled(false);
        plusMonthlyFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthlyFeeIndex++;
                monthlyFee.setText(String.valueOf(monthlyFees.get(monthlyFeeIndex)));
                if(monthlyFeeIndex + 1 == monthlyFees.size()){
                    plusMonthlyFee.setEnabled(false);
                }
                if(monthlyFeeIndex > 0){
                    minusMonthlyFee.setEnabled(true);
                }
            }
        });
        minusMonthlyFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthlyFeeIndex--;
                monthlyFee.setText(String.valueOf(monthlyFees.get(monthlyFeeIndex)));
                if(monthlyFeeIndex == 0){
                    minusMonthlyFee.setEnabled(false);
                }
                if(monthlyFeeIndex + 1 < monthlyFees.size()){
                    plusMonthlyFee.setEnabled(true);
                }
            }
        });
    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if(recyclerView == null){
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new CategoryItemAdapter(this, selectedProducts, false));
    }

    private void hardcodeData() {
        selectedProducts = new ArrayList<>();
        StoreProduct product;
        for(int i = 0; i < 3; i ++){
            product = new StoreProduct();
            product.setName("Coffee cup");
            product.setListPrice(6300d);
            product.setImage("https://middleware-paymentez.s3.amazonaws.com/fca455ef6b32ad512033367e0d52e951.jpeg");
            selectedProducts.add(product);
        }
        tips = new ArrayList<>(Arrays.asList("0%", "10%", "20%", "30%", "40%", "50%"));
        monthlyFees = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            monthlyFees.add(i + 1);
        }
        monthlyFeeIndex = monthlyFees.size() - 1;

        cards = new ArrayList<>();
        Card card = new Card();
        card.setLastDigits("1234");
        CardBin cardBin = new CardBin();
        cardBin.setImage("/uploads/images/master_clasica.png");
        card.setBin(cardBin);
        cards.add(card);
        card = new Card();
        card.setLastDigits("4321");
        cardBin = new CardBin();
        cardBin.setImage("/uploads/images/default.png");
        card.setBin(cardBin);
        cards.add(card);
        card = new Card();
        card.setLastDigits("1221");
        cardBin = new CardBin();
        cardBin.setImage("/uploads/images/diners_azul.png");
        card.setBin(cardBin);
        cards.add(card);
        selectedCard = cards.get(0);
    }

    private class ComboAdapter extends BaseAdapter {

        private List<Card> cards;

        private Card selectedCard;

        public ComboAdapter(List<Card> cards, Card selectedCard) {
            this.cards = cards;
            this.selectedCard = selectedCard;
        }

        @Override
        public int getCount() {
            return cards.size();
        }

        @Override
        public Card getItem(int position) {
            return cards.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.valueOf(cards.get(position).getLastDigits());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            ViewGroup row;
            if (convertView == null) {
                row = (ViewGroup) inflater.inflate(R.layout.combo_item_with_images, parent, false);
            } else {
                row = (ViewGroup) convertView;
            }
            final Card card = getItem(position);
            TextView cardNumber = (TextView) row.findViewById(R.id.four_digits);
            ImageView cardLogo = (ImageView) row.findViewById(R.id.card_logo);
            ImageUtils.loadCardImage(StorePaymentActivity.this, cardLogo, card.getBin().getImage());
            cardNumber.setText(card.getLastDigits());
            if (selectedCard != null && selectedCard.getLastDigits().equals(card.getLastDigits())) {
                cardNumber.setTextColor(ContextCompat.getColor(StorePaymentActivity.this, R.color.combo_item_text_color_selected));
            } else {
                cardNumber.setTextColor(ContextCompat.getColor(StorePaymentActivity.this, R.color.combo_item_text_color));
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCard = card;
                    notifyDataSetChanged();
                }
            });
            return row;
        }

    }

    public void showCombo(){
        final ComboAdapter adapter = new ComboAdapter(cards, selectedCard);
        showCombo(adapter, new IComboListener() {
            @Override
            public void onAccept() {
                selectedCard = adapter.selectedCard;
                setSelectedCardData();
                dialogPlus.dismiss();
            }

            @Override
            public void setSelectedItem() {
                setSelectedCardData();
            }
        });
    }

    private void setSelectedCardData() {
        ImageUtils.loadCardImage(this, cardLogo, selectedCard.getBin().getImage());
        fourDigits.setText(selectedCard.getLastDigits());
    }
}
