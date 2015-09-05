package com.example.market.ljw.core.common.frame;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.koushikdutta.ion.Ion;

/**
 * Created by GYH on 2015/8/8.
 */
public class MyBaseActivity extends AppCompatActivity{

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    public Handler getHandler() {
        return handler;
    }

    public void post(final Runnable action) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (MyBaseActivity.this.isFinishing()) {
                    return;
                }
                action.run();
            }
        });
    }

    /**
     * 统一 post 接口
     */
    public void post(final Runnable action, int delayMillis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MyBaseActivity.this.isFinishing()) {
                    return;
                }
                action.run();
            }
        }, delayMillis);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Ion.getDefault(this).cancelAll(this);
    }
}
