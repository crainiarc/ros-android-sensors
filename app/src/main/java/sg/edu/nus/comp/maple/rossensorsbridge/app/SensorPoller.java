package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Keng Kiat Lim on 12/26/14.
 */
public class SensorPoller implements SensorEventListener {
    private SensorManager mSensorManager;
    private int mSensorType
    private Sensor mSensor;
    private DeferredObject<List, Void, Void> mDeferredObject;

    public SensorPoller(SensorManager sensorManager, int sensorType) {
        this.mSensorManager = sensorManager;
        this.mSensorType = sensorType;
        this.mSensor = this.mSensorManager.getDefaultSensor(this.mSensorType);
    }

    public Promise getSensorValues() {
        this.mDeferredObject = new DeferredObject();
        this.mSensorManager.registerListener(this, this.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return this.mDeferredObject.promise();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.mSensorManager.unregisterListener(this);
        List values = Collections.unmodifiableList(Arrays.asList(event.values));
        this.mDeferredObject.resolve(values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO
    }
}
