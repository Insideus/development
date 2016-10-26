package ar.com.fennoma.davipocket.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ar.com.fennoma.davipocket.model.ServiceException;
import ar.com.fennoma.davipocket.model.Store;
import ar.com.fennoma.davipocket.model.StoreCategory;
import ar.com.fennoma.davipocket.service.Service;
import ar.com.fennoma.davipocket.session.Session;
import ar.com.fennoma.davipocket.ui.adapters.CategoryAdapter;
import ar.com.fennoma.davipocket.utils.ImageUtils;
import ar.com.fennoma.davipocket.utils.LocationUtils;

public class StoreDetailActivity extends BaseActivity {

    public static final String STORE_KEY = "store_key";

    private Store store;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_detail_layout);
        handleIntent();
        setToolbar(R.id.toolbar, true, store.getName());
        setLayout();
        setRecyclerView();
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
        //storeName.setText(store.getName());
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
        new GetCategoriesByStore(store.getId()).execute();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.shop_button){
            startActivity(new Intent(this, StorePaymentActivity.class));
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
