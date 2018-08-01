package com.myselfkiller.superbluetoothlibrary;

import android.content.Context;

import com.myselfkiller.superbluetoothlibrary.b2.SBluetoothImpl;

/**
 * Created by Killer on 2018/7/18.
 */

public class SBluetooth {

    private SBluetoothImpl sBluetooth;
    private static volatile SBluetooth self;
    private SBluetooth(Context context){
        sBluetooth = new SBluetoothImpl(context);
    }
    public static SBluetooth getInstance(Context context){
        if (null == self){
            synchronized (SBluetooth.class){
                self = new SBluetooth(context);

            }
        }
        return self;
    }

    public void setListener(SBluetoothListener listener){
        sBluetooth.setListener(listener);
    }

    public void removeListener(SBluetoothListener listener){
        sBluetooth.removeListener(listener);
    }

    public void isShowBluetoothList(){
        sBluetooth.isShowBluetoothList();
    }

    public void searchBluetooth(){
        sBluetooth.searchBluetooth();
    }
}
