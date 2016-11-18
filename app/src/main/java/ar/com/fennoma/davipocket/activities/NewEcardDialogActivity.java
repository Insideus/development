package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class NewEcardDialogActivity extends BaseActivity {

    public static final String CARD_KEY = "card_key";
    public static final int RESULT_FAILED = -2;

    private Card card;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ecard_dialog_layout);
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
        }
        if(card == null) {
            this.finish();
        }
        setLayouts();
        animateOpening();
    }

    private void animateOpening() {
        View container = findViewById(R.id.container);
        if (container == null) {
            return;
        }
        container.startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_dot_to_full_size));
    }

    @Override
    public void onBackPressed() {
        closeDialog();
    }

    private void setLayouts() {
        findViewById(R.id.close_button).setOnClickListener(getCloseListener());
        findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });
        TextView textTv = (TextView) findViewById(R.id.new_ecard_text);
        if(card.getBin() != null && !TextUtils.isEmpty(card.getBin().getImage())) {
            ImageView cardImage = (ImageView) findViewById(R.id.card);
            ImageUtils.loadCardImage(this, cardImage, card.getBin().getImage());
        }
        if(card.getAvailableNow()) {
            textTv.setText(getString(R.string.new_ecard_text));
            intent = new Intent();
            intent.putExtra(FIRST_LOGIN_WITH_E_CARD, card);
        } else {
            textTv.setText(getString(R.string.new_ecard_24hs_text));
        }
        TextView cardNumber = (TextView) findViewById(R.id.credit_card_number);
        cardNumber.setText(card.getFullNumber());
        TextView expirationDate = (TextView) findViewById(R.id.expiration_date);
        expirationDate.setText(String.format("%s%s%s", card.getExpirationMonth(), " / ", card.getExpirationYear()));
    }

    @NonNull
    private View.OnClickListener getCloseListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        };
    }

    private void closeDialog() {
        if(intent == null) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_OK, intent);
        }
        finish();
    }

}
