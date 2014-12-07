package com.example.market.ljw.common.frame;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.market.ljw.common.http.HttpGroup;

/**
 * 提供基本操作的接口
 * 
 * @author yepeng
 * 
 */
public interface IMyActivity {

	

	/**
	 * 页面销毁时执行此回调方法
	 * IDestroyListener
	 * @param paramIDestroyListener
	 */
	public void addDestroyListener(IDestroyListener paramIDestroyListener);

	/**
	 * 获取发送消息队列的handler
	 * 
	 * @return
	 */
	public Handler getHandler();

	/**
	 * 获取默认的http请求池
	 * 
	 * @return
	 */
	public HttpGroup getHttpGroupaAsynPool();

	/**
	 * 获取定制的http请求池
	 * 
	 * @param type
	 *            请求类型
	 * @return
	 */
	public HttpGroup getHttpGroupaAsynPool(int type);

	/**
	 * 获取当前的活动
	 * 
	 * @return
	 */
	public FragmentActivity getThisActivity();

	/**
	 * 给handler推送消息
	 * 
	 * @param paramRunnable
	 */
	public void post(Runnable paramRunnable);

	/**
	 * 给handler延时推送消息
	 * 
	 * @param paramRunnable
	 *            延时时间
	 */
	public void post(Runnable paramRunnable, int paramInt);

	/**
	 * 实例化布局
	 */
	public View inflate(int layoutResourceId);
	
	public void executeImage(String imageUrl, HttpGroup.OnEndListener onEndListener);

}