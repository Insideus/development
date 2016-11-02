package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.ui.adapters.CategoryItemAdapter;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DateUtils;
import ar.com.fennoma.davipocket.utils.DavipointUtils;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class StoreReceiptActivity extends BaseActivity{

    public static final String CART_KEY = "cart_key";

    private Cart cart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bought_items_layout);
        setToolbar(R.id.toolbar, false);
        setToolbarWOHomeButton(R.id.toolbar, getString(R.string.store_receipt_title));
        handleIntent();
        setLayoutData();
        setRecycler();
        //setRaitingBar();
        setButtons();
        scrollUp();
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
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cart = savedInstanceState.getParcelable(CART_KEY);
    }

    private void setLayoutData() {
        ImageView logo = (ImageView) findViewById(R.id.brand_logo);
        if(cart.getStore().getLogo() != null && cart.getStore().getLogo().length() > 0) {
            ImageUtils.loadImageFullURL(logo, cart.getStore().getLogo());
        }
        TextView daviPrice = (TextView) findViewById(R.id.davi_price);
        daviPrice.setText(String.valueOf(cart.getCartDavipoints()));

        Double cashAmount = DavipointUtils.cashDifference(cart.getCartPrice(), cart.getCartDavipoints());
        TextView cashPrice = (TextView) findViewById(R.id.cash_price);
        cashPrice.setText("$".concat(CurrencyUtils.getCurrencyForString(cashAmount)));

        TextView totalPrice = (TextView) findViewById(R.id.total_price);
        totalPrice.setText("$".concat(CurrencyUtils.getCurrencyForString(cart.getCartPrice())));

        TextView storeName = (TextView) findViewById(R.id.brand_name);
        storeName.setText(cart.getStore().getName());

        TextView buyDate = (TextView) findViewById(R.id.buy_date);
        buyDate.setText(DateUtils.getBuyDateFormated(new Date()));

        ImageView cardLogo = (ImageView) findViewById(R.id.card_logo);
        TextView fourDigits = (TextView) findViewById(R.id.last_card_digits);
        ImageUtils.loadCardImage(this, cardLogo, cart.getSelectedCard().getBin().getImage());
        fourDigits.setText(cart.getSelectedCard().getLastDigits());

    }

    private void setButtons() {
        View goHomeButton = findViewById(R.id.go_home_button);
        View repeatOrderButton = findViewById(R.id.repeat_order_button);
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });
        repeatOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void scrollUp() {
        ScrollView scroll = (ScrollView) findViewById(R.id.scroll_view);
        scroll.fullScroll(ScrollView.FOCUS_UP);
        scroll.smoothScrollTo(0, 0);
    }

   /*private void setRaitingBar() {
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        ratingBar.setActivity(this).setAmountOfStars(5).setAsClickable().setSelectedTill(1);
    }*/

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(new CategoryItemAdapter(this, cart.getProducts(), false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
    }

    @Override
    public void onBackPressed() {
        finish();
        goToHome();
    }
}
