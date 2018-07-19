package com.myselfkiller.superbluetoothlibrary.b4;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.myselfkiller.superbluetoothlibrary.tools.BleBroadcastAction;
import com.myselfkiller.superbluetoothlibrary.tools.BleLogInterface;
import com.myselfkiller.superbluetoothlibrary.tools.BluetoothDataMessage;
import com.myselfkiller.superbluetoothlibrary.tools.UUIDGattAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothDeviceConnectService extends Service implements BleDataListener {
    private final static String TAG = "xubin1";

    /**
     * 蓝牙设备循环搜索Handler
     */
    private Handler handler = new Handler();
    /**
     * 蓝牙设备搜索Handler
     */
    private Handler mHandler_Handler;
    /**
     * 省电模式Handler
     */
    private Handler mHandler_Conntion;

    /**
     * 消息处理Handler
     */
    private BlueMessageHandle m_Handler = null;
    /**
     * 蓝牙设备管理Adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * 蓝牙设备
     */
    private BluetoothDevice mDevice = null;

    private boolean mScanning = false;
    private final long SCAN_PERIOD = 10000;
    private final long CONNECTION_TIME = 60 * 1000;
    private String mDeviceName = "";
    private String mDeviceAddress = "";

    private boolean mConnected_Data_Post = false;
    private boolean mConnected = false;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private static BluetoothDeviceConnectService mThis = null;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(UUIDGattAttributes.HEART_RATE_MEASUREMENT);

    private long m_ConnectionS = 0;
    private long m_ConnectionE = 0;

    private int m_DataSize = 0;

    private final static UUID[] uuid = new UUID[]{UUID.fromString(UUIDGattAttributes.UUID_FFF6)};

    public String getDeviceName() {
        return mDeviceName;
    }

    public String getDeviceMac() {
        return mDeviceAddress;
    }

    public BluetoothDevice getmDevice() {
        return mDevice;
    }

    int continue_error = 0;
    private BluetoothNavigationHandler bluetoothNavigationHandler = null;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //连接状态改变触发
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                //正在连接
                Log.e("xubin", "正在链接");
//                self.LogAdd("正在连接");
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.e("xubin", "链接");
//                self.LogAdd("连接");
                GattUpdate(BluetoothDataMessage.ACTION_GATT_CONNECTED, null, null);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e("xubin", "断开 " + status);
//                self.LogAdd("断开");
//                if(mConnectionState == STATE_CONNECTED){
//                    if(BleTools.turnOffBluetooth())
//                    BleTools.GetBluetooth_Open(EAApplication.self);
//                }

                GattUpdate(BluetoothDataMessage.ACTION_GATT_DISCONNECTED, null, null);
                if (status == 129) {
                    close();
                    mBluetoothAdapter.disable();
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBluetoothAdapter.enable();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                Log.e("xubin", " disconnection ");
            }
        }

        //搜索设备支持的所有service
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("xubin", " onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                GattUpdate(BluetoothDataMessage.ACTION_GATT_SERVICES_DISCOVERED, null, null);
            } else {
                if (status == 129) {
                    close();
                    mBluetoothAdapter.disable();
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBluetoothAdapter.enable();
                }
            }

        }

        /**
         * BLE终端数据被读的事件
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.e("xubin", " onCharacteristicRead ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(characteristic);
            }
        }

        /**
         *  notification对应onCharacteristicChanged；
         */

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.e("xubin", " onCharacteristicChanged ");
            broadcastUpdate(characteristic);
        }

        /**
         * 收到BLE终端写入数据回调
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("xubin", " onCharacteristicWrite ");
            GattUpdate(BluetoothDataMessage.ACTION_CHAR_WRITE_CALLBALK, BluetoothDataMessage.EXTRA_UUID, characteristic.getUuid().toString());
//            broadcastUpdate(BluetoothDataMessage.ACTION_CHAR_WRITE_CALLBALK, BluetoothDataMessage.EXTRA_UUID, characteristic.getUuid().toString());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e("xubin", " onDescriptorRead ");
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e("xubin", " onDescriptorWrite ");
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Log.e("xubin", " onReliableWriteCompleted ");
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            broadcastUpdate(BluetoothDataMessage.ACTION_CHAR_WRITE_CALLBALK, BluetoothDataMessage.EXTRA_RSSI, rssi + "");
        }
    };

    private void broadcastUpdate(final String action, String key, String value) {
        final Intent intent = new Intent(action);
        intent.putExtra(key, value);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final BluetoothGattCharacteristic characteristic) {
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            GattUpdate(BluetoothDataMessage.ACTION_DATA_AVAILABLE, null, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                GattUpdate(BluetoothDataMessage.ACTION_DATA_AVAILABLE, null, stringBuilder.toString());
            }
        }
    }


    public static BluetoothDeviceConnectService getInstance() {
        return mThis;
    }

    private BluetoothSocket clientSocket;
    private InputStream is;
    private String SERIAL_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    public boolean connect(final String address) throws InterruptedException, IOException {
        if (mBluetoothAdapter == null || address == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        m_ConnectionS = System.currentTimeMillis();
        // Previously connected device.  Try to reconnect.
        Log.e("hm", " connection 1");
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.e("hm", " connection 2");
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();

            if (mBluetoothGatt.connect()) {
                connectionTimer();
                Log.e("hm", " connection 3");
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                Log.e("hm", " connection 4");
                return false;
            }
        }

        Log.e("hm", " connection 5");
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e("hm", " connection 6");
            BleLogInterface.GetInstance().AddLog_Ble("通过" + address + "获取不到设备");
            return false;
        }
        Log.e("hm", " connection 7");
        if (device.getName().equals("HC-31")) {
            ParcelUuid[] uids = device.getUuids();
            for (int z = 0; z < uids.length; z++) {
                Log.e("hm", "getUuids=" + uids[z].toString());
            }
            // 这里需要try catch一下，以防异常抛出
//            try {
            // 判断客户端接口是否为空
            if (clientSocket == null) {
                // 获取到客户端接口
//                    try {
//                        clientSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(SERIAL_UUID));
//                        // 向服务端发送连接
//                        clientSocket.connect();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                try {
                    clientSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SERIAL_UUID));
                } catch (Exception e) {
                    Log.e("", "Error creating socket");
                }

                try {
                    clientSocket.connect();
                    is = clientSocket.getInputStream();
                    Log.e("hm", "Connected");
                } catch (IOException e) {
                    Log.e("hm", e.getMessage());
                    try {
                        Log.e("hm", "trying fallback...");


                        Class<?> clazz = clientSocket.getRemoteDevice().getClass();
                        Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                        Method m = clazz.getMethod("createInsecureRfcommSocketToServiceRecord", paramTypes);
                        Object[] params = new Object[]{Integer.valueOf(1)};
                        clientSocket = (BluetoothSocket) m.invoke(clientSocket.getRemoteDevice(), params);

                        clientSocket.connect();
                        is = clientSocket.getInputStream();
                        Log.e("hm", "Connected");
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                        Thread.sleep(500);
                        clientSocket.connect();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                        Thread.sleep(500);
                        clientSocket.connect();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        Thread.sleep(500);
                        clientSocket.connect();
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                        Thread.sleep(500);
                        clientSocket.connect();
                    }
                }

            }

            // 判断是否拿到输出流
            if (is != null) {
                // 需要发送的信息
                String text = "成功读取消息";
                final BufferedReader br = new BufferedReader(new InputStreamReader(is));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String line;
                        try {
                            while ((line = br.readLine()) != null) {
                                Log.e("hm", "蓝牙接收的原始数据：" + line);
                                m_Handler.ReceiveData(line);
//                                    BluetoothDeviceConnectService.this.onBleDataChanged(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
            Toast.makeText(this, "发送信息成功，请查收", Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                // 如果发生异常则告诉用户发送失败
//                Toast.makeText(this, "发送信息失败", Toast.LENGTH_SHORT).show();
//            }
        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
            connectionTimer();
            BleLogInterface.GetInstance().AddLog_Ble("通过" + address + "获取BluetoothGatt");
            mBluetoothDeviceAddress = address;
            Log.e("hm", " connection 8");
            mConnectionState = STATE_CONNECTING;

        }
        return true;
    }

    private Runnable getConntinue_runnable = new Runnable() {
        @Override
        public void run() {
            if (mConnected)
                return;
            ondetory();

        }
    };

    public void connectionTimer() {
        mHandler_Handler.removeCallbacks(getConntinue_runnable);
        mHandler_Handler.postDelayed(getConntinue_runnable, 10000);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        mHandler_Handler.removeCallbacks(getConntinue_runnable);
        ismConnected_Data_Post = false;
        BleLogInterface.GetInstance().AddLog_Ble(" Bluetooth Le Service Close");
        if (mBluetoothGatt == null) {
            return;
        }
        try {
            Log.e("xubin", " close ");
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            mBluetoothDeviceAddress = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte b) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return false;
        }

        byte[] val = new byte[1];
        val[0] = b;
        characteristic.setValue(val);

        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return false;
        }
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(UUIDGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    private Runnable conntinue_runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            ondetory();
        }

    };
    private Runnable runnable = new Runnable() {

        public void run() {
            //连接失败,并且 不在搜索流程，并且 如果是、、、
            if (!mConnected && !mScanning) {
                int isAirplaneMode = Settings.System.getInt(getContentResolver(), "airplane_mode_on", 0);
                if (isAirplaneMode == 1) {
                    Toast.makeText(mThis, "您的手机处于飞行模式,无法使用蓝牙", Toast.LENGTH_LONG).show();
                    return;
                }
                PackageManager pm = getPackageManager();
                boolean permission = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.BLUETOOTH", mThis.getPackageName()));
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                boolean isEnabled = mBluetoothAdapter.isEnabled();
                // 获取蓝牙服务isEnabled
                if (permission && isEnabled) {//蓝牙打开并有蓝牙权限
                    checkBluetooth();
                }
            }
        }

    };

    public void setTimer() {
        try {
            cleatTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (handler != null)
            handler.postDelayed(runnable, 1);
    }

    public void cleatTimer() {
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mThis = this;
        mHandler_Conntion = new Handler();
        mHandler_Handler = new Handler();
        m_Handler = new BlueMessageHandle(this, this);
        // 获取蓝牙服务
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


//        myweakup = WeakUp.getInstance(getApplicationContext());
//        myweakup.start();
//        if (myweakup == null) {
//            myweakup = new WeakUp();
//            Log.d("wakeup", "weakup service start");
//            myweakup.init(getApplicationContext());
//            myweakup.start();
//        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void Destory() {
        ismConnected_Data_Post = false;
        if (mBluetoothGatt == null) {
            return;
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            if (mScanning)
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

        }

        mConnected = false;
    }

    /**
     * 蓝牙设备检测
     */
    public void checkBluetooth() {
        //如果已连接
        if (mConnected)
            return;
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, 1);
        }
        if (null == mBluetoothAdapter) {
            Toast.makeText(this, "您的手机不支持蓝牙服务",
                    Toast.LENGTH_SHORT).show();

        } else {
            Log.e("hm", "搜索BLE设备");
            boolean hasDev = false;
            Set<BluetoothDevice> list = mBluetoothAdapter.getBondedDevices();
            if (list != null && list.size() > 0) {
                Log.e("hm", "有配对设备");
                for (BluetoothDevice device : list) {
                    Log.e("hm", "device.getName=" + device.getName() + ",device.getAddress=" + device.getAddress());
                    if (device.getName().equalsIgnoreCase("HC-31")) {
                        Log.e("hm", "device.getName=" + device.getName() + ",device.getAddress=" + device.getAddress());
                        hasDev = true;
//                        BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(device.getAddress());
                        mDevice = device;
                        mDeviceName = device.getName();
                        mDeviceAddress = device.getAddress();
                        try {
                            if (!connect(mDeviceAddress)) {
                                ismConnected_Data_Post = false;
                                ondetory();
                                return;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mScanning = false;
                    }
                }
            }
//            if (!hasDev){
//                scanLeDevice(true);
//            }
        }
    }

    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    };
    boolean ismScanning = false;
    UUID[] uuids = new UUID[]{UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")};

    /**
     * 搜索蓝牙设备
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if ((mLeScanCallback != null) & (mBluetoothAdapter != null)) {
            if ((enable || !ismScanning)) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                //            mBluetoothAdapter.startLeScan(uuids,mLeScanCallback);
                ismScanning = true;
            } else {
                ismScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        } else {
            Log.e(TAG, "蓝牙问题复现");
        }
    }

    boolean ismConnected_Data_Post = false;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            if (device == null || device.getName() == null || ismConnected_Data_Post)
                return;
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(self);
//            String address = sp.getString("ble_bind_mac", null);
            Log.e("hm", "device.getName=" + device.getName() + "device.getAddress=" + device.getAddress());
            if (device.getName() == null)
                return;
            if ((device.getName().equalsIgnoreCase("chexiaona") || device.getName().equalsIgnoreCase("carbean")
                    || device.getName().equals("BLE4.0") || device.getName().equalsIgnoreCase("ble4.0")
                    || device.getName().equalsIgnoreCase("JDY-10-V2.4")
                    || device.getName().equalsIgnoreCase("HC-31")
                    || device.getName().equalsIgnoreCase("SH-HC-08") || device.getName().startsWith("MLT")
                    || device.getName().equalsIgnoreCase("jdy-10")
                    || device.getName().equalsIgnoreCase("BluetoothAudioV2"))) {

                scanLeDevice(false);
                ismConnected_Data_Post = true;
                mDevice = device;
                mDeviceName = device.getName();
                mDeviceAddress = device.getAddress();
                try {
                    if (!connect(mDeviceAddress)) {
                        ismConnected_Data_Post = false;
                        ondetory();
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mScanning = false;
                return;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBReceive != null)
            unregisterReceiver(mBReceive);
        unregisterReceiver(bluetoothNaviReceiver);
    }

    private void GattUpdate(String action, String key, String msg) {
        if (BluetoothDataMessage.ACTION_GATT_CONNECTED.equals(action)) {
            mHandler_Handler.removeCallbacks(getConntinue_runnable);
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }

            m_DataSize = 0;
            m_ConnectionE = System.currentTimeMillis();
            mConnectionState = STATE_CONNECTED;
            mBluetoothGatt.discoverServices();
            if (!mConnected_Data_Post) {
                mConnected_Data_Post = true;
                mHandler_Conntion.postDelayed(conntinue_runnable, CONNECTION_TIME);
            }

            mConnected = true;
            cleatTimer();
            BluetoothGattInterface.getInterface().setmBluetoothName(mDeviceName);
            BluetoothGattInterface.getInterface().setmBluetoothMac(mDeviceAddress);
            BluetoothGattInterface.getInterface().setmConn(true);

            Intent intent = new Intent(BleBroadcastAction.BLE_STATUS);
            intent.putExtra(BleBroadcastAction.CONNTINUE, true);
            intent.putExtra(BleBroadcastAction.MAC, mDeviceAddress);
            intent.putExtra(BleBroadcastAction.NAME, mDeviceName);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


        } else if (BluetoothDataMessage.ACTION_GATT_DISCONNECTED.equals(action)) {
            ondetory();
        } else if (BluetoothDataMessage.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            //FIXME 获取蓝牙设备列表
            displayGattServices(getSupportedGattServices());
        } else if (BluetoothDataMessage.ACTION_DATA_AVAILABLE.equals(action)) {
            //FIXME 获取硬件发送的数据
            displayData(msg);
        } else if (BluetoothDataMessage.ACTION_CHAR_WRITE_CALLBALK.equals(action)) {
//                UUIDData(intent.getStringExtra(BluetoothLeService.EXTRA_UUID));
//                RSSIData(intent.getStringExtra(BluetoothLeService.EXTRA_RSSI));
        }
    }

    private void ondetory() {

        scanLeDevice(true);

        m_DataSize = 0;
        mConnectionState = STATE_DISCONNECTED;
        if (mConnected_Data_Post) {
            mConnected_Data_Post = false;
            mHandler_Conntion.removeCallbacks(conntinue_runnable);
        }
        ismConnected_Data_Post = false;
        mScanning = false;
        mConnected = false;
        mDevice = null;
        mDeviceAddress = null;
        mDeviceName = null;
        BluetoothGattInterface.getInterface().setmBluetoothName("");
        BluetoothGattInterface.getInterface().setmBluetoothMac("");
        BluetoothGattInterface.getInterface().setmConn(false);
        close();
        Destory();//FIXME 销毁连接
//        setTimer();

        Intent intent = new Intent(BleBroadcastAction.BLE_STATUS);
        intent.putExtra(BleBroadcastAction.CONNTINUE, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void GetVersion() {

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (mNotifyCharacteristic == null)
                    return;

                byte[] value = new byte[1];
                value[0] = (byte) 0x01;
                mNotifyCharacteristic.setValue(value[0],
                        BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                writeCharacteristic(mNotifyCharacteristic);
                timer.cancel();
                Log.e("hm", "发送获取版本请求");
            }
        }, 1000);
    }

    public void GetElectricity(int time) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (mNotifyCharacteristic == null)
                    return;
                if (mConnected) {
                    byte[] value = new byte[1];
                    value[0] = (byte) 0x02;
                    mNotifyCharacteristic.setValue(value[0],
                            BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    writeCharacteristic(mNotifyCharacteristic);
                    Log.e("hm", "发送获取设备电量请求");
                }
            }
        }, time);


    }

    private static ComponentName componentName;
    private static DevicePolicyManager policyManager;
    int targ = 0;

    public static void wakeUpAndUnlock(Context context) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
            //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
            //释放
            wl.release();
        }

    }

    private List<BleDataListener> listenerList;

    public void addListener(BleDataListener listener) {
        if (null == listener) {
            return;
        }
        if (null == listenerList) {
            listenerList = new ArrayList<>();
        }
        listenerList.add(listener);
    }

    public void removeListener(BleDataListener listener) {
        if (null == listener) {
            return;
        }
        if (null == listenerList || listenerList.size() <= 0) {
            return;
        }
        listenerList.remove(listener);
    }

    @Override
    public void onBleDataChanged(final String data) {
        if (null == listenerList || listenerList.size() <= 0) {
            return;
        }

        String[] dataArr = data.split(",");
        if (dataArr[6].equals("2") || dataArr[6].equals("4") || dataArr[6].equals("5")) {
            for (BleDataListener listener : listenerList) {
                listener.onBleDataChanged(data);
            }
        }
    }


    class AdminReceiver extends DeviceAdminReceiver {

    }

    private void displayData(String data) {
        Log.e("xubin", "Data = " + data);
        //唤醒屏幕
        wakeUpAndUnlock(this);
        //解锁
        disableKeyguard();
        if (data != null) {
            //FIXME 处理数据，APP做对应的响应
            m_Handler.ReceiveData(data, false);
            if (m_Handler.GetKeyStatus(data)) {
                m_DataSize++;
                if (m_DataSize == 1) {
                    GetVersion();
                    GetElectricity(3000);
                }
            }

//            SharedPreferencesUtil util = new SharedPreferencesUtil(this);
            //&& !util.getSettingBluetoothLongConnect()
            if (mConnected_Data_Post) {
                mHandler_Conntion.removeCallbacks(conntinue_runnable);
            }
//            if (!util.getSettingBluetoothLongConnect()) {
//                mHandler_Conntion.postDelayed(conntinue_runnable, CONNECTION_TIME);
////                mHandler_Conntion.postDelayed(conntinue_runnable, 10000);
//            }
        }
    }

//    public void Conntinue_Ten() {
//        SharedPreferencesUtil util = new SharedPreferencesUtil(this);
//        if (mConnected_Data_Post && !util.getSettingBluetoothLongConnect()) {
//            mHandler_Conntion.removeCallbacks(conntinue_runnable);
//        }
//        if (!util.getSettingBluetoothLongConnect()) {
//            mHandler_Conntion.postDelayed(conntinue_runnable, CONNECTION_TIME);
//        }
//    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, UUIDGattAttributes.lookup(uuid, "UnKnow"));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equals(UUIDGattAttributes.UUID_FFE1)) {
                    final int charaProp = gattCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = gattCharacteristic;
                        setCharacteristicNotification(
                                gattCharacteristic, true);
                    }

                    send();
                    byte[] value = new byte[1];
                    value[0] = (byte) 0x03;
                    gattCharacteristic.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT16, 0);

                    writeCharacteristic(gattCharacteristic);

                    GetVersion();
                    GetElectricity(3000);
                    return;
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mConnected)
            setTimer();

        if (intent != null) {
            boolean getEC = intent.getBooleanExtra("getEC", false);
            if (getEC) {
                GetElectricity(3000);
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BleBroadcastAction.ELECTRICITY);
        filter.addAction(BleBroadcastAction.UPDATE_DATA);
        mBReceive = new GetReceiver();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_SCREEN_ON);
        filter1.addAction(Intent.ACTION_SCREEN_OFF);
        filter1.setPriority(999);
        registerReceiver(mBReceive, filter);
        registerReceiver(screenState, filter1);
        registerReceiver(bluetoothState, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        //注册广播
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothNavigationHandler.BLUETOOTHNAVIGATION);
        registerReceiver(bluetoothNaviReceiver, filter2);

        return START_STICKY;
    }

    GetReceiver mBReceive = null;

    public class GetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BleBroadcastAction.ELECTRICITY)) {
                GetElectricity(3000);
            } else if (intent.getAction().equals(BleBroadcastAction.UPDATE_DATA)) {
                //更新数据--发广播
                Intent intent2 = new Intent(BleBroadcastAction.BLE_STATUS);
                intent2.putExtra(BleBroadcastAction.CONNTINUE, mConnected);
//                intent2.putExtra(BleBroadcastAction.MAC,mDeviceAddress);
//                intent2.putExtra(BleBroadcastAction.NAME,mDeviceName);
                //LocalBroadcastManager.getInstance(mThis).sendBroadcast(intent2);
            }
        }
    }

    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_ON:
                    scanLeDevice(true);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    //GattUpdate(BluetoothDataMessage.ACTION_GATT_DISCONNECTED, null,null);
                    break;
                case BluetoothAdapter.STATE_OFF:
                    GattUpdate(BluetoothDataMessage.ACTION_GATT_DISCONNECTED, null, null);
                    scanLeDevice(false);
                    break;
            }
        }
    };

    BroadcastReceiver screenState = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //屏幕关闭之后,锁屏
//                reenableKeyguard();
            }
        }
    };
    KeyguardManager km = null;

    String LOG_TAG = "unLock";
    boolean isLocked = false;

    public synchronized void disableKeyguard() {
        try {
            if (km == null)
                km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//参数是LogCat里用的Tag

            if (isLocked) {
                isLocked = false;
            }
        } catch (Exception e) {

        }

    }

//    public synchronized void reenableKeyguard() {
//        try {
//            if (!isLocked) {
//                if (keyguardLock == null) {
//                    keyguardLock = km.newKeyguardLock(LOG_TAG);
//                }
//                if (keyguardLock == null)
//                    return;
//                keyguardLock.reenableKeyguard();
//                keyguardLock = null;
//                isLocked = true;
//            }
//        } catch (Exception e) {
//
//        }
//
//    }

    BroadcastReceiver mBR = null;
    IntentFilter mIF = null;

    //向下位机发送开始导航、结束导航、路名
    private BroadcastReceiver bluetoothNaviReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothNavigationHandler.BLUETOOTHNAVIGATION)) {
                if (mBluetoothGatt != null && mNotifyCharacteristic != null) {
                    byte[] data = intent.getByteArrayExtra("data");

                    mNotifyCharacteristic.setValue(data);
                    mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);

                    for (int i = 0; i < data.length; i++) {
                        Log.d("mytraffic date", data[i] + "");
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public void send() {
        if (mBluetoothGatt != null) {
//            byte[] data = EAApplication.self.getTimeBytes();
//            if (mNotifyCharacteristic != null) {
//                for (int i = 0; i < data.length; i++)
//                    Log.d("time value", data[i] + "");
//
//                mNotifyCharacteristic.setValue(data);
//                mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
//            }

        }
        TimeHandle.sendEmptyMessageDelayed(3, 55500);
    }

    //向下位机发送时间
    Handler TimeHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                send();
            }
        }
    };
}
