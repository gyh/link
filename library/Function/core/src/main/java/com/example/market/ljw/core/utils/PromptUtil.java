package com.example.market.ljw.core.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.market.ljw.core.R;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

/**
 * Created by GYH on 2014/10/20.
 */
public class PromptUtil {

    /**
     * 弹出提示信息
     * @param msgId
     */
    public static void showMessage(BaseActivity baseActivity,int msgId){
        showMessage(baseActivity,baseActivity.getResources().getString(msgId));
    }

    /**
     * 弹出提示信息
     * @param msg 提示信息内容
     */
    public static void showMessage(BaseActivity baseActivity,String msg){
        Toast.makeText(baseActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showExitAlert(final NiftyDialogBuilder dialogBuilder,BaseActivity baseActivity, Effectstype effect, View view,
                                     View.OnClickListener clickyes){
        if(dialogBuilder == null){
            baseActivity.finish();
            return;
        }
        dialogBuilder
                .withTitle("链接网")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage("确定要退出积分系统！")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(baseActivity.getResources().getColor(R.color.theme))                               //def  | withDialogColor(int resid)
                .withIcon(baseActivity.getResources().getDrawable(R.drawable.ic_launcher))
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(700)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("确定")                                      //def gone
                .withButton2Text("取消")                                  //def gone
                .setCustomView(R.layout.custom_view,view.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(clickyes)
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
    }

    public static void showXiaomiInfo(BaseActivity baseActivity, Effectstype effect, NiftyDialogBuilder dialogBuilder,View view,
                                     View.OnClickListener clickfirst,View.OnClickListener clicksecond){
        dialogBuilder
                .withTitle("链接网")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage("请先设置悬浮窗口！")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(baseActivity.getResources().getColor(R.color.theme))                               //def  | withDialogColor(int resid)
                .withIcon(baseActivity.getResources().getDrawable(R.drawable.ic_launcher))
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .withDuration(700)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("我去设置")                                      //def gone
                .withButton2Text("我已设置")                                  //def gone
                .setCustomView(R.layout.custom_view,view.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(clickfirst)
                .setButton2Click(clicksecond)
                .show();
    }

    public static void showIsNetWork(BaseActivity baseActivity, Effectstype effect, NiftyDialogBuilder dialogBuilder,View view,
                                      View.OnClickListener clickfirst,View.OnClickListener clicksecond){
        dialogBuilder
                .withTitle("链接网")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage("请连接网络！")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(baseActivity.getResources().getColor(R.color.theme))                               //def  | withDialogColor(int resid)
                .withIcon(baseActivity.getResources().getDrawable(R.drawable.ic_launcher))
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(700)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("我已连网")                                      //def gone
                .withButton2Text("我去设置")                                  //def gone
                .setCustomView(R.layout.custom_view,view.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(clickfirst)
                .setButton2Click(clicksecond)
                .show();
    }

    public static void showNewerVersion(Context context, Effectstype effect, NiftyDialogBuilder dialogBuilder,View view,
                                        View.OnClickListener clickfirst,View.OnClickListener clicksecond,String msg){
        dialogBuilder
                .withTitle("链接网")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage(msg)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(context.getResources().getColor(R.color.theme))                               //def  | withDialogColor(int resid)
                .withIcon(context.getResources().getDrawable(R.drawable.ic_launcher))
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .withDuration(700)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("暂不升级")                                      //def gone
                .withButton2Text("立刻升级")                                  //def gone
                .setCustomView(R.layout.custom_view,view.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(clickfirst)
                .setButton2Click(clicksecond)
                .show();
    }

    public static void showonJsConfirm(Context context, Effectstype effect, NiftyDialogBuilder dialogBuilder,View view,
                                        View.OnClickListener clickfirst,View.OnClickListener clicksecond,String msg){
        dialogBuilder
                .withTitle("链接网")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage(msg)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(context.getResources().getColor(R.color.theme))                               //def  | withDialogColor(int resid)
                .withIcon(context.getResources().getDrawable(R.drawable.ic_launcher))
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .isCancelable(false)
                .withDuration(700)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("确定")                                      //def gone
                .withButton2Text("取消")                                  //def gone
                .setCustomView(R.layout.custom_view,view.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(clickfirst)
                .setButton2Click(clicksecond)
                .show();
    }

    public static void showonJsAlert(Context context, Effectstype effect, NiftyDialogBuilder dialogBuilder,View view,
                                       View.OnClickListener clickfirst,String msg){
        dialogBuilder
                .withTitle("链接网")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage(msg)                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(context.getResources().getColor(R.color.theme))                               //def  | withDialogColor(int resid)
                .withIcon(context.getResources().getDrawable(R.drawable.ic_launcher))
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .isCancelable(false)
                .withDuration(700)                                          //def
                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("确定")                                      //def gone
                .setCustomView(R.layout.custom_view,view.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(clickfirst)
                .show();
    }
}
