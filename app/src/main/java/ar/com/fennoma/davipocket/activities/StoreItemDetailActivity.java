package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreConfiguration;
import ar.com.fennoma.davipocket.model.StoreConfigurationItem;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.StoreItemDetailAdapter;
import ar.com.fennoma.davipocket.utils.CurrencyUtils;
import ar.com.fennoma.davipocket.utils.DavipointUtils;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.ImageUtils;

public class StoreItemDetailActivity extends BaseActivity {

    public static final String PRODUCT_KEY = "product_key";
    public static final String ACCEPT_DAVIPOINTS_KEY = "accept_davipoints_key";

    private StoreProduct product;
    private StoreProduct selectedProduct;
    private TextView daviPointsAmount;
    private TextView amount;
    private Double currentAmount;
    private Integer currentDaviPointAmount;
    private Boolean acceptDavipoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_item_detail_layout);
        handleIntent();
        setToolbar(R.id.toolbar, true, product.getName().toUpperCase());
        setViews();
        setRecycler();
    }

    private void handleIntent() {
        if(getIntent() == null || getIntent().getParcelableExtra(PRODUCT_KEY) == null){
            return;
        }
        acceptDavipoints = getIntent().getBooleanExtra(ACCEPT_DAVIPOINTS_KEY, false);
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

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
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
        daviPointsAmount = (TextView) findViewById(R.id.store_davi_points_amount);
        setProductAmount();
        if(!acceptDavipoints) {
            daviPointsAmount.setVisibility(View.GONE);
        }
    }

    public void setProductAmount() {
        currentAmount = selectedProduct.getSelectedProductPrice();
        currentDaviPointAmount = currentAmount.intValue() / DavipointUtils.getDavipointsEquivalence();
        daviPointsAmount.setText(String.valueOf(currentDaviPointAmount));
        amount.setText(CurrencyUtils.getCurrencyForString(currentAmount));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_button){
            validateProductAndAddToCart();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_item_menu, menu);
        return true;
    }

    public boolean addConfiguration(StoreConfiguration selectedConfig, StoreConfigurationItem selectedItem) {
        int i = 0;
        for(StoreConfiguration config : selectedProduct.getConfigurations()) {
            if(config.equals(selectedConfig)) {
                if(config.getMaxConfiguration() == 1 && config.getMinConfiguration() ==1) {
                    selectedProduct.getConfigurations().get(i).getConfigurations().clear();
                    selectedProduct.getConfigurations().get(i).getConfigurations().add(selectedItem);
                    return true;
                } else if(config.getConfigurations().size() < config.getMaxConfiguration()) {
                    selectedProduct.getConfigurations().get(i).getConfigurations().add(selectedItem);
                    return true;
                } else {
                    return false;
                }
            }
            i++;
        }
        return false;
    }

    public boolean removeConfiguration(StoreConfiguration selectedConfig, StoreConfigurationItem selectedItem) {
        for(StoreConfiguration config : selectedProduct.getConfigurations()) {
            if(config.equals(selectedConfig)) {
                int i = 0;
                for(StoreConfigurationItem item : config.getConfigurations()) {
                    if (item.equals(selectedItem)) {
                        config.getConfigurations().remove(i);
                        return true;
                    }
                    i++;
                }
            }
        }
        return false;
    }

    public boolean isConfigurationAdded(StoreConfiguration configuration, StoreConfigurationItem item) {
        StoreConfiguration config = getSelectedProductConfiguration(configuration);
        if(config != null) {
            for (StoreConfigurationItem itemIter : config.getConfigurations()) {
                if (itemIter.getId() == item.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public StoreProduct getSelectedProduct() {
        return selectedProduct;
    }

    public StoreConfiguration getSelectedProductConfiguration(StoreConfiguration configuration) {
        for(StoreConfiguration config : selectedProduct.getConfigurations()) {
            if(config.equals(configuration)) {
                return config;
            }
        }
        return null;
    }

    public void showConfigurationNotAdded(StoreConfiguration configuration) {
        Resources res = getResources();
        String text = res.getQuantityString(R.plurals.configuration_not_added_text, configuration.getMaxConfiguration(), configuration.getMaxConfiguration());
        DialogUtil.toast(this,
                getString(R.string.configuration_not_added_title),
                "",
                text);
    }

    public void validateProductAndAddToCart() {
        ArrayList<String> errors = isValid();
        if(errors.size() > 0) {
            DialogUtil.toast(this,
                    getString(R.string.configuration_not_added_title),
                    "",
                    DialogUtil.concatMessages(errors));
        } else {
            setResultAndFinish();
        }
    }

    private void setResultAndFinish() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PRODUCT_KEY, selectedProduct);
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        finish();
    }

    private ArrayList<String> isValid() {
        ArrayList<String> errors = new ArrayList<>();
        Resources res = getResources();
        for(StoreConfiguration configuration : selectedProduct.getConfigurations()) {
            if(configuration.getConfigurations().size() < configuration.getMinConfiguration()) {
                String text = res.getQuantityString(R.plurals.configuration_error_text, configuration.getMinConfiguration(), configuration.getMinConfiguration(), configuration.getSubType());
                errors.add(text);
            }
        }
        return errors;
    }

}
