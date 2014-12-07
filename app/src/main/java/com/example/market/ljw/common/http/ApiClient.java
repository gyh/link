package com.example.market.ljw.common.http;

import com.example.market.ljw.R;
import com.example.market.ljw.bean.Base;
import com.example.market.ljw.bean.Entity;
import com.example.market.ljw.common.frame.AppContext;
import com.example.market.ljw.exceptions.AppException;
import com.example.market.ljw.utils.IOUtil;
import com.example.market.ljw.utils.PromptUtil;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * API客户端接口：用于访问网络数据
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

    /**
     * 获取网络图片流
     *
     * @param url
     * @return
     */
    public static void getImageFileByNetUrl(String url, File file, HttpGroup.StopController stopcontroller) throws AppException {
        HttpRequest httpRequest = HttpRequest.get(url);
        InputStream inputStream;
        if (httpRequest.ok()) {
            inputStream = httpRequest.buffer();
            try {
                IOUtil.readAsFile(inputStream, new FileOutputStream(file), null, stopcontroller);
            } catch (Exception e) {
                //如果发生异常则删除废文件
                file.delete();
                throw AppException.io(e);
            }
        } else {
            throw AppException.http(httpRequest.code());
        }
    }

    /**
     * 获取数据的通用方法
     *
     * @return
     * @throws AppException 依据类型自动解析，或者回调函数手动解析
     */
    public static Entity getDataByAutoType(HttpGroup.HttpRequest httpRequest) throws AppException {
        try {
            //请求为get请求；以后依据设置内的实体类型去封装数据
            Class entityClass = httpRequest.getHttpSetting().getCurrentEntity();
            Type entityType = httpRequest.getHttpSetting().getCurrentType();
            if (!httpRequest.getHttpSetting().isPost()) {
                HttpRequest httpRequestInstance = HttpRequest.get(httpRequest.getHttpSetting().
                        getFinalUrl(), httpRequest.getHttpSetting().getParamMaps(), true);
                String result = httpRequestInstance.body();
                //解析数据
                Entity entityResult = httpRequest.getHttpSetting().onParse(result);
                if (entityResult == null) {
                    return new Gson().fromJson(
                            new JsonReader(
                                    new InputStreamReader(new ByteArrayInputStream(result.getBytes()),
                                            Base.UTF8)
                            ), entityClass == null ? entityType : entityClass
                    );
                }
                return entityResult;
            } else {
                //或取到请求设置内的参数，然后设置给http-request,请求为post请求；以后依据设置内的实体类型去封装数据
                Map<String, Object> paramMaps = httpRequest.getHttpSetting().getParamMaps();
                String path = httpRequest.getHttpSetting().getFinalUrl();
                HttpRequest httpRequestInstance;
                httpRequestInstance = HttpRequest.post(path, true);
                String result = httpRequestInstance.form(paramMaps).body();
                //解析数据
                Entity entityResult = httpRequest.getHttpSetting().onParse(result);
                if (entityResult == null) {
                    return new Gson().fromJson(
                            new JsonReader(new InputStreamReader(new ByteArrayInputStream(result.getBytes()), Base.UTF8)),
                            entityClass == null ? entityType : entityClass);
                }
                return entityResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException)
                throw (AppException) e;
            if (e instanceof HttpRequest.HttpRequestException && //当产生超时异常时，提示请求超时
                    e.getMessage().toString().contains("SocketTimeoutException")) {
                AppContext.getInstance().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        PromptUtil.showMessage(AppContext.getInstance().getBaseActivity(), R.string.timeout);
                    }
                });
            }
            throw AppException.network(e);
        }
    }

}
