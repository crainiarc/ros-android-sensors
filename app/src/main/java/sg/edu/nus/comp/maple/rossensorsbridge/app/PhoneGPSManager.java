package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Keng Kiat Lim on 12/24/14.
 */
public class PhoneGPSManager implements LocationListener {

    private LocationManager mLocationManager;
    private Location mLocation;

    public PhoneGPSManager(LocationManager locationManager) {
        this.mLocationManager = locationManager;
    }

    public void startLocationFix() {
        this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public void stopLocationFix() {
        this.mLocationManager.removeUpdates(this);
    }

    public Location getLocation() {
        return this.mLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Currently set to get the latest location.
        // TODO: Optimise for accuracy and power consumption
        this.mLocation = location;
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
}
