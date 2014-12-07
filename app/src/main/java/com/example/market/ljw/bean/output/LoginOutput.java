package com.example.market.ljw.bean.output;

import android.content.Intent;

import com.example.market.ljw.LJWActivity;
import com.example.market.ljw.bean.Entity;
import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.PopUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by GYH on 2014/11/9.
 */
public class LoginOutput extends Entity{

    Gson gson = new Gson();
    private Member member;

    public void setContent(String result){
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(result);
        JsonObject jsonObject= jsonElement.getAsJsonObject();
        boolean success = jsonObject.get("success").getAsBoolean();
        if(success){
            JsonObject data = jsonObject.get("data").getAsJsonObject();
            String token = data.get("Token").getAsString();
            setToken(token);
            member = gson.fromJson(data.get("Member").getAsJsonObject(),Member.class);
            setSuccess(true);
        }else {
            String error_message = jsonObject.get("error_message").getAsString();
            setErrmsg(error_message);
            setSuccess(false);
        }
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
