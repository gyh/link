package com.example.market.ljw.core.utils;

import com.example.market.ljw.entity.bean.output.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/10/20.
 */
public class Constant {

    //请求数据的KEY  todo YW
    public static final String KEY = "3T-NO+f@ULCSx3afEMOrvFgM8DrEq&e^I^oV$$";
    //cookie token key  todo yw
    public static final String KEY_S_TOKEN_ID = "s_token_id";

    //碎片历史TAG
    public static final String BACK_STACK_TAG = "back_stack_tag";
    //锁屏
    public static final String LOCK_SCREEN_ON_OFF = "lock_screen_on_off";
    public static boolean mIsLockScreenOn;
    //图片 基础站点
    public static String SERVERBASE_URL = "http://www.lj3w.com/";
    public static String SERVERBASE_DEBUG_URL="http://markettest.strosoft.com/";
    //网站 基础
    public static String LJWBASE_URL = "http://m.lj3w.com/";
    //站点的URL
    public static String SERVER_URL = "";
    //客户端类型
    public static final String ClientType = "Android";

    //注册地址
    public static String REGISTERURL = "";

    //判断是否是测试
    public static boolean DEBUG = true;

    public static final String SharedPreferencesKey = "LJW";

    //最后等待时间
    public static int theNextLen = 600;
    public static final int theWaitTime = 600;
    //定义一秒
    public  static final long delayMillis = 1000;
    //定义的心跳
    public static long intervalTime = 10;
    //本地记录用户信息
    public static Member member = null;
    //记录广告图片
    public static List<String> imageurls = new ArrayList<String>();

    //打开应用的包名
    public static String makeAppName = "";
    //lock图片
    public final static int IMG_WIDTH = 640;
    public final static int IMG_HEIGHT = 150;

    public final static long ENDTIME = 365*24*60*60*1000;

    //判断是否显示锁屏界面
    public static boolean isShowLock = true;

    public final static String baiduAppkey = "12bf738d12";

    public static final String PACKAGENAME = "com.example.market.ljw";
    public static final String PHONEPACKAGENAME = "com.android.phone";
    public static final String MMSPACKAGENAME="com.android.mms";
    public static final String CONTACTSPACKAGENAME ="com.android.contacts";

    public static final String SCHEME = "package";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    public static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    public static final String APP_PKG_NAME_22 = "pkg";
    /**
     * InstalledAppDetails所在包名
     */
    public static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * InstalledAppDetails类名
     */
    public static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    public static class ValueKey{
        public static final String URLKEY = "urlkey";
    }
    public static class ExtraKey{
        public static final String MEMBER ="member";
    }

    /**
     * 请求参数
     service_name
     version
     client_type
     token
     data
     request_data_format
     response_data_format
     * */
    public static class RequestKeys{
        public static final String SERVICENAME = "service_name";
        public static final String VERSION = "version";
        public static final String CLIENTTYPE = "client_type";
        public static final String TOKEN = "token";
        public static final String DATA = "data";
        public static final String REQUESTDATAFORMAT = "request_data_format";
        public static final String RESPONSEDATAFORMAT = "response_data_format";
    }

    /**
     * 本地存储的数据
     * */
    public static class SaveKeys{
        public static final String TOKENKEY = "token";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "passoword";
    }

    /**
     * 标志从哪里进入主页
     * */
    public static class FromWhere{
        public static final String KEY = "fromkey";
        public static final String LOGINACTIVITY = "fromlogin";
        public static final String FXSERVICE = "fromfxservice";
    }

    /**
     * 标志其他要注意的系统
     * */
    public static class TheOtherSystem{
        public static final String XIAOMI = "Xiaomi";
    }

    public static class TimeSystemCal{
        public static String TEMPTIMESYSTEM = "";
        public static long calResult = 0;
    }
    public static class ShowPackName{
        public static String ANDROID_HOME="com.sec.android.app.launcher";
    }

    public static class ReceiveBroadCastKey{
        //main防止销毁广播接受key
        public static final String PREVENTBROAD_FLAG = "fangzhixiaohuiflag";
    }

}
