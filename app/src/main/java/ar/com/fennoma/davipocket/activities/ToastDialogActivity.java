package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

public class ToastDialogActivity extends BaseActivity {

    public static final String SHOW_RETRY_CONNECTION_BUTTON = "retry connection button";
    public static final String TITLE_KEY = "toast_title_key";
    public static final String SUBTITLE_KEY = "toast_subtitle_key";
    public static final String TEXT_KEY = "toast_text_key";
    public static final String INVALID_SESSION_KEY = "invalid_session_key";
    public static final String SHOW_CALL_BUTTON_KEY = "show_call_button_key";
    public static final String CALL_BUTTON_NUMBER_KEY = "call_button_number_key";

    private String title;
    private String subtitle;
    private String text;
    private Boolean invalidSessionToast;
    private Boolean showCallButton;
    private Boolean showRetryButton;
    private String callNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_dialog_activity_layout);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TITLE_KEY, "");
            subtitle = savedInstanceState.getString(SUBTITLE_KEY, "");
            text = savedInstanceState.getString(TEXT_KEY, "");
            invalidSessionToast = savedInstanceState.getBoolean(INVALID_SESSION_KEY, false);
            showCallButton = savedInstanceState.getBoolean(SHOW_CALL_BUTTON_KEY, false);
            showRetryButton = savedInstanceState.getBoolean(SHOW_RETRY_CONNECTION_BUTTON, false);
            callNumber = savedInstanceState.getString(CALL_BUTTON_NUMBER_KEY, "");
        } else {
            title = getIntent().getStringExtra(TITLE_KEY);
            subtitle = getIntent().getStringExtra(SUBTITLE_KEY);
            text = getIntent().getStringExtra(TEXT_KEY);
            invalidSessionToast = getIntent().getBooleanExtra(INVALID_SESSION_KEY, false);
            showCallButton = getIntent().getBooleanExtra(SHOW_CALL_BUTTON_KEY, false);
            callNumber = getIntent().getStringExtra(CALL_BUTTON_NUMBER_KEY);
            showRetryButton = getIntent().getBooleanExtra(SHOW_RETRY_CONNECTION_BUTTON, false);
        }
        setLayouts();
        animateOpening();
    }

    private void animateOpening() {
        View container = findViewById(R.id.container);
        if(container == null){
            return;
        }
        container.startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_dot_to_full_size));
    }

    private void setLayouts() {
        View cancelButton = findViewById(R.id.close_button);
        View container = findViewById(R.id.outside);
        if(container == null || cancelButton == null){
            return;
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(invalidSessionToast) {
                    goToLoginActivity();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(invalidSessionToast) {
                    goToLoginActivity();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        TextView button = (TextView) findViewById(R.id.button);
        if(showCallButton) {
            setCallButtonLayouts(button);
        }

        if(showRetryButton){
            setRetryLayouts(button);
        }

        TextView titleTv = (TextView) findViewById(R.id.toast_title);
        if(title != null && title.length() > 0) {
            titleTv.setText(title);
        } else {
            titleTv.setVisibility(LinearLayout.GONE);
        }
        TextView subtitleTv = (TextView) findViewById(R.id.toast_subtitle);
        if(subtitle != null && subtitle.length() > 0) {
            subtitleTv.setText(subtitle);
        } else {
            subtitleTv.setVisibility(LinearLayout.GONE);
        }
        TextView textTv = (TextView) findViewById(R.id.toast_text);
        if(text != null && text.length() > 0) {
            textTv.setText(text);
        } else {
            textTv.setVisibility(LinearLayout.GONE);
        }
    }

    private void setRetryLayouts(TextView retryButton) {
        retryButton.setText(getString(R.string.no_connection_dialog_retry_button));
        retryButton.setVisibility(LinearLayout.VISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void setCallButtonLayouts(TextView callButton) {
        checkCallPermissions();
        callButton.setText(getString(R.string.call_button_text));
        callButton.setVisibility(LinearLayout.VISIBLE);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ToastDialogActivity.this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + callNumber)));
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public void checkCallPermissions() {
        if (!checkPermission(android.Manifest.permission.CALL_PHONE, getApplicationContext(), this)) {
            requestPermission(android.Manifest.permission.CALL_PHONE, 101, getApplicationContext(), this);
        }
    }

    private void goToLoginActivity() {
        Intent facebookIntent = new Intent(this, LoginActivity.class);
        facebookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(facebookIntent);
    }


}
