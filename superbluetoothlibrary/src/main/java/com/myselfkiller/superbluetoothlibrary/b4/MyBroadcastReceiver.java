package com.myselfkiller.superbluetoothlibrary.b4;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import com.myselfkiller.superbluetoothlibrary.tools.ConstantBle;

import java.util.List;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("hm",context == null ? "你好":"我不好");
		if(context == null){
			Intent i = new Intent(context,BluetoothDeviceConnectService.class);
			context.startService(i);
			return;
		}
		if (context != null && !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
			return;

		boolean isAppRunning = false;
		boolean isSerRun = false;

		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			Log.e("hm", "接收到时间改变的广播");
			// 检查Service状态
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> processList = manager.getRunningAppProcesses();
			if (null != processList && processList.size()>0){
				for (ActivityManager.RunningAppProcessInfo process : processList) {
					//判断当前进程是否已运行
					if (process.processName.equals(ConstantBle.MY_PKG_NAME)){
						isAppRunning = true;
						Log.e("hm", "进程已运行！");
					}
				}
			}
//			ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> infos = manager.getRunningServices(200); //200是最大值
			for(ActivityManager.RunningServiceInfo info : infos){
				if(info.service.getClassName().equals(ConstantBle.MY_BLUETOOTH_SERVICE_NAME)){
					isSerRun = true;
					Log.e("hm", "服务已运行！");
				}
			}
			if (!isAppRunning ) {
				Intent i = new Intent(context,BluetoothDeviceConnectService.class);
				if (isSerRun){
					Log.e("hm", "进程未运行，服务已运行！");
					context.stopService(i);
					Log.e("hm", "重启蓝牙服务");
				}
				Log.e("hm", "进程未运行，服务未运行！");
				context.startService(i);
			}else {
				if (!isSerRun){
					Log.e("hm", "进程已运行，服务未运行！");
					Intent i = new Intent(context,BluetoothDeviceConnectService.class);
					context.startService(i);
				}
			}
		}
	}

}
