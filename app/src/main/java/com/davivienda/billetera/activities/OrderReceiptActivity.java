package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.Cart;
import com.davivienda.billetera.model.CartType;
import com.davivienda.billetera.model.ErrorMessages;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.ui.adapters.CategoryItemAdapter;
import com.davivienda.billetera.utils.CurrencyUtils;
import com.davivienda.billetera.utils.DateUtils;
import com.davivienda.billetera.utils.DavipointUtils;
import com.davivienda.billetera.utils.DialogUtil;
import com.davivienda.billetera.utils.ImageUtils;

import java.util.Date;

public class OrderReceiptActivity extends BaseActivity{

    public static final String CART_KEY = "cart_key";
    public static final String FROM_MADE_SHOP = "from_made_shop";
    public static final String FROM_OTT_NOTIFICATION = "from_ott_notification";
    public static final String FROM_OTT_CONFIRMED_NOTIFICATION = "from_ott_confirmed_notification";
    public static final String FROM_ORDER_READY_NOTIFICATION_KEY = "from_order_ready_notification";
    public static final String FROM_OTT_REJECTED_NOTIFICATION_KEY = "from_order_rejected_notification";
    private static final int PAY_REQUEST = 2003;

    private Cart cart;
    private boolean fromMadeShop;
    private boolean fromOttNotification;
    private boolean fromOrderReadyNotification;
    private boolean fromOttConfirmedNotification;
    private boolean fromOttRejectedNotification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bought_items_layout);
        setToolbar(R.id.toolbar, false);
        handleIntent();
        if(fromMadeShop || fromOrderReadyNotification){
            setToolbar(R.id.toolbar, true, getString(R.string.store_made_receipt_title));
        } else if(fromOttNotification) {
            setToolbarWOHomeButton(R.id.toolbar, getString(R.string.store_receipt_title));
        } else if(fromOttRejectedNotification) {
            setToolbarWOHomeButton(R.id.toolbar, getString(R.string.store_ott_rejected_title));
        } else {
            setToolbarWOHomeButton(R.id.toolbar, getString(R.string.store_ott_confirmed_title));
        }
        hideNeededLabel();
        setLayoutData();
        setRecycler();
        //setRatingBar();
        //setCommentSection();
        setButtons();
        scrollUp();
    }

    private void handleIntent() {
        if(getIntent() == null || getIntent().getParcelableExtra(CART_KEY) == null){
            return;
        }
        fromMadeShop = getIntent().getBooleanExtra(FROM_MADE_SHOP, false);
        fromOttNotification = getIntent().getBooleanExtra(FROM_OTT_NOTIFICATION, false);
        fromOrderReadyNotification = getIntent().getBooleanExtra(FROM_ORDER_READY_NOTIFICATION_KEY, false);
        fromOttConfirmedNotification = getIntent().getBooleanExtra(FROM_OTT_CONFIRMED_NOTIFICATION, false);
        fromOttRejectedNotification = getIntent().getBooleanExtra(FROM_OTT_REJECTED_NOTIFICATION_KEY, false);
        cart = getIntent().getParcelableExtra(CART_KEY);
        if(cart == null) {
            cart = new Cart();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(CART_KEY, cart);
        outState.putBoolean(FROM_MADE_SHOP, fromMadeShop);
        outState.putBoolean(FROM_OTT_NOTIFICATION, fromOttNotification);
        outState.putBoolean(FROM_ORDER_READY_NOTIFICATION_KEY, fromOrderReadyNotification);
        outState.putBoolean(FROM_OTT_CONFIRMED_NOTIFICATION, fromOttConfirmedNotification);
        outState.putBoolean(FROM_OTT_REJECTED_NOTIFICATION_KEY, fromOttRejectedNotification);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CART_KEY, cart);
        outState.putBoolean(FROM_MADE_SHOP, fromMadeShop);
        outState.putBoolean(FROM_OTT_NOTIFICATION, fromOttNotification);
        outState.putBoolean(FROM_ORDER_READY_NOTIFICATION_KEY, fromOrderReadyNotification);
        outState.putBoolean(FROM_OTT_CONFIRMED_NOTIFICATION, fromOttConfirmedNotification);
        outState.putBoolean(FROM_OTT_REJECTED_NOTIFICATION_KEY, fromOttRejectedNotification);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cart = savedInstanceState.getParcelable(CART_KEY);
        fromMadeShop = savedInstanceState.getBoolean(FROM_MADE_SHOP, false);
        fromOttNotification = savedInstanceState.getBoolean(FROM_OTT_NOTIFICATION, false);
        fromOrderReadyNotification = savedInstanceState.getBoolean(FROM_ORDER_READY_NOTIFICATION_KEY, false);
        fromOttConfirmedNotification = savedInstanceState.getBoolean(FROM_OTT_CONFIRMED_NOTIFICATION, false);
        fromOttRejectedNotification = savedInstanceState.getBoolean(FROM_OTT_REJECTED_NOTIFICATION_KEY, false);
    }

    @Override
    public void onBackPressed() {
        if(fromMadeShop){
            super.onBackPressed();
            return;
        }
        finish();
        goToHome();
    }

    private void hideNeededLabel() {
        findViewById(R.id.thanks_for_buying).setVisibility(View.GONE);
        if(CartType.getType(cart.getCartType()) ==  CartType.OTT) {
            findViewById(R.id.products_container).setVisibility(View.GONE);
            findViewById(R.id.older_purchase_button_container).setVisibility(View.GONE);
            if(fromOttNotification) {
                findViewById(R.id.pay_ott_button_container).setVisibility(View.VISIBLE);
            }
        }
    }

    /*private void setCommentSection() {
        if(fromMadeShop){
            if(TextUtils.isEmpty(cart.getComment())) {
                findViewById(R.id.comment_container).setVisibility(View.GONE);
                return;
            }
            findViewById(R.id.comment_input).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.comment_output)).setText(cart.getComment());
            return;
        }
        findViewById(R.id.comment_output).setVisibility(View.GONE);
    }*/

    private void setLayoutData() {
        ImageView logo = (ImageView) findViewById(R.id.brand_logo);
        if(cart.getStore().getLogo() != null && cart.getStore().getLogo().length() > 0) {
            ImageUtils.loadImageFullURL(logo, cart.getStore().getLogo());
        } else {
            logo.setImageResource(R.drawable.placeholder_small);
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
        buyDate.setText(DateUtils.toCamelCase(DateUtils.getBuyDateFormated(new Date())));

        ImageView cardLogo = (ImageView) findViewById(R.id.card_logo);
        TextView fourDigits = (TextView) findViewById(R.id.last_card_digits);
        ImageUtils.loadCardImage(this, cardLogo, cart.getSelectedCard().getBin().getLogo());
        fourDigits.setText(cart.getSelectedCard().getLastDigits());

        TextView receiptNumber = (TextView) findViewById(R.id.receipt_number);
        if(CartType.getType(cart.getCartType()) ==  CartType.OTT) {
            receiptNumber.setText(cart.getOttTransaccionId());
        } else {
            receiptNumber.setText(cart.getReceiptNumber());
        }
        findViewById(R.id.pay_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaymentDetails();
            }
        });

    }

    private void showPaymentDetails() {
        Intent intent = new Intent(this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.ott_payment_confirmation_title));
        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.ott_payment_confirmation_subtitle));
        intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.ott_payment_confirmation_text, cart.getSelectedCard().getLastDigits(), CurrencyUtils.getCurrencyForString(cart.getCartPrice())));
        intent.putExtra(ActionDialogActivity.IS_CARD_PAY, true);
        startActivityForResult(intent, PAY_REQUEST);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    private void setButtons() {
        if(fromMadeShop || fromOttNotification){
            findViewById(R.id.new_buy_buttons).setVisibility(View.GONE);
            findViewById(R.id.repeat_purchase_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            return;
        }
        findViewById(R.id.older_purchase_button_container).setVisibility(View.GONE);
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

   /*private void setRatingBar() {
       RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
       ratingBar.setActivity(this).setAmountOfStars(5);
       if(cart.getStarsGiven() != null){
           ratingBar.setSelectedTill(cart.getStarsGiven());
       }else{
           ratingBar.setAsClickable().setSelectedTill(1);
       }
    }*/

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(new CategoryItemAdapter(this, cart.getProducts(), false, cart.getStore()));
    }

    private class PayOttTask extends DaviPayTask<Boolean> {

        public PayOttTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = null;
            try {
                response = Service.acceptOttPay(Session.getCurrentSession(getApplicationContext()).getSid(), cart.getReceiptNumber());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                findViewById(R.id.pay_ott_button_container).setVisibility(View.GONE);
                DialogUtil.toast(OrderReceiptActivity.this,
                        getString(R.string.ott_payment_title),
                        getString(R.string.ott_payment_subtitle),
                        getString(R.string.ott_payment_text));
            }
        }

    }

    public void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if(error != null) {
            switch(error) {
                case OTT_EXPIRED:
                    showOttExpiredMessage();
                    break;
                default:
                    super.processErrorAndContinue(error, additionalParam);
            }
        } else {
            showServiceGenericError();
        }
    }

    private void showOttExpiredMessage() {
        DialogUtil.toast(this,
                getString(R.string.otp_payment_expired_title),
                getString(R.string.otp_payment_expired_subtitle),
                getString(R.string.otp_payment_expired_text));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_REQUEST && resultCode == RESULT_OK) {
            new PayOttTask(this).execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
    }

}
