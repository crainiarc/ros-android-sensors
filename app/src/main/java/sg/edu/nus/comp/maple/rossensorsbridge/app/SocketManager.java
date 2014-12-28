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

    private PrintWriter mOutPrintWriter;
    private DataOutputStream mDataOutputStream;
    private BufferedReader mInBufferedReader;

    public SocketManager(String host, int port) {
        this.mHost = host;
        this.mPort = port;
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
        this.mOutPrintWriter.println("13");
        this.mOutPrintWriter.println("Hello, world!");
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
            this.mDataOutputStream.close();
            this.mInBufferedReader.close();
            this.mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
