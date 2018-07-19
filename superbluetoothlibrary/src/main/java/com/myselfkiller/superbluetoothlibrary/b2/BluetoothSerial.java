package com.myselfkiller.superbluetoothlibrary.b2;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.util.Set;

public class BluetoothSerial {

    private static final String TAG = "BluetoothSerial";

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    protected static final int MESSAGE_STATE_CHANGE = 1;
    protected static final int MESSAGE_READ = 2;
    protected static final int MESSAGE_WRITE = 3;
    protected static final int MESSAGE_DEVICE_INFO = 4;

    protected static final String KEY_DEVICE_NAME = "DEVICE_NAME";
    protected static final String KEY_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final byte[] CRLF = { 0x0D, 0x0A }; // \r\n

    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private BluetoothSerialListener mListener;
    private SPPService mService;
    private Context context;

    private String mConnectedDeviceName, mConnectedDeviceAddress;

    private boolean isRaw;

    private BluetoothDevice device;
    private boolean isConnected = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        String name = device.getName();
                        String address = device.getAddress();
                        if (name != null) {
                            Log.e(TAG, "onReceive: name= " + name+", address = "+ address);
                            mListener.onBluetoothDeviceFound(device);
//                            if (name.contains("HC-31")) {
//                                if (mAdapter.isDiscovering())
//                                    mAdapter.cancelDiscovery();
//                                device.createBond();
//                            }
                        }
                    }

                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            Log.e(TAG, "取消配对");
//                            searchDevices();
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.e(TAG, "配对中");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            mAdapter.cancelDiscovery();
                            Log.e(TAG, "配对成功");
//                            getBluetoothA2DP();
                            connect(device);
                            break;
                    }
                    break;
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
                        case BluetoothA2dp.STATE_CONNECTING:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Log.e(TAG, "device: " + device.getName() + " connecting");
                            break;
                        case BluetoothA2dp.STATE_CONNECTED:
                            isConnected = true;
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if (device != null) {
                                String name = device.getName();
                                if (name != null) {
//                                    mName.setText(name);
                                }
                            }
                            Log.e(TAG, "device: " + device.getName() + " connected");
                            break;
                        case BluetoothA2dp.STATE_DISCONNECTING:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            break;
                        case BluetoothA2dp.STATE_DISCONNECTED:
                            isConnected = false;
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Log.e(TAG, "device: " + device.getName() + " disconnected");
//                            searchDevices();
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    };



    public BluetoothSerial(Context context, BluetoothSerialListener listener) {
        mAdapter = getAdapter(context);
        mListener = listener;
        this.context = context;
        isRaw = mListener instanceof BluetoothSerialRawListener;
    }

    public static BluetoothAdapter getAdapter(Context context) {
        BluetoothAdapter bluetoothAdapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null)
                bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return bluetoothAdapter;
    }

    /**
     * Check the presence of a Bluetooth adapter on this device and set up the Bluetooth Serial Port Profile (SPP) service.
     */
    public void setup() throws IOException {
        if (checkBluetooth()) {
            mPairedDevices = mAdapter.getBondedDevices();
            mService = new SPPService(context,mHandler);
        }
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if this device's adapter is turned on
     */
    public boolean isBluetoothEnabled() {
        return mAdapter.isEnabled();
    }

    public boolean checkBluetooth() {
        if (mAdapter == null) {
            mListener.onBluetoothNotSupported();
            return false;
        } else {
            if (!mAdapter.isEnabled()) {
                mListener.onBluetoothDisabled();
                return false;
            } else {
                return true;
            }
        }
    }

    public void ScanDevices(){
        if (checkBluetooth()){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
            context.registerReceiver(broadcastReceiver, intentFilter);
            mAdapter.startDiscovery();
        }
//        handler.post(seracheThread);
        handler.postDelayed(seracheThread, 15000);
    }

    Handler handler = new Handler();

    Runnable seracheThread = new Runnable() {
        public void run() {
            // 线程每次执行时输出"UpdateThread..."文字,且自动换行
            // textview的append功能和Qt中的append类似，不会覆盖前面
            // 的内容，只是Qt中的append默认是自动换行模式
            // text_view.append("\nUpdateThread...");
            // 延时1s后又将线程加入到线程队列中
            if (!isConnected) {
                ScanDevices();
            }
            Log.e(TAG,"---seracheThread");
//            handler.postDelayed(seracheThread, 15*1000);
        }
    };

    public void stopScanDevice(){
        if (mAdapter.isDiscovering())
            Log.e("hm","正在搜索，停止搜索");
            mAdapter.cancelDiscovery();
        handler.removeCallbacks(seracheThread);
        Log.e(TAG,"----stopScanDevice");
    }

    public void setIsSaveBTData(boolean tag) {

        if (mService != null) {
            mService.isSaveBTDataToFile = tag;
        }
    }

    /**
     * Open a Bluetooth serial port and get ready to establish a connection with a remote device.
     */
    public void start() {
        if (mService != null && mService.getState() == STATE_DISCONNECTED) {
            mService.start();
        }
    }

    /**
     * Connect to a remote Bluetooth device with the specified MAC address.
     *
     * @param address The MAC address of a remote Bluetooth device.
     */
    public void connect(String address) {
        BluetoothDevice device = null;
        try {
            device = mAdapter.getRemoteDevice(address);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Device not found!");
        }
        if (device != null)
            connect(device);
    }

    /**
     * Connect to a remote Bluetooth device.
     *
     * @param device A remote Bluetooth device.
     */
    public void connect(BluetoothDevice device) {
        if (mService != null) {
            mService.connect(device);
        }
    }

    /**
     * Write the specified bytes to the Bluetooth serial port.
     *
     * @param data The data to be written.
     */
    public void write(byte[] data) {
        if (mService.getState() == STATE_CONNECTED) {
            mService.write(data);
        }
    }

    /**
     * Write the specified bytes to the Bluetooth serial port.
     *
     * @param data The data to be written.
     * @param crlf Set true to end the data with a newline (\r\n).
     */
    public void write(String data, boolean crlf) {
        write(data.getBytes());
        if (crlf)
            write(CRLF);
    }

    /**
     * Write the specified string to the Bluetooth serial port.
     *
     * @param data The data to be written.
     */
    public void write(String data) {
        write(data.getBytes());
    }

    /**
     * Write the specified string ended with a new line (\r\n) to the Bluetooth serial port.
     *
     * @param data The data to be written.
     */
    public void writeln(String data) {
        write(data.getBytes());
        write(CRLF);
    }

    /**
     * Disconnect from the remote Bluetooth device and close the active Bluetooth serial port.
     */
    public void stop() {
        if (mService != null) {
             context.unregisterReceiver(broadcastReceiver);
            mService.stop();
        }
    }

    /**
     * Get the current state of the Bluetooth serial port.
     *
     * @return the current state
     */
    public int getState() {
        return mService.getState();
    }

    /**
     * Return true if a connection to a remote Bluetooth device is established.
     *
     * @return true if connected to a device
     */
    public boolean isConnected() {
        return (mService.getState() == STATE_CONNECTED);
    }

    /**
     * Get the name of the connected remote Bluetooth device.
     *
     * @return the name of the connected device
     */
    public String getConnectedDeviceName() {
        return mConnectedDeviceName;
    }

    /**
     * Get the MAC address of the connected remote Bluetooth device.
     *
     * @return the MAC address of the connected device
     */
    public String getConnectedDeviceAddress() {
        return mConnectedDeviceAddress;
    }

    /**
     * Get the paired Bluetooth devices of this device.
     *
     * @return the paired devices
     */
    public Set<BluetoothDevice> getPairedDevices() {
        return mPairedDevices;
    }

    /**
     * Get the names of the paired Bluetooth devices of this device.
     *
     * @return the names of the paired devices
     */
    public String[] getPairedDevicesName() {
        if (mPairedDevices != null) {
            String[] name = new String[mPairedDevices.size()];
            int i = 0;
            for (BluetoothDevice d : mPairedDevices) {
                name[i] = d.getName();
                i++;
            }
            return name;
        }
        return null;
    }

    /**
     * Get the MAC addresses of the paired Bluetooth devices of this device.
     *
     * @return the MAC addresses of the paired devices
     */
    public String[] getPairedDevicesAddress() {
        if (mPairedDevices != null) {
            String[] address = new String[mPairedDevices.size()];
            int i = 0;
            for (BluetoothDevice d : mPairedDevices) {
                address[i] = d.getAddress();
                i++;
            }
            return address;
        }
        return null;
    }

    /**
     * Get the name of this device's Bluetooth adapter.
     *
     * @return the name of the local Bluetooth adapter
     */
    public String getLocalAdapterName() {
        return mAdapter.getName();
    }

    /**
     * Get the MAC address of this device's Bluetooth adapter.
     *
     * @return the MAC address of the local Bluetooth adapter
     */
    public String getLocalAdapterAddress() {
        return mAdapter.getAddress();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case STATE_CONNECTED:
                            mListener.onBluetoothDeviceConnected(mConnectedDeviceName, mConnectedDeviceAddress);
                            break;
                        case STATE_CONNECTING:
                            isConnected = true;
                            mListener.onConnectingBluetoothDevice();
                            break;
                        case STATE_DISCONNECTED:
                            mListener.onBluetoothDeviceDisconnected();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] bufferWrite = (byte[]) msg.obj;
                    String messageWrite = new String(bufferWrite);
                    mListener.onBluetoothSerialWrite(messageWrite);
                    if (isRaw) {
                        ((BluetoothSerialRawListener) mListener).onBluetoothSerialWriteRaw(bufferWrite);
                    }
                    break;
                case MESSAGE_READ:
                    byte[] bufferRead = (byte[]) msg.obj;
                    String messageRead = new String(bufferRead);
                    mListener.onBluetoothSerialRead(messageRead);
                    if (isRaw) {
                        ((BluetoothSerialRawListener) mListener).onBluetoothSerialReadRaw(bufferRead);
                    }
                    break;
                case MESSAGE_DEVICE_INFO:
                    mConnectedDeviceName = msg.getData().getString(KEY_DEVICE_NAME);
                    mConnectedDeviceAddress = msg.getData().getString(KEY_DEVICE_ADDRESS);
                    break;
            }
        }
    };

}
