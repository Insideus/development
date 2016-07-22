package ar.com.fennoma.davipocket.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginSteps;
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
        DialogUtil.toast(this, getString(R.string.generic_service_error_title), "",
                getString(R.string.generic_service_error));
    }

    public void handleInvalidSessionError() {
        DialogUtil.invalidSessionToast(this);
    }

    void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if(error != null) {
            switch(error) {
                case LOGIN_ERROR:
                    DialogUtil.toast(this,
                            getString(R.string.login_error_message_title),
                            "", getString(R.string.login_error_message_text));
                    break;
                case WEB_PASSWORD_ERROR:
                    DialogUtil.toast(this,
                            getString(R.string.login_web_password_error_message_title),
                            "", getString(R.string.login_web_password_error_message_text));
                    break;
                case VALIDATE_PRODUCT_ERROR:
                    DialogUtil.toast(this,
                            getString(R.string.login_error_message_title), "",
                            getString(R.string.login_error_message_text));
                    break;
                case VIRTUAL_PASSWORD_VALIDATION_ERROR:
                    DialogUtil.toast(this,
                            getString(R.string.login_error_message_title), "",
                            additionalParam);
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

    void goToRegistrationStep(LoginSteps step) {
        if(step != null) {
            switch(step) {
                case FACEBOOK:
                    Intent facebookIntent = new Intent(this, FacebookLoginActivity.class);
                    facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(facebookIntent);
                    break;
                case ADDITIONAL_INFO:
                    Intent addtionalInfoIntent = new Intent(this, LoginConfirmationActivity.class);
                    addtionalInfoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(addtionalInfoIntent);
                    break;
                case ACCOUNT_VERIFICATION:
                    Intent accountVerificationIntent = new Intent(this, AccountActivationActivity.class);
                    accountVerificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(accountVerificationIntent);
                    break;
                case COMMUNICATION_PERMISSIONS:
                    Intent communicationPermissionsActivity = new Intent(this, PolicyPickerActivity.class);
                    communicationPermissionsActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(communicationPermissionsActivity);
                    break;
                case REGISTRATION_COMPLETED:
                    Intent mainActivityIntent = new Intent(this, MainActivity.class);
                    mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainActivityIntent);
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
