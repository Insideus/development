package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liulishuo.magicprogresswidget.MagicProgressBar;

import java.util.ArrayList;
import java.util.List;

import com.davivienda.billetera.R;
import com.davivienda.billetera.model.Card;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.utils.DialogUtil;
import com.davivienda.billetera.utils.ImageUtils;

public class OtpPaymentActivity extends BaseActivity {

    public static final String CARDS_KEY = "cards_key";
    public static final String SELECTED_CARD_KEY = "selected_card_key";
    private static int TIME_TO_USE = 240000;

    private ArrayList<Card> cards = new ArrayList<>();
    private Card selectedCard;

    private ImageView cardLogo;
    private TextView fourDigits;
    private MyCountDownTimer myCountDownTimer;
    private MagicProgressBar progressBar;
    private boolean showExpiredMessage = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_payment_activity);
        setToolbar(R.id.toolbar, true, getString(R.string.main_activity_title));
        new GetCardsToPay(this).execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelableArrayList(CARDS_KEY, cards);
        outState.putParcelable(SELECTED_CARD_KEY, selectedCard);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(CARDS_KEY, cards);
        outState.putParcelable(SELECTED_CARD_KEY, selectedCard);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cards = savedInstanceState.getParcelableArrayList(CARDS_KEY);
        selectedCard = savedInstanceState.getParcelable(SELECTED_CARD_KEY);
    }

    private void setLayouts() {
        progressBar = (MagicProgressBar) findViewById(R.id.progress_bar);
        findCardLayouts();
        setPayButton();
    }

    private void setPayButton() {
        View getOtpButton = findViewById(R.id.get_otp_button);
        if(getOtpButton == null){
            return;
        }
        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    new GetOttTokenTask(OtpPaymentActivity.this).execute();
                } else {
                    DialogUtil.toast(OtpPaymentActivity.this,
                            getString(R.string.selected_card_error_title),
                            "",
                            getString(R.string.selected_card_error_text));
                }
            }
        });
    }

    private boolean validate() {
        if(selectedCard != null) {
            return true;
        }
        return false;
    }

    private void findCardLayouts() {
        cardLogo = (ImageView) findViewById(R.id.card_logo);
        fourDigits = (TextView) findViewById(R.id.four_digits);
        View cardContainer = findViewById(R.id.card_container);
        if(cards != null && cards.size() > 0) {
            setSelectedCardData();
            cardContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCombo();
                }
            });
        }
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
            ImageUtils.loadCardImage(OtpPaymentActivity.this, cardLogo, card.getBin().getLogo());
            cardNumber.setText(card.getLastDigits());
            if (selectedCard != null && selectedCard.getLastDigits().equals(card.getLastDigits())) {
                cardNumber.setTextColor(ContextCompat.getColor(OtpPaymentActivity.this, R.color.combo_item_text_color_selected));
            } else {
                cardNumber.setTextColor(ContextCompat.getColor(OtpPaymentActivity.this, R.color.combo_item_text_color));
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
        if(selectedCard == null) {
            selectedCard = cards.get(0);
        }
        ImageUtils.loadCardImage(this, cardLogo, selectedCard.getBin().getLogo());
        fourDigits.setText(selectedCard.getLastDigits());
    }

    private class GetCardsToPay extends DaviPayTask<ArrayList<Card>> {

        public GetCardsToPay(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected ArrayList<Card> doInBackground(Void... params) {
            ArrayList<Card> cards = null;
            try {
                cards = Service.getOttCards(Session.getCurrentSession(getApplicationContext()).getSid());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return cards;
        }

        @Override
        protected void onPostExecute(ArrayList<Card> response) {
            super.onPostExecute(response);
            if(!processedError) {
                cards = response;
                setLayouts();
            }
        }

    }

    private class GetOttTokenTask extends DaviPayTask<String> {

        public GetOttTokenTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            String data = null;
            try {
                data = Service.getOttToken(Session.getCurrentSession(getApplicationContext()).getSid(), selectedCard.getLastDigits());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(!processedError) {
                startOttTimer(response);
            }
        }

    }

    private void startOttTimer(String response) {
        myCountDownTimer = new MyCountDownTimer(TIME_TO_USE, 1000, response);
        myCountDownTimer.start();
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval, String ottToken) {
            super(millisInFuture, countDownInterval);
            findViewById(R.id.receipt_number_layout).setVisibility(View.VISIBLE);
            TextView receiptNumber = (TextView) findViewById(R.id.receipt_number);
            receiptNumber.setText(ottToken);
            findViewById(R.id.card_container).setEnabled(false);
            findViewById(R.id.get_otp_button).setEnabled(false);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            float division = 240000;
            float progress = ((TIME_TO_USE - Float.valueOf(millisUntilFinished)) / division);
            progressBar.setPercent(progress);
        }

        @Override
        public void onFinish() {
            findViewById(R.id.receipt_number_layout).setVisibility(View.GONE);
            findViewById(R.id.card_container).setEnabled(true);
            findViewById(R.id.get_otp_button).setEnabled(true);
            if(showExpiredMessage) {
                showNotUsedMessage();
            }
        }

    }

    private void showNotUsedMessage() {
        DialogUtil.toast(this,
                getString(R.string.otp_payment_not_used_title),
                getString(R.string.otp_payment_not_used_subtitle),
                getString(R.string.otp_payment_not_used_text));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
        showExpiredMessage = true;
    }

    @Override
    protected void onPause() {
        showExpiredMessage = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(myCountDownTimer != null) {
            myCountDownTimer.cancel();
        }
        super.onDestroy();
    }

}
