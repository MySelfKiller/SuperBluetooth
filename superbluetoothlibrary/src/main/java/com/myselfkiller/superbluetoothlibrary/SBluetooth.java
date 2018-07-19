package com.myselfkiller.superbluetoothlibrary;

import android.bluetooth.BluetoothDevice;

import java.util.HashSet;

/**
 * Created by Killer on 2018/7/18.
 */

public class SBluetooth implements SBluetoothListener{
    private HashSet<SBluetoothListener> listenerList;
    private static volatile SBluetooth self;
    private SBluetooth(){
        listenerList = new HashSet<SBluetoothListener>();
    }
    public static SBluetooth getInstance(){
        if (null == self){
            synchronized (SBluetooth.class){
                self = new SBluetooth();

            }
        }
        return self;
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

    }





    @Override
    public void onBluetoothNotSupported() {

    }

    @Override
    public void onBluetoothDisabled() {

    }

    @Override
    public void onBluetoothDeviceDisconnected() {

    }

    @Override
    public void onConnectingBluetoothDevice() {

    }

    @Override
    public void onBluetoothDeviceConnected(String name, String address) {

    }

    @Override
    public void onBluetoothSerialRead(String message) {

    }

    @Override
    public void onBluetoothSerialWrite(String message) {

    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {

    }
}
