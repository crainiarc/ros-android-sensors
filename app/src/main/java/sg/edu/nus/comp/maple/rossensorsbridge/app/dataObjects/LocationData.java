package sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects;

import android.location.Location;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.JSONifiable;

/**
 * Created by Keng Kiat Lim on 12/29/14.
 */
public class LocationData implements JSONifiable {

    public static final String stringName = "Location Data";
    private final Location mLocation;

    public LocationData(Location location) {
        this.mLocation = location;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", LocationData.stringName);
            jsonObject.put("accuracy", this.mLocation.getAccuracy());
            jsonObject.put("altitude", this.mLocation.getAltitude());
            jsonObject.put("bearing", this.mLocation.getBearing());
            jsonObject.put("latitude", this.mLocation.getLatitude());
            jsonObject.put("longitude", this.mLocation.getLongitude());
            jsonObject.put("speed", this.mLocation.getSpeed());
            jsonObject.put("time", this.mLocation.getTime());

        } catch (JSONException e) {
            e.printStackTrace();

        } finally {
            return jsonObject;
        }
    }

    public Location getLocation() {
        return mLocation;
    }
}
