package ar.com.fennoma.davipocket.activities;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.ui.controls.RoundedCornerImageView;

public class StoreItemDetailActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_item_detail_layout);
        setViews();
    }

    private void setViews() {
        RoundedCornerImageView imageView = (RoundedCornerImageView) findViewById(R.id.image);
        if (imageView == null) {
            return;
        }
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mocked_store_item_image));
    }
}
