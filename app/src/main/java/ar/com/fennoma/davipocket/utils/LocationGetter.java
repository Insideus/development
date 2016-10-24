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

import java.util.Calendar;

public class LocationGetter implements LocationListener {

    private final Activity activity;
    private LocationManager mlocManager;
    private int loccounter;
    private Handler serviceHandler;
    private boolean timertest;
    private Location location;
    private ILocationListener listener;

    public interface ILocationListener {
        void onGotLocation(Location location);

        void failedGettingLocation();
    }

    public LocationGetter(Activity activity, ILocationListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void onStart() {
        if (!timertest) {
            timertest = true;
            serviceHandler = new Handler();
            serviceHandler.postDelayed(new timer(), 1000L);
        }
    }

    public void locUpdate(int minTime, float minDistance) {
        mlocManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (mlocManager != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mlocManager.removeUpdates(this);
        }
        loccounter = 0;
        onStart();
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
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
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    public void stopListening() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.removeUpdates(this);
        loccounter = 0;
        if (location == null) {
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation() {
        Location bestLastLocation = getBestLastLocation();
        if(bestLastLocation == null) {
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
        if (!mlocManager.isProviderEnabled(provider)) {
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
            ++loccounter;
            if (location != null || loccounter > 8) {
                stopListening();
            } else serviceHandler.postDelayed(this, 1000L);
        }
    }
}
