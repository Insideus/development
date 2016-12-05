package com.davivienda.billetera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.davivienda.billetera.R;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    public final static String LAT_LNG_KEY = "lat lang key";
    private static final float STARTING_ZOOM = 12;
    private LatLng latLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        setToolbar(R.id.toolbar, true);
        handleIntent();
        setMapFragment();
    }

    private void handleIntent() {
        if(getIntent() == null || getIntent().getParcelableExtra(LAT_LNG_KEY) == null){
            return;
        }
        latLng = getIntent().getParcelableExtra(LAT_LNG_KEY);
    }

    private void setMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(latLng == null){
            return;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, STARTING_ZOOM);
        googleMap.moveCamera(cameraUpdate);
    }
}
