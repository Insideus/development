package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.StoreItemDetailAdapter;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class StoreItemDetailActivity extends BaseActivity{

    public static final String PRODUCT_KEY = "product_key";
    private StoreItemDetailAdapter adapter;
    private StoreProduct product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_item_detail_layout);
        handleIntent();
        setToolbar(R.id.toolbar, true, product.getName());
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
        ImageView productImage = (ImageView) findViewById(R.id.image);
        if(product.getImage() != null && product.getImage().length() > 0) {
            ImageUtils.loadImageFullURL(productImage, product.getImage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.shop_button){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        return true;
    }

}
