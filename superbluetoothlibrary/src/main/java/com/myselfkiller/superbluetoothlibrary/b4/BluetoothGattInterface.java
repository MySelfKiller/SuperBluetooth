package com.myselfkiller.superbluetoothlibrary.b4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class BluetoothGattInterface {

	private static BluetoothGattInterface m_Interface = null;
	public static BluetoothGattInterface getInterface()
	{
		if(m_Interface == null)
			m_Interface = new BluetoothGattInterface();
		return m_Interface;
	}
	
	private Context m_Context = null;
	//BluetoothGattCharacteristic 集合
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
	            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//Oad集合
	private List<BluetoothGattCharacteristic> m_OadBluetoothGattCharacteristic = null;
	//BluetoothGattService
	private BluetoothGattService m_OadBluetoothGattService = null;
	//默认使用的BluetoothGattCharacteristic
	private BluetoothGattCharacteristic m_Notification = null;
	//默认使用的BluetoothGatt
	private BluetoothGatt mBluetoothGatt;
	//设备adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	//设备manager
	private BluetoothManager mBluetoothManager = null;
	//
    private String  mBluetoothName = "";
    private String mBluetoothMac = "";
    private boolean mConn = false;

    public boolean ismConn() {
        return mConn;
    }

    public void setmConn(boolean mConn) {
        this.mConn = mConn;
    }

    public String getmBluetoothName() {
        return mBluetoothName;
    }

    public void setmBluetoothName(String mBluetoothName) {
        this.mBluetoothName = mBluetoothName;
    }

    public String getmBluetoothMac() {
        return mBluetoothMac;
    }

    public void setmBluetoothMac(String mBluetoothMac) {
        this.mBluetoothMac = mBluetoothMac;
    }

    public Context getM_Context() {
		return m_Context;
	}
	public void setM_Context(Context m_Context) {
		this.m_Context = m_Context;
	}
	public BluetoothGattService getM_OadBluetoothGattService() {
		return m_OadBluetoothGattService;
	}
	public void setM_OadBluetoothGattService(
			BluetoothGattService m_OadBluetoothGattService) {
		this.m_OadBluetoothGattService = m_OadBluetoothGattService;
	}
	public ArrayList<ArrayList<BluetoothGattCharacteristic>> getmGattCharacteristics() {
		return mGattCharacteristics;
	}
	public void setmGattCharacteristics(
			ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics) {
		this.mGattCharacteristics = mGattCharacteristics;
	}
	public List<BluetoothGattCharacteristic> getM_OadBluetoothGattCharacteristic() {
		return m_OadBluetoothGattCharacteristic;
	}
	public void setM_OadBluetoothGattCharacteristic(
			List<BluetoothGattCharacteristic> m_OadBluetoothGattCharacteristic) {
		this.m_OadBluetoothGattCharacteristic = m_OadBluetoothGattCharacteristic;
	}
	public BluetoothGattCharacteristic getM_Notification() {
		return m_Notification;
	}
	public void setM_Notification(BluetoothGattCharacteristic m_Notification) {
		this.m_Notification = m_Notification;
	}
	public BluetoothGatt getmBluetoothGatt() {
		return mBluetoothGatt;
	}
	public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
		this.mBluetoothGatt = mBluetoothGatt;
	}
	public BluetoothAdapter getmBluetoothAdapter() {
		return mBluetoothAdapter;
	}
	public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
		this.mBluetoothAdapter = mBluetoothAdapter;
	}
	public BluetoothManager getmBluetoothManager() {
		return mBluetoothManager;
	}
	public void setmBluetoothManager(BluetoothManager mBluetoothManager) {
		this.mBluetoothManager = mBluetoothManager;
	}
	
	
}
