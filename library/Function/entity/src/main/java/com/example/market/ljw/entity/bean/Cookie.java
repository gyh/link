package com.example.market.ljw.entity.bean;

/**
 * Created by GYH on 2014/10/20.
 * @author guoyuehua
 * cookie实体
 */
public class Cookie {

    //cookie存储的Key
    private String cookieKey;

    //cookie存储的value
    private String cookieValue;

    public Cookie(String cookieKey, String cookieValue) {
        this.cookieKey = cookieKey;
        this.cookieValue = cookieValue;
    }

    public String getCookieKey() {
        return cookieKey;
    }

    public void setCookieKey(String cookieKey) {
        this.cookieKey = cookieKey;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }
}
