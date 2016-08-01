package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import ar.com.fennoma.davipocket.R;

public class CardPayDetailActivity extends BaseActivity {

    private String priceIndicator;
    private CheckBox totalPayment;
    private CheckBox minimumPayment;
    private CheckBox otherPayment;
    private boolean justDeletedOtherPaymentText = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        priceIndicator = getString(R.string.card_detail_item_transaction_price_indicator);
        setContentView(R.layout.activity_card_pay_detail);
        setToolbar(R.id.toolbar_layout, false, getString(R.string.mocked_master_card_title));
        setLayouts();
    }

    private void setLayouts() {
        totalPayment = (CheckBox) findViewById(R.id.total_payment);
        minimumPayment = (CheckBox) findViewById(R.id.minimum_payment);
        otherPayment = (CheckBox) findViewById(R.id.other_payment);
        final View otherPaymentContainer = findViewById(R.id.other_payment_container);
        final EditText otherPaymentValue = (EditText) findViewById(R.id.other_payment_value);
        if(totalPayment == null || minimumPayment == null || otherPayment == null || otherPaymentContainer == null
                || otherPaymentValue == null){
            return;
        }
        totalPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(minimumPayment, otherPayment);
            }
        });
        minimumPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(totalPayment, otherPayment);
            }
        });
        otherPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectCheckboxes(totalPayment, minimumPayment);
            }
        });
        otherPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    otherPaymentContainer.setVisibility(View.VISIBLE);
                } else {
                    otherPaymentContainer.setVisibility(View.GONE);
                }
            }
        });
        otherPaymentValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(justDeletedOtherPaymentText){
                    justDeletedOtherPaymentText = false;
                    return;
                }
                if(s.toString().equals(priceIndicator)){
                    justDeletedOtherPaymentText = true;
                    otherPaymentValue.setText(priceIndicator.concat(" "));
                    Selection.setSelection(otherPaymentValue.getText(), otherPaymentValue.getText().length());
                    return;
                }
                if(!s.toString().contains(priceIndicator)){
                    otherPaymentValue.setText(priceIndicator.concat(" ").concat(s.toString()));
                    Selection.setSelection(otherPaymentValue.getText(), otherPaymentValue.getText().length());
                }
            }
        });
    }

    private void unselectCheckboxes(CheckBox checkBox1, CheckBox checkBox2) {
        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
    }
}
