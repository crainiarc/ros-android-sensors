package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;


public class CameraPreviewActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.safeOpenCamera();
        this.mPreview = new CameraPreview(this, this.mCamera);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        frameLayout.addView(this.mPreview);
        this.mPreview.startCameraPreview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_connect:
                return true;

            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean safeOpenCamera() {
        boolean opened = false;
        try {
            this.releaseCameraAndPreview();
            this.mCamera = Camera.open();
            opened = (this.mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "Failed to open Camera");
            e.printStackTrace();
        }
        return opened;
    }

    private void releaseCameraAndPreview() {
        if (this.mPreview != null) {
            this.mPreview.setmCamera(null);
        }

        if (this.mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
        }
    }
}
