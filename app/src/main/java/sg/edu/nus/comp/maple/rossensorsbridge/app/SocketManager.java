package sg.edu.nus.comp.maple.rossensorsbridge.app;

import android.util.Log;
import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneResult;
import org.json.JSONException;
import org.json.JSONObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects.ImageData;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.JSONifiable;
import sg.edu.nus.comp.maple.rossensorsbridge.app.interfaces.Pollable;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by Keng Kiat Lim on 12/25/14.
 */
public class SocketManager implements Runnable {

    private static final String LOG_TAG = "ROS_SENSORS_BRIDGE_SOCKET_MANAGER";
    private Socket mSocket;
    private String mHost;
    private int mPort;

    private PrintWriter mOutPrintWriter;
    private DataOutputStream mDataOutputStream;
    private BufferedReader mInBufferedReader;
    private List<Pollable> mSensorPollers;
    private DeferredManager mDeferredManager;

    public SocketManager(String host, int port, List<Pollable> sensorPollers) {
        this.mHost = host;
        this.mPort = port;
        this.mSensorPollers = sensorPollers;
        this.mDeferredManager = new DefaultDeferredManager();
    }

    @Override
    public void run() {
        try {
            this.startConnection();
            while (true) {
                this.mInBufferedReader.readLine();
                this.sendSensorData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            this.stopConnection();
        }
    }

    private void sendSensorData() {
        Promise[] promises = new Promise[this.mSensorPollers.size()];
        for (int i = 0; i < this.mSensorPollers.size(); i++) {
            promises[i] = this.mSensorPollers.get(i).getSensorValues();
        }

        this.mDeferredManager.when(promises).done(new DoneCallback<MultipleResults>() {
            @Override
            public void onDone(MultipleResults results) {
                Log.d(SocketManager.LOG_TAG, "Promises are done");
                JSONObject jsonToSend = new JSONObject();
                for (OneResult result : results) {
                    JSONObject jsonResultObject = ((JSONifiable) result.getResult()).toJSONObject();
                    try {
                        String name = jsonResultObject.getString("name");
                        jsonResultObject.remove("name");

                        if (name.equals(ImageData.stringName)) {
                            String base64Jpeg = (String) jsonResultObject.get("image");
                            jsonToSend.put(name, base64Jpeg);
                        } else {
                            jsonToSend.put(name, jsonResultObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String jsonString = jsonToSend.toString();
                mOutPrintWriter.println(jsonString.length());
                mOutPrintWriter.println(jsonString);
            }
        });
    }

    private void startConnection() throws IOException {
        this.mSocket = new Socket(this.mHost, this.mPort);
        this.mOutPrintWriter = new PrintWriter(this.mSocket.getOutputStream(), true);
        this.mDataOutputStream = new DataOutputStream(this.mSocket.getOutputStream());
        this.mInBufferedReader = new BufferedReader(new InputStreamReader(this.mSocket.getInputStream()));
    }

    private void stopConnection() {
        this.mOutPrintWriter.close();
        try {
            if (this.mDataOutputStream != null) {
                this.mDataOutputStream.close();
            }
            if (this.mInBufferedReader != null) {
                this.mInBufferedReader.close();
            }
            if (this.mSocket != null) {
                this.mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
