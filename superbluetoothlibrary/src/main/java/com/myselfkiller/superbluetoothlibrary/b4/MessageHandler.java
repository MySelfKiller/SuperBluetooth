package com.myselfkiller.superbluetoothlibrary.b4;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Killer on 2018/6/20.
 */

class MessageHandler extends Handler {
    private BleDataListener listener;
    public MessageHandler(BleDataListener listener){
        this.listener = listener;
    }
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case 0:
               String strData = (String) msg.obj;
                listener.onBleDataChanged(strData);
        }
        super.handleMessage(msg);
    }
}
