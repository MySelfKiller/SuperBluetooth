package com.myselfkiller.superbluetoothlibrary.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * Created by bing on 2015/8/31.
 */
public class AppTaskTools {
    public static boolean isBackground(String name ,Context m_poContext) {
        ActivityManager activityManager = (ActivityManager) m_poContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(100);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(m_poContext.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
