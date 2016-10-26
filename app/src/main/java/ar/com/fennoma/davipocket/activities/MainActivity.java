package ar.com.fennoma.davipocket.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.ui.adapters.HomePagerAdapter;
import ar.com.fennoma.davipocket.ui.controls.TypoTabLayout;
import ar.com.fennoma.davipocket.utils.LocationUtils;
import ar.com.fennoma.davipocket.utils.SharedPreferencesUtils;

public class MainActivity extends BaseActivity {

    public static final String OPEN_TOUR = "tour open";
    public static int LOCATION_PERMISSION_CODE = 169;

    private HomePagerAdapter adapter;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForTour();
        setPager();
        checkLocationPermissions();
        setToolbar();
    }

    public void checkLocationPermissions() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getApplicationContext(), this)) {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
        } else {
            getLocation();
        }
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
                adapter.setLocation(latLng);
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

    private void setPager() {
        TypoTabLayout tabLayout = (TypoTabLayout) findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        if(tabLayout == null || pager == null){
            return;
        }
        tabLayout.setPager(pager)
                .setTypo("MyriadPro-Regular.otf")
                .addTitle(getString(R.string.home_store_tab_title))
                .addTitle(getString(R.string.home_domicile_tab_title))
                .build();
        adapter = new HomePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pager.setAdapter(adapter);
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
        startTour();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.maps){
            if(latLng == null){
                Toast.makeText(this, "Locación no encontrada", Toast.LENGTH_SHORT).show();
                return false;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(MapActivity.LAT_LNG_KEY, latLng);
            startActivity(new Intent(this, MapActivity.class).putExtras(bundle));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                hideLoading();
                adapter.setLocation(null);
            }
        }
    }

}
