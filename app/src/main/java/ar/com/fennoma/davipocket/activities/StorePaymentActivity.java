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

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.PreCheckout;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.CategoryItemAdapter;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DavipointUtils;
import ar.com.fennoma.davipocket.utils.ImageUtils;
import ar.com.fennoma.davipocket.utils.LocationUtils;

public class StorePaymentActivity extends BaseActivity {

    private List<StoreProduct> selectedProducts;
    public static final String CART_KEY = "cart_key";
    public static final String PRE_CHECKOUT_DATA_KEY = "pre_checkout_data_key";

    private Cart cart;
    private PreCheckout preCheckoutData;

    private int tipIndex = 0;
    private int monthlyFeeIndex;
    private ImageView cardLogo;
    private TextView fourDigits;
    private TextView monthlyFee;
    private View monthlyFeeDisabler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_payment_activity);
        handleIntent();
        setToolbar(R.id.toolbar, true, getString(R.string.main_activity_title));
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
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
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
        setStoreLayout();
        setRecycler();
        setSeekBar();
        setPriceLayout();
        setMonthlyFleeLayouts();
        findCardLayouts();
        setPayButton();
        scrollUp();
    }

    private void setSeekBar() {
        DiscreteSeekBar seekBar = (DiscreteSeekBar) findViewById(R.id.davipoints_cash_seekbar);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if(fromUser) {
                    updatePriceAndDavipoints(value);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        Double cartPrice = cart.getCartPrice();
        if(cartPrice != null) {
            Integer currentDaviPointAmount = DavipointUtils.toDavipoints(cartPrice.intValue());
            seekBar.setMin(0);
            seekBar.setMax(currentDaviPointAmount);
            seekBar.setProgress(cart.getCartDavipoints());
            updatePriceAndDavipoints(cart.getCartDavipoints());
        } else {
            seekBar.setEnabled(false);
        }
    }

    private void setPriceLayout() {
        TextView cartPrice = (TextView) findViewById(R.id.cart_price);
        if(cart.getCartPrice() != null) {
            cartPrice.setText("$".concat(CurrencyUtils.getCurrencyForString(String.valueOf(cart.getCartPrice()))));
        }
    }

    private void updatePriceAndDavipoints(int davipointsQuantitySelected) {
        cart.setCartDavipoints(davipointsQuantitySelected);
        int davipointsEquivalence = DavipointUtils.getDavipointsEquivalence();
        Integer davipointCashAmount = davipointsQuantitySelected * davipointsEquivalence;
        Double cashAmount = cart.getCartPrice() - davipointCashAmount;

        TextView cashAmountTv = (TextView) findViewById(R.id.cash_amount);
        cashAmountTv.setText(CurrencyUtils.getCurrencyForString(String.valueOf(cashAmount)));

        TextView davipointsAmountTv = (TextView) findViewById(R.id.davi_points_cart);
        davipointsAmountTv.setText(String.valueOf(davipointsQuantitySelected));
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
                Intent intent = new Intent(StorePaymentActivity.this, StoreReceiptActivity.class);
                intent.putExtra(StoreReceiptActivity.CART_KEY, cart);
                startActivity(intent);
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
        if(preCheckoutData.getCards() != null && preCheckoutData.getCards().size() > 0) {
            setSelectedCardData();
            cardContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCombo();
                }
            });
        }
    }

    /*
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
    */

    private void setMonthlyFleeLayouts(){
        monthlyFeeDisabler = findViewById(R.id.monthly_fee_disabler);
        monthlyFee = (TextView) findViewById(R.id.monthly_fee);
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
                cart.setSelectedInstallment(preCheckoutData.getInstallments().get(monthlyFeeIndex));
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
                cart.setSelectedInstallment(preCheckoutData.getInstallments().get(monthlyFeeIndex));
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

        ComboAdapter(List<Card> cards, Card selectedCard) {
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
        final ComboAdapter adapter = new ComboAdapter(preCheckoutData.getCards(), cart.getSelectedCard());
        showCombo(adapter, new IComboListener() {
            @Override
            public void onAccept() {
                cart.setSelectedCard(adapter.selectedCard);
                setSelectedCardData();
                dialogPlus.dismiss();
            }

            @Override
            public void setSelectedItem() {
                setSelectedCardData();
            }
        });
    }

    //La tarjeta que seleccionás es un eCard, te deshabilite las cuotas

    private void setSelectedCardData() {
        if(cart.getSelectedCard() == null) {
            cart.setSelectedCard(preCheckoutData.getCards().get(0));
        }
        ImageUtils.loadCardImage(this, cardLogo, cart.getSelectedCard().getBin().getImage());
        fourDigits.setText(cart.getSelectedCard().getLastDigits());
        if(cart.getSelectedCard().getECard()){
            monthlyFeeDisabler.setVisibility(View.VISIBLE);
            monthlyFeeIndex = 0;
            Integer integer = preCheckoutData.getInstallments().get(monthlyFeeIndex);
            monthlyFee.setText(String.valueOf(integer));
            cart.setSelectedInstallment(integer);
        } else {
            monthlyFeeDisabler.setVisibility(View.GONE);
        }
    }

    private class GetPreCheckoutData extends AsyncTask<Void, Void, PreCheckout> {

        private String id;
        String errorCode;

        GetPreCheckoutData(long id) {
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
                errorCode = e.getErrorCode();
            }
            return data;
        }

        @Override
        protected void onPostExecute(PreCheckout response) {
            super.onPostExecute(response);
            hideLoading();
            if(response == null){
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                preCheckoutData = response;
                setLayouts();
            }
        }

    }
}
