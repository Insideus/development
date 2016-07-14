package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;

public class ToastDialogActivity extends BaseActivity{

    public static String TITLE_KEY = "toast_title_key";
    public static String SUBTITLE_KEY = "toast_subtitle_key";
    public static String TEXT_KEY = "toast_text_key";

    private String title;
    private String subtitle;
    private String text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_dialog_activity_layout);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TITLE_KEY, "");
            subtitle = savedInstanceState.getString(SUBTITLE_KEY, "");
            text = savedInstanceState.getString(TEXT_KEY, "");
        } else {
            title = getIntent().getStringExtra(TITLE_KEY);
            subtitle = getIntent().getStringExtra(SUBTITLE_KEY);
            text = getIntent().getStringExtra(TEXT_KEY);
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
                finish();
            }
        });
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
}
