package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by Keng Kiat Lim on 12/22/14.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "ROSSERIALBRIDGE.CAMERAPREVIEW";
    private SurfaceHolder holder;
    private Camera camera;
    private final Context context;
    private Camera.Size previewSize;
    private List<Camera.Size> supportedPreviewSizes;
    private List<String> supportedFlashModes;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        this.context = context;
        this.setCamera(camera);

        this.holder = getHolder();
        this.holder.addCallback(this);
        this.holder.setKeepScreenOn(true);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Deprecated since Android 3.0
    }

    public void startCameraPreview() {
        try {
            this.camera.setPreviewDisplay(this.holder);
            this.camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.camera == null) { return; }

        try {
            this.camera.setPreviewDisplay(this.holder);
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.stopPreviewAndFreeCamera();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.holder.getSurface() == null || this.camera == null) { return; }

        try {
            this.camera.stopPreview();

            Camera.Parameters params = this.camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            if (this.previewSize != null) {
                Camera.Size previewSize = this.previewSize;
                params.setPreviewSize(previewSize.width, previewSize.height);
            }

            this.camera.setParameters(params);
            this.camera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (this.supportedPreviewSizes != null) {
            this.previewSize = this.getOptimalPreviewSize(this.supportedPreviewSizes, width, height);
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;

            if (this.previewSize != null) {
                Display display = ((WindowManager)this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation()) {
                    case Surface.ROTATION_0:
                        previewWidth = this.previewSize.height;
                        previewHeight = this.previewSize.width;
                        this.camera.setDisplayOrientation(90);
                        break;

                    case Surface.ROTATION_90:
                        previewWidth = this.previewSize.width;
                        previewHeight = this.previewSize.height;
                        break;

                    case Surface.ROTATION_180:
                        previewWidth = this.previewSize.height;
                        previewHeight = this.previewSize.width;
                        break;

                    case Surface.ROTATION_270:
                        previewWidth = this.previewSize.width;
                        previewHeight = this.previewSize.height;
                        this.camera.setDisplayOrientation(180);
                        break;
                }
            }

            final int scaledChildHeight = previewHeight * width / previewWidth;
            this.layout(0, height - scaledChildHeight, width, height);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        for (Camera.Size size : sizes) {
            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                optimalSize = size;
            }
        }

        if (optimalSize == null) {
            // TODO
            optimalSize = this.supportedPreviewSizes.get(0);
        }

        return optimalSize;
    }

    public void setCamera(Camera camera) {
        if (this.camera == camera) { return; }

        this.stopPreviewAndFreeCamera();
        this.camera = camera;
        if (this.camera != null) {
            this.supportedPreviewSizes = this.camera.getParameters().getSupportedPreviewSizes();
            this.supportedFlashModes = this.camera.getParameters().getSupportedFlashModes();

            if (this.supportedFlashModes != null && this.supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters params = this.camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                this.camera.setParameters(params);
            }
        }

        requestLayout();
    }

    public void stopPreviewAndFreeCamera() {
        if (this.camera != null) {
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }
}
