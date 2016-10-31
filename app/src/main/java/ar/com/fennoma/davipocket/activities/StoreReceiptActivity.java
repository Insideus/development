package ar.com.fennoma.davipocket.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.ui.adapters.CategoryItemAdapter;
import ar.com.fennoma.davipocket.ui.controls.RatingBar;

public class StoreReceiptActivity extends BaseActivity{

    private ArrayList<StoreProduct> selectedProducts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bought_items_layout);
        setToolbar(R.id.toolbar, false);
        setToolbarWOHomeButton(R.id.toolbar, getString(R.string.store_receipt_title));
        hardcodeData();
        setRecycler();
        //setRaitingBar();
        setButtons();
        scrollUp();
    }

    private void setButtons() {
        View goHomeButton = findViewById(R.id.go_home_button);
        View repeatOrderButton = findViewById(R.id.repeat_order_button);
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });
        repeatOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void scrollUp() {
        ScrollView scroll = (ScrollView) findViewById(R.id.scroll_view);
        scroll.fullScroll(ScrollView.FOCUS_UP);
        scroll.smoothScrollTo(0, 0);
    }

   /*private void setRaitingBar() {
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        ratingBar.setActivity(this).setAmountOfStars(5).setAsClickable().setSelectedTill(1);
    }*/

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

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
    }

    @Override
    public void onBackPressed() {
        finish();
        goToHome();
    }
}
