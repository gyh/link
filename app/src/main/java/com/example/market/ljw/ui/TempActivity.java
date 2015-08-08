package com.example.market.ljw.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.receiver.ScreenBCReceiver;

/**
 * Created by GYH on 2015/7/22.
 */
public class TempActivity extends BaseActivity{

    public static final String TEMPGOTOMAIN="com.example.market.ljw.ui.gotomain";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Constant.makeAppName = Constant.PACKAGENAME;
            Intent intent2 = Utils.getLJWAppIntent2(context);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
            context.startActivity(intent2);
            TempActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        IntentFilter mScreenOffFilter = new IntentFilter();
        mScreenOffFilter.addAction(TEMPGOTOMAIN);
        registerReceiver(receiver, mScreenOffFilter);
        Utils.showSystem("temp-onCreate",Utils.getCurrentDate());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.showSystem("temp-onResume",Utils.getCurrentDate());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
