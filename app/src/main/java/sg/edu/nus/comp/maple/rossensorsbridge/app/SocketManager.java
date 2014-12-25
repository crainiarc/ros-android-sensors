package sg.edu.nus.comp.maple.rossensorsbridge.app;

import java.io.*;
import java.net.Socket;

/**
 * Created by Keng Kiat Lim on 12/25/14.
 */
public class SocketManager implements Runnable {
    private Socket mSocket;
    private String mHost;
    private int mPort;
    private boolean mIsSocketOpen;
    private PrintWriter mOutPrintWriter;
    private DataOutputStream mDataOutputStream;
    private BufferedReader mInBufferedReader;

    public SocketManager(String host, int port) {
        this.mHost = host;
        this.mPort = port;
    }

    public void startConnection() throws IOException {
        this.mSocket = new Socket(this.mHost, this.mPort);
        this.mIsSocketOpen = true;
        this.mOutPrintWriter = new PrintWriter(this.mSocket.getOutputStream(), true);
        this.mDataOutputStream = new DataOutputStream(this.mSocket.getOutputStream());
        this.mInBufferedReader = new BufferedReader(new InputStreamReader(this.mSocket.getInputStream()));
    }

    public void stopConnection() throws IOException {
        this.mOutPrintWriter.close();
        this.mDataOutputStream.close();
        this.mInBufferedReader.close();
        this.mSocket.close();
        this.mIsSocketOpen = false;
    }

    @Override
    public void run() {
        while (this.mIsSocketOpen) {
            try {
                String request = this.mInBufferedReader.readLine();
                this.sendSensorData();
                this.sendImageData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSensorData() {
        this.mOutPrintWriter.println("13");
        this.mOutPrintWriter.println("Hello, world!");
    }

    private void sendImageData() {
    }
}
