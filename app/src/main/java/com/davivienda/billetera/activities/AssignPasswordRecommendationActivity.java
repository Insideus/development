package com.davivienda.billetera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.davivienda.billetera.R;
import com.davivienda.billetera.utils.DialogUtil;

public class AssignPasswordRecommendationActivity extends BaseActivity {

    public static String ID_TYPE_KEY = "id_type_key";
    public static String ID_NUMBER_KEY = "id_number_key";
    public static String PRODUCT_CODE_KEY = "product_code_key";

    private String personId;
    private String personIdType;
    private String productCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_password_recommendation_activity);
        if (savedInstanceState != null) {
            personIdType = savedInstanceState.getString(ID_TYPE_KEY, "");
            personId = savedInstanceState.getString(ID_NUMBER_KEY, "");
            productCode = savedInstanceState.getString(PRODUCT_CODE_KEY, "");
        } else {
            personIdType = getIntent().getStringExtra(ID_TYPE_KEY);
            personId = getIntent().getStringExtra(ID_NUMBER_KEY);
            productCode = getIntent().getStringExtra(PRODUCT_CODE_KEY);
        }
        setActionBar(getString(R.string.assign_password_title), true);
        setLayouts();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putString(PRODUCT_CODE_KEY, productCode);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ID_TYPE_KEY, personIdType);
        outState.putString(ID_NUMBER_KEY, personId);
        outState.putString(PRODUCT_CODE_KEY, productCode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        personId = savedInstanceState.getString(ID_NUMBER_KEY);
        personIdType = savedInstanceState.getString(ID_TYPE_KEY);
        productCode = savedInstanceState.getString(PRODUCT_CODE_KEY);
    }

    private void setLayouts() {
        View continueButton = findViewById(R.id.continue_button);
        final CheckBox termsAndConditions = (CheckBox) findViewById(R.id.terms_and_conditions);
        if(continueButton == null || termsAndConditions == null){
            return;
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!termsAndConditions.isChecked()){
                    DialogUtil.toast(AssignPasswordRecommendationActivity.this,
                            getString(R.string.input_data_error_generic_title),
                            "",
                            getString(R.string.assign_password_recommendation_terms_and_conditions));
                } else {
                    Intent intent = new Intent(AssignPasswordRecommendationActivity.this, ChangePasswordStep2Activity.class);
                    intent.putExtra(ChangePasswordStep2Activity.ID_TYPE_KEY, personIdType);
                    intent.putExtra(ChangePasswordStep2Activity.ID_NUMBER_KEY, personId);
                    intent.putExtra(ChangePasswordStep2Activity.PRODUCT_CODE_KEY, productCode);
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.terms_and_conditions_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTermsAndConditionsPage(getString(R.string.policy_picker_check_box_terms_and_conditions));
            }
        });
    }

}
