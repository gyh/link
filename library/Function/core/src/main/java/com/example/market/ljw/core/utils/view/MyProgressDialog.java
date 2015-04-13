package com.example.market.ljw.core.utils.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by GYH on 2014/10/21.
 */
public class MyProgressDialog extends ProgressDialog{

    public MyProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
