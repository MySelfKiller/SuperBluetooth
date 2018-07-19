package com.myselfkiller.superbluetoothlibrary.tools;

/**
 * 蓝牙广播action
 * Created by bing on 2015/5/1.
 */
public class BleBroadcastAction {
    /**
     * 默认模式,全部消息处理
     */
    public static final int ACTION_DEFAULE = 0x01;
    /**
     * 导航模式,开启导航相关内容
     */
    public static final int ACTION_NAVI = ACTION_DEFAULE + 1;

    /**
     * 开启导航
     */
    public static final String NAVI = "Navi";
    /**
     * 按键
     */
    public static final String KEY= "key_event";
    /**
     * 短按键
     */
    public static final String KEY_ONCLICK = "key_onclick";
    /**
     * 长按键
     */
    public static final String KEY_LONG = "key_longclick";
    /**
     * 双击按键
     */
    public static final String KEY_DOUBLE = "key_doubleclick";
    /**
     * 连续按键
     */
    public static final String KEY_MOVE = "key_moveclick";
    /**
     * 蓝牙状态
     */
    public static final String BLE_STATUS = "ble_status";
    /**
     * 连接状态
     */
    public static final String CONNTINUE = "continue";
    /**
     * 点火状态
     */
    public static final String ISSTART = "is_start";

    /**
     * dock开机
     */
    public static final String POWER_ON = "power_on";


    public static final String ELECTRICITY = "skylead.hear.electricity";
    public static final String MAC = "skylead.hear.blemac";
    public static final String NAME = "skylead.hear.bleadress";
    public static final String UPDATE_DATA = "skylead.hear.update.data";
    /**
     * 关闭导航
     */
    public static final String NAVI_DESTROY = "Navi_Destory";
    /**
     * 路线预览
     */
    public static final String NAVI_PATHLOOK= "path_look";

    /**
     * 放大地图
     */
    public static final String NAVI_ZOOMIN = "zoomin";
    /**
     * 缩小地图
     */
    public static final String NAVI_ZOOMOUT = "zoomout";
    /**
     * 静音
     */
    public static final String NAVI_SOUNDPLAYER = "mute";
    /**
     * 截屏
     */
    public static final String NAVI_SCREEN = "screen";
    /**
     * 一键回家
     */
    public static final String NAVI_GOHOME = "gohome";
    /**
     * 一键回公司
     */
    public static final String NAVI_GOWORK = "gowork";
    /**
     * 重新播报导航语音
     */
    public static final String NAVI_RESOUND = "reset_voice_navi";
    /**
     * 重新规划合适路线
     */
    public static final String NAVI_RESETPATH = "reset_path";
    /**
     * 地图平移
     */
    public static final String NAVI_MAPTRANSLATION_TOP = "maptranslation_top";
    public static final String NAVI_MAPTRANSLATION_BUTTON = "maptranslation_button";
    public static final String NAVI_MAPTRANSLATION_RIGHT = "maptranslation_right";
    public static final String NAVI_MAPTRANSLATION_LEFT = "maptranslation_left";
    /**
     * 交通信息播报关闭开启
     */
    public static final String NAVI_TRAFFIC_VOICE_OFF = "traffic_vocie_off";
    public static final String NAVI_TRAFFIC_VOICE_ON = "traffic_vocie_on";

    /**
     * 路况开启关闭
     */
    public static final String NAVI_ROAD_CONDTION_OFF = "roadcondtion_off";
    public static final String NAVI_ROAD_CONDTION_ON = "roadcondtion_on";
    /**
     * 导航结束
     */
    public static final String NAVI_STOP = "navi_stop";
    /**
     * 路况上报
     */
    public static final String NAVI_TRAFFIC_UPLOAD = "navi_traffic_upload";
    /**
     * 开启黑夜模式
     */
    public static final String NAVI_MODE_NIGHT = "night";
    /**
     * 开启三维模式
     */
    public static final String NAVI_MODE_TREE_DIMENSIONAL  = "three_dimensional";
    /**
     * 开启3D模式
     */
    public static final String NAVI_MODE_3d = "3d";
    /**
     * 开启2D模式
     */
    public static final String NAVI_MODE_2d = "2d";
    /**
     * 开启街景模式
     */
    public static final String NAVI_MODE_STREET = "streetscape";
    /**
     * 开启HUD模式
     */
    public static final String NAVI_MODE_HUD = "hud";
    /**
     * 开启驾车模式
     */
    public static final String NAVI_MODE_CAR_ON = "car_on";
    /**
     * 关闭驾车模式
     */
    public static final String NAVI_MODE_CAR_OFF = "car_off";
    /**
     * 开启电子狗
     */
    public static final String NAVI_MODE_ELECTRONDOG_ON = "electrondog_on";
    /**
     * 关闭电子狗
     */
    public static final String NAVI_MODE_ELECTRONDOG_OFF = "electrondog_off";
    
    /**
     * 按键事件
     */
    public static final String ACTION_KEY_TOP = "com.pdager.key.top";
    public static final String ACTION_KEY_DOWN = "com.pdager.key.down";
    public static final String ACTION_KEY_CENTER = "com.pdager.key.center";

    public static final String ACTION_KEY_LAFT_TOP = "com.pdager.key.laft_top";
    public static final String ACTION_KEY_RIGHT_TOP = "com.pdager.key.right_top";
    public static final String ACTION_KEY_LAFT_DOWN = "com.pdager.key.laft_down";
    public static final String ACTION_KEY_RIGHT_DOWM = "com.pdager.key.right_dowm";

    public static final String ACTION_KEY_LONG_TOP = "com.pdager.key.long_top";
    public static final String ACTION_KEY_LONG_DOWN = "com.pdager.key.long_down";
    public static final String ACTION_KEY_LONG_CENTER = "com.pdager.key.long_center";

    public static final String ACTION_KEY_LONG_LAFT_TOP = "com.pdager.key.long_laft_top";
    public static final String ACTION_KEY_LONG_RIGHT_TOP = "com.pdager.key.long_right_top";
    public static final String ACTION_KEY_LONG_LAFT_DOWN = "com.pdager.key.long_laft_down";
    public static final String ACTION_KEY_LONG_RIGHT_DOWM = "com.pdager.key.long_right_dowm";

//    public static final String KEY_UP_LEFT = "up_left";
//    public static final String KEY_UP_RIGHT = "up_right";
//    public static final String KEY_UP_TOP = "up_top";
//    public static final String KEY_UP_BUTTON = "up_button";
//    public static final String KEY_UP_CENTER = "up_center";
//
//    public static final String KEY_DOWN_LEFT = "down_left";
//    public static final String KEY_DOWN_RIGHT = "down_right";
//    public static final String KEY_DOWN_TOP = "down_top";
//    public static final String KEY_DOWN_BUTTON = "down_button";
//    public static final String KEY_DOWN_CENTER = "down_center";
//
//    public static final String KEY_LONG_LEFT = "long_left";
//    public static final String KEY_LONG_RIGHT = "long_right";
//    public static final String KEY_LONG_TOP = "long_top";
//    public static final String KEY_LONG_BUTTON = "long_button";
//    public static final String KEY_LONG_CENTER = "long_center";
    public static final String KEY_HOME = "key_home";

}
