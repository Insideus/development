package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.tasks.DaviPayTask;

public class ActionDialogActivity extends BaseActivity implements BaseActivity.OtpCodeReceived {

    public static final String SUCCESS_TITLE = "success title";
    public static final String SUCCESS_SUBTITLE = "success subtitle";
    public static final String SUCCESS_TEXT = "success text";
    public static final String ERROR_TITLE = "error title";
    public static final String ERROR_SUBTITLE = "error subtitle";
    public static final String ERROR_MESSAGE = "error message";

    public static final String TITLE_KEY = "toast_title_key";
    public static final String SUBTITLE_KEY = "toast_subtitle_key";
    public static final String TEXT_KEY = "toast_text_key";
    public static final String CARD_KEY = "card_key";

    public static final String LAST_FOUR_DIGITS = "last four digits";
    public static final String IS_CARD_PAY = "is_card_pay_key";
    public static final String IS_CCV_DIALOG = "is_ccv_dialog_key";
    public static final String IS_CARD_NUMBER_DIALOG = "is_card_number_dialog_key";
    public static final String IS_BLOCK_CARD_DIALOG = "is_block_card_dialog_key";
    public static final String E_CARD_GET_CVV = "ecard get cvv";
    public static final String E_CARD_SHOW_DATA = "ecard show data";

    public static final String OTP_VALIDATION_DIALOG = "otp_validation_needed_key";
    public static final String OTP_VALIDATION_DIALOG_TEXT_KEY = "otp_validation_text_key";

    public static final String SHOW_CALL_BUTTON_KEY = "show_call_button_key";
    public static final String CALL_BUTTON_NUMBER_KEY = "call_button_number_key";
    public static final String SHOW_PAY_BUTTON_KEY = "show_pay_button_key";

    public static final String NEW_DEVICE_DETECTED = "new device detected";
    public static final String CANCEL_PURCHASE = "cancel purchase";

    public static final String E_CARD_CREATE = "ecard create";
    public static final String LOGIN_DATA_CHECK = "login data check";
    public static final String LOGOUT_REQUEST = "logout request";
    public static final String NEXT_TO_EXPIRE = "next to expire";

    public static final int RESULT_FAILED = -2;

    private String title;
    private String subtitle;
    private String text;
    private Boolean showCallButton;
    private Boolean isCcvDialog;
    private Boolean isCardNumberDialog;
    private Boolean isBlockCardDialog;
    private Boolean showPayButton;
    private Boolean showECardCreateDialog;
    private String lastFourDigits;
    private Boolean eCardGetCVV;
    private Boolean eCardShowData;
    private boolean isCardPay;
    private String callNumber;
    private Card card;
    private Boolean newDeviceDetected;
    private Boolean otpValidationNeeded;
    private Boolean cancelPurchase;
    private Boolean loginDataCheck;
    private Boolean logoutRequest;
    private boolean nextToExpire;

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
            showECardCreateDialog = savedInstanceState.getBoolean(E_CARD_CREATE, false);
            eCardGetCVV = savedInstanceState.getBoolean(E_CARD_GET_CVV, false);
            lastFourDigits = savedInstanceState.getString(LAST_FOUR_DIGITS, "");
            eCardShowData = savedInstanceState.getBoolean(E_CARD_SHOW_DATA, false);
            newDeviceDetected = savedInstanceState.getBoolean(NEW_DEVICE_DETECTED, false);
            otpValidationNeeded = savedInstanceState.getBoolean(OTP_VALIDATION_DIALOG, false);
            cancelPurchase = savedInstanceState.getBoolean(CANCEL_PURCHASE, false);
            loginDataCheck = savedInstanceState.getBoolean(LOGIN_DATA_CHECK, false);
            logoutRequest = savedInstanceState.getBoolean(LOGOUT_REQUEST, false);
            nextToExpire = savedInstanceState.getBoolean(NEXT_TO_EXPIRE, false);
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
            showECardCreateDialog = getIntent().getBooleanExtra(E_CARD_CREATE, false);
            eCardGetCVV = getIntent().getBooleanExtra(E_CARD_GET_CVV, false);
            lastFourDigits = getIntent().getStringExtra(LAST_FOUR_DIGITS);
            eCardShowData = getIntent().getBooleanExtra(E_CARD_SHOW_DATA, false);
            newDeviceDetected = getIntent().getBooleanExtra(NEW_DEVICE_DETECTED, false);
            otpValidationNeeded = getIntent().getBooleanExtra(OTP_VALIDATION_DIALOG, false);
            cancelPurchase = getIntent().getBooleanExtra(CANCEL_PURCHASE, false);
            loginDataCheck = getIntent().getBooleanExtra(LOGIN_DATA_CHECK, false);
            logoutRequest = getIntent().getBooleanExtra(LOGOUT_REQUEST, false);
            nextToExpire = getIntent().getBooleanExtra(NEXT_TO_EXPIRE, false);
        }
        setLayouts();
        animateOpening();
        setOtpCodeReceived(this);
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
        EditText otpInput = (EditText) findViewById(R.id.otp_code);

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

        if (showECardCreateDialog) {
            showECardCreateLayouts(acceptButton, ignoreButton);
        }

        if (isCardPay) {
            showCardPayLayouts(acceptButton, ignoreButton);
        }

        if (eCardGetCVV) {
            setECardCVVLayouts(acceptButton, ignoreButton);
        }

        if(eCardShowData){
            setECardShowDataLayouts(acceptButton, ignoreButton);
        }

        if(newDeviceDetected){
            setNewDeviceDetectedLayouts(acceptButton, ignoreButton);
        }

        if(otpValidationNeeded) {
            setOtpValidationLayouts(acceptButton, ignoreButton);
        } else {
            otpInput.setVisibility(View.GONE);
        }

        if(cancelPurchase){
            setCancelPurchase(acceptButton, ignoreButton);
        }

        if(loginDataCheck){
            setLoginDataCheck(acceptButton, ignoreButton);
        }

        if(logoutRequest){
            setLogoutRequest(acceptButton, ignoreButton);
        }
        if(nextToExpire){
            setNextToExpireLabel(acceptButton, ignoreButton);
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

    private void setNextToExpireLabel(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.my_cards_pay_button_text));
        ignoreButton.setVisibility(View.GONE);
        acceptButton.setOnClickListener(getGoToCardDetailsActivity());
    }

    private void setLogoutRequest(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.my_cards_block_card_accept));
        ignoreButton.setText(getString(R.string.my_cards_block_card_cancel));
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setLoginDataCheck(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.login_retry_connection));
        ignoreButton.setText(getString(R.string.login_close_app));
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_FAILED);
                finish();
            }
        });
    }

    private void setCancelPurchase(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.my_cards_block_card_accept));
        ignoreButton.setText(getString(R.string.my_cards_block_card_cancel));
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setNewDeviceDetectedLayouts(TextView acceptButton, TextView ignoreButton) {
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
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void setECardShowDataLayouts(TextView acceptButton, TextView ignoreButton) {
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
                if(TextUtils.isEmpty(lastFourDigits)){
                    eCardFailed(getString(R.string.my_cards_failed_opperation_message));
                }
                new ECardShowData(ActionDialogActivity.this, lastFourDigits, null).execute();
            }
        });
    }

    private void setECardCVVLayouts(TextView acceptButton, TextView ignoreButton) {
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
                if(TextUtils.isEmpty(lastFourDigits)){
                    eCardFailed(getString(R.string.my_cards_failed_opperation_message));
                }
                new ECardGetCVV(ActionDialogActivity.this, lastFourDigits).execute();
            }
        });
    }

    private void showECardCreateLayouts(TextView acceptButton, TextView ignoreButton) {
        final CheckBox termsAndConditions = (CheckBox) findViewById(R.id.terms_and_conditions);
        termsAndConditions.setVisibility(View.VISIBLE);
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
                if (termsAndConditions.isChecked()) {
                    new eCardCreateTask(ActionDialogActivity.this).execute();
                } else {
                    eCardFailed(getString(R.string.card_action_terms_and_conditions_not_accepted_error));
                }
            }
        });
    }

    private void showBlockLayouts(TextView acceptButton, TextView ignoreButton) {
        acceptButton.setText(getString(R.string.card_pay_pay_button_text));
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
                new BlockCardTask(ActionDialogActivity.this, "").execute();
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

    private void setOtpValidationLayouts(TextView acceptButton, TextView ignoreButton) {
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
                EditText otpInput = (EditText) findViewById(R.id.otp_code);
                if(otpInput.getText().toString().length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(OTP_VALIDATION_DIALOG_TEXT_KEY, otpInput.getText().toString());
                    setResult(RESULT_OK, new Intent().putExtras(bundle));
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
    }

    private void showCallLayouts(View callButton, View acceptButton, View ignoreButton) {
        checkCallPermissions();
        callButton.setVisibility(LinearLayout.VISIBLE);
        acceptButton.setVisibility(LinearLayout.GONE);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ActionDialogActivity.this,
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
                if (cardNumberInput.getText().length() > 13 && cardNumberInput.getText().length() < 17) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ERROR_MESSAGE, getString(R.string.card_action_not_valid_card_number_error));
                    setResult(RESULT_FAILED, new Intent().putExtras(bundle));
                    finish();
                    return;
                }
                new ActivateCardTask(ActionDialogActivity.this, cardNumberInput.getText().toString(), null).execute();
            }
        };
    }

    private View.OnClickListener getBlockOrAddCardListener(final EditText ccvInput) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ccvInput.getText()) || ccvInput.getText().length() < 3) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ERROR_MESSAGE, getString(R.string.card_action_not_valid_ccv_error));
                    setResult(RESULT_FAILED, new Intent().putExtras(bundle));
                    finish();
                    return;
                }
                new AddCardTask(ActionDialogActivity.this, ccvInput.getText().toString(), null).execute();
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
        if (!checkPermission(android.Manifest.permission.CALL_PHONE, getApplicationContext())) {
            requestPermission(android.Manifest.permission.CALL_PHONE, 101);
        }
    }

    public View.OnClickListener getGoToCardDetailsActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActionDialogActivity.this, CardDetailActivity.class);
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
                Intent intent;
                if(card.getECard() != null && card.getECard()) {
                    intent = new Intent(ActionDialogActivity.this, ECardRechargeActivity.class);
                } else {
                    intent = new Intent(ActionDialogActivity.this, CardPayDetailActivity.class);
                }
                intent.putExtra(CardPayDetailActivity.CARD_KEY, card);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }
        };
    }

    public class ActivateCardTask extends DaviPayTask<Boolean> {

        String cardNumber;
        String otpCode;

        public ActivateCardTask(BaseActivity activity, String cardNumber, String otpCode) {
            super(activity);
            this.cardNumber = cardNumber;
            this.otpCode = otpCode;
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
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                String cardLastDigits = "";
                if(card != null) {
                    cardLastDigits = card.getLastDigits();
                }
                response = Service.activateCard(sid, cardNumber, getTodo1Data(), otpCode, cardLastDigits);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    setResult(RESULT_FAILED);
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
    }

    public class BlockCardTask  extends DaviPayTask<Boolean> {

        String otpCode;

        public BlockCardTask(BaseActivity activity, String otpCode) {
            super(activity);
            this.otpCode = otpCode;
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
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.blockCard(sid, card.getLastDigits(), getTodo1Data(), otpCode);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    setResult(RESULT_FAILED);
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
    }

    public class AddCardTask extends DaviPayTask<Boolean> {

        String ccv;
        String otpCode;

        public AddCardTask(BaseActivity activity, String ccv, String otpCode) {
            super(activity);
            this.ccv = ccv;
            this.otpCode = otpCode;
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
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                response = Service.addCard(sid, card.getLastDigits(), ccv, getTodo1Data(), otpCode);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    setResult(RESULT_FAILED);
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }

    }

    private class eCardCreateTask extends DaviPayTask<Card> {


        public eCardCreateTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Card doInBackground(Void... params) {
            Card response = null;
            try {
                response = Service.newECard(Session.getCurrentSession(getApplicationContext()).getSid(), getTodo1Data());
            } catch (ServiceException e) {
                e.printStackTrace();
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Card response) {
            ErrorMessages error = ErrorMessages.getError(errorCode);
            if(error == null) {
                errorCode = ErrorMessages.ECARD_NOT_CREATED.getErrorCode();
            }
            super.onPostExecute(response);
            if(!processedError) {
                createECardSuccess(response);
            }
        }

    }

    private void createECardSuccess(Card card) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(E_CARD_SHOW_DATA, card);
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        finish();
    }

    private void eCardShowDataSuccess(Card response) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(E_CARD_SHOW_DATA, response);
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        finish();
    }

    private void eCardFailed(String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(ERROR_MESSAGE, errorMessage);
        setResult(RESULT_FAILED, new Intent().putExtras(bundle));
        finish();
    }

    private class ECardGetCVV extends DaviPayTask<Boolean> {

        private String lastDigits;

        public ECardGetCVV(BaseActivity activity, String lastDigits) {
            super(activity);
            this.lastDigits = lastDigits;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean response = false;
            try {
                response = Service.getCVV(Session.getCurrentSession(getApplicationContext()).getSid(), lastDigits, getTodo1Data());
            } catch (ServiceException e) {
                e.printStackTrace();
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if(!processedError) {
                if (!response) {
                    //Hancdle invalid session error.
                    eCardFailed(getString(R.string.e_card_get_cvv_error));
                } else {
                    eCardGetCVVSuccess();
                }
            }
        }

    }

    private void eCardGetCVVSuccess() {
        Bundle bundle = new Bundle();
        bundle.putString(SUCCESS_TITLE, getString(R.string.e_card_get_cvv_success_title));
        bundle.putString(SUCCESS_SUBTITLE, getString(R.string.e_card_get_cvv_success_subtitle));
        bundle.putString(SUCCESS_TEXT, getString(R.string.e_card_get_cvv_success_text));
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        finish();
    }

    private class ECardShowData  extends DaviPayTask<Card> {

        private String lastDigits;
        private String otpCode;

        public ECardShowData(BaseActivity activity, String lastFourDigits, String otpCode) {
            super(activity);
            this.lastDigits = lastFourDigits;
            this.otpCode = otpCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Card doInBackground(Void... params) {
            Card response = null;
            try {
                response = Service.getECardData(Session.getCurrentSession(getApplicationContext()).getSid(),
                        lastDigits, getTodo1Data(), otpCode);
            } catch (ServiceException e) {
                e.printStackTrace();
                errorCode = e.getErrorCode();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Card response) {
            ErrorMessages error = ErrorMessages.getError(errorCode);
            if(error == null && response == null) {
                errorCode = ErrorMessages.ECARD_DATA_NOT_SENT.getErrorCode();
            }
            super.onPostExecute(response);
            if(!processedError) {
                eCardShowDataSuccess(response);
            }
        }

    }

    @Override
    public void onOtpCodeReceived(String otpCode) {
        if(isCardNumberDialog) {
            EditText cardNumberInput = (EditText) findViewById(R.id.card_number);
            if(cardNumberInput != null) {
                new ActivateCardTask(this, cardNumberInput.getText().toString(), otpCode).execute();
                return;
            }
        }
        if(isCcvDialog) {
            EditText ccvInput = (EditText) findViewById(R.id.ccv_code);
            if(ccvInput != null) {
                new AddCardTask(this, ccvInput.getText().toString(), otpCode).execute();
                return;
            }
        }
        if(isBlockCardDialog) {
            if(card != null) {
                new BlockCardTask(this, otpCode).execute();
                return;
            }
        }
        if(eCardShowData) {
            TextView cvv = (TextView) findViewById(R.id.ccv_code);
            if(cvv != null) {
                new ECardShowData(this, lastFourDigits, otpCode).execute();
                return;
            }
        }
        showServiceGenericError();
    }

    public void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if(error != null) {
            switch(error) {
                case ECARD_NOT_CREATED:
                    eCardFailed(getString(R.string.my_cards_e_card_create_failed_text));
                    break;
                case ECARD_DATA_NOT_SENT:
                    eCardFailed(getString(R.string.e_card_show_data_error));
                    break;
                default:
                    super.processErrorAndContinue(error, additionalParam);
            }
        } else {
            showServiceGenericError();
        }
    }

}
