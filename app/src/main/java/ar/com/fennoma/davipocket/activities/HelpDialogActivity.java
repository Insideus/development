package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AnimationUtils;

import ar.com.fennoma.davipocket.R;

public class HelpDialogActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_dialog_activity_layout);
        animateOpening();
        setLayouts();
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
    }
}
