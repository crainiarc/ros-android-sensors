package sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects;

import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.JSONifiable;

/**
 * Created by crainiarc on 12/29/14.
 */
public class ImageData implements JSONifiable {

    public static final String stringName = "Image Data";
    private byte[] mByteJpeg;

    public ImageData(byte[] byteJpeg) {
        this.mByteJpeg = byteJpeg;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            String base64Jpeg = Base64.encodeToString(this.mByteJpeg, Base64.DEFAULT);
            jsonObject.put("name", ImageData.stringName);
            jsonObject.put("image", base64Jpeg);

        } catch (JSONException e) {
            e.printStackTrace();

        } finally {
            return jsonObject;
        }
    }

    public byte[] getByteJpeg() {
        return mByteJpeg;
    }
}
