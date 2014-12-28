package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects.SensorData;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.JSONifiable;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.Pollable;

/**
 * Created by Keng Kiat Lim on 12/26/14.
 */
public class SensorPoller implements Pollable, SensorEventListener {
    private SensorManager mSensorManager;
    private int mSensorType;
    private Sensor mSensor;
    private DeferredObject<JSONifiable, Void, Void> mDeferredObject;

    public SensorPoller(SensorManager sensorManager, int sensorType) {
        this.mSensorManager = sensorManager;
        this.mSensorType = sensorType;
        this.mSensor = this.mSensorManager.getDefaultSensor(this.mSensorType);
    }

    @Override
    public Promise getSensorValues() {
        this.mDeferredObject = new DeferredObject();
        this.mSensorManager.registerListener(this, this.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return this.mDeferredObject.promise();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.mSensorManager.unregisterListener(this);
        SensorData sensorData = new SensorData(this.mSensor, event.values, event.accuracy);
        this.mDeferredObject.resolve(sensorData);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO
    }
}
