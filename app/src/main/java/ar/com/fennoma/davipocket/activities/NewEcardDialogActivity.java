package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;

public class NewEcardDialogActivity extends BaseActivity {

    public static final String CARD_KEY = "card_key";
    public static final int RESULT_FAILED = -2;

    private Card card;

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
        if(card.getAvailableNow()) {
            textTv.setText(getString(R.string.new_ecard_text));
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
        setResult(RESULT_OK);
        finish();
    }

}
