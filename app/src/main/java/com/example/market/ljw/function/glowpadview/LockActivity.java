package com.example.market.ljw.function.glowpadview;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DPIUtil;
import com.example.market.ljw.core.utils.Util;
import com.example.market.ljw.core.utils.Utils;

/**
 * Created by GYH on 2014/10/22.
 */
public class LockActivity extends BaseActivity implements GlowPadView.OnTriggerListener {

    private GlowPadView mGlowPadView;

    private LinearLayout pview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onTrigger(View v, int target) {
        final int resId = mGlowPadView.getResourceIdForTarget(target);
        switch (resId) {
            case R.drawable.ic_item_camera:
                Constant.isShowLock = true;
                Constant.TimeSystemCal.calResult =
                        Utils.calculationDurFrom(Constant.TimeSystemCal.TEMPTIMESYSTEM, Utils.getCurrentDate());
                finish();
                break;
            case R.drawable.ic_item_google:
                Constant.isShowLock = true;
                Constant.TimeSystemCal.calResult =
                        Utils.calculationDurFrom(Constant.TimeSystemCal.TEMPTIMESYSTEM, Utils.getCurrentDate());
                finish();
                break;
            default:
                // Code should never reach here.
        }

    }

    @Override
    public void onGrabbedStateChange(View v, int handle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinishFinalAnimation() {
        // TODO Auto-generated method stub

    }
}
