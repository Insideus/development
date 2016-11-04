package ar.com.fennoma.davipocket.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.Store;
import ar.com.fennoma.davipocket.model.StoreCategory;
import ar.com.fennoma.davipocket.model.StoreProduct;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.CategoryAdapter;
import ar.com.fennoma.davipocket.utils.DialogUtil;
import ar.com.fennoma.davipocket.utils.ImageUtils;
import ar.com.fennoma.davipocket.utils.LocationUtils;

public class StoreDetailActivity extends BaseActivity {

    private static final int CANCEL_PURCHASE_REQUEST = 10;

    public static final String STORE_KEY = "store_key";
    public static final String CART_KEY = "cart_key";

    public static final int ADD_ITEM_TO_CART = 190;
    private static final int CALL_PHONE_PERMISSION_REQUEST = 101;

    private Store store;
    private CategoryAdapter adapter;
    private Cart cart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_detail_layout);
        handleIntent();
        setToolbar(R.id.toolbar, true, store.getName().toUpperCase());
        setLayout();
        setRecyclerView();
        setStoreButtons();
    }

    private void setStoreButtons() {
        View callButton = findViewById(R.id.call_button);
        View locationButton = findViewById(R.id.location_button);
        if (callButton == null || locationButton == null) {
            return;
        }
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(store.getPhone())){
                    DialogUtil.toast(StoreDetailActivity.this, getString(R.string.input_data_error_generic_title),
                            "", getString(R.string.store_detail_no_phone_number_error));
                    return;
                }
                if (!checkPermission(android.Manifest.permission.CALL_PHONE, getApplicationContext())) {
                    requestPermission(android.Manifest.permission.CALL_PHONE, CALL_PHONE_PERMISSION_REQUEST);
                }else {
                    makeCall();
                }
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(store.getLatitude() == null || store.getLongitude() == null){
                    DialogUtil.toast(StoreDetailActivity.this, getString(R.string.input_data_error_generic_title),
                            "", getString(R.string.store_detail_no_location_error));
                    return;
                }
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format("%s%s%s%s%s%s%s",
                        "geo:0,0?q=",
                        String.valueOf(store.getLatitude()),
                        ",",
                        String.valueOf(store.getLongitude()),
                        "(",
                        store.getName(),
                        ")")));
                startActivity(intent);
            }
        });
    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + store.getPhone()));
        if (ActivityCompat.checkSelfPermission(StoreDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
    }

    private void setLayout() {
        ImageView storeImage = (ImageView) findViewById(R.id.image);
        if(store.getImage() != null && store.getImage().length() > 0) {
            ImageUtils.loadImageFullURL(storeImage, store.getImage());
        }
        ImageView storeLogo = (ImageView) findViewById(R.id.brand_logo);
        if(store.getLogo() != null && store.getLogo().length() > 0) {
            ImageUtils.loadImageFullURL(storeLogo, store.getLogo());
        }
        TextView storeName = (TextView) findViewById(R.id.name);
        storeName.setText(store.getName());
        TextView storeAddress = (TextView) findViewById(R.id.address);
        storeName.setText(store.getName());
        if(TextUtils.isEmpty(store.getAddress())) {
            storeAddress.setVisibility(View.GONE);
        } else {
            storeAddress.setVisibility(View.VISIBLE);
            storeAddress.setText(store.getAddress());
        }
        TextView storeDistance = (TextView) findViewById(R.id.distance);
        storeDistance.setText(LocationUtils.calculateDistance(this, LocationUtils.getLastKnowLocation(this),
                store.getLatitude(), store.getLongitude()));
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if(recyclerView == null){
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void handleIntent() {
        if(getIntent() == null || getIntent().getParcelableExtra(STORE_KEY) == null){
            return;
        }
        store = getIntent().getParcelableExtra(STORE_KEY);
        if(cart == null) {
            cart = new Cart();
            cart.setStore(store);
        }
        new GetCategoriesByStore(store.getId()).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CART_KEY, cart);
        outState.putParcelable(STORE_KEY, store);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(CART_KEY, cart);
        outState.putParcelable(STORE_KEY, store);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        store = savedInstanceState.getParcelable(STORE_KEY);
        cart = savedInstanceState.getParcelable(CART_KEY);
    }

    private class GetCategoriesByStore extends AsyncTask<Void, Void, Void> {

        private String id;
        private List<StoreCategory> categories;

        public GetCategoriesByStore(long id) {
            this.id = String.valueOf(id);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String sid = Session.getCurrentSession(getApplicationContext()).getSid();
            try {
                categories = Service.getStoreById(sid, id);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideLoading();
            if(categories == null){
                return;
            }
            adapter.setCategories(categories);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CALL_PHONE_PERMISSION_REQUEST && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makeCall();
        }
    }

    @Override
    public void onBackPressed() {
        if(cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            super.onBackPressed();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(ActionDialogActivity.CANCEL_PURCHASE, true);
        bundle.putString(ActionDialogActivity.TITLE_KEY, getString(R.string.configuration_not_added_title));
        bundle.putString(ActionDialogActivity.TEXT_KEY, getString(R.string.store_payment_on_back_text_explanation));
        startActivityForResult(new Intent(this, ActionDialogActivity.class).putExtras(bundle), CANCEL_PURCHASE_REQUEST);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.shop_button){
            if(cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()){
                DialogUtil.toast(this, getString(R.string.generic_service_error_title), "",
                        getString(R.string.store_detail_no_items_selected));
                return true;
            }
            Intent intent = new Intent(this, StorePaymentActivity.class);
            intent.putExtra(StorePaymentActivity.CART_KEY, cart);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_ITEM_TO_CART && resultCode == RESULT_OK) {
            if(data != null) {
                if(cart == null || cart.getProducts() == null) {
                    showServiceGenericError();
                } else {
                    StoreProduct product = data.getParcelableExtra(StoreItemDetailActivity.PRODUCT_KEY);
                    cart.getProducts().add(product);
                    cart.calculateCartPrice();
                }
            } else {
                showServiceGenericError();
            }
        }
        if(requestCode == CANCEL_PURCHASE_REQUEST && resultCode == RESULT_OK){
            finish();
        }
    }
}
