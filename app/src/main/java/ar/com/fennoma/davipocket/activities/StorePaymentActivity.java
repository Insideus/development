package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.PreCheckout;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.CategoryItemAdapter;
import ar.com.fennoma.davipocket.utils.ImageUtils;
import ar.com.fennoma.davipocket.utils.LocationUtils;

public class StorePaymentActivity extends BaseActivity {

    private static final int CANCEL_PURCHASE_REQUEST = 10;
    private List<StoreProduct> selectedProducts;
    public static final String CART_KEY = "cart_key";
    public static final String PRE_CHECKOUT_DATA_KEY = "pre_checkout_data_key";

    private Cart cart;
    private PreCheckout preCheckoutData;

    private int tipIndex = 0;
    private int monthlyFeeIndex;
    private Card selectedCard;
    private ImageView cardLogo;
    private TextView fourDigits;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_payment_activity);
        handleIntent();
        if(cart != null && cart.getStore() != null) {
            new GetPreCheckoutData(cart.getStore().getId()).execute();
        }
    }

    private void handleIntent() {
        if(getIntent() == null || getIntent().getParcelableExtra(CART_KEY) == null){
            return;
        }
        cart = getIntent().getParcelableExtra(CART_KEY);
        if(cart == null) {
            cart = new Cart();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(CART_KEY, cart);
        outState.putParcelable(PRE_CHECKOUT_DATA_KEY, preCheckoutData);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cart = savedInstanceState.getParcelable(CART_KEY);
        preCheckoutData = savedInstanceState.getParcelable(PRE_CHECKOUT_DATA_KEY);
    }

    private void setLayouts() {
        setToolbar(R.id.toolbar, true);
        setTitle(getString(R.string.app_name));
        setStoreLayout();
        setRecycler();
        //setTipLayouts();
        setMonthlyFleeLayouts();
        findCardLayouts();
        setPayButton();
        scrollUp();
    }

    private void setStoreLayout() {
        ImageView storeImage = (ImageView) findViewById(R.id.image);
        if(cart.getStore().getImage() != null && cart.getStore().getImage().length() > 0) {
            ImageUtils.loadImageFullURL(storeImage, cart.getStore().getImage());
        }
        ImageView storeLogo = (ImageView) findViewById(R.id.brand_logo);
        if(cart.getStore().getLogo() != null && cart.getStore().getLogo().length() > 0) {
            ImageUtils.loadImageFullURL(storeLogo, cart.getStore().getLogo());
        }
        TextView storeName = (TextView) findViewById(R.id.name);
        storeName.setText(cart.getStore().getName());
        TextView storeAddress = (TextView) findViewById(R.id.address);
        storeName.setText(cart.getStore().getName());
        if(TextUtils.isEmpty(cart.getStore().getAddress())) {
            storeAddress.setVisibility(View.GONE);
        } else {
            storeAddress.setVisibility(View.VISIBLE);
            storeAddress.setText(cart.getStore().getAddress());
        }
        TextView storeDistance = (TextView) findViewById(R.id.distance);
        storeDistance.setText(LocationUtils.calculateDistance(this, LocationUtils.getLastKnowLocation(this),
                cart.getStore().getLatitude(), cart.getStore().getLongitude()));
    }

    private void setPayButton() {
        View payButton = findViewById(R.id.pay_button);
        if(payButton == null){
            return;
        }
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StorePaymentActivity.this, StoreReceiptActivity.class));
            }
        });
    }

    private void scrollUp() {
        ScrollView scroll = (ScrollView) findViewById(R.id.scroll_view);
        scroll.fullScroll(ScrollView.FOCUS_UP);
        scroll.smoothScrollTo(0, 0);
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

    /*private void setTipLayouts(){
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
    }*/

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ActionDialogActivity.CANCEL_PURCHASE, true);
        startActivityForResult(new Intent(this, ActionDialogActivity.class).putExtras(bundle), CANCEL_PURCHASE_REQUEST);
    }

    private void setMonthlyFleeLayouts(){
        final TextView monthlyFee = (TextView) findViewById(R.id.monthly_fee);
        final View plusMonthlyFee = findViewById(R.id.plus_monthly_fee);
        final View minusMonthlyFee = findViewById(R.id.minus_monthly_fee);
        if(preCheckoutData.getInstallments() == null || preCheckoutData.getInstallments().isEmpty()){
            return;
        }
        monthlyFee.setText(String.valueOf(preCheckoutData.getInstallments().get(monthlyFeeIndex)));
        minusMonthlyFee.setEnabled(false);
        plusMonthlyFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthlyFeeIndex++;
                monthlyFee.setText(String.valueOf(preCheckoutData.getInstallments().get(monthlyFeeIndex)));
                if(monthlyFeeIndex + 1 == preCheckoutData.getInstallments().size()){
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
                monthlyFee.setText(String.valueOf(preCheckoutData.getInstallments().get(monthlyFeeIndex)));
                if(monthlyFeeIndex == 0){
                    minusMonthlyFee.setEnabled(false);
                }
                if(monthlyFeeIndex + 1 < preCheckoutData.getInstallments().size()){
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
        recyclerView.setAdapter(new CategoryItemAdapter(this, cart.getProducts(), false));
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
        final ComboAdapter adapter = new ComboAdapter(preCheckoutData.getCards(), selectedCard);
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
        if(selectedCard == null) {
            selectedCard = preCheckoutData.getCards().get(0);
        }
        ImageUtils.loadCardImage(this, cardLogo, selectedCard.getBin().getImage());
        fourDigits.setText(selectedCard.getLastDigits());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CANCEL_PURCHASE_REQUEST && resultCode == RESULT_OK){
            finish();
        }
    }

    private class GetPreCheckoutData extends AsyncTask<Void, Void, PreCheckout> {

        private String id;

        public GetPreCheckoutData(long id) {
            this.id = String.valueOf(id);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected PreCheckout doInBackground(Void... params) {
            PreCheckout data = null;
            try {
                data = Service.preCheckout(Session.getCurrentSession(getApplicationContext()).getSid(), id);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(PreCheckout response) {
            super.onPostExecute(response);
            hideLoading();
            if(response == null){
                showServiceGenericError();
                return;
            }
            preCheckoutData = response;
            setLayouts();
        }

    }
}
