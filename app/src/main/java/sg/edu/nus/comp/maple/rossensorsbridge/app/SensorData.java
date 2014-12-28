package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.hardware.Sensor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Keng Kiat Lim on 12/27/14.
 */
public class SensorData {

    private final int mSensorType;
    private final String mSensorStringType;
    private final float[] mValues;
    private final int mAccuracy;

    public SensorData(Sensor sensor, float[] values, int accuracy) {
        this.mSensorType = sensor.getType();
        this.mSensorStringType = sensor.getName();
        this.mValues = values;
        this.mAccuracy = accuracy;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonValues = new JSONArray();
            for (float value : this.mValues) {
                jsonValues.put(value);
            }

            jsonObject.put("sensorIntType", this.mSensorType);
            jsonObject.put("sensorStringType", this.mSensorStringType);
            jsonObject.put("accuracy", this.mAccuracy);
            jsonObject.put("values", jsonValues);

        } catch (JSONException e) {
            e.printStackTrace();

        } finally {
            return jsonObject;
        }
    }

    public int getSensorType() {
        return this.mSensorType;
    }

    public String getSensorStringType() {
        return this.mSensorStringType;
    }

    public float[] getValues() {
        return this.mValues;
    }

    public int getAccuracy() {
        return this.mAccuracy;
    }
}
