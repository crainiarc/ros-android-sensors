package sg.edu.nus.comp.maple.rossensorsbridge.app;

import org.jdeferred.DeferredManager;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneResult;
import org.json.JSONArray;
import org.json.JSONObject;
import sg.edu.nus.comp.maple.rossensorsbridge.app.dataObjects.SensorData;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by Keng Kiat Lim on 12/25/14.
 */
public class SocketManager implements Runnable {

    private Socket mSocket;
    private String mHost;
    private int mPort;

    private PrintWriter mOutPrintWriter;
    private DataOutputStream mDataOutputStream;
    private BufferedReader mInBufferedReader;
    private List<SensorPoller> mSensorPollers;
    private DeferredManager mDeferredManager;

    public SocketManager(String host, int port, List<SensorPoller> sensorPollers) {
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
                this.sendImageData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.stopConnection();
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
                JSONArray jsonArray = new JSONArray();
                for (OneResult result : results) {
                    SensorData sensorData = (SensorData) result.getResult();
                    JSONObject jsonResultObject = sensorData.toJSONObject();
                    jsonArray.put(jsonResultObject);
                }
                String jsonString = jsonArray.toString();
                mOutPrintWriter.println(jsonString.length());
                mOutPrintWriter.println(jsonString);
            }
        });
    }

    private void sendImageData() {
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
