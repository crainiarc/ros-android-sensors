package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by crainiarc on 12/24/14.
 */
public class PhoneSensorManager implements SensorEventListener {

    private SensorManager mSensorManager;
    private List<Sensor> mDeviceSensors;
    private Map<Sensor, List> mSensorValues;

    public PhoneSensorManager(SensorManager sensorManager) {
        this.mSensorManager = sensorManager;
        this.mDeviceSensors = this.mSensorManager.getSensorList(Sensor.TYPE_ALL);

        this.mSensorValues = new ConcurrentHashMap<Sensor, List>();
        for (Sensor sensor : this.mDeviceSensors) {
            this.mSensorValues.put(sensor, new ArrayList());
        }
    }

    public void startSensors() {
        for (Sensor sensor : this.mDeviceSensors) {
            if (sensor != null) {
                this.mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    public void stopSensors() {
        this.mSensorManager.unregisterListener(this);
    }

    public HashMap<Sensor, List> getSensorValues() {
        return new HashMap<Sensor, List>(this.mSensorValues);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.mSensorValues.put(event.sensor, new ArrayList(Arrays.asList(event.values)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
