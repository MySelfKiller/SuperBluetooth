package com.myselfkiller.superbluetoothlibrary.b4;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HuangMin on 2015/8/20.
 */
public class BlueMessageHandle {

    private final int TIMER = 1000;
    private Context m_poContext = null;
    private String m_PreMsg = null;
    private long m_Time;
    private Timer m_pTimer;
    private TimerTask m_pTask;

    private Handler mHandler;

    private boolean m_lClick = false;

    private void ClearTimer() {
        if (m_pTask != null)
            m_pTask.cancel();

        if (m_pTimer != null)
            m_pTimer.cancel();
        m_pTimer = null;
        m_pTask = null;
        m_lClick = false;
    }

    private void SetTimer() {
        ClearTimer();
        m_pTimer = new Timer();
        m_pTask = new TimerTask() {
            @Override
            public void run() {
                m_lClick = true;
                ReceiveData(m_PreMsg, true);
            }
        };
        m_pTimer.schedule(m_pTask, TIMER, 100);
    }

//    public BlueMessageHandle(Handler handler, Context context) {
//        m_poContext = context;//EnaviAplication.getContext();
//    }

    private BleDataListener listener;
    public BlueMessageHandle(Context context,BleDataListener listener) {
        m_poContext = context;//EnaviAplication.getContext();
        this.listener = listener;
        mHandler = new MessageHandler(listener);
    }

    public void ResetData() {
        ClearTimer();
        m_PreMsg = null;
    }
//$GNGGA,,,,,,0,00,127.00,,,,,,*62
//    public static final String GGA = "$GNGGA,062259.000,3959.202797,N,11622.892641,E,5,18,0.84,36.639,M,0,M,1,2933*5D\r\n";//默认数据，需要客户提供
    public void ReceiveData(String data){
        if (data.startsWith("$GNGGA")){
            String[] dataArr = data.split(",");
            if (dataArr.length>=14){
                if ((null != dataArr[2] && !dataArr[2].equals("")) &&
                        (null != dataArr[4] && !dataArr[4].equals(""))){
//                    Log.e("ReceiveData", data);
                    mHandler.sendMessage(mHandler.obtainMessage(0,data));
                }
            }
        }
    }
    /**
     * 新版本数据协议解析
     *
     * @param msg
     */
    public void ReceiveData(String msg, boolean lclick) {
        if (null != msg && !"".equals(msg)) {
            Log.d("xubin", "  Receive Data ");
            String[] dateList = msg.split(" ");
            //            if (dateList.length == 5 && dateList[0].equals("5B")) {
            //                int hardwareversion = Integer.parseInt(dateList[1], 16);
            //                int bleversion = Integer.parseInt(dateList[2], 16);
            //                int appversion = Integer.parseInt(dateList[3], 16);
            //                //版本号
            //                //Toast.makeText(m_poContext, "硬件版本号:" + hardwareversion / 10.0 + " 蓝牙版本 ：" + bleversion / 10.0 + " 最低要求应用版本：" + appversion / 10.0, Toast.LENGTH_LONG).show();
            //                return;
            //            }
            //            if (dateList.length == 3 && dateList[0].equals("5C")) {
            //                int electricity = Integer.valueOf(dateList[1], 16);//电量百分比
            //                //FIXME 发送小硬件电量广播
            //                Intent intent = new Intent();
            //                intent.setAction(BleBroadcastAction.BLE_STATUS);
            //                intent.putExtra(BleBroadcastAction.ELECTRICITY,electricity);
            //                LocalBroadcastManager.getInstance(m_poContext).sendBroadcast(intent);
            //Toast.makeText(m_poContext, "当前电量：" + electricity + "%", Toast.LENGTH_SHORT).show();
            //                return;
            //            }

            if (dateList[0].equals("DF") && dateList[dateList.length - 1].equals("FD")) {//按键识别
                //                if (m_PreMsg == null) {
                //                    ClearTimer();
                //                    m_PreMsg = msg;
                //                    m_Time = System.currentTimeMillis();
                //                    SetTimer();
                //                } else {
                //                    ClearTimer();
                //                    msg = m_PreMsg;
                //                    String[] keys = msg.split(" ");
                //                    key = Integer.parseInt(keys[1], 16);
                //                    m_lClick = lclick;
                //                    initsound();
                //                }
                ClearTimer();
                m_PreMsg = msg;
                String[] keys = msg.split(" ");
                key = Integer.parseInt(keys[1], 16);
                m_lClick = lclick;
            }
        }
    }



    int key = -1;
    public boolean GetKeyStatus(String msg) {
        String[] list1 = msg.split(" ");
        if (list1.length != 3 || (list1.length == 3 && !list1[0].equals("DF"))) {
            return false;
        }

        if (list1[1].equals("00")) {
            return true;
        }
        return false;
    }
}
