package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import ar.com.fennoma.davipocket.R;

public class HelpDialogActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_dialog_activity_layout);
        setLayouts();
    }

    private void setLayouts() {
        View container = findViewById(R.id.container);
        if(container == null){
            return;
        }
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
