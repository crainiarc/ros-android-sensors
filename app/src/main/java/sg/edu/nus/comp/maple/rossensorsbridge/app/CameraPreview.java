package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Keng Kiat Lim on 12/22/14.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = "ROSSERIALBRIDGE.CAMERAPREVIEW";
    private SurfaceHolder holder;
    private Camera camera;
    private List<Camera.Size> supportedPreviewSizes;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;

        this.holder = getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Deprecated since Android 3.0
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.camera.setPreviewDisplay(this.holder);
            this.camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. release Camera Preview in activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.holder.getSurface() == null || this.camera == null) {
            return;
        }

        try {
            this.camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            this.camera.setPreviewDisplay(this.holder);
            this.camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCamera(Camera camera) {
        if (this.camera == camera) {
            return;
        }

        this.stopPreviewAndFreeCamera();
        this.camera = camera;
        if (this.camera != null) {
            List<Camera.Size> localSizes = this.camera.getParameters().getSupportedPreviewSizes();
            this.supportedPreviewSizes = localSizes;
            requestLayout();
        }

        try {
            this.camera.setPreviewDisplay(this.holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.camera.startPreview();
    }

    public void stopPreviewAndFreeCamera() {
        if (this.camera != null) {
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }
}
