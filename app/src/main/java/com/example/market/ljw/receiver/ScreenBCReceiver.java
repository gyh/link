package com.example.market.ljw.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.ui.MainActivity;
import com.example.market.ljw.ui.RegisterActivity;
import com.example.market.ljw.ui.TempActivity;

/**
 * Created by GYH on 2015/7/21.
 */
public class ScreenBCReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (AppContext.getInstance().getBaseActivity() == null) {
            return;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        if(tm.getCallState() == TelephonyManager.CALL_STATE_RINGING || tm.getCallState() ==TelephonyManager.CALL_STATE_OFFHOOK){
            return;
        }
        if (intent != null && Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Utils.showSystem("startTime", Utils.getCurrentDate());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Constant.makeAppName = Constant.PACKAGENAME;
                    Intent intent2 = new Intent(context, MainActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
                    context.startActivity(intent2);
                }
            }, 10);
        }
    }


}
