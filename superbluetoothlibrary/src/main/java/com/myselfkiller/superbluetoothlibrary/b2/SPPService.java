package com.myselfkiller.superbluetoothlibrary.b2;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SPPService {

    public Boolean isSaveBTDataToFile = false;

    private static final String TAG = "SPPService";

    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    private String mFilePath;
    private static final String FILE_NAME = "NMEA";
    private static final String FILE_EXT = ".txt";
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");


    private String getSavingFilePath() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return mFilePath + FILE_NAME + "-" + date + FILE_EXT;
    }

    public SPPService(Context context,Handler handler) throws IOException {
        mState = BluetoothSerial.STATE_DISCONNECTED;
        mHandler = handler;
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + FILE_NAME + File.separator;
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException(TAG+"init - create file path failed: " + file.getAbsolutePath());
            }
        }
        mFilePath = filePath;
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);

        mState = state;
        mHandler.obtainMessage(BluetoothSerial.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        Log.d(TAG, "start()");

        resetThreads();
        setState(BluetoothSerial.STATE_DISCONNECTED);
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect(" + device + ")");

        if (mState == BluetoothSerial.STATE_CONNECTING) {
            resetConnectThread();
        }

        if (mState == BluetoothSerial.STATE_CONNECTED) {
            resetConnectedThread();
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(BluetoothSerial.STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "Connected to " + device + "!");

        resetThreads();
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Message msg = mHandler.obtainMessage(BluetoothSerial.MESSAGE_DEVICE_INFO);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothSerial.KEY_DEVICE_NAME, device.getName());
        bundle.putString(BluetoothSerial.KEY_DEVICE_ADDRESS, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(BluetoothSerial.STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.d(TAG, "stop()");

        resetThreads();
        setState(BluetoothSerial.STATE_DISCONNECTED);
    }

    public void write(byte[] data) {
        ConnectedThread t;
        synchronized (this) {
            if (mState == BluetoothSerial.STATE_CONNECTED)
                t = mConnectedThread;
            else
                return;
        }
        t.write(data);
    }

    private synchronized void resetThreads() {
        resetConnectThread();
        resetConnectedThread();
    }

    private synchronized void resetConnectThread() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }

    private synchronized void resetConnectedThread() {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    private void reconnect() {
        SPPService.this.start();
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device) {
            Log.d(TAG, "ConnectThread(" + device + ")");
            mDevice = device;
            BluetoothSocket tempSocket = null;
            try {
                tempSocket = device.createRfcommSocketToServiceRecord(UUID_SPP);
            } catch (IOException e1) {
                Log.e(TAG, "Failed to create a secure socket!");
                try {
                    tempSocket = device.createInsecureRfcommSocketToServiceRecord(UUID_SPP);
                } catch (IOException e2) {
                    Log.e(TAG, "Failed to create an insecure socket!");
                }
            }
            mSocket = tempSocket;
        }

        public void run() {
            try {
                mSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "Failed to connect to the socket!");
                cancel();
                reconnect(); // Connection failed
                return;
            }

            synchronized (SPPService.this) {
                mConnectThread = null;
            }
            connected(mSocket, mDevice);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Unable to close the socket!");
            }
        }

    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread()");

            mSocket = socket;
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;

            try {
                tempInputStream = socket.getInputStream();
                tempOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "I/O streams cannot be created from the socket!");
            }

            mInputStream = tempInputStream;
            mOutputStream = tempOutputStream;
        }

        public void run() {

            Process process = null;
            FileOutputStream fos = null;
            BufferedReader br = null;

            String linel;
            try {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
                File file = new File(getSavingFilePath());
                fos = new FileOutputStream(file, true);
                while ((linel = bufferedReader.readLine()) != null) {
                    byte[] read = linel.getBytes();
                    mHandler.obtainMessage(BluetoothSerial.MESSAGE_READ, read.length, -1, read).sendToTarget();

                    if (isSaveBTDataToFile) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(linel);
                        fos.write((sb.toString() + "\n").getBytes());
                        fos.flush();
                        if (file.length() > MAX_FILE_SIZE) {
                            fos.close();
                            file.renameTo(new File(getSavingFilePath()));
                            file = new File(getSavingFilePath());
                            fos = new FileOutputStream(file);
                        }
                    }
                }
            } catch (IOException e) {
                reconnect();
                e.printStackTrace();
            }
        }

        public void write(byte[] data) {
            try {
                mOutputStream.write(data);
                mHandler.obtainMessage(BluetoothSerial.MESSAGE_WRITE, -1, -1, data).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Unable to write the socket!");
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Unable to close the socket!");
            }
        }

    }

}
