package com.davivienda.billetera.activities.interfaces;

import android.location.Location;

/**
 * Created by Julian Vega on 06/01/2017.
 */

public interface OnLocationUpdate {

    public void onGotLocation(Location location);
    public void failedGettingLocation();

}
