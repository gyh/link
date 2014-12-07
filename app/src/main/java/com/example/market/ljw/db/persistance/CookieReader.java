package com.example.market.ljw.db.persistance;

import android.database.Cursor;
import android.util.Log;

import com.example.market.ljw.bean.Cookie;

import java.util.ArrayList;
import java.util.List;

/**
 * cookie读入类
 */
public class CookieReader{

    private final DatabaseReader databaseReader;

    public CookieReader(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }


    /**
     * 依据key获取对应的cookie信息
     * @param cookieKey
     * @return
     */
    public List<Cookie> getCookieByKey(String cookieKey) {
        Cursor cursor = databaseReader.getAllFromArg(DatabaseConstants.TBL_COOKIE
                ,DatabaseConstants.RawWhere.COOKIE_KEY + "'" +  cookieKey + "'");
        List<Cookie> cookies = populateListWith(cursor);
        cursor.close();
        return cookies;
    }


    /**
     * 是否验证登陆了
     * @return
     */
    public boolean isVerify() {
        Cursor cursor = databaseReader.getAllFromArg(DatabaseConstants.TBL_COOKIE
                ,DatabaseConstants.RawWhere.COOKIE_VERIFY_KEY);
        List<Cookie> cookies = populateListWith(cursor);
        cursor.close();
        return cookies.size() != 0;
    }



    /**
     * 获取所有的cookie信息
     * @return
     */
    public List<Cookie> getCookies() {
        Cursor cursor = databaseReader.getAllFrom(DatabaseConstants.TBL_COOKIE);
        List<Cookie> cookies = populateListWith(cursor);
        cursor.close();
        return cookies;
    }

    private List<Cookie> populateListWith(Cursor cursor) {
        List<Cookie> data = new ArrayList<Cookie>();
        if (cursor.moveToFirst()) {
            do {
                Cookie cookie = getCookie(cursor);
                data.add(cookie);
            } while (cursor.moveToNext());
        } else {
            Log.e("db", "No data in the cursor.");
        }
        return data;
    }

    /**
     * 获取游标内的cookie
     * @param cursor
     * @return
     */
   private Cookie getCookie(Cursor cursor){
       String cookieKey = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.Cookie.cookieKey));
       String cookieValue = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.Cookie.cookieValue));
       return new Cookie(cookieKey,cookieValue);
   }

}
