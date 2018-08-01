package com.myselfkiller.superbluetoothlibrary.b2;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.myselfkiller.superbluetoothlibrary.SBluetoothListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Killer on 2018/7/20.
 */

public class SBluetoothImpl implements BluetoothSerialListener {
    private HashSet<SBluetoothListener> listenerList;
    private BluetoothSerial bluetoothSerial;
    private Context context;
    public SBluetoothImpl(Context context){
        this.context = context;
        listenerList = new HashSet<SBluetoothListener>();
        bluetoothSerial = new BluetoothSerial(context,this);
        try {
            bluetoothSerial.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
            if (!bluetoothSerial.isConnected()) {
                bluetoothSerial.start();
            }
        }
    }

    public void setListener(SBluetoothListener listener){
        listenerList.add(listener);
    }

    public void removeListener(SBluetoothListener listener){
        listenerList.remove(listener);
    }

    public void isShowBluetoothList(){

    }

    public void searchBluetooth(){

        showDeviceListDialog();
    }

    BluetoothDeviceListDialog dialog;
    private void showDeviceListDialog() {
        // Display dialog for selecting a remote Bluetooth device
        dialog = new BluetoothDeviceListDialog(context);
        dialog.setOnDeviceSelectedListener(new BluetoothDeviceListDialog.OnDeviceSelectedListener() {
            @Override
            public void onBluetoothDeviceSelected(BluetoothDevice device) {

            }
        });
        dialog.setTitle("选择配对设备连接");
        Set<BluetoothDevice> set = new HashSet<>();
        Set<BluetoothDevice> temSet = bluetoothSerial.getPairedDevices();
        if (null != temSet){
            set.addAll(temSet);
        }
        dialog.setDevices(set);
        dialog.showAddress(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bluetoothSerial.stopScanDevice();
            }
        });
//        dialog.setOnDismissListener(this);
        dialog.show();
        bluetoothSerial.ScanDevices();
    }


    @Override
    public void onBluetoothNotSupported() {
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onBluetoothNotSupported();
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }
    }

    @Override
    public void onBluetoothDisabled() {
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onBluetoothDisabled();
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }
    }

    @Override
    public void onBluetoothDeviceDisconnected() {
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onBluetoothDeviceDisconnected();
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }

    }

    @Override
    public void onConnectingBluetoothDevice() {
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onConnectingBluetoothDevice();
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }
    }

    @Override
    public void onBluetoothDeviceConnected(String name, String address) {
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onBluetoothDeviceConnected(name,address);
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }
    }

    @Override
    public void onBluetoothSerialRead(String message) {
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onBluetoothSerialRead(message);
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }
    }

    @Override
    public void onBluetoothSerialWrite(String message) {
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onBluetoothSerialWrite(message);
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }
    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {
        dialog.updateDevices(device);
        if (listenerList.size()>0){
            for (SBluetoothListener listener : listenerList){
                listener.onBluetoothDeviceFound(device);
            }
        }else {
            Log.e("SBluetoothImpl","没有回调函数");
        }
    }
}
