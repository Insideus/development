package ar.com.fennoma.davipocket.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liulishuo.magicprogresswidget.MagicProgressBar;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class OtpPaymentActivity extends BaseActivity {

    public static final String CARDS_KEY = "cards_key";
    public static final String SELECTED_CARD_KEY = "selected_card_key";

    private ArrayList<Card> cards = new ArrayList<>();
    private Card selectedCard;

    private ImageView cardLogo;
    private TextView fourDigits;
    //private ProgressBar progressBar;
    private MyCountDownTimer myCountDownTimer;
    private MagicProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_payment_activity);
        setToolbar(R.id.toolbar, true, getString(R.string.main_activity_title));
        new GetCardsToPay().execute();
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
                //progressBar.setMax(240000);
                myCountDownTimer = new MyCountDownTimer(240000, 1000);
                myCountDownTimer.start();
                //new PayOrderTask().execute();
            }
        });
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
            ImageUtils.loadCardImage(OtpPaymentActivity.this, cardLogo, card.getBin().getImage());
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
        ImageUtils.loadCardImage(this, cardLogo, selectedCard.getBin().getImage());
        fourDigits.setText(selectedCard.getLastDigits());
    }

    private class GetCardsToPay extends AsyncTask<Void, Void, ArrayList<Card>> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected ArrayList<Card> doInBackground(Void... params) {
            ArrayList<Card> data = null;
            try {
                data = Service.cardsToPay(Session.getCurrentSession(getApplicationContext()).getSid());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Card> response) {
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
                cards = response;
                setLayouts();
            }
        }

    }

    /*
    private class PayOrderTask extends AsyncTask<Void, Void, String> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            String data = null;
            try {
                data = Service.payOrder(Session.getCurrentSession(getApplicationContext()).getSid(), cart);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            hideLoading();
            if(response == null || response.length() < 1){
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    showServiceGenericError();
                }
            } else {
                cart.setReceiptNumber(response);
                //goToReceiptActivity();
            }
        }

    }
    */

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            float total = 240000;
            float division = 240000;
            float progress = ((total - Float.valueOf(millisUntilFinished)) / division);
            Log.d("Progress", String.valueOf(progress));
            //progressBar.setProgress(progressBar.getMax() - progress);
            progressBar.setPercent(progress);
        }

        @Override
        public void onFinish() {
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
    }

}
