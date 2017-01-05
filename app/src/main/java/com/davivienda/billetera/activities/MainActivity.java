package com.davivienda.billetera.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.davivienda.billetera.R;
import com.davivienda.billetera.fragments.WithoutDeliveryStoreFragment;
import com.davivienda.billetera.model.Card;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends BaseActivity implements BaseActivity.OnLocationUpdate {

    public static final String SHOULD_RECREATE_KEY = "should_recreate_key";

    private WithoutDeliveryStoreFragment fragment;

    private boolean shouldRecreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null && getIntent().getParcelableExtra(FIRST_LOGIN_WITH_E_CARD) != null){
            handleIntent();
            shouldRecreate = true;
            return;
        }
        createScreen();
        // Start IntentService to register this application with GCM.
        startGcmService();
        locationListener = this;
    }

    private void handleIntent() {
        Card eCard = getIntent().getParcelableExtra(FIRST_LOGIN_WITH_E_CARD);
        startActivity(new Intent(this, ECardRechargeActivity.class).putExtra(FIRST_LOGIN_WITH_E_CARD, eCard));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(SHOULD_RECREATE_KEY, shouldRecreate);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOULD_RECREATE_KEY, shouldRecreate);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        shouldRecreate = savedInstanceState.getBoolean(SHOULD_RECREATE_KEY, true);
    }

    private void createScreen(){
        setContentView(R.layout.activity_main);
        findFragment();
        checkLocationPermissions();
        setToolbar(R.id.toolbar, false, getString(R.string.main_activity_title));
    }

    private void findFragment() {
        fragment = (WithoutDeliveryStoreFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldRecreate){
            createScreen();
            shouldRecreate = false;
        }
        updateDaviPoints();
    }

    @Override
    public void onBackPressed() {
        if(!isMenuOpened()) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.main_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(fragment != null) {
                    if (!TextUtils.isEmpty(newText)) {
                        fragment.doQuery(newText);
                    }else{
                        fragment.showAllResults();
                    }
                }
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setItemsVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, searchItem, true);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.otp_pay) {
            startActivity(new Intent(this, OtpStoreListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    @Override
    protected void onPause() {
        hideLoading();
        super.onPause();
    }

    @Override
    public void onGotLocation(Location location) {
        hideLoading();
        if(location == null){
            failedGettingLocation();
            return;
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        fragment.setLocation(latLng);
    }

    @Override
    public void failedGettingLocation() {
        fragment.setLocation(null);
        hideLoading();
    }

}
