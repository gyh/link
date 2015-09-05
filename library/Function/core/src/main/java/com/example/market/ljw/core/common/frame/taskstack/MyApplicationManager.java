package com.example.market.ljw.core.common.frame.taskstack;

/**
 * Created by GYH on 2014/10/20.
 */

import android.support.v4.app.FragmentManager;

import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.common.frame.MyActivity;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment跳转工具类
 *
 * @author yepeng
 */
public class MyApplicationManager {

    private static List<MyFragment> myFragmentList = new ArrayList<>();

    public static MyFragment getCurrentFragemnt(){
        if(myFragmentList!=null&&myFragmentList.size()>0){
           return myFragmentList.get(myFragmentList.size()-1);
        }else {
            return null;
        }
    }

    public static void setFragment(MyFragment myFragment){
        myFragmentList.add(myFragment);
    }
    public static boolean removeFragment(){
        if(myFragmentList.size()>0){
            myFragmentList.remove(myFragmentList.size()-1);
            return true;
        }else {
            return false;
        }
    }

    public static boolean clearFragment(){
        if(myFragmentList!=null){
            myFragmentList.clear();
            return true;
        }else {
            return false;
        }
    }
}
