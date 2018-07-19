package com.myselfkiller.superbluetoothlibrary.b4;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import com.myselfkiller.superbluetoothlibrary.tools.ConstantBle;

import java.util.List;

/**
 * Created by bing on 2015/8/31.
 */
public class BleTools {

    public static boolean GetBluetooth_feature(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0);
        if (isAirplaneMode == 1) {
            Toast.makeText(context, "您的手机处于飞行模式,无法使用蓝牙", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        return true;
    }

    public static boolean GetBluttoothMode(Context context) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
            return false;
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
            return false;

        return mBluetoothAdapter.isEnabled();
    }

    public static boolean turnOffBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.disable();
        }
        return false;
    }


//    public static boolean GetBluetooth_Open(Context context) {
//        if (GetBluetooth_feature(context)) {
//            PackageManager pm = context.getPackageManager();
//            if (!PermissionUtils.checkPermission("android.permission.BLUETOOTH")) {
//                Toast.makeText(context, "没有蓝牙权限,请开启应用权限!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
//            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
//            boolean isEnabled = mBluetoothAdapter.isEnabled();
//            // 获取蓝牙服务isEnabled
//            if (!isEnabled) {//蓝牙打开并有蓝牙权限
//                return mBluetoothAdapter.enable();// 打开蓝牙
//            }
//
//            return true;
//        }
//        return false;
//    }

    public static boolean BluetoothConnect_Service_Open(Context context) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(200); //30是最大值
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (info.service.getClassName().equals(ConstantBle.MY_BLUETOOTH_SERVICE_NAME)) {
                isRunning = true;
            }
        }
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if (!isRunning) {
                Intent intt = new Intent(context, BluetoothDeviceConnectService.class);
                intt.putExtra("conn", true);
                context.startService(intt);
                isRunning = true;
                return true;
            }
        }

        return isRunning;

    }

    private String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }
}
