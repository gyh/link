package com.example.market.ljw.db.persistance;

import android.content.ContentValues;

import com.example.market.ljw.bean.Cookie;

import java.util.List;

/**
 * cookie库存储类
 */
public class CookieWriter {

    private final  DatabaseWriter databaseWriter;

    public CookieWriter(DatabaseWriter databaseWriter) {
        this.databaseWriter = databaseWriter;
    }

    /**
     * 更新cookie的值
     * @paramcategories 服务器获取的城市
     */
    public void updateCookieByKey(Cookie cookie,String cookieKey) {
        ContentValues value = new ContentValues();
        value.put(DatabaseConstants.Cookie.cookieKey, cookie.getCookieKey());
        value.put(DatabaseConstants.Cookie.cookieValue, cookie.getCookieValue());
        databaseWriter.updateDataToCookiesTable(value,DatabaseConstants.RawWhere.COOKIE_KEY + "'" + cookieKey + "'");
    }

    /**
     * 存储cookie的值
     * @param cookies
     */
    public void saveCookies(List<Cookie> cookies){
        ContentValues[] values = new ContentValues[cookies.size()];
        for(int i=0;i<cookies.size();i++){
            Cookie cookie = cookies.get(i);
            ContentValues value = new ContentValues();
            value.put(DatabaseConstants.Cookie.cookieKey, cookie.getCookieKey());
            value.put(DatabaseConstants.Cookie.cookieValue, cookie.getCookieValue());
            values[i] = value;
        }
        databaseWriter.saveDataToCookieTable(values);
    }


    /**
     * 清除所有的cookie信息
     */
    public void delCookies(){
        databaseWriter.deleteDataToCookieTable();
    }
}
