package com.example.market.ljw.ui;

import android.os.Bundle;
import android.view.KeyEvent;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.fragment.WebViewFragment;
import com.example.market.ljw.core.utils.Constant;

/**
 * Created by GYH on 2014/11/14.
 */
public class RegisterActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.showSystem("startTime", Utils.getCurrentDate());
    }
}
