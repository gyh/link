package com.example.market.ljw.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.market.ljw.ui.MainActivity;
import com.example.market.ljw.R;

import com.example.market.ljw.core.common.frame.AppContext;
import com.example.market.ljw.core.common.frame.MyActivity;
import com.example.market.ljw.core.common.frame.taskstack.NeedShowAgainModule;
import com.example.market.ljw.core.utils.AppsItemInfo;
import com.example.market.ljw.service.ApplistAdapter;
import com.example.market.ljw.core.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/10/18.
 */
public class MarketListFragment extends MyActivity implements AdapterView.OnItemClickListener {

    List<AppsItemInfo> appsItemInfos;
    List<String> urlList = new ArrayList<String>();
    private GridView gridview;
    private WebView mWebView;
    private ApplistAdapter applistAdapter;

    @Override
    protected View realCreateViewMethod(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup) {
        View view = paramLayoutInflater.inflate(R.layout.fragment_marketlist,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        initData();
        initView(view);
        return view;
    }

    private void initData(){
        appsItemInfos = new ArrayList<AppsItemInfo>();
        AppsItemInfo appsItemInfo = new AppsItemInfo();
        appsItemInfo.setIcon(getActivity().getResources().getDrawable(R.drawable.icon_my_market));
        appsItemInfo.setlabelName("我的商城");
        urlList.add(Constant.LJWBASE_URL+"MemberShop/Login.aspx?returnUrl=/MemberShop/Default.aspx" +
                "&token="+ AppContext.getInstance().getBaseActivity()
                .getDataForShPre(Constant.SaveKeys.TOKENKEY,""));
        appsItemInfos.add(appsItemInfo);

        AppsItemInfo appsItemInfo1 = new AppsItemInfo();
        appsItemInfo1.setIcon(getActivity().getResources().getDrawable(R.drawable.icon_my_account));
        appsItemInfo1.setlabelName("会员中心");
        urlList.add(Constant.LJWBASE_URL+"MemberShop/Login.aspx?returnUrl=/MemberCenterHome.aspx" +
                "&token="+AppContext.getInstance().getBaseActivity()
                .getDataForShPre(Constant.SaveKeys.TOKENKEY,""));
        appsItemInfos.add(appsItemInfo1);

        AppsItemInfo appsItemInfo4 = new AppsItemInfo();
        appsItemInfo4.setIcon(getActivity().getResources().getDrawable(R.drawable.icon_baidu));
        appsItemInfo4.setlabelName("百度");
        urlList.add("http://m.baidu.com");
        appsItemInfos.add(appsItemInfo4);

        AppsItemInfo appsItemInfo3 = new AppsItemInfo();
        appsItemInfo3.setIcon(getActivity().getResources().getDrawable(R.drawable.jd_buy_icon));
        appsItemInfo3.setlabelName("京东商城");
        urlList.add("http://m.jd.com");
        appsItemInfos.add(appsItemInfo3);
        AppsItemInfo appsItemInfo5 = new AppsItemInfo();
        appsItemInfo5.setIcon(getActivity().getResources().getDrawable(R.drawable.icon_ganji));
        appsItemInfo5.setlabelName("赶集");
        urlList.add("http://wap.ganji.com");
        appsItemInfos.add(appsItemInfo5);

        AppsItemInfo appsItemInfo6 = new AppsItemInfo();
        appsItemInfo6.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_taobao));
        appsItemInfo6.setlabelName("淘宝");
        urlList.add("http://h5.m.taobao.com/index2.html");
        appsItemInfos.add(appsItemInfo6);

        AppsItemInfo appsItemInfo7 = new AppsItemInfo();
        appsItemInfo7.setIcon(getActivity().getResources().getDrawable(R.drawable.wb_new_icon));
        appsItemInfo7.setlabelName("同城");
        urlList.add("http://m.58.com");
        appsItemInfos.add(appsItemInfo7);

        AppsItemInfo appsItemInfo10 = new AppsItemInfo();
        appsItemInfo10.setIcon(getActivity().getResources().getDrawable(R.drawable.icon_dangdang));
        appsItemInfo10.setlabelName("当当");
        urlList.add("http://m.dangdang.com");
        appsItemInfos.add(appsItemInfo10);

        AppsItemInfo appsItemInfo11 = new AppsItemInfo();
        appsItemInfo11.setIcon(getActivity().getResources().getDrawable(R.drawable.amzn_icon_android));
        appsItemInfo11.setlabelName("亚马逊");
        urlList.add("http://www.amazon.cn");
        appsItemInfos.add(appsItemInfo11);

        AppsItemInfo appsItemInfo12 = new AppsItemInfo();
        appsItemInfo12.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_meituan));
        appsItemInfo12.setlabelName("美团网");
        urlList.add("http://i.meituan.com");
        appsItemInfos.add(appsItemInfo12);

        AppsItemInfo appsItemInfo8 = new AppsItemInfo();
        appsItemInfo8.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_weibo_logo));
        appsItemInfo8.setlabelName("新浪");
        urlList.add("http://sina.cn/?vt=4");
        appsItemInfos.add(appsItemInfo8);

        AppsItemInfo appsItemInfo9 = new AppsItemInfo();
        appsItemInfo9.setIcon(getActivity().getResources().getDrawable(R.drawable.icon_youku));
        appsItemInfo9.setlabelName("优酷");
        urlList.add("http://www.youku.com/");
        appsItemInfos.add(appsItemInfo9);

        AppsItemInfo appsItemInfo13 = new AppsItemInfo();
        appsItemInfo13.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_qunawang));
        appsItemInfo13.setlabelName("去哪网");
        urlList.add("http://touch.qunar.com/");
        appsItemInfos.add(appsItemInfo13);

        AppsItemInfo appsItemInfo14 = new AppsItemInfo();
        appsItemInfo14.setIcon(getActivity().getResources().getDrawable(R.drawable.icon_dazhongdianping));
        appsItemInfo14.setlabelName("点评");
        urlList.add("http://m.dianping.com/");
        appsItemInfos.add(appsItemInfo14);
    }

    private void initView(View view){
        gridview = (GridView)view.findViewById(R.id.gridview);
        applistAdapter = new ApplistAdapter(getActivity(),appsItemInfos);
        gridview.setAdapter(applistAdapter);
        gridview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ((MainActivity)getBaseActivity()).showWebView(0, urlList.get(i));
    }

    /**
     * 商场列表图
     */
    public static class MarketListFragmentTM extends NeedShowAgainModule {

        private MarketListFragment marketListFragment;
        private int id;

        public MarketListFragmentTM(int id){
            this.id = id;
        }

        protected void doInit() {
            this.marketListFragment = new MarketListFragment();
            this.marketListFragment.setArguments(getBundle());
        }

        protected void doShow() {
            replaceAndCommit(id, this.marketListFragment);
        }
    }
}
