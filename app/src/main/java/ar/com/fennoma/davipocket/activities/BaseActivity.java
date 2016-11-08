package ar.com.fennoma.davipocket.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.model.ErrorMessages;
import ar.com.fennoma.davipocket.model.LoginSteps;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.User;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.controls.ComboHolder;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.SharedPreferencesUtils;
import ar.com.fennoma.davipocket.utils.Todo1Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    private static final int OTP_NEEDED = 181;
    public static final int SHOW_CREATED_ECARD_POPUP = 182;
    private static final int LOGOUT_REQUEST = 183;
    protected static final int CLOSE_ACTIVITY_REQUEST = 100;

    protected DialogPlus dialogPlus;
    private Dialog loadingDialog = null;
    private Handler loadingHandler = null;
    private Runnable loadingRunnable = null;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public OtpCodeReceived otpCodeReceived;

    public static boolean checkPermission(String strPermission, Context context) {
        int result = ContextCompat.checkSelfPermission(context, strPermission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setActionBar(String title, boolean backButton) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        TextView titleTv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleTv.setText(title);
        setSupportActionBar(toolbar);
        if (backButton) {
            toolbar.setNavigationIcon(R.drawable.ab_back_icon);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    protected void setToolbar(int toolbarId, boolean backButton) {
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        hideTitle();
        setNavigationDrawer(R.id.drawer_layout, toolbarId, !backButton);
    }

    protected void setToolbar(int toolbarId, boolean backButton, String title) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);
        hideTitle();
        setTitle(toolbar, title);
        setNavigationDrawer(R.id.drawer_layout, toolbarId, !backButton);
    }

    protected void setToolbarWOHomeButton(int toolbarId, String title){
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);
        hideTitle();
        setTitle(toolbar, title);
        if(getSupportActionBar() == null){
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    private void setTitle(Toolbar toolbar, String title) {
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        if (titleTextView == null) {
            return;
        }
        titleTextView.setText(title);
    }

    protected void updateDaviPoints() {
        ((TextView) findViewById(R.id.davi_points_amount)).setText(SharedPreferencesUtils.getUser().getPoints());
    }

    public void showLoading() {
        loadingHandler = new Handler();
        loadingRunnable = new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog == null || !loadingDialog.isShowing()) {
                            hideLoading();
                            loadingDialog = new Dialog(BaseActivity.this, R.style.CustomAlertDialog);
                            loadingDialog.setContentView(R.layout.dialog_loading);
                            loadingDialog.setCancelable(false);
                            loadingDialog.show();
                        }
                    }
                });
            }
        };
        loadingHandler.postDelayed(loadingRunnable, 500);
    }

    public void hideLoading() {
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
        showServiceGenericError(false);
    }

    public void showServiceGenericError(boolean closeActivity) {
        if(!closeActivity) {
            DialogUtil.toast(this, getString(R.string.generic_service_error_title), "",
                    getString(R.string.generic_service_error));
        }else{
            DialogUtil.toast(this, getString(R.string.generic_service_error_title), "",
                    getString(R.string.generic_service_error), CLOSE_ACTIVITY_REQUEST);
        }
    }

    public void handleInvalidSessionError() {
        DialogUtil.invalidSessionToast(this);
    }

    void processErrorAndContinue(ErrorMessages error, String additionalParam) {
        if (error != null) {
            switch (error) {
                case LOGIN_ERROR:
                    DialogUtil.toast(this,
                            getString(R.string.login_error_message_title),
                            "", getString(R.string.login_error_message_text));
                    break;
                case WEB_PASSWORD_ERROR:
                    DialogUtil.toastWithCallButton(this,
                            getString(R.string.login_web_password_error_message_title),
                            getString(R.string.login_web_password_error_message_subtitle),
                            getString(R.string.login_web_password_error_message_text),
                            getString(R.string.login_web_password_phone));
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
                case OTP_VALIDATION_NEEDED:
                    showOtpValidationDialog();
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
        if (step != null) {
            switch (step) {
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
                case CATEGORIES_OF_INTEREST:
                    Intent interestActivity = new Intent(this, InterestsPickerActivity.class);
                    interestActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(interestActivity);
                    break;
                case GET_E_CARD:
                    Intent getEcardActivity = new Intent(this, EcardLoginActivity.class);
                    getEcardActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(getEcardActivity);
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

    public void requestPermission(String strPermission, int perCode) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, strPermission)) {
            ActivityCompat.requestPermissions(this, new String[]{strPermission}, perCode);
        }
    }

    public void showHelpDialog(String title, String subtitle, String text) {
        Intent intent = new Intent(this, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, title);
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ToastDialogActivity.TEXT_KEY, text);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    public void showHelpDialog(String subtitle, String text) {
        Intent intent = new Intent(this, ToastDialogActivity.class);
        intent.putExtra(ToastDialogActivity.TITLE_KEY, getString(R.string.help_dialog_generic_title));
        intent.putExtra(ToastDialogActivity.SUBTITLE_KEY, subtitle);
        intent.putExtra(ToastDialogActivity.TEXT_KEY, text);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    @Override
    protected void onPause() {
        if (loadingDialog != null) {
            hideLoading();
        }
        super.onPause();
    }

    protected void hideTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    protected void setNavigationDrawer(int drawerLayoutId, int toolbarId, boolean homeButton) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        if (toolbar == null) {
            return;
        }
        setNavigationDrawer(drawerLayoutId, toolbar, homeButton);
    }

    protected void setNavigationDrawer(int drawerLayoutId, Toolbar toolbar, boolean homeButton) {
        if (getSupportActionBar() == null || toolbar == null) {
            return;
        }
        if (homeButton) {
            drawerLayout = (DrawerLayout) findViewById(drawerLayoutId);
            if (drawerLayout == null) {
                return;
            }
            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                    R.string.drawer_open, R.string.drawer_close) {

                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }
            };
            drawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.navigation_drawer_icon);
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        updateUserData();
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            setButtonListeners();
            updateUserData();
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    protected void updateUserData() {
        User user = SharedPreferencesUtils.getUser();
        ((TextView) findViewById(R.id.drawer_last_login)).setText(user.getLastLogin());
        ((TextView) findViewById(R.id.drawer_davi_points_amount)).setText(String.format("%s", user.getPoints()));
        ((TextView) findViewById(R.id.drawer_name)).setText(user.getName());
    }

    private void setButtonListeners() {
        View home = findViewById(R.id.home_shortcut);
        View logout = findViewById(R.id.logout_shortcut);
        View myCards = findViewById(R.id.my_cards_shortcut);
        View myShops = findViewById(R.id.my_shopping_shortcut);
        View help = findViewById(R.id.help_shortcut);
        View myAddresses = findViewById(R.id.my_addresses_shortcut);
        View preferences = findViewById(R.id.preferences_shortcut);
        if (home == null || logout == null || myCards == null || help == null || myShops == null || myAddresses == null
                || preferences == null) {
            return;
        }
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insecureDevice();
            }
        });
        myAddresses.setOnClickListener(null);
        preferences.setOnClickListener(null);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
                closeDrawer();
            }
        });
        myCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseActivity.this, MyCardsActivity.class));
                closeDrawer();
            }
        });
        myShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivity.this, MyShopsActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ActionDialogActivity.class);
                intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.logout_dialog_title));
                intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.logout_dialog_subtitle));
                intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.logout_dialog_text));
                intent.putExtra(ActionDialogActivity.LOGOUT_REQUEST, true);
                startActivityForResult(intent, LOGOUT_REQUEST);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
            }
        });
    }

    protected void goToHome() {
        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        startActivity(intent.putExtras(bundle));
    }

    private void closeDrawer() {
        if (drawerLayout == null) {
            return;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle == null) {
            return;
        }
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle == null) {
            return;
        }
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle == null) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    protected class BaseLogoutTask extends AsyncTask<Void, Void, Boolean>{
        String errorCode;

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
                response = Service.logout(sid);
            } catch (ServiceException e) {
                errorCode = e.getErrorCode();
            }
            return response;
        }
    }

    public class LogoutTask extends BaseLogoutTask {

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            if (response == null && errorCode != null) {
                //Expected error.
                ErrorMessages error = ErrorMessages.getError(errorCode);
                processErrorAndContinue(error, "");
            } else if (response == null) {
                //Service error.
                showServiceGenericError();
            } else {
                //Success login.
                goLoginActivity();
            }
            hideLoading();
        }
    }

    public void goLoginActivity() {
        Session.getCurrentSession(this).logout();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    public String getTodo1Data() {
        Todo1Utils.initMobileSdk(this);
        return Todo1Utils.getData(this);
    }

    public void showOtpValidationDialog() {
        Intent intent = new Intent(this, ActionDialogActivity.class);
        intent.putExtra(ActionDialogActivity.OTP_VALIDATION_DIALOG, true);
        intent.putExtra(ActionDialogActivity.TITLE_KEY, getString(R.string.otp_needed_title));
        intent.putExtra(ActionDialogActivity.SUBTITLE_KEY, getString(R.string.otp_needed_subtitle));
        intent.putExtra(ActionDialogActivity.TEXT_KEY, getString(R.string.otp_needed_text));
        startActivityForResult(intent, OTP_NEEDED);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OTP_NEEDED) {
            if (resultCode == RESULT_OK && data != null) {
                String otpCode = data.getStringExtra(ActionDialogActivity.OTP_VALIDATION_DIALOG_TEXT_KEY);
                if(otpCodeReceived != null) {
                    otpCodeReceived.onOtpCodeReceived(otpCode);
                }
            } else if (resultCode == ActionDialogActivity.RESULT_CANCELED) {
                DialogUtil.toast(this,
                        getString(R.string.otp_needed_canceled_title),
                        getString(R.string.otp_needed_canceled_subtitle),
                        getString(R.string.otp_needed_canceled_text));
            } else {
                showServiceGenericError();
            }
        }
        if(requestCode == LOGOUT_REQUEST && resultCode == RESULT_OK){
            new LogoutTask().execute();
        }
    }

    public interface OtpCodeReceived {
        void onOtpCodeReceived(String otpCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //Todo1Utils.destroyMobileSdk(this);
        hideLoading();
        super.onDestroy();
    }

    public void insecureDevice(){
        Intent intent = new Intent(BaseActivity.this, InsecureDeviceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void showEcardCreatedPopup(Card card) {
        Intent intent = new Intent(this, NewEcardDialogActivity.class);
        intent.putExtra(NewEcardDialogActivity.CARD_KEY, card);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
        startActivityForResult(intent, SHOW_CREATED_ECARD_POPUP);
    }

    public void setOtpCodeReceived(OtpCodeReceived otpCodeReceived) {
        this.otpCodeReceived = otpCodeReceived;
    }

    @Override
    public void onBackPressed() {
        if (isMenuOpened()) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public Boolean isMenuOpened() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            return true;
        } else {
            return false;
        }
    }

    protected interface IComboListener{
        void onAccept();
        void setSelectedItem();
    }

    public void showCombo(BaseAdapter adapter, final IComboListener listener) {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setContentHolder(new ComboHolder())
                .setFooter(R.layout.combo_footer)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();
        View footerView = dialog.getFooterView();
        footerView.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAccept();
                dialog.dismiss();
            }
        });
        footerView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        listener.setSelectedItem();
        dialog.show();
        dialogPlus = dialog;
    }

}
