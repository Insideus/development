package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.service.Service;

public class ECardRechargeActivity extends AbstractPayActivity {

    private EditText otherPaymentValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_card_recharge_activity);
        if (savedInstanceState != null) {
            card = savedInstanceState.getParcelable(CARD_KEY);
        } else {
            card = getIntent().getParcelableExtra(CARD_KEY);
        }
        priceIndicator = getString(R.string.card_detail_item_transaction_price_indicator);
        setToolbar(R.id.toolbar_layout, true, getString(R.string.e_card_title));
        setLayouts();
        new GetCardPayDetail().execute();
    }

    @Override
    protected void setBottomLayouts() {

    }

    @Override
    protected void setLayoutData() {
        if (detail.getAccounts() != null && !detail.getAccounts().isEmpty()) {
            selectedAccount = detail.getAccounts().get(0);
            setSelectedIdAccountName();
            selectedAccountText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCombo();
                }
            });
        }
    }

    private void setLayouts() {
        selectedAccountText = (TextView) findViewById(R.id.account_spinner);
        otherPaymentValue = (EditText) findViewById(R.id.other_payment_value);
        View payButton = findViewById(R.id.pay_button);
        if(selectedAccountText == null || otherPaymentValue == null || payButton == null){
            return;
        }
        otherPaymentValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (justDeletedOtherPaymentText) {
                    justDeletedOtherPaymentText = false;
                    return;
                }
                if (s.toString().equals(priceIndicator)) {
                    justDeletedOtherPaymentText = true;
                    otherPaymentValue.setText(priceIndicator.concat(" "));
                    Selection.setSelection(otherPaymentValue.getText(), otherPaymentValue.getText().length());
                    return;
                }
                if (!s.toString().contains(priceIndicator)) {
                    otherPaymentValue.setText(priceIndicator.concat(" ").concat(s.toString()));
                    Selection.setSelection(otherPaymentValue.getText(), otherPaymentValue.getText().length());
                }
            }
        });
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = getAmount();
                if (validateAmount(amount)) {
                    Intent intent = new Intent(ECardRechargeActivity.this, CardActionDialogActivity.class);
                    intent.putExtra(CardActionDialogActivity.TITLE_KEY, "CONFIRMAR PAGO");
                    intent.putExtra(CardActionDialogActivity.SUBTITLE_KEY, "");
                    intent.putExtra(CardActionDialogActivity.TEXT_KEY, getPayConfirmationText(amount));
                    intent.putExtra(CardActionDialogActivity.IS_CARD_PAY, true);
                    intent.putExtra(CardActionDialogActivity.CARD_KEY, card);
                    startActivityForResult(intent, PAY_REQUEST);
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                }
            }
        });
    }

    @Override
    protected boolean callService(String sid, String amount) throws ServiceException {
        return Service.payCard(Service.ECARD, sid, card.getLastDigits(), selectedAccount.getLastDigits(), amount);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_REQUEST && resultCode == RESULT_OK) {
            new PayTask(getAmount()).execute();
        }
        if (requestCode == ON_CLOSE_REQUEST && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private String getAmount() {
        String value = otherPaymentValue.getText().toString();
        value = value.replace(priceIndicator, "");
        value = value.replace(" ", "");
        return value;
    }
}
