package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;


public class CameraPreviewActivity extends Activity {

    private Camera camera;
    private CameraPreview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.safeOpenCamera();
        this.preview = new CameraPreview(this, this.camera);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        frameLayout.addView(this.preview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.camera.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean safeOpenCamera() {
        boolean opened = false;
        try {
            this.releaseCameraAndPreview();
            this.camera = Camera.open();
            opened = (this.camera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "Failed to open Camera");
            e.printStackTrace();
        }
        return opened;
    }

    private void releaseCameraAndPreview() {
        if (this.preview != null) {
            this.preview.setCamera(null);
        }

        if (this.camera != null) {
            this.camera.release();
            this.camera = null;
        }
    }
}
