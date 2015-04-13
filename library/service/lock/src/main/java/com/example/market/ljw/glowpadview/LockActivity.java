package com.example.market.ljw.glowpadview;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DPIUtil;
import com.example.market.ljw.core.utils.Utils;

/**
 * Created by GYH on 2014/10/22.
 */
public class LockActivity extends BaseActivity implements GlowPadView.OnTriggerListener {

    private GlowPadView mGlowPadView;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志 home键
    private LinearLayout pview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏蔽Home键
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        } else {
            this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        }
        setContentView(R.layout.activity_lock);
        pview = (LinearLayout)findViewById(R.id.pview);
        pview.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams linearParams = pview.getLayoutParams();
                ImageView imageView = new ImageView(LockActivity.this.getApplicationContext());
                linearParams.height = (int) ((float) DPIUtil.getWidth() / (float) Constant.IMG_WIDTH * Constant.IMG_HEIGHT);
                linearParams.width = DPIUtil.getWidth();
                imageView.setLayoutParams(linearParams);
                pview.addView(imageView);
            }
        });
        mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);
        mGlowPadView.setOnTriggerListener(LockActivity.this);
        mGlowPadView.setShowTargetsOnIdle(true);
    }

    @Override
    public void onGrabbed(View v, int handle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReleased(View v, int handle) {
        mGlowPadView.ping();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Constant.isShowLock = true;
    }

    @Override
    public void onTrigger(View v, int target) {
        final int resId = mGlowPadView.getResourceIdForTarget(target);
        if (resId == R.drawable.ic_item_camera) {
            Constant.isShowLock = true;
            Constant.TimeSystemCal.calResult =
                    Utils.calculationDurFrom(Constant.TimeSystemCal.TEMPTIMESYSTEM, Utils.getCurrentDate());
            goMainActivity();
            finish();

        } else if (resId == R.drawable.ic_item_google) {
            Constant.isShowLock = true;
            Constant.TimeSystemCal.calResult =
                    Utils.calculationDurFrom(Constant.TimeSystemCal.TEMPTIMESYSTEM, Utils.getCurrentDate());
            goMainActivity();
            finish();

        } else {// Code should never reach here.
        }

    }

    private void goMainActivity(){
        Constant.makeAppName = Constant.PACKAGENAME;
        Intent intent = new Intent();
        intent.setClass(this, AppContext.getInstance().getMainActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.FXSERVICE);
        startActivity(intent);
    }

    @Override
    public void onGrabbedStateChange(View v, int handle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinishFinalAnimation() {
        // TODO Auto-generated method stub

    }
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event. KEYCODE_HOME || keyCode == event.KEYCODE_BACK ||keyCode == KeyEvent.KEYCODE_MENU) {
            Constant.isShowLock = true;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
