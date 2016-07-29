package ar.com.fennoma.davipocket.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

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

    protected void setToolbar(int toolbarId, boolean backButton) {
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        hideTitle();
        setNavigationDrawer(R.id.drawer_layout, toolbarId, !backButton);
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

    protected void hideTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    protected void setNavigationDrawer(int drawerLayoutId, int toolbarId, boolean homeButton) {
        drawerLayout = (DrawerLayout) findViewById(drawerLayoutId);
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        if (drawerLayout == null || getSupportActionBar() == null || toolbar == null) {
            return;
        }
        if (homeButton) {
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
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }else{
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setButtonListeners();
    }

    private void setButtonListeners() {
        View home = findViewById(R.id.home_shortcut);
        View logout = findViewById(R.id.logout_shortcut);
        View myCards = findViewById(R.id.my_cards_shortcut);
        if (home == null || logout == null || myCards == null) {
            return;
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        myCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(BaseActivity.this, MyCardsActivity.class));
                startActivity(new Intent(BaseActivity.this, NewCardActivity.class));
                closeDrawer();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

}
