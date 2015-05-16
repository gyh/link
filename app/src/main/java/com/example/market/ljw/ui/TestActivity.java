package com.example.market.ljw.ui;

import android.os.Bundle;
import android.widget.BaseAdapter;

import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.utils.FileService;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.core.utils.UtilsServer;

/**
 * Created by GYH on 2015/4/23.
 */
public class TestActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileService fileService = new FileService("ljwlog.txt");
        String servicetimestr = "2015-04-23 18:10:09";
        for(int i=0;i<10;i++){
            servicetimestr = UtilsServer.modifyServerTime(servicetimestr,10);
            Utils.setFileLog(servicetimestr,fileService);
        }
    }

}
