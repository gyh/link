package com.example.market.ljw.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.MyActivity;
import com.example.market.ljw.core.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.core.common.frame.taskstack.NeedShowAgainModule;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.ui.LoginActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by GYH on 2014/10/21.
 */
public class WebViewFragment extends MyActivity {

    private String H5_REDIRECT_URL = "ljwphone://LJWH5/";

    public static final int LJW_SIGN_OUT_APP = 1000;//退出应用

    public static final int LJW_SIGN_OUT_H5 = 1001;//退出h5

    public static WebView mWebView;
    private View fragmentview;
    private NiftyDialogBuilder dialogBuilder;

    @Override
    protected View realCreateViewMethod(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup) {
        fragmentview = paramLayoutInflater.inflate(R.layout.fragment_webview, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fragmentview.setLayoutParams(layoutParams);
        mWebView = (WebView) fragmentview.findViewById(R.id.webviewhtml);
        dialogBuilder = NiftyDialogBuilder.getInstance(getBaseActivity());

        //可以通过getSettings()获得WebSettings，然后用setJavaScriptEnabled()使能JavaScript：
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.requestFocus();//触摸焦点起作用.如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件

        webSettings.setAllowFileAccess(true);// 设置允许访问文件数据

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置页面可以弹出框

        mWebView.setWebChromeClient(new MyWebChromeClient());//设置弹出框

        //WebView cookies清理
        CookieSyncManager.createInstance(getBaseActivity());
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        //清理cache 和历史记录的方法：
        mWebView.clearCache(true);
        mWebView.clearHistory();


        //加载网页
        String url = getArguments().getString(Constant.ValueKey.URLKEY);
        mWebView.loadUrl(url);

        //设置点击在本地打开
        WebViewClient client = new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    url = java.net.URLDecoder.decode(url, "utf-8");
                    if (url != null && url.contains(H5_REDIRECT_URL)) {
                        String data = url.replace(H5_REDIRECT_URL, "");
                        data = data.replace("page_type","PAGE_TYPE").replace("page_key", "PAGE_KEY");
                        JsonParser jsonParser = new JsonParser();
                        JsonElement jsonElement = jsonParser.parse(data);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        int pageType = jsonObject.get("PAGE_TYPE").getAsInt();
                        String pageKey = null;//备用字段
                        if(jsonObject.has("PAGE_KEY")){
                            JsonElement pageKeyElement = jsonObject.get("PAGE_KEY");
                            if(pageKeyElement.isJsonObject()){
                                pageKey = jsonObject.get("PAGE_KEY").getAsJsonObject().toString();
                            }else{
                                pageKey = jsonObject.get("PAGE_KEY").getAsString();
                            }
                        }
                        switch (pageType){
                            case LJW_SIGN_OUT_H5:
                                ApplicationManager.back();
                                break;
                            case LJW_SIGN_OUT_APP:
                                getThisActivity().finish();
//                                startActivity(new Intent(getThisActivity(), LoginActivity.class));
                                break;
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

        };
        mWebView.setWebViewClient(client);
        return fragmentview;
    }

    @Override
    public void onResume() {
        super.onResume();
        dialogBuilder.dismiss();
    }

    /**
     * 点击弹出框
     * */
    class MyWebChromeClient extends WebChromeClient{

        //两个按钮的弹出框
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            PromptUtil.showonJsConfirm(getBaseActivity(), Effectstype.Shake, dialogBuilder, fragmentview, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    result.confirm();
                    dialogBuilder.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    result.cancel();
                    dialogBuilder.dismiss();
                }
            }, message);
            return true;
        }

        //一个按钮的弹出框
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            PromptUtil.showonJsAlert(getBaseActivity(), Effectstype.Shake, dialogBuilder, fragmentview, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    result.confirm();
                    dialogBuilder.dismiss();
                }
            }, message);
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    /**
     * 商场
     */
    public static class WebViewFragmentTM extends NeedShowAgainModule {

        private WebViewFragment webViewFragment;
        private int id;

        public WebViewFragmentTM(int id) {
            this.id = id;
        }

        protected void doInit() {
            this.webViewFragment = new WebViewFragment();
            this.webViewFragment.setArguments(getBundle());
        }

        protected void doShow() {
            replaceAndCommit(id, this.webViewFragment);
        }
    }
}
