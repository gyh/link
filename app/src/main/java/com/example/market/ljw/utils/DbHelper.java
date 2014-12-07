package com.example.market.ljw.utils;

import android.content.ContentResolver;

import com.example.market.ljw.db.persistance.DatabaseReader;
import com.example.market.ljw.db.persistance.DatabaseWriter;

/**
 * Created by GYH on 2014/10/20.
 */
public class DbHelper {

    //数据库基本写入类
    private static DatabaseWriter databaseWriter;

    //数据库基本读类
    private static DatabaseReader databaseReader;

    /**
     * 获取基本数据库读入类
     * @return
     */
    public static DatabaseReader getDatabaseReader(ContentResolver contentResolver){
        if (databaseReader == null) {
            databaseReader = new DatabaseReader(contentResolver);
        }
        return databaseReader;
    }

    /**
     * 获取基本数据库写入类
     * @return
     */
    public static DatabaseWriter getDatabaseWriter(ContentResolver contentResolver){
        if (databaseWriter == null) {
            databaseWriter = new DatabaseWriter(contentResolver);
        }
        return databaseWriter;
    }
}
