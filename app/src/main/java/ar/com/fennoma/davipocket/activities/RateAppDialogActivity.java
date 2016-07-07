package ar.com.fennoma.davipocket.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.utils.DialogUtil;

public class RateAppDialogActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_application_dialog_layout);
        setLayouts();
    }

    private void setLayouts() {
        View container = findViewById(R.id.container);
        View rateApp = findViewById(R.id.rate_app);
        View cancelButton = findViewById(R.id.cancel_button);
        View rememberLater = findViewById(R.id.remember_later);
        if(container == null || rateApp == null || cancelButton == null || rememberLater == null){
            return;
        }
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rememberLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.toast(RateAppDialogActivity.this, "Davipocket le recordará más tarde");
                finish();
            }
        });
        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.toast(RateAppDialogActivity.this, "Aún no implementado");
                finish();
            }
        });
    }
}
