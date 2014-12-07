package com.example.market.ljw.utils.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

import com.baidu.kirin.KirinConfig;
import com.baidu.kirin.PostChoiceListener;
import com.baidu.kirin.StatUpdateAgent;
import com.example.market.ljw.R;
import com.example.market.ljw.common.frame.BaseActivity;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.PromptUtil;
import com.example.market.ljw.utils.Utils;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GYH on 2014/11/22.
 */
public class UpdateDialog {

    private Context mContext = null;
    private String mAppName = null;
    private final PostChoiceListener mmPostChoiceListener;

    public UpdateDialog(Context context, String appName,
                        PostChoiceListener _mPostUpdateChoiceListener) {
        mContext = context;
        this.mAppName = appName;
        mmPostChoiceListener = _mPostUpdateChoiceListener;

    }

    public void doUpdate(String downloadUrl, String content, BaseActivity baseActivity, View view) {
        showNewerVersionFoundDialog(downloadUrl, content, baseActivity, view);
    }

    private void showNewerVersionFoundDialog(final String downloadUrl, String content, BaseActivity baseActivity, View view) {

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(baseActivity);
        PromptUtil.showNewerVersion(baseActivity, Effectstype.Fliph, dialogBuilder, view, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将用户选择反馈给服务器
                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.put("isdown", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mmPostChoiceListener.PostUpdateChoiceResponse(jsonobject);
                StatUpdateAgent.postUserChoice(mContext, KirinConfig.LATER_UPDATE, mmPostChoiceListener);
                dialogBuilder.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将用户选择反馈给服务器
                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.put("isdown", "1");
                    jsonobject.put("downloadUrl", downloadUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mmPostChoiceListener.PostUpdateChoiceResponse(jsonobject);
                StatUpdateAgent.postUserChoice(mContext, KirinConfig.CONFIRM_UPDATE, mmPostChoiceListener);
                dialogBuilder.dismiss();
            }
        },content);
    }
}
