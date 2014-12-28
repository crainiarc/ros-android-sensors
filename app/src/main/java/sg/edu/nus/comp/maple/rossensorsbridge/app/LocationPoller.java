package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects.LocationData;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.JSONifiable;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.Pollable;

/**
 * Created by Keng Kiat Lim on 12/29/14.
 */
public class LocationPoller implements Pollable, LocationListener {

    private static final String LOG_TAG = "ROS_SENSORS_BRIDGE_LOCATION_POLLER";
    private LocationManager mLocationManager;
    private DeferredObject<JSONifiable, Void, Void> mDeferredObject;

    public LocationPoller(LocationManager locationManager) {
        this.mLocationManager = locationManager;
    }

    @Override
    public Promise getSensorValues() {
        this.mDeferredObject = new DeferredObject();

        if (this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(LocationPoller.LOG_TAG, "GPS Enabled");
            this.mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper());
        } else {
            Log.d(LocationPoller.LOG_TAG, "GPS Not Enabled");
            this.mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());

        }
        return this.mDeferredObject.promise();
    }

    @Override
    public void onLocationChanged(Location location) {
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
