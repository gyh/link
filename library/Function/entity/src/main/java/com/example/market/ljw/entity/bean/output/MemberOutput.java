package com.example.market.ljw.entity.bean.output;

import com.example.market.ljw.entity.bean.Entity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by GYH on 2014/11/8.
 */
public class MemberOutput extends Entity {
    Gson gson = new Gson();
    private Member member;

    public void setContent(String result){
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(result);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        boolean success = jsonObject.get("success").getAsBoolean();
        if (success) {
            member = gson.fromJson(jsonObject.get("data").getAsJsonObject(), Member.class);
            setSuccess(true);
        } else {
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
