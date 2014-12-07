package com.example.market.ljw.common.http;

/**
 * 请求设置
 * */
public interface HttpSettingParams {

	public abstract void putJsonParam(String s, Object obj);

	public abstract void putMapParams(String s, String s1);

	public abstract void setReady(boolean flag);
}
