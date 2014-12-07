package com.example.market.ljw.db.persistance;

/**
 * Created by GYH on 2014/10/20.
 */
public final class DatabaseConstants {

    //cookie 表名
    protected static final String TBL_COOKIE = "cookie";

    /**
     * cookie存储库
     */
    public static class Cookie{
        protected static final String cookieKey = "cookieKey";
        protected static final String cookieValue = "cookieValue";
    }

    //todo yw
    public static class RawWhere {

//        public static final String CITY_GEOLEVEL = Cities.geoLevel + "=" + "1";
//        public static final String CITY_GEOLEVEL2 = Cities.geoLevel + "=" + "2";
//        public static final String CITY_GEOLEVEL3 = Cities.geoLevel + "=" + "3";
//        public static final String CITY_GEOLEVEL4 = Cities.geoLevel + "=" + "4";
//
//        public static final String CITY_PID = Cities.pID + "=";
//        public static final String CITY_ID = Cities.geoID + "=";
//        public static final String CITY_NAME = Cities.geoName + " like ";
//
//        public static final String CATE_ROOT = Category.rootID + "=" + "-1";
//        public static final String CATE_PART_PID = Category.saleCatPID + "=";
//
//        public static final String SHOPPING_GOODSID = ShoppingCarItem.goodsID + "=";
//        public static final String SHOPPING_SUBSTATIONID = ShoppingCarItem.substationID + "=";
//
//        //查看商品历史搜索某个
//        public static final String GOODSID = Goods.goodsID + "=";
//        //搜索某个信息
//        public static final String SEARCHMSG = History.searchMsg + "=";

        //cookie 检索条件
        public static final String COOKIE_KEY = Cookie.cookieKey + "=";
        //是否登陆的条件
        public static final String COOKIE_VERIFY_KEY = Cookie.cookieKey + "=" + "'sstk'";
        //查询区域对应分站的id
//        public static final String GEO_STATION = GeoSubStation.geoID + "=";
    }
}
