package com.davivienda.billetera.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.davivienda.billetera.R;
import com.davivienda.billetera.activities.interfaces.OnLocationUpdate;
import com.davivienda.billetera.model.ServiceException;
import com.davivienda.billetera.model.Store;
import com.davivienda.billetera.service.Service;
import com.davivienda.billetera.session.Session;
import com.davivienda.billetera.tasks.DaviPayTask;
import com.davivienda.billetera.ui.controls.MapMarkerInfoShower;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends BaseActivity implements OnMapReadyCallback, OnLocationUpdate {

    public final static String LAT_LNG_KEY = "lat lang key";
    public final static String FROM_HOME_KEY = "from home key";
    public static final String FROM_OTP_KEY = "from otp key";
    private static final float STARTING_ZOOM = 12;
    private LatLng latLng;
    private boolean fromHome = false;
    private boolean fromOtp = false;
    private ArrayList<Store> stores;
    private HashMap<String, Store> allMarkers;
    private GoogleMap googleMap;
    private MapMarkerInfoShower infoShower;
    private View infoShowerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        setToolbar(R.id.toolbar, true, getString(R.string.main_activity_title));
        locationListener = this;
        handleIntent();
        setMapFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LAT_LNG_KEY, latLng);
        outState.putParcelableArrayList(STORE_KEY, stores);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable(LAT_LNG_KEY, latLng);
        outState.putParcelableArrayList(STORE_KEY, stores);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stores = savedInstanceState.getParcelableArrayList(STORE_KEY);
        latLng = savedInstanceState.getParcelable(LAT_LNG_KEY);
        if (stores == null || stores.isEmpty() && latLng != null) {
            new GetStoresTask(this).execute();
        }
    }

    private void handleIntent() {
        if (getIntent() == null) {
            return;
        }
        fromHome = getIntent().getBooleanExtra(FROM_HOME_KEY, false);
        fromOtp = getIntent().getBooleanExtra(FROM_OTP_KEY, false);
        latLng = getIntent().getParcelableExtra(LAT_LNG_KEY);
        setInfoShowerData();

        checkForDefaultWay();
    }

    private void setInfoShowerData() {
        infoShowerView = findViewById(R.id.marker_data);
        infoShower = new MapMarkerInfoShower(this, latLng, infoShowerView, fromHome);
    }

    private void checkForDefaultWay() {
        fromHome = fromHome || !fromOtp;
    }

    private void setMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.list_button) {
            Intent intent;
            if (fromHome) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, OtpStoreListActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                infoShowerView.setVisibility(View.GONE);
            }
        });
        if (latLng != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, STARTING_ZOOM);
            googleMap.moveCamera(cameraUpdate);
            new GetStoresTask(this).execute();
        } else {
            checkLocationPermissions();
        }
    }

    @Override
    public void onGotLocation(Location location) {
        hideLoading();
        showMyLocation();
        if(location == null){
            failedGettingLocation();
            return;
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        infoShower = new MapMarkerInfoShower(this, latLng, infoShowerView, fromHome);
        new GetStoresTask(this).execute();
    }

    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void failedGettingLocation() {
        hideLoading();
        new GetStoresTask(this).execute();
    }

    private class GetStoresTask extends DaviPayTask<ArrayList<Store>> {

        GetStoresTask(BaseActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected ArrayList<Store> doInBackground(Void... voids) {
            String sid = Session.getCurrentSession(getApplicationContext()).getSid();
            try {
                if(fromHome) {
                    stores = Service.getStoresWithoutDelivery(sid,
                            latLng != null ? String.valueOf(latLng.latitude) : "",
                            latLng != null ? String.valueOf(latLng.longitude) : "");
                }else if(fromOtp){
                    stores = Service.getStoresOtp(sid,
                            latLng != null ? String.valueOf(latLng.latitude) : "",
                            latLng != null ? String.valueOf(latLng.longitude) : "");
                }
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return stores;
        }

        @Override
        protected void onPostExecute(ArrayList<Store> stores) {
            super.onPostExecute(stores);
            if(!processedError) {
                loadMarkers(stores);
            }
        }
    }

    private void loadMarkers(ArrayList<Store> stores) {
        this.stores = stores;
        if (stores.size() > 0) {
            allMarkers = new HashMap<>();
            googleMap.clear();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Store store : stores) {
                if (store.getLatitude() != null && store.getLongitude() != null) {
                    int imageResId;
                    if(fromHome) {
                        imageResId = R.drawable.davi_marker;
                    }else{
                        imageResId = R.drawable.otp_code_marker;
                    }
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(imageResId))
                            .position(new LatLng(store.getLatitude(), store.getLongitude()))
                            .title(store.getName()));
                    allMarkers.put(marker.getId(), store);

                    builder.include(marker.getPosition());
                }
            }

            if (allMarkers.size() > 0) {
                BitmapFactory.Options dimensions = new BitmapFactory.Options();
                dimensions.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(getResources(), R.drawable.davi_marker, dimensions);
                int padding = dimensions.outHeight;
                padding += 10;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
                googleMap.animateCamera(cameraUpdate);
            }

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.hideInfoWindow();
                    Store store = allMarkers.get(marker.getId());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(store.getLatitude(),
                            store.getLongitude()), googleMap.getCameraPosition().zoom));
                    infoShower.setStore(store);
                    infoShowerView.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
    }
}
