package com.example.market.ljw.core.common.frame.taskstack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by GYH on 2015/8/15.
 */
public class MyFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyApplicationManager.setFragment(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
