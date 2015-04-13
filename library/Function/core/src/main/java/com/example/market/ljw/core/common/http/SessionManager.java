package com.example.market.ljw.core.common.http;
import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DbHelper;
import com.example.market.ljw.db.persistance.CookieReader;
import com.example.market.ljw.db.persistance.CookieWriter;
import com.example.market.ljw.entity.bean.Cookie;
import com.github.kevinsawicki.http.HttpRequest;
import org.json.JSONObject;
import java.util.Collections;
import java.util.List;


/**
 * cookie管理类
 */
public class SessionManager {

    //cookie读取
    private static CookieReader cookieReader;
    //cookie写入
    private static CookieWriter cookieWriter;

    /**
     * 获取cookie读取类
     * @return
     */
    private static CookieReader getCookieReader() {
        if(cookieReader == null)
            cookieReader = new CookieReader(DbHelper.getDatabaseReader(AppContext.getInstance().getContentResolver()));
        return cookieReader;
    }

    /**
     * 获取cookie写入类
     * @return
     */
    private static CookieWriter getCookieWriter() {
        if(cookieWriter == null)
            cookieWriter = new CookieWriter(DbHelper.getDatabaseWriter(AppContext.getInstance().getContentResolver()));
        return cookieWriter;
    }

    /**
     * 如果返回1401登陆失效状态码时则清除cookies
     */
    public static void clearCookiesIfFailure(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String code = jsonObject.getString("err_code");
            if ("1401".equals(code)) { //没有登陆或者登陆失效的话，则清空sessionid
                clearCookie();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果返回1401登陆失效状态码时则清除cookies
     */
    public static void clearCookie() {
        getCookieWriter().delCookies();
    }

    public static void addCookies(HttpRequest httpRequest) {
        String[] cookies = httpRequest.headers("Set-Cookie");
        //更新cookie到数据库
        for(int i=0;i<cookies.length;i++){
            String cookieUnit = cookies[i];
            if(cookieUnit.indexOf("=") != -1) {
                String key = cookieUnit.substring(0, cookies[i].indexOf("="));
                if(getCookieReader().getCookieByKey(key).size() == 0) {
                    getCookieWriter().saveCookies(Collections.singletonList(new Cookie(key, cookieUnit)));
                }else{
                    getCookieWriter().updateCookieByKey(new Cookie(key, cookieUnit),key);
                }
            }
        }
    }

    /**
     * 获取从本地获取 Api cookies
     */
    public static String getCookies() {
        List<Cookie> cookies = getCookieReader().getCookies();
        StringBuilder cookiesStr = new StringBuilder();
        for(int i=0;i<cookies.size();i++){
            cookiesStr.append(cookies.get(i).getCookieValue());
            if(i != cookies.size() -1)
                cookiesStr.append(";");
        }
        return cookiesStr.toString();
    }

    /**
     * 为httpRequest增加token
     * @param httpRequest 请求参数类
     */
    public static void addToken(HttpGroup.HttpRequest httpRequest) {
        //如果存在s_token_id存在的话
        List<Cookie> cookies = getCookieReader().getCookieByKey(Constant.KEY_S_TOKEN_ID);
        if(cookies.size() == 0)
            return;
        //如果内容不对的话，则返回
        String cookieValue = cookies.get(0).getCookieValue();
        int startPosition = cookieValue.indexOf("=");
        int endPosition = cookieValue.indexOf(";");
        if(startPosition == -1 || endPosition == -1)
            return;
        //将token放到param中
        httpRequest.getHttpSetting().getParamMaps().put("cookie_s_token_id",
                cookieValue.substring(startPosition+1,endPosition));
    }

    /**
     * 获取h5需要的cookie
     * @return
     */
    public static Object[] getH5Cookies() {
        return getCookieReader().getCookies().toArray();
    }
}
