package com.example.market.ljw.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.example.market.ljw.bean.AppsItemInfo;
import com.example.market.ljw.common.frame.AppContext;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by GYH on 2014/10/16.
 */
public class Utils {
    private static final String LogTag = "hello";

    public static List<PackageInfo> getAllApps(Context context) {

        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);

            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                apps.add(pak);
            }
        }
        return apps;
    }

    public static List<ApplicationInfo> getInstallAppInfo(Context context) {
        PackageManager mypm = context.getPackageManager();
        List<ApplicationInfo> appInfoList = mypm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(appInfoList, new ApplicationInfo.DisplayNameComparator(mypm));// 排序
        for (ApplicationInfo app : appInfoList) {
            //Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app label = " + (String)app.loadLabel(umpm));
            //Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app packageName = " + app.packageName);
        }
        return appInfoList;
    }

    //获取系统应用信息
    public static ArrayList<String> getSystemAppInfo(Context context) {
        List<ApplicationInfo> appList = getInstallAppInfo(context);
        List<ApplicationInfo> sysAppList = new ArrayList<ApplicationInfo>();
        sysAppList.clear();
        for (ApplicationInfo app : appList) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                sysAppList.add(app);
            }
        }
        PackageManager mypm = context.getPackageManager();
        ArrayList<String> sysAppNameList = new ArrayList<String>();
        for (ApplicationInfo app : sysAppList) {
            Log.v(LogTag, "RunningAppInfoParam getThirdAppInfo app label = " + (String) app.loadLabel(mypm));
            sysAppNameList.add((String) app.loadLabel(mypm));
        }
        return sysAppNameList;

    }

    /**
     * 获取第一个站的信息
     * */
    public static String getFirstTask(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> appTask = activityManager.getRecentTasks(Integer.MAX_VALUE, 1);
        return  appTask.get(0).baseIntent.getComponent().getPackageName();
    }

    /**
     * 获取手机系统launcher包名
     * */
    public static String getLauncherPackageName(Context context){
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if(res.activityInfo == null){//如果是不同桌面主题，可能会出现某些问题，这部分暂未处理
            return "";
        }
        if(res.activityInfo.packageName.equals("android")){
            return "";
        }else{
            return res.activityInfo.packageName;
        }
    }

    /**
     * 判断当前运行的应用是否是制定的应用
     * */
    public static boolean isAppOnForeground(Context context) {
        boolean isShow = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> appTask = activityManager.getRecentTasks(Integer.MAX_VALUE, 1);
        if (appTask == null) {
            isShow = true;
        }else if (appTask.get(0).baseIntent.getComponent().getPackageName().equals(Constant.ShowPackName.ANDROID_HOME)){
            isShow = true;
        }else {
            for(int i=0;i<appTask.size();i++){
                if(appTask.get(i).baseIntent.getComponent().getPackageName().equals(Constant.PACKAGENAME)){
                    isShow = false;
                }
            }
        }
        Utils.showSystem("packagename",appTask.get(0).baseIntent.getComponent().getPackageName());
        appTask.clear();
        return isShow;
    }

    /**
     * 获取短信跳转的Intent
     * */
    public static Intent getIntentForMMS(){
        Intent intent = null;
        return intent;
    }

    /**
     * 获取连接网appIntent
     * */
    public static Intent getLJWAppIntent(Context context){
        Intent intent = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> appTask = activityManager.getRecentTasks(Integer.MAX_VALUE, 1);
        for(int i=0;i<appTask.size();i++){
            if(appTask.get(i).baseIntent.getComponent().getPackageName().equals(Constant.PACKAGENAME)){
                intent = appTask.get(i).baseIntent;
            }
        }
        return intent;
    }

    /**
     * 用来判断服务是否运行.
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 获取手机制造商
     * */
    public static String  getPhoneSystemInfo(){
//        String phoneInfo = "Product: " + android.os.Build.PRODUCT;
//        phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI;
//        phoneInfo += ", TAGS: " + android.os.Build.TAGS;
//        phoneInfo += ", VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE;
//        phoneInfo += ", MODEL: " + android.os.Build.MODEL;
//        phoneInfo += ", SDK: " + android.os.Build.VERSION.SDK;
//        phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
//        phoneInfo += ", DEVICE: " + android.os.Build.DEVICE;
//        phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY;
//        phoneInfo += ", BRAND: " + android.os.Build.BRAND;
//        phoneInfo += ", BOARD: " + android.os.Build.BOARD;
//        phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT;
//        phoneInfo += ", ID: " + android.os.Build.ID;
        String phoneInfo = android.os.Build.MANUFACTURER;
//        phoneInfo += ", USER: " + android.os.Build.USER;
        // Toast.makeText(this, phoneInfo, Toast.LENGTH_LONG).show();
        return phoneInfo;
    }

    public static void showInstalledAppDetails(Context context, String packageName) {

        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(Constant.SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? Constant.APP_PKG_NAME_22
                    : Constant.APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(Constant.APP_DETAILS_PACKAGE_NAME,
                    Constant.APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    /**
     * 获取壁纸
     * */
    public static Bitmap getAndroidSystmeBtmp(Context mContext){
        //获取WallpaperManager 壁纸管理器
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext); // 获取壁纸管理器
        // 获取当前壁纸
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        //将Drawable,转成Bitmap
        Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        return bm;
    }

    /**
     * 判断是否有网络
     * */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断是否能够加积分
     * */
    public static boolean isCanAddScore(String timeStr){
       String hour = "0";
       if("".equals(timeStr)){
           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
           try {
               Date timeb = df.parse(getCurrentDate());
               hour = timeb.getHours()+"";
           } catch (ParseException e) {
               e.printStackTrace();
           }
           Utils.showSystem("empty","hour = "+hour);
       }else {
           hour = timeStr.substring(timeStr.indexOf("T")+1, timeStr.indexOf(":"));
           Utils.showSystem("noempty","hour = "+hour);
       }
        if(Integer.valueOf(hour)<6){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取当前时间
     * */
    public static String getCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String strt = df.format(new Date());
        return strt;
    }

    /**
     * 计算时间差
     * 计算是不是同一天
     * 如果不是同一天则判断第二个时间是不是不是可以加积分 如果可以则将第一个时间改为当天的6点，如果不可以则返回积分为0
     * 如果是同一天，则判断第二个时间可不可以加积分，如果不可以则返回积分为0
     * 如果可以则判断第一个时间是否是在6点后，如果不是则将第一个时间改为6点，如果是，则计算返回差值
     * */
    public static long calculationDurFrom(String startstr,String endstr){
        Utils.showSystem("calculationDurFrom","startstr--"+startstr+"endstr---"+endstr);
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin;
        Date end;
        long between = 0;
        try {
             begin = dfs.parse(startstr);
             end = dfs.parse(endstr);
            if(areSameDay(begin,end)){
                if(isCanUseOfTime(endstr)){
                    if(isCanUseOfTime(startstr)){
                        between = (end.getTime() - begin.getTime())/1000;// 得到两者的毫秒数
                    }else {
                        begin.setHours(6);
                        begin.setMinutes(0);
                        begin.setSeconds(0);
                        between = (end.getTime() - begin.getTime())/1000;// 得到两者的毫秒数
                    }

                }else {
                    between = 0;
                }
            }else {
                if(isCanUseOfTime(endstr)){
                    begin.setYear(end.getYear());
                    begin.setMonth(end.getMonth());
                    begin.setDate(end.getDate());
                    begin.setHours(6);
                    begin.setMinutes(0);
                    begin.setSeconds(0);
                    between = (end.getTime() - begin.getTime())/1000;// 得到两者的毫秒数
                }else {
                    between = 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return between;
    }

    /**
     * 判断时间是否可以加积分
     * */
    public static boolean isCanUseOfTime(String timestr){
        boolean iscan = false;
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date timeb = dfs.parse(timestr);
            Utils.showSystem("isCanUseOfTime",timeb.getHours()+"");
            if(timeb.getHours()<6){
                iscan = false;
            }else {
                iscan = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return iscan;
    }

    /**
     * 判断是否是同一天
     * @param dateA
     * @param dateB
     * */
    public static boolean areSameDay(Date dateA,Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                &&  calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * @param string
     * @return
     */
    public static String encodeGB(String string){
        //转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0]+"/"+split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");//处理空格
        return split[0];
    }

    public static String convertURL(String url){
        String timstamp = String.valueOf(new Date().getTime());
        if (url.indexOf("?")>=0){
            url = url + "&t=" + timstamp;
        }else {
            url = url + "?t=" + timstamp;
        }
        return url;
    }

    /**
     * 测试打印
     * */
    public static void showSystem(String key,String context){
        if(Constant.DEBUG){
            System.out.println("gyh--"+key+"--" + context);
        }
    }

    /**
     * 获取当前版本号
     * */
    public static String getVersionName(Context context) throws Exception{
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }
    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight =0 ;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}
