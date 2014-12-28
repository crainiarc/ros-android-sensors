package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.hardware.Camera;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects.ImageData;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.JSONifiable;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.Pollable;

/**
 * Created by crainiarc on 12/29/14.
 */
public class CameraPoller implements Pollable, Camera.PictureCallback, Camera.ShutterCallback {

    private Camera mCamera;
    private DeferredObject<JSONifiable, Void, Void> mDeferredObject;

    public CameraPoller(Camera camera) {
        this.mCamera = camera;
    }

    @Override
    public Promise getSensorValues() {
        this.mDeferredObject = new DeferredObject();
        this.mCamera.takePicture(this, null, this);
        return this.mDeferredObject.promise();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        ImageData imageData = new ImageData(data);
        this.mDeferredObject.resolve(imageData);
    }

    @Override
    public void onShutter() {

    }
}
