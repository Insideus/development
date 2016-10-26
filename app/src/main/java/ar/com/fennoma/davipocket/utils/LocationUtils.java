package ar.com.fennoma.davipocket.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Locale;

import ar.com.fennoma.davipocket.R;

public class LocationUtils implements LocationListener {

    private final Activity activity;
    private LocationManager locationManager;
    private int locationCounter;
    private Handler serviceHandler;
    private boolean timerTest;
    private Location location;
    private ILocationListener listener;

    public interface ILocationListener {
        void onGotLocation(Location location);

        void failedGettingLocation();
    }

    public LocationUtils(Activity activity, ILocationListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void onStart() {
        if (!timerTest) {
            timerTest = true;
            serviceHandler = new Handler();
            serviceHandler.postDelayed(new timer(), 1000L);
        }
    }

    public void locUpdate(int minTime, float minDistance) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
        locationCounter = 0;
        onStart();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            listener.onGotLocation(location);
            stopListening();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void stopListening() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        locationCounter = 0;
        if (location == null) {
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation() {
        Location bestLastLocation = getBestLastLocation();
        if (bestLastLocation == null) {
            listener.failedGettingLocation();
            return;
        }
        listener.onGotLocation(bestLastLocation);
    }

    private Location getBestLastLocation() {
        Location gpsLocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpsLocation == null) {
            return networkLocation;
        }
        if (networkLocation == null) {
            return gpsLocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        long currentTime = System.currentTimeMillis();
        long old = currentTime - (currentTime - calendar.getTimeInMillis());
        boolean gpsIsOld = (gpsLocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            return gpsLocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpsLocation.getTime() > networkLocation.getTime()) {
            return gpsLocation;
        } else {
            return networkLocation;
        }
    }

    private Location getLocationByProvider(String provider) {
        Location location = null;
        if (!locationManager.isProviderEnabled(provider)) {
            return null;
        }
        LocationManager locationManager = (LocationManager) activity.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return location;
    }

    class timer implements Runnable {
        public void run() {
            ++locationCounter;
            if (location != null || locationCounter > 8) {
                stopListening();
            } else serviceHandler.postDelayed(this, 1000L);
        }
    }

    public static String calculateDistance(Context ctx, LatLng latLng, Double latitude, Double longitude) {
        if (latitude == null || longitude == null || latLng == null) {
            return ctx.getString(R.string.delivery_adapter_not_available_distance);
        }
        Location locationA = new Location("Location A");
        locationA.setLatitude(latitude);
        locationA.setLatitude(longitude);
        Location locationB = new Location("Location B");
        locationB.setLatitude(latLng.latitude);
        locationB.setLongitude(latLng.longitude);
        double inKms = locationA.distanceTo(locationB) / 1000;
        String finalResult = String.format(Locale.US, "%.2f", inKms).replace(".", ",");
        return finalResult.concat(" ").concat(ctx.getString(R.string.delivery_adapter_km_indicator));
    }

    public static LatLng getLastKnowLocation(Context ctx) {
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(gpsLocation != null) {
            return new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());
        }
        if(networkLocation != null) {
            return new LatLng(networkLocation.getLatitude(), networkLocation.getLongitude());
        }
        return null;
    }

}
