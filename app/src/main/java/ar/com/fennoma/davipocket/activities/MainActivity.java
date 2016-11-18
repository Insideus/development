package ar.com.fennoma.davipocket.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.fragments.WithoutDeliveryStoreFragment;
import ar.com.fennoma.davipocket.model.Card;
import ar.com.fennoma.davipocket.utils.LocationUtils;
import ar.com.fennoma.davipocket.utils.SharedPreferencesUtils;

public class MainActivity extends BaseActivity {

    public static final String OPEN_TOUR = "tour open";
    public static final String SHOULD_RECREATE_KEY = "should_recreate_key";
    public static int LOCATION_PERMISSION_CODE = 169;

    private WithoutDeliveryStoreFragment fragment;
    private LatLng latLng;
    private LocationUtils locationUtils;

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
        checkForTour();
        fragment.setLocation(latLng);
    }

    private void findFragment() {
        fragment = (WithoutDeliveryStoreFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    public void checkLocationPermissions() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getApplicationContext())) {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
        } else {
            getLocation();
        }
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

    private void getLocation() {
        showLoading();
        locationUtils = new LocationUtils(this, new LocationUtils.ILocationListener() {
            @Override
            public void onGotLocation(Location location) {
                if(location == null){
                    failedGettingLocation();
                    return;
                }
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                fragment.setLocation(latLng);
                hideLoading();
            }

            @Override
            public void failedGettingLocation() {
                fragment.setLocation(null);
                hideLoading();
            }
        });
        locationUtils.locUpdate(2000, 1);
    }

    private void checkForTour() {
        if (getIntent() != null && getIntent().getBooleanExtra(OPEN_TOUR, false)) {
            startTour();
            return;
        }
        if (TextUtils.isEmpty(SharedPreferencesUtils.getString(OPEN_TOUR))) {
            startTour();
            SharedPreferencesUtils.setString(OPEN_TOUR, SharedPreferencesUtils.FALSE);
        }
    }

    private void startTour() {
        startActivity(new Intent(this, TourActivity.class));
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
    protected void goToHome() {
        //startTour();
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
            startActivity(new Intent(this, OtpPaymentActivity.class));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                hideLoading();
                fragment.setLocation(null);
            }
        }
    }

    @Override
    protected void onPause() {
        locationUtils.cancelListening();
        super.onPause();
    }

}
