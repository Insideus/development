package ar.com.fennoma.davipocket.activities;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.StoreItemDetailAdapter;
import ar.com.fennoma.davipocket.ui.controls.RoundedCornerImageView;

public class StoreItemDetailActivity extends BaseActivity{

    public static final String PRODUCT_KEY = "product key";
    private StoreItemDetailAdapter adapter;
    private StoreProduct product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_item_detail_layout);
        handleIntent();
        setViews();
        setRecycler();
    }

    private void handleIntent() {
        if(getIntent() == null || getIntent().getParcelableExtra(PRODUCT_KEY) == null){
            return;
        }
        product = getIntent().getParcelableExtra(PRODUCT_KEY);
        setViews();
        setRecycler();
    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if(recyclerView == null){
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StoreItemDetailAdapter(this, product);
        recyclerView.setAdapter(adapter);
    }

    private void setViews() {
        RoundedCornerImageView imageView = (RoundedCornerImageView) findViewById(R.id.image);
        if (imageView == null) {
            return;
        }
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mocked_store_item_image));
    }
}
