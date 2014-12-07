package com.example.market.ljw.db.persistance;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.example.market.ljw.db.provider.DataProvider;

public class DatabaseWriter {

    private final ContentResolver contentResolver;

    public DatabaseWriter(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }
//todo YW
//    /**
//     * 保存数据到分类表
//     * @param values
//     */
//    public void saveDataToCategoryTable(ContentValues[] values) {
//        saveDataToTable(DatabaseConstants.TBL_CATEGORY, values);
//    }
//
//    /**
//     * 保存数据到城市表
//     * @param values
//     */
//    public void saveDataToCitiesTable(ContentValues[] values) {
//        saveDataToTable(DatabaseConstants.TBL_CITIES, values);
//    }
//
//    /**
//     * 保存数据到分站表
//     * @param values
//     */
//    public void saveSubStationTable(ContentValues[] values) {
//        saveDataToTable(DatabaseConstants.TBL_SUBSTATION, values);
//    }
//
//    /**
//     * 保存数据到区域分站表
//     * @param values
//     */
//    public void saveGeoSubStationTable(ContentValues[] values) {
//        saveDataToTable(DatabaseConstants.TBL_GEO_SUBSTATION, values);
//    }
//
//    /**
//     * 保存数据到热门城市表
//     * @param values
//     */
//    public void saveDataToHotCityTable(ContentValues[] values) {
//        saveDataToTable(DatabaseConstants.TBL_HOTCITY, values);
//    }
//
//    /**
//     * 保存数据到购物车表
//     * @param values
//     */
//    public void saveDataToShoppingCarTable(ContentValues values) {
//        saveDataToTable(DatabaseConstants.TBL_SHOPPINGCAR, values);
//    }
//
//    /**
//     * 保存商品历史表
//     * @param values
//     */
//    public void saveDataToGoodsTable(ContentValues values) {
//        saveDataToTable(DatabaseConstants.TBL_GOODS, values);
//    }
//
//    /**
//     * 保存搜索的历史
//     * @param values
//     */
//    public void saveDataToHistoryTable(ContentValues values) {
//        saveDataToTable(DatabaseConstants.TBL_HISTORY, values);
//    }

//
//    /**
//     * 公共的写入表方法
//     * @param table 表名
//     * @param values 字段-》字段值
//     */
//    private void saveDataToTable(String table, ContentValues values) {
//        Uri uri = createUri(table);
//        contentResolver.insert(uri, values);
//    }

    /**
     * 公共的批量写入表方法
     * @param table 表名
     * @param values 字段-》字段值,需要批量保存的操作
     */
    private void saveDataToTable(String table, ContentValues[] values) {
        Uri uri = createUri(table);
        contentResolver.bulkInsert(uri, values);
    }
//
//    /**
//     *
//     * @param values 字段-》字段值
//     * @param whereStr where字符串
//     */
//    public void updateDataToShoppingCarTable(ContentValues values,String whereStr) {
//        updateDataToTable(DatabaseConstants.TBL_SHOPPINGCAR, values,whereStr);
//    }
//
    /**
     * 保存数据到cookie表
     * @param values
     */
    public void saveDataToCookieTable(ContentValues[] values) {
        saveDataToTable(DatabaseConstants.TBL_COOKIE, values);
    }


    /**
     * 更新cookie中的某个值
     * @param values 字段-》字段值
     * @param whereStr where字符串
     */
    public void updateDataToCookiesTable(ContentValues values,String whereStr) {
        updateDataToTable(DatabaseConstants.TBL_COOKIE, values,whereStr);
    }

    /**
     * 公共的更新表方法
     * @param table 表名
     * @param values 字段-》字段值
     * @param whereStr where字符串
     */
    private void updateDataToTable(String table, ContentValues values,String whereStr) {
        Uri uri = createUri(table);
        contentResolver.update(uri, values,whereStr,null);
    }
//
//    /**
//     * 删除购物车的某个商品
//     * @param whereStr where字符串
//     */
//    public void deleteDataToShoppingCarTable(String whereStr) {
//        deleteDataToTable(DatabaseConstants.TBL_SHOPPINGCAR,whereStr);
//    }
//
//    /**
//     * 删除历史表中的所有商品
//     */
//    public void deleteDataToGoodsTable() {
//        deleteDataToTable(DatabaseConstants.TBL_GOODS,null);
//    }
//
//    /**
//     * 删除历史搜索表中的所有内容
//     */
//    public void deleteDataToHistoryTable() {
//        deleteDataToTable(DatabaseConstants.TBL_HISTORY,null);
//    }

    /**
     * 清除cookie所有信息
     */
    public void deleteDataToCookieTable() {
        deleteDataToTable(DatabaseConstants.TBL_COOKIE,null);
    }

//    /**
//     * 清除分站所有信息
//     */
//    public void deleteDataToSubStationTable() {
//        deleteDataToTable(DatabaseConstants.TBL_SUBSTATION,null);
//    }
//
//    /**
//     * 清除分站所有信息
//     */
//    public void deleteDataToGeoSubStationTable() {
//        deleteDataToTable(DatabaseConstants.TBL_GEO_SUBSTATION,null);
//    }
//
//    /**
//     * 清除分类所有信息
//     */
//    public void deleteDataToCategoryTable() {
//        deleteDataToTable(DatabaseConstants.TBL_CATEGORY,null);
//    }
//
//    /**
//     * 清除热门城市所有信息
//     */
//    public void deleteDataToHotCityTable() {
//        deleteDataToTable(DatabaseConstants.TBL_HOTCITY,null);
//    }
//
//    /**
//     * 清除区域所有信息
//     */
//    public void deleteDataToGeoTable() {
//        deleteDataToTable(DatabaseConstants.TBL_CITIES,null);
//    }
//
//
    /**
     * 公共的删除表数据方法
     * @param table 表名
     * @param whereStr where字符串
     */
    private void deleteDataToTable(String table,String whereStr) {
        Uri uri = createUri(table);
        contentResolver.delete(uri,whereStr,null);
    }
//
    /**
     * 创建对应表的url
     * @param tableName 表名
     * @return
     */
    private Uri createUri(String tableName) {
        return Uri.parse(DataProvider.AUTHORITY + tableName);
    }
}
