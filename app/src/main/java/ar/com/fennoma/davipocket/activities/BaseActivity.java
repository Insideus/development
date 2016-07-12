package ar.com.fennoma.davipocket.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    private Dialog loadingDialog = null;
    private Handler loadingHandler = null;
    private Runnable loadingRunnable = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setActionBar(String title, boolean backButton) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar == null){
            return;
        }
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        TextView titleTv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleTv.setText(title);
        setSupportActionBar(toolbar);
        if(backButton) {
            toolbar.setNavigationIcon(R.drawable.ab_back_icon);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    protected void showLoading() {
        loadingHandler = new Handler();
        loadingRunnable = new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();

                        loadingDialog = new Dialog(BaseActivity.this, R.style.CustomAlertDialog);
                        loadingDialog.setContentView(R.layout.dialog_loading);
                        loadingDialog.setCancelable(false);
                        loadingDialog.show();
                    }
                });
            }
        };
        loadingHandler.postDelayed(loadingRunnable, 500);
    }

    protected void hideLoading() {
        if (loadingHandler != null) {
            loadingHandler.removeCallbacks(loadingRunnable);
            loadingHandler = null;
        }

        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public void showServiceGenericError() {
        DialogUtil.toast(this, getString(R.string.generic_service_error));
    }

    public void handleInvalidSessionError() {

    }

    void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if(error != null) {
            switch(error) {
                case LOGIN_ERROR:
                    DialogUtil.toast(this, getString(R.string.login_error_message_text));
                    break;
                case VALIDATE_PRODUCT_ERROR:
                    DialogUtil.toast(this, getString(R.string.login_error_message_text));
                    break;
                case INVALID_SESSION:
                    handleInvalidSessionError();
                    break;
                default:
                    showServiceGenericError();
            }
        } else {
            showServiceGenericError();
        }
    }

    @Override
    protected void onPause() {
        if (loadingDialog != null) {
            hideLoading();
        }
        super.onPause();
    }

}
