package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.CategoryItemAdapter;

public abstract class AbstractBoughtItemsActivity extends BaseActivity {

    private ArrayList<StoreProduct> selectedProducts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bought_items_layout);
        hardcodeData();
        setRecycler();
    }

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(new CategoryItemAdapter(this, selectedProducts, false));
    }

    private void hardcodeData() {
        selectedProducts = new ArrayList<>();
        StoreProduct product;
        for(int i = 0; i < 3; i ++){
            product = new StoreProduct();
            product.setName("Coffee cup");
            product.setListPrice(6300d);
            product.setImage("https://middleware-paymentez.s3.amazonaws.com/fca455ef6b32ad512033367e0d52e951.jpeg");
            selectedProducts.add(product);
        }
    }
}
