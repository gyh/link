package com.example.market.ljw.core.common.http;


import com.example.market.ljw.entity.bean.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * 请求设置
 * */
public class HttpSetting implements HttpSettingParams {

	public static final int CACHE_MODE_AUTO = 0;
	public static final int CACHE_MODE_ONLY_CACHE = 1;
	public static final int CACHE_MODE_ONLY_NET = 2;
	//请求参数
	private int id;
	private Map<String,Object> paramMaps;
	private int cacheMode;
	private String finalUrl;
	private boolean isTopPriority;
	private JSONObject jsonParams;
	private HttpGroup.OnEndListener onEndListener;
	private HttpGroup.OnErrorListener onErrorListener;
	private HttpGroup.OnReadyListener onReadyListener;
	private HttpGroup.OnStartListener onStartListener;
    private HttpGroup.OnParseListener onParseListener;
	private boolean post;
	private int priority;
	private int type;
	//需要返回的实体类型
	private Class currentEntity;
    //需要返回的Type
    private Type currentType;
	private boolean isReady;

    public Type getCurrentType() {
        return currentType;
    }

    public void setCurrentType(Type currentType) {
        this.currentType = currentType;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Class getCurrentEntity() {
		return currentEntity;
	}

	public void setCurrentEntity(Class currentEntity) {
		this.currentEntity = currentEntity;
	}

	//获取缓存的key
	public String getCacheKey() {
		StringBuilder keyValue = new StringBuilder();
		keyValue.append(finalUrl);
		if (paramMaps != null) {
			Iterator iterator = paramMaps.keySet().iterator();
			while (iterator.hasNext()) {
				String s = (String) iterator.next();
                if(!"vcode".equals(s) && !"t".equals(s)){
                    keyValue.append(s+ paramMaps.get(s));
                }
			}
		}
		return keyValue.toString().replace("/", "").trim();
	}


	public int getCacheMode() {
		return cacheMode;
	}


	public String getFinalUrl() {
		return finalUrl;
	}

	public JSONObject getJsonParams() {
		if (jsonParams == null)
			jsonParams = new JSONObject();
		return jsonParams;
	}

	public HttpGroup.OnEndListener getOnEndListener() {
		return onEndListener;
	}

	public HttpGroup.OnErrorListener getOnErrorListener() {
		return onErrorListener;
	}

	public HttpGroup.OnReadyListener getOnReadyListener() {
		return onReadyListener;
	}

	public HttpGroup.OnStartListener getOnStartListener() {
		return onStartListener;
	}

	public int getPriority() {
		return priority;
	}


	public int getType() {
		return type;
	}



	public boolean isPost() {
		return post;
	}


	public boolean isTopPriority() {
		return isTopPriority;
	}


	public void onEnd(HttpResponse httpresponse) {
		if (onEndListener != null)
			onEndListener.onEnd(httpresponse);
	}

	public void onError(HttpError httperror) {
		if (onErrorListener != null) {
			onErrorListener.onError(httperror);
		}
	}

	public void onStart() {
		if (onStartListener != null)
			onStartListener.onStart();
	}

    public Entity onParse(String result) {
        if (onParseListener != null)
            return onParseListener.onParse(result);
        return null;
    }


	public void setCacheMode(int i) {
		cacheMode = i;
	}

	public void setFinalUrl(String s) {
		finalUrl = s;
	}

	
	public void setJsonParams(JSONObject jsonobject) {
		if (jsonobject != null)
			try {
				jsonParams = new JSONObject(jsonobject.toString());
			} catch (JSONException jsonexception) {
				jsonexception.printStackTrace();
			}
	}

	public void setListener(HttpGroup.HttpTaskListener httptasklistener) {
		if (httptasklistener instanceof HttpGroup.OnErrorListener)
			onErrorListener = (HttpGroup.OnErrorListener) httptasklistener;
		if (httptasklistener instanceof HttpGroup.OnStartListener)
			onStartListener = (HttpGroup.OnStartListener) httptasklistener;
		if (httptasklistener instanceof HttpGroup.OnEndListener)
			onEndListener = (HttpGroup.OnEndListener) httptasklistener;
		if (httptasklistener instanceof HttpGroup.OnReadyListener)
			onReadyListener = (HttpGroup.OnReadyListener) httptasklistener;
        if (httptasklistener instanceof HttpGroup.OnParseListener)
            onParseListener = (HttpGroup.OnParseListener) httptasklistener;


	}

	public Map<String, Object> getParamMaps() {
		return paramMaps;
	}

	public void setParamMaps(Map<String, Object> paramMaps)
    {
		this.paramMaps = paramMaps;
	}

	public void setPost(boolean flag) {
		post = flag;
	}

	public void setPriority(int i) {
		priority = i;
	}

	public void setTopPriority(boolean flag) {
		isTopPriority = flag;
	}

	public void setType(int i) {
		type = i;
	}

	public HttpSetting() {
	}

	@Override
	public void putJsonParam(String s, Object obj) {

	}

	@Override
	public void putMapParams(String s, String s1) {

	}

	@Override
	public void setReady(boolean flag) {
		isReady = flag;
	}
	
	public boolean isReady() {
		return isReady;
	}

}
