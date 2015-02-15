package com.example.market.ljw.ui;

import android.os.Bundle;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.fragment.FragmenTest;

/**
 * Created by GYH on 2014/12/6.
 */
public class BaseActivityTest extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmenttest);
        FragmenTest.FragmenTestTM marketListFragmentTM = new FragmenTest.FragmenTestTM(R.id.fragmenttest);
        Bundle bundle = new Bundle();
        bundle.putInt("ceshi",1);
        marketListFragmentTM.setBundle(bundle);
        ApplicationManager.go(marketListFragmentTM);
    }
}
