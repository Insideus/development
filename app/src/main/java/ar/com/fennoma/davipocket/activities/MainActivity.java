package ar.com.fennoma.davipocket.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.fragments.WithoutDeliveryStoreFragment;
import ar.com.fennoma.davipocket.utils.LocationUtils;
import ar.com.fennoma.davipocket.utils.SharedPreferencesUtils;

public class MainActivity extends BaseActivity {

    public static final String OPEN_TOUR = "tour open";
    public static int LOCATION_PERMISSION_CODE = 169;

    private WithoutDeliveryStoreFragment fragment;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findFragment();
        checkLocationPermissions();
        setToolbar();
        checkForTour();
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
        updateDaviPoints();
        fragment.setLocation(latLng);
    }

    private void getLocation() {
        showLoading();
        new LocationUtils(this, new LocationUtils.ILocationListener() {
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
                hideLoading();
            }
        }).locUpdate(2000, 1);
    }

    private void setToolbar(){
        View toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) toolbar);
        hideTitle();
        setNavigationDrawer(R.id.drawer_layout, R.id.toolbar, true);
        ImageView logo = (ImageView) toolbar.findViewById(R.id.toolbar_logo);
        if(logo == null){
            return;
        }
        logo.setImageResource(R.drawable.home_toolbar_logo);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
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

}
