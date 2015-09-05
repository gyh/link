package com.example.market.ljw.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.MyAppContext;
import com.example.market.ljw.core.common.frame.MyBaseActivity;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.PopUtils;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.core.utils.StringUtils;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.entity.bean.output.LoginOutput;
import com.example.market.ljw.service.InputDataUtils;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GYH on 2014/10/21.
 * 登陆活动页面
 * 继承BaseActivity,实现视图点击监听，选择框点击监听
 * 主要功能：
 * 1.判断用户输入数据是否正确
 * 2.判断是否记住用户名和密码
 * 3.提交登陆信息，并且判断返回数据
 * 4.检查网络状态，判断结果
 */
public class LoginActivity extends MyBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText username;//用户名
    private EditText password;//密码
    private View btlogin, registerbtn;//登陆，注册按钮
    private CheckBox reUsername, reUserpassword;//记住用户名 密码
    private TextView versiontv;
    private Future<JsonObject> loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();//初始化视图
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //初始化视图
        versiontv = (TextView) findViewById(R.id.tv_version);
        registerbtn = findViewById(R.id.registerbtn);
        reUsername = (CheckBox) findViewById(R.id.reUsername);
        reUserpassword = (CheckBox) findViewById(R.id.reUserpassword);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        //从本地存储中获取用户名和密码
        username.setText(MyAppContext.getInstance().getDataForShPre(Constant.SaveKeys.USERNAME, ""));
        password.setText(MyAppContext.getInstance().getDataForShPre(Constant.SaveKeys.PASSWORD,""));
        btlogin = findViewById(R.id.bt_login);
        //按钮点击监听事件
        btlogin.setOnClickListener(this);
        registerbtn.setOnClickListener(this);
        //选择框点击监听
        reUsername.setOnCheckedChangeListener(this);
        reUserpassword.setOnCheckedChangeListener(this);
        //判断用户名和密码是否存在，如果存在则默认选择，如果没有则取消选择
        if(!"".equals(MyAppContext.getInstance().getDataForShPre(Constant.SaveKeys.USERNAME,""))){//判断用户名是否存在
            reUsername.setChecked(true);//设置记住用户名
        }else {
            reUsername.setChecked(false);//设置不记住用户名
        }
        if(!"".equals(MyAppContext.getInstance().getDataForShPre(Constant.SaveKeys.PASSWORD,""))){//判断密码是否存在
            reUserpassword.setChecked(true);//设置记住密码
        }else {
            reUserpassword.setChecked(false);//设置不记住密码
        }
        try {
            String versionstr = Utils.getVersionName(this);
            versiontv.setText("版本号：" + versionstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.reUsername://当用户名选择框改变是
                if (!b && reUserpassword.isChecked()) {//判断当用户名选择框没有选择，并且记住密码选择，则选中
                    reUsername.setChecked(true);
                }
                break;
            case R.id.reUserpassword://记住密码
                if (b) {
                    reUsername.setChecked(true);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login://点击登录按钮，提交登录流程
                if (checked()) {//检查是否可以提交信息
                    if (Utils.isNetworkConnected(LoginActivity.this)) {//判断是否有网络
                        loginService();//如果有网络，则提交请求
                    } else {//如没有网络，则提示没有网络跳转网络设置页面
                        showNoNetworkDailog();
                    }
                }
                break;
            case R.id.registerbtn://点击我要注册跳转到注册页面
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 如果没有网络，显示提示框，并且提示去设置网络
     */
    private void showNoNetworkDailog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(LoginActivity.this);
        PromptUtil.showIsNetWork(LoginActivity.this, Effectstype.Fadein, dialogBuilder,
                findViewById(R.id.layoutlogin), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                        startActivity(wifiSettingsIntent);
                    }
                });
    }

    /**
     * 检查是否可以提交
     */
    private boolean checked() {
        boolean isok = true;
        String name = username.getText().toString();
        String pwd = password.getText().toString();
        if (StringUtils.isEmpty(name)) {
            isok = false;
            PromptUtil.showMessage(LoginActivity.this, "用户名不能为空");
        } else if (StringUtils.isEmpty(pwd)) {
            PromptUtil.showMessage(LoginActivity.this, "密码不能为空");
            isok = false;
        }
        return isok;
    }

    private void loginService() {
        if (loading != null && !loading.isDone() && !loading.isCancelled())
            return;
        loading = Ion.with(this)
                .load(Constant.SERVER_URL)
                .setBodyParameter(Constant.RequestKeys.SERVICENAME, "member_login")
                .setBodyParameter(Constant.RequestKeys.DATA, new Gson().toJson(InputDataUtils.getLoginData(
                        username.getText().toString(), password.getText().toString(), this)))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(LoginActivity.this, "网络异常，稍后重试", Toast.LENGTH_LONG).show();
                            return;
                        }
                        final LoginOutput memberInfo = new LoginOutput();
                        try {
                            memberInfo.setContent(result.toString());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            PromptUtil.showMessage(LoginActivity.this, e1.toString());
                        }

                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (memberInfo.isSuccess()) {
                                    MyAppContext.getInstance().setDataForShPre(Constant.SaveKeys.TOKENKEY, memberInfo.getToken());
                                    MyAppContext.getInstance().setDataForShPre("userId",memberInfo.getMember().getID()+"");
                                    reCheckbox();//检查选择框设置，存储用户信息
                                    Intent intent = new Intent();//跳转流程
                                    intent.setClass(LoginActivity.this, MainActivity.class);
                                    intent.putExtra(Constant.FromWhere.KEY, Constant.FromWhere.LOGINACTIVITY);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    PromptUtil.showMessage(LoginActivity.this, memberInfo.getErrmsg());
                                }
                            }
                        });
                    }
                });
    }


    /**
     * 检查选中状态，如果记住密码选中则，用户名和密码都保存，如果只选中用户名，则只保存用户名
     */
    private void reCheckbox() {
        if (reUserpassword.isChecked()) {
            MyAppContext.getInstance().setDataForShPre(Constant.SaveKeys.USERNAME, username.getText().toString());
            MyAppContext.getInstance().setDataForShPre(Constant.SaveKeys.PASSWORD, password.getText().toString());
        } else if (reUsername.isChecked()) {
            MyAppContext.getInstance().setDataForShPre(Constant.SaveKeys.USERNAME, username.getText().toString());
            MyAppContext.getInstance().setDataForShPre(Constant.SaveKeys.PASSWORD, "");
        } else {
            MyAppContext.getInstance().setDataForShPre(Constant.SaveKeys.USERNAME, "");
            MyAppContext.getInstance().setDataForShPre(Constant.SaveKeys.PASSWORD, "");
        }
    }
}
