package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.Pollable;

import java.util.ArrayList;
import java.util.List;


public class CameraPreviewActivity extends Activity {

    public static final int[] SUPPORTED_SENSOR_TYPES = {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_LIGHT, Sensor.TYPE_PRESSURE, Sensor.TYPE_GRAVITY,
            Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_ROTATION_VECTOR
    };

    private SocketManager mSocketManager;
    private Camera mCamera;
    private CameraPreview mPreview;
    private List<Pollable> mSensorPollers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialise sensor pollers for each sensor type
        this.mSensorPollers = new ArrayList<Pollable>();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (int i : CameraPreviewActivity.SUPPORTED_SENSOR_TYPES) {
            this.mSensorPollers.add(new SensorPoller(sensorManager, i));
        }

        // Add location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.mSensorPollers.add(new LocationPoller(locationManager));
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.safeOpenCamera();
        this.mPreview = new CameraPreview(this, this.mCamera);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        frameLayout.addView(this.mPreview);
        this.mPreview.startCameraPreview();

        this.mSensorPollers.add(new CameraPoller(this.mCamera));
    }

    @Override
    protected void onPause() {
        super.onPause();

        for (Pollable pollable : this.mSensorPollers) {
            if (pollable instanceof CameraPoller) {
                this.mSensorPollers.remove(pollable);
            }
        }
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
                EditText hostEditText = (EditText) findViewById(R.id.server_host);
                EditText portEditText = (EditText) findViewById(R.id.server_port);

                String host = hostEditText.getText().toString();
                int port = Integer.parseInt(portEditText.getText().toString());

                this.mSocketManager = new SocketManager(host, port, this.mSensorPollers);
                (new Thread(this.mSocketManager)).start();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Camera helper methods
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
