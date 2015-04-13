package com.example.market.ljw.core.utils;

import android.widget.Toast;

import com.example.market.ljw.core.common.frame.AppContext;

/**
 * Created by GYH on 2014/10/22.
 */
public class PopUtils {

    public static void showToast(String msg){
        Toast.makeText(AppContext.getInstance().getBaseActivity(),msg,Toast.LENGTH_SHORT).show();
    }

}
