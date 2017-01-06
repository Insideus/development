package com.davivienda.billetera.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.interfaces.OnLocationUpdate;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.ui.adapters.OtpStoreListAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class OtpStoreListActivity extends BaseActivity implements OnLocationUpdate {

    private OtpStoreListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_store_list_layout);
        setToolbar(R.id.toolbar, true, getString(R.string.main_activity_title));
        handleIntent();
        locationListener = this;
        checkLocationPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDaviPoints();
    }

    private void handleIntent() {
        if(getIntent() == null){
            return;
        }
        setViews();
    }

    private void setViews() {

    }

    private void setRecycler() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OtpStoreListAdapter(this, latLng);
        recycler.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.otp_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.otp_map){
            goToMap();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToMap() {
        startActivity(new Intent(this, MapActivity.class)
                .putExtra(MapActivity.LAT_LNG_KEY, latLng)
                .putExtra(MapActivity.FROM_OTP_KEY, true));
    }

    private class GetStoresTask extends DaviPayTask<List<Store>> {

        GetStoresTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected List<Store> doInBackground(Void... params) {
            List<Store> stores = null;
            if(activity != null) {
                String sid = Session.getCurrentSession(getApplicationContext()).getSid();
                try {
                    stores = Service.getStoresOtp(sid,
                            latLng != null ? String.valueOf(latLng.latitude) : "",
                            latLng != null ? String.valueOf(latLng.longitude) : "");
                } catch (ServiceException e) {
                    e.printStackTrace();
                    errorCode = e.getErrorCode();
                }
            } else {
                this.cancel(true);
            }
            return stores;
        }

        @Override
        protected void onPostExecute(List<Store> stores) {
            super.onPostExecute(stores);
            hideLoading();
            if(!processedError) {
                adapter.setStores(stores);
            }
        }

    }

    @Override
    public void onGotLocation(Location location) {
        hideLoading();
        if(location == null){
            failedGettingLocation();
            return;
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setRecycler();
        new GetStoresTask(OtpStoreListActivity.this).execute();
    }

    @Override
    public void failedGettingLocation() {
        hideLoading();
        setRecycler();
        new GetStoresTask(OtpStoreListActivity.this).execute();
    }

}
