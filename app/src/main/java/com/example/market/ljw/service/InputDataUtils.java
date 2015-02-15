package com.example.market.ljw.service;

import com.example.market.ljw.entity.bean.input.LoginUser;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.Log;

/**
 * Created by GYH on 2014/10/27.
 */
public class InputDataUtils {

    /**
     * 封装提交积分的数据
     * */
    public static LoginUser submitScore(String token,long curDuration,String id){
        LoginUser loginUser = new LoginUser();
//        loginUser.setTooken(token);
//        loginUser.setClientType(Constant.ClientType);
        loginUser.setDuration(curDuration);
        loginUser.setMemberID(id);
        return loginUser;
    }

    public static LoginUser getUserInfo(String id){
        LoginUser loginUser = new LoginUser();
//        loginUser.setTooken(token);
//        loginUser.setClientType(Constant.ClientType);
        loginUser.setMemberID(id);
        return loginUser;
    }

    //欢迎页面
    public static LoginUser getWelcome(String token){
        LoginUser loginUser = new LoginUser();
        loginUser.setTooken(token);
        loginUser.setClientType(Constant.ClientType);
        return loginUser;
    }

    public static LoginUser getLoginData(String username,String password){
        LoginUser loginUser = new LoginUser();
        loginUser.setLoginName(username);
        loginUser.setPassword(password);
        loginUser.setClientType(Constant.ClientType);
        return loginUser;
    }

    public static LoginUser getPics(){
        LoginUser loginUser = new LoginUser();
        loginUser.setAdvertisementTypeCode("product");
        return loginUser;
    }

}
