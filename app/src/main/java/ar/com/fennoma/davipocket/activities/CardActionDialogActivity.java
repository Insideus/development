package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;

public class CardActionDialogActivity extends BaseActivity {

    public static final String TITLE_KEY = "toast_title_key";
    public static final String SUBTITLE_KEY = "toast_subtitle_key";
    public static final String TEXT_KEY = "toast_text_key";
    public static final String CARD_KEY = "card_key";

    public static final String IS_CARD_PAY = "is_card_pay_key";
    public static final String IS_CCV_DIALOG = "is_ccv_dialog_key";
    public static final String IS_CARD_NUMBER_DIALOG = "is_card_number_dialog_key";
    public static final String IS_BLOCK_CARD_DIALOG = "is_block_card_dialog_key";

    public static final String SHOW_CALL_BUTTON_KEY = "show_call_button_key";
    public static final String CALL_BUTTON_NUMBER_KEY = "call_button_number_key";
    public static final String SHOW_PAY_BUTTON_KEY = "show_pay_button_key";

    public static final int RESULT_FAILED = -2;

    private String title;
    private String subtitle;
    private String text;
    private Boolean showCallButton;
    private Boolean isCcvDialog;
    private Boolean isCardNumberDialog;
    private Boolean isBlockCardDialog;
    private Boolean showPayButton;
    private boolean isCardPay;
    private String callNumber;
    private Card card;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_action_dialog_layout);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TITLE_KEY, "");
            subtitle = savedInstanceState.getString(SUBTITLE_KEY, "");
            text = savedInstanceState.getString(TEXT_KEY, "");
            showCallButton = savedInstanceState.getBoolean(SHOW_CALL_BUTTON_KEY, false);
            isCcvDialog = savedInstanceState.getBoolean(IS_CCV_DIALOG, false);
            isBlockCardDialog = savedInstanceState.getBoolean(IS_BLOCK_CARD_DIALOG, false);
            isCardNumberDialog = savedInstanceState.getBoolean(IS_CARD_NUMBER_DIALOG, false);
            callNumber = savedInstanceState.getString(CALL_BUTTON_NUMBER_KEY, "");
            card = savedInstanceState.getParcelable(CARD_KEY);
            showPayButton = savedInstanceState.getBoolean(SHOW_PAY_BUTTON_KEY, false);
            isCardPay = savedInstanceState.getBoolean(IS_CARD_PAY, false);
        } else {
            title = getIntent().getStringExtra(TITLE_KEY);
            subtitle = getIntent().getStringExtra(SUBTITLE_KEY);
            text = getIntent().getStringExtra(TEXT_KEY);
            showCallButton = getIntent().getBooleanExtra(SHOW_CALL_BUTTON_KEY, false);
            isCcvDialog = getIntent().getBooleanExtra(IS_CCV_DIALOG, false);
            isBlockCardDialog = getIntent().getBooleanExtra(IS_BLOCK_CARD_DIALOG, false);
            isCardNumberDialog = getIntent().getBooleanExtra(IS_CARD_NUMBER_DIALOG, false);
            callNumber = getIntent().getStringExtra(CALL_BUTTON_NUMBER_KEY);
            card = getIntent().getParcelableExtra(CARD_KEY);
            showPayButton = getIntent().getBooleanExtra(SHOW_PAY_BUTTON_KEY, false);
            isCardPay = getIntent().getBooleanExtra(IS_CARD_PAY, false);
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
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void setLayouts() {
        View cancelButton = findViewById(R.id.close_button);
        if (cancelButton == null) {
            return;
        }
        cancelButton.setOnClickListener(getCloseListener());

        TextView callButton = (TextView) findViewById(R.id.button);
        TextView acceptButton = (TextView) findViewById(R.id.accept_button);
        TextView ignoreButton = (TextView) findViewById(R.id.ignore_button);

        EditText ccvInput = (EditText) findViewById(R.id.ccv_code);
        EditText cardNumberInput = (EditText) findViewById(R.id.card_number);

        if (showCallButton) {
            showCallLayouts(callButton, acceptButton, ignoreButton);
        } else {
            callButton.setVisibility(LinearLayout.GONE);
        }

        if (isCardNumberDialog) {
            showCardLayouts(acceptButton, ignoreButton, cardNumberInput);
        } else {
            cardNumberInput.setVisibility(LinearLayout.GONE);
        }

        if (isBlockCardDialog) {
            showBlockLayouts(acceptButton, ignoreButton);
        }

        if (isCcvDialog) {
            showCvvLayouts(acceptButton, ignoreButton, ccvInput);
        } else {
            ccvInput.setVisibility(LinearLayout.GONE);
        }

        if (showPayButton) {
            showPayButtonLayouts(acceptButton, ignoreButton);
        }

        if (isCardPay) {
            showCardPayLayouts(acceptButton, ignoreButton);
        }

        TextView titleTv = (TextView) findViewById(R.id.toast_title);
        if (title != null && title.length() > 0) {
            titleTv.setText(title);
        } else {
            titleTv.setVisibility(LinearLayout.GONE);
        }
        TextView subtitleTv = (TextView) findViewById(R.id.toast_subtitle);
        if (subtitle != null && subtitle.length() > 0) {
            subtitleTv.setText(subtitle);
        } else {
            subtitleTv.setVisibility(LinearLayout.GONE);
        }
        TextView textTv = (TextView) findViewById(R.id.toast_text);
        if (text != null && text.length() > 0) {
            textTv.setText(text);
        } else {
            textTv.setVisibility(LinearLayout.GONE);
        }
    }

    private void showBlockLayouts(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.my_cards_block_card_accept));
        ignoreButton.setText(getString(R.string.my_cards_block_card_cancel));
        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BlockCardTask().execute();
            }
        });
    }

    private void showCardPayLayouts(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.card_pay_pay_button_text));
        ignoreButton.setText(getString(R.string.card_pay_cancel_pay_button_text));

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void showPayButtonLayouts(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.my_cards_pay_button_text));
        ignoreButton.setText(getString(R.string.my_cards_pay_continue_button_text));

        acceptButton.setOnClickListener(getGoToPayCardActivity());
        ignoreButton.setOnClickListener(getGoToCardDetailsActivity());
    }

    private void showCvvLayouts(TextView acceptButton, TextView ignoreButton, EditText ccvInput) {
        acceptButton.setText(getString(R.string.my_cards_continue_button_text));
        ignoreButton.setText(getString(R.string.my_cards_cancel_button_text));

        acceptButton.setOnClickListener(getBlockOrAddCardListener(ccvInput));
        ignoreButton.setOnClickListener(getCloseListener());
    }

    private void showCardLayouts(TextView acceptButton, TextView ignoreButton, EditText cardNumberInput) {
        acceptButton.setText(getString(R.string.my_cards_continue_button_text));
        ignoreButton.setText(getString(R.string.my_cards_cancel_button_text));

        acceptButton.setOnClickListener(getActivateCardListener(cardNumberInput));
        ignoreButton.setOnClickListener(getCloseListener());
    }

    private void showCallLayouts(View callButton, View acceptButton, View ignoreButton) {
        checkCallPermissions();
        callButton.setVisibility(LinearLayout.VISIBLE);
        acceptButton.setVisibility(LinearLayout.GONE);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(CardActionDialogActivity.this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + callNumber)));
                setResult(RESULT_OK);
            }
        });
        ignoreButton.setOnClickListener(getCloseListener());
    }

    private View.OnClickListener getActivateCardListener(final EditText cardNumberInput) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardNumberInput.getText().length() == 16) {
                    new ActivateCardTask().execute(cardNumberInput.getText().toString());
                }
            }
        };
    }

    private View.OnClickListener getBlockOrAddCardListener(final EditText ccvInput) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(ccvInput.getText()) || ccvInput.getText().length() < 3){
                    setResult(RESULT_FAILED);
                    finish();
                    return;
                }
                new AddCardTask().execute(ccvInput.getText().toString());
            }
        };
    }

    @NonNull
    private View.OnClickListener getCloseListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        };
    }

    public void checkCallPermissions() {
        if (!checkPermission(android.Manifest.permission.CALL_PHONE, getApplicationContext(), this)) {
            requestPermission(android.Manifest.permission.CALL_PHONE, 101, getApplicationContext(), this);
        }
    }

    public View.OnClickListener getGoToCardDetailsActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActionDialogActivity.this, CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.CARD_KEY, card);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }
        };
    }

    public View.OnClickListener getGoToPayCardActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActionDialogActivity.this, CardPayDetailActivity.class);
                intent.putExtra(CardPayDetailActivity.CARD_KEY, card);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }
        };
    }

    public class ActivateCardTask extends AsyncTask<String, Void, Boolean> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.activateCard(sid, params[0]);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            hideLoading();
            if (response == null || !response) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    //showServiceGenericError();
                    setResult(RESULT_FAILED);
                    finish();
                }
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public class BlockCardTask extends AsyncTask<String, Void, Boolean> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.blockCard(sid, card.getLastDigits());
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            hideLoading();
            if (response == null || !response) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    //showServiceGenericError();
                    setResult(RESULT_FAILED);
                    finish();
                }
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public class AddCardTask extends AsyncTask<String, Void, Boolean> {

        String errorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean response = null;
            try {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.addCard(sid, card.getLastDigits(), params[0]);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            hideLoading();
            if (response == null || !response) {
                //Hancdle invalid session error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                if (error != null && error == ErrorMessages.INVALID_SESSION) {
                    handleInvalidSessionError();
                } else {
                    //showServiceGenericError();
                    setResult(RESULT_FAILED);
                    finish();
                }
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

}
