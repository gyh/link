package com.example.market.ljw.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.market.ljw.R;
import com.example.market.ljw.entity.bean.Entity;
import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.common.http.HttpGroup;
import com.example.market.ljw.core.common.http.HttpResponse;
import com.example.market.ljw.entity.bean.LoginOutput;
import com.example.market.ljw.service.InputDataUtils;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.PopUtils;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.core.utils.StringUtils;
import com.example.market.ljw.core.utils.Utils;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.LinkedHashMap;
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
public class LoginActivity extends BaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private EditText username;//用户名
    private EditText password;//密码
    private View btlogin,registerbtn;//登陆，注册按钮
    private CheckBox reUsername,reUserpassword;//记住用户名 密码
    private TextView versiontv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();//初始化视图
    }

    /**
     * 初始化视图
     * */
    private void initView(){
        //初始化视图
        versiontv = (TextView)findViewById(R.id.tv_version);
        registerbtn = findViewById(R.id.registerbtn);
        reUsername = (CheckBox)findViewById(R.id.reUsername);
        reUserpassword = (CheckBox)findViewById(R.id.reUserpassword);
        username = (EditText)findViewById(R.id.et_username);
        password = (EditText)findViewById(R.id.et_password);
        //从本地存储中获取用户名和密码
        username.setText(getDataForShPre(Constant.SaveKeys.USERNAME,""));
        password.setText(getDataForShPre(Constant.SaveKeys.PASSWORD,""));
        btlogin = findViewById(R.id.bt_login);
        //按钮点击监听事件
        btlogin.setOnClickListener(this);
        registerbtn.setOnClickListener(this);
        //选择框点击监听
        reUsername.setOnCheckedChangeListener(this);
        reUserpassword.setOnCheckedChangeListener(this);
        //判断用户名和密码是否存在，如果存在则默认选择，如果没有则取消选择
        if(!"".equals(getDataForShPre(Constant.SaveKeys.USERNAME,""))){//判断用户名是否存在
            reUsername.setChecked(true);//设置记住用户名
        }else {
            reUsername.setChecked(false);//设置不记住用户名
        }
        if(!"".equals(getDataForShPre(Constant.SaveKeys.PASSWORD,""))){//判断密码是否存在
            reUserpassword.setChecked(true);//设置记住密码
        }else {
            reUserpassword.setChecked(false);//设置不记住密码
        }
        try {
            String versionstr = Utils.getVersionName(this);
            versiontv.setText("版本号："+versionstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.reUsername://当用户名选择框改变是
                if(!b && reUserpassword.isChecked()){//判断当用户名选择框没有选择，并且记住密码选择，则选中
                    reUsername.setChecked(true);
                }
                break;
            case R.id.reUserpassword://记住密码
                if(b){
                    reUsername.setChecked(true);
                }
                break;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_login://点击登录按钮，提交登录流程
                if(checked()){//检查是否可以提交信息
                    if(Utils.isNetworkConnected(LoginActivity.this)){//判断是否有网络
                        setRequest();//如果有网络，则提交请求
                    }else {//如没有网络，则提示没有网络跳转网络设置页面
                        showNoNetworkDailog();
                    }
                }
                break;
            case R.id.registerbtn://点击我要注册跳转到注册页面
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 如果没有网络，显示提示框，并且提示去设置网络
     * */
    private void showNoNetworkDailog(){
        final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(LoginActivity.this);
        PromptUtil.showIsNetWork(LoginActivity.this, Effectstype.Fadein,dialogBuilder,
                findViewById(R.id.layoutlogin),new View.OnClickListener() {
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
     * */
    private boolean checked(){
        boolean isok = true;
        String name = username.getText().toString();
        String pwd = password.getText().toString();
        if(StringUtils.isEmpty(name)){
            isok=false;
            PopUtils.showToast("用户名不能为空");
        }else if(StringUtils.isEmpty(pwd)){
            PopUtils.showToast("密码不能为空");
            isok=false;
        }
        return isok;
    }

    /**
     * 检查选中状态，如果记住密码选中则，用户名和密码都保存，如果只选中用户名，则只保存用户名
     * */
    private void reCheckbox(){
        if(reUserpassword.isChecked()){
            setDataForShPre(Constant.SaveKeys.USERNAME,username.getText().toString());
            setDataForShPre(Constant.SaveKeys.PASSWORD,password.getText().toString());
        }else if(reUsername.isChecked()){
            setDataForShPre(Constant.SaveKeys.USERNAME,username.getText().toString());
            setDataForShPre(Constant.SaveKeys.PASSWORD,"");
        }else {
            setDataForShPre(Constant.SaveKeys.USERNAME,"");
            setDataForShPre(Constant.SaveKeys.PASSWORD,"");
        }
    }

    /**
     * 发送请求
     * http://markettest.strosoft.com/API/Service.ashx?
     * service_name=member_login&data={LoginName:%27hfjjs12%27,Password:%27123456%27,ClientType:%27Android%27}
     * */
    private void setRequest(){
        Map<String, Object> param = new LinkedHashMap<String, Object>();
        param.put(Constant.RequestKeys.SERVICENAME, "member_login");
        param.put(Constant.RequestKeys.DATA, gson.toJson(InputDataUtils.getLoginData(
                username.getText().toString(),password.getText().toString(),this)));
        showProgressDialog(R.string.loginning, true);
        execute(Constant.SERVER_URL, true, param, null, new HttpGroup.OnEndListener() {
            @Override
            public void onEnd(HttpResponse httpresponse) {
                dismissProgressDialog();
                LoginOutput loginOutput = (LoginOutput)httpresponse.getResultObject();
                if(loginOutput.isSuccess()){
                    setDataForShPre(Constant.SaveKeys.TOKENKEY,loginOutput.getToken());
                    reCheckbox();//检查选择框设置，存储用户信息
                    Intent intent = new Intent();//跳转流程
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    intent.putExtra(Constant.ExtraKey.MEMBER,loginOutput.getMember());
                    intent.putExtra(Constant.FromWhere.KEY,Constant.FromWhere.LOGINACTIVITY);
                    startActivity(intent);
                    finish();
                }else {
                    PopUtils.showToast(loginOutput.getErrmsg());
                }
            }
        }, new HttpGroup.OnParseListener() {
            @Override
            public Entity onParse(String result) {
               LoginOutput memberInfo = new LoginOutput();
               memberInfo.setContent(result);
               return memberInfo;
            }
        });
    }


}
