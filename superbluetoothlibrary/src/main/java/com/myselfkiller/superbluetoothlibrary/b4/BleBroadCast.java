package com.myselfkiller.superbluetoothlibrary.b4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.myselfkiller.superbluetoothlibrary.tools.BleBroadcastAction;


/**
 * Created by bing on 2015/8/25.
 */
public class BleBroadCast extends BroadcastReceiver {
    public static final int BLE_DATA_CONNTINUE = 1;//连接相关
    public static final int BLE_DATA_ELECTRICITY = 2;//电量相关
    public static final int BLE_DATA_MAC = 3;//地址相关
    public static final int BLE_DATA_KEY = 4;//key
    public static final int BLE_DATA_ISSTART = 6;//是否点火
    public static final int BLE_DATA_ISPOWERON = 7;//是否点火
    private Handler mHandler = null;
    public BleBroadCast(Handler handler) {
        mHandler = handler;
    }

    public IntentFilter GetMyBroadCastFiter()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BleBroadcastAction.KEY);
        filter.addAction(BleBroadcastAction.BLE_STATUS);
        return filter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null)
            return ;

        String active = intent.getAction();
        if(active == null)
            return ;

        if(active.equals(BleBroadcastAction.KEY))
        {
            Key_OnClick_Active(context,intent.getStringExtra(BleBroadcastAction.KEY_ONCLICK));
            Key_Long_Active(intent.getStringExtra(BleBroadcastAction.KEY_LONG));
            Key_Double_Active(intent.getStringExtra(BleBroadcastAction.KEY_DOUBLE));
            Key_Move_Active(intent.getStringExtra(BleBroadcastAction.KEY_MOVE));
        }else if(active.equals(BleBroadcastAction.BLE_STATUS))
        {
            BleStatus_Active(intent);
        }

    }

    public void BleStatus_Active(Intent intent)
    {
        if(intent == null)
        {
            return;
        }

        if(intent.hasExtra(BleBroadcastAction.CONNTINUE))
        {
            boolean con = intent.getBooleanExtra(BleBroadcastAction.CONNTINUE,false);
            if(con)
                mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_CONNTINUE,1,0));
            else
                mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_CONNTINUE,0,0));
        }
        if(intent.hasExtra(BleBroadcastAction.ELECTRICITY))
        {
            int con = intent.getIntExtra(BleBroadcastAction.ELECTRICITY, 0);
            mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_ELECTRICITY,con,0));
        }
        if(intent.hasExtra(BleBroadcastAction.MAC))
        {
            String mac = intent.getStringExtra(BleBroadcastAction.MAC);
            String name = intent.getStringExtra(BleBroadcastAction.NAME);
            mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_MAC,mac+"_" + name));
        }
        if (intent.hasExtra(BleBroadcastAction.ISSTART))
        {
            boolean isStart = intent.getBooleanExtra(BleBroadcastAction.ISSTART,false);
            mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_ISSTART,isStart));
        }
        if (intent.hasExtra(BleBroadcastAction.POWER_ON))
        {
            boolean is_power_on = intent.getBooleanExtra(BleBroadcastAction.POWER_ON,false);
            mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_ISPOWERON,is_power_on));
        }


    }
    public void Key_OnClick_Active(Context context,String key)
    {
        if(key == null)
            return;
        mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_KEY,key));
//        if(key.equals(BleBroadcastAction.ACTION_KEY_LAFT_TOP))
//        {
//        	mHandler.sendEmptyMessage(6);
////        	MainInfo.GetInstance().getM_NaviTools().SetViewVisable(true);
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_LAFT_DOWN))
//        {
//        	MainInfo.GetInstance().getM_NaviTools().SetViewVisable(false);
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_CENTER))
//        {
//        	AMapNavi.getInstance(context).readTrafficInfo(0);
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_TOP))
//        {
//        	SoundPlayerControler.getInstance().stopPlay();
//        	AMapNavi.getInstance(context).readNaviInfo();
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_DOWN))
//        {
//        	MainInfo.GetInstance().getM_NaviTools().getAMapNaviView().findViewById(2131427365).performClick();
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_RIGHT_TOP))
//        {
//        	AMapNavi.getInstance(context).reCalculateRoute(AMapNavi.DrivingFastestTime);
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_RIGHT_DOWM))
//        {
//        	MainInfo.GetInstance().setNaviMute(!MainInfo.GetInstance().getNaviMute());
//        	SoundPlayerControler.getInstance().stopPlay();
//        }

    }

    public void Key_Long_Active(String key)
    {
        if(key == null)
            return;
        mHandler.sendMessage(mHandler.obtainMessage(BLE_DATA_KEY,key));
//        if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_LAFT_TOP))
//        {
//
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_LAFT_DOWN))
//        {
//            mHandler.sendEmptyMessage(5);
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_TOP))
//        {
//
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_DOWN))
//        {
//
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_CENTER))
//        {
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_RIGHT_TOP))
//        {
//
//        }else if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_RIGHT_DOWM))
//        {
//
//        }

    }
    public void Key_Double_Active(String key)
    {
        if(key == null)
            return;
        mHandler.sendMessage(mHandler.obtainMessage(4,key));
        if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_LAFT_TOP))
        {

        }

    }

    public void Key_Move_Active(String key)
    {
        if(key == null)
            return;
        mHandler.sendMessage(mHandler.obtainMessage(4,key));
        if(key.equals(BleBroadcastAction.ACTION_KEY_LONG_LAFT_TOP))
        {

        }

    }
}
