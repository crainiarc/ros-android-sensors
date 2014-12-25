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
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private final Context mContext;
    private Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;
    private List<String> mSupportedFlashModes;

    public CameraPreview(Context mContext, Camera mCamera) {
        super(mContext);

        this.mContext = mContext;
        this.setmCamera(mCamera);

        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setKeepScreenOn(true);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Deprecated since Android 3.0
    }

    public void startCameraPreview() {
        try {
            this.mCamera.setPreviewDisplay(this.mHolder);
            this.mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takePictureForJPEG(Camera.PictureCallback pictureCallback) {
        if (pictureCallback != null) {
            this.mCamera.takePicture(null, null, pictureCallback);
            this.startCameraPreview();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.mCamera == null) { return; }

        try {
            this.mCamera.setPreviewDisplay(this.mHolder);
        } catch (IOException e) {
            Log.d(TAG, "Error setting mCamera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.stopPreviewAndFreeCamera();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.mHolder.getSurface() == null || this.mCamera == null) { return; }

        try {
            this.mCamera.stopPreview();

            Camera.Parameters params = this.mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            if (this.mPreviewSize != null) {
                Camera.Size previewSize = this.mPreviewSize;
                params.setPreviewSize(previewSize.width, previewSize.height);
            }

            this.mCamera.setParameters(params);
            this.mCamera.startPreview();

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

        if (this.mSupportedPreviewSizes != null) {
            this.mPreviewSize = this.getOptimalPreviewSize(this.mSupportedPreviewSizes, width, height);
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

            if (this.mPreviewSize != null) {
                Display display = ((WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation()) {
                    case Surface.ROTATION_0:
                        previewWidth = this.mPreviewSize.height;
                        previewHeight = this.mPreviewSize.width;
                        this.mCamera.setDisplayOrientation(90);
                        break;

                    case Surface.ROTATION_90:
                        previewWidth = this.mPreviewSize.width;
                        previewHeight = this.mPreviewSize.height;
                        break;

                    case Surface.ROTATION_180:
                        previewWidth = this.mPreviewSize.height;
                        previewHeight = this.mPreviewSize.width;
                        break;

                    case Surface.ROTATION_270:
                        previewWidth = this.mPreviewSize.width;
                        previewHeight = this.mPreviewSize.height;
                        this.mCamera.setDisplayOrientation(180);
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
            optimalSize = this.mSupportedPreviewSizes.get(0);
        }

        return optimalSize;
    }

    public void setmCamera(Camera mCamera) {
        if (this.mCamera == mCamera) { return; }

        this.stopPreviewAndFreeCamera();
        this.mCamera = mCamera;
        if (this.mCamera != null) {
            this.mSupportedPreviewSizes = this.mCamera.getParameters().getSupportedPreviewSizes();
            this.mSupportedFlashModes = this.mCamera.getParameters().getSupportedFlashModes();

            if (this.mSupportedFlashModes != null && this.mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters params = this.mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                this.mCamera.setParameters(params);
            }
        }

        requestLayout();
    }

    public void stopPreviewAndFreeCamera() {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }
}
