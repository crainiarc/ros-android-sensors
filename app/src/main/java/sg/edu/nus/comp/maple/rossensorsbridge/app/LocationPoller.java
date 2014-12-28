package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects.LocationData;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.Pollable;

/**
 * Created by Keng Kiat Lim on 12/29/14.
 */
public class LocationPoller implements Pollable, LocationListener {

    private LocationManager mLocationManager;
    private DeferredObject<LocationData, Void, Void> mDeferredObject;

    public LocationPoller(LocationManager locationManager) {
        this.mLocationManager = locationManager;
    }

    @Override
    public Promise getSensorValues() {
        this.mDeferredObject = new DeferredObject<LocationData, Void, Void>();
        this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        return this.mDeferredObject.promise();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.mLocationManager.removeUpdates(this);
        LocationData locationData = new LocationData(location);
        this.mDeferredObject.resolve(locationData);
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
