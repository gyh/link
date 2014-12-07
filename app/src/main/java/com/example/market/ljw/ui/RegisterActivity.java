package com.example.market.ljw.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.example.market.ljw.R;
import com.example.market.ljw.common.frame.BaseActivity;
import com.example.market.ljw.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.common.frame.taskstack.BackStackManager;
import com.example.market.ljw.fragment.WebViewFragment;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.PromptUtil;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

/**
 * Created by GYH on 2014/11/14.
 */
public class RegisterActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        WebViewFragment.WebViewFragmentTM webViewFragmentTM = new WebViewFragment.WebViewFragmentTM(R.id.contain);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ValueKey.URLKEY, Constant.REGISTERURL);
        webViewFragmentTM.setBundle(bundle);
        ApplicationManager.go(webViewFragmentTM);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
