package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.CategoryItemAdapter;

public class StorePaymentActivity extends BaseActivity {

    private List<StoreProduct> selectedProducts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_payment_activity);
        hardcodeProducts();
        setRecycler();
    }

    private void setRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if(recyclerView == null){
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new CategoryItemAdapter(this, selectedProducts, false));
    }

    private void hardcodeProducts() {
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
