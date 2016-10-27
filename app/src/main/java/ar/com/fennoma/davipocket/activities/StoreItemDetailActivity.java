package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreConfiguration;
import ar.com.fennoma.davipocket.model.StoreConfigurationItem;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.StoreItemDetailAdapter;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DavipointUtils;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class StoreItemDetailActivity extends BaseActivity{

    public static final String PRODUCT_KEY = "product_key";
    private StoreProduct product;
    private StoreProduct selectedProduct;
    private TextView daviPointsAmount;
    private TextView amount;
    private Double currentAmount;
    private Integer currentDaviPointAmount;

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
        selectedProduct = product.createEmptyProduct();
        setViews();
        setRecycler();
    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if(recyclerView == null){
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new StoreItemDetailAdapter(this, product));
    }

    private void setViews() {
        ImageView productImage = (ImageView) findViewById(R.id.image);
        if(product.getImage() != null && product.getImage().length() > 0) {
            ImageUtils.loadImageFullURL(productImage, product.getImage());
        }
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(product.getName());
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(product.getAppDisplayName());
        amount = (TextView) findViewById(R.id.product_amount);
        daviPointsAmount = (TextView) findViewById(R.id.davi_points_amount);
        setProductAmount();
    }

    private void setProductAmount() {
        currentAmount = selectedProduct.getListPrice();
        if(currentAmount == null) {
            currentAmount = 0d;
        }
        for(StoreConfiguration config : selectedProduct.getConfigurations()) {
            for(StoreConfigurationItem item : config.getConfigurations()) {
                currentAmount += item.getExtraPrice();
            }
        }
        currentDaviPointAmount = currentAmount.intValue() / DavipointUtils.getDavipointsEquivalence();
        daviPointsAmount.setText(String.valueOf(currentDaviPointAmount));
        amount.setText(CurrencyUtils.getCurrencyForString(String.valueOf(currentAmount)));
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
        getMenuInflater().inflate(R.menu.add_item_menu, menu);
        return true;
    }

}
