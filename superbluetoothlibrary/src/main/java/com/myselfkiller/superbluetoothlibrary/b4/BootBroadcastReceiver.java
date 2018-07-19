package com.myselfkiller.superbluetoothlibrary.b4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法  
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务  
//    	if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//    		return;
//    	}
        Intent service = new Intent(context,BluetoothDeviceConnectService.class);
        context.startService(service);  
        Log.v("KAIJIQIDONG", "开机自动服务自动启动.....");

//        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("skylead.hear");
//
//        context.startActivity(launchIntent);
    }
  
} 