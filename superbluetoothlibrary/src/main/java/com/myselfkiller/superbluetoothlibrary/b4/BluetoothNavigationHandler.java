package com.myselfkiller.superbluetoothlibrary.b4;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Administrator on 2017/1/23.
 */

public class BluetoothNavigationHandler extends Handler {

    public final static String BLUETOOTHNAVIGATION = "bluetooth_navigation";
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(msg.what ==1){
            startSend();
        }
        else if (msg.what == 2){
            sendRoute();
        }
        else if (msg.what == 3){
            sendTraffic();
        }

    }

    public void sendTraffic(){
        sendEmptyMessageDelayed(3,30000);

    }

    public void startSend(){
        //发送广播
        sendEmptyMessageDelayed(1,1000);

    }
    public void sendRoute(){
        sendEmptyMessageDelayed(2,3069);

    }

}
