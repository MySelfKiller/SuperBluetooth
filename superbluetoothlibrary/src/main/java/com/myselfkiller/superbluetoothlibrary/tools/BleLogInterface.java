package com.myselfkiller.superbluetoothlibrary.tools;


import java.text.SimpleDateFormat;

/**
 * Created by bing on 2015/8/31.
 */
public class BleLogInterface {
    private static BleLogInterface mLogInterface = null;
    public static BleLogInterface GetInstance()
    {
          if(mLogInterface == null)
              mLogInterface = new BleLogInterface();
        return mLogInterface;
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public void AddLog_Ble(String log) {
//		if (!mbUserLog)
//			return;
//        String dir = UtilsFile.getEnaviBaseStorage(EAApplication.self)+"/log/bluetooth/";
//        if (null == dir)
//            return;
//        try {
//            FileWriter fw = new FileWriter((dir + "_Ble_log.txt"), true);
//            String date = dateFormat.format(new Date());
//            fw.write(date + " " + log + "\r\n");
//            fw.close();
//        } catch (Exception ex) {
//        }
    }

}
