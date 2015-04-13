package com.example.market.ljw.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.market.ljw.ui.MainActivity;
import com.example.market.ljw.R;
import com.example.market.ljw.core.utils.AppsItemInfo;
import com.example.market.ljw.core.common.frame.MyActivity;
import com.example.market.ljw.core.common.frame.taskstack.NeedShowAgainModule;
import com.example.market.ljw.service.ApplistAdapter;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DateUtils;
import com.example.market.ljw.core.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/10/18.
 */
public class AppListFragment extends MyActivity implements AdapterView.OnItemClickListener,View.OnClickListener {

    // 用来记录应用程序的信息
    List<AppsItemInfo> appsItemInfos = new ArrayList<AppsItemInfo>();
    private GridView gridview;
    private PackageManager pManager;
    private ApplistAdapter applistAdapter;
    private List<Intent> intentList = new ArrayList<Intent>();
    private View fragmentview ,layoutPhone,layoutMsg,layoutpeople,applistbottom,applistbottombtn;
    private ImageView applistbottomimg;
    private TextView tvTime , tvDate;
    private boolean isshowClock=false, ishowbottom = false; //判断是否点击显示时间
    private long delayMillis = 1000;
    private Intent mmsIntent = null;

    Handler clockhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    DateUtils.setDateStringtoView(tvDate);
                    DateUtils.setTimeStringToView(tvTime);
                    sendMessageDelayed(clockhandler.obtainMessage(1), delayMillis);
                    break;
            }
            //显示日期
        }
    };

    //轮询通知操作
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            applistAdapter.notifyDataSetChanged();
        }
    };
    @Override
    protected View realCreateViewMethod(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup) {
        fragmentview = paramLayoutInflater.inflate(R.layout.fragment_applist, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fragmentview.setLayoutParams(layoutParams);
        initView();
        return fragmentview;
    }

    /**
     * 获取图片、应用名、包名
     */
    private void initData() {
        appsItemInfos.clear();
        intentList.clear();
        pManager = getActivity().getPackageManager();
        List<ApplicationInfo> applicationInfos = Utils.getInstallAppInfo(getActivity());
        AppsItemInfo ljw = new AppsItemInfo();
        ljw.setlabelName("链接网");
        appsItemInfos.add(ljw);
        intentList.add(null);
        for (int i = 0; i < applicationInfos.size(); i++) {
            ApplicationInfo pinfo = applicationInfos.get(i);

            Intent intent = getBaseActivity().getPackageManager().getLaunchIntentForPackage(pinfo.packageName);
            if (intent == null) {
                continue;
            }
            if(Constant.PACKAGENAME.equals(pinfo.packageName)){
                appsItemInfos.get(0).setIcon(pManager.getApplicationIcon(pinfo));
                continue;
            }
            AppsItemInfo shareItem = new AppsItemInfo();
            // 设置图片
            shareItem.setIcon(pManager.getApplicationIcon(pinfo));
            // 设置应用程序名字
            shareItem.setlabelName(pManager.getApplicationLabel(pinfo).toString());
            Utils.showSystem("pManager","Label"+shareItem.getlabelName());
            // 设置应用程序的包名
            shareItem.setPackageName(pinfo.packageName);
            Utils.showSystem("pManager","PackageName"+shareItem.getPackageName());
            //获取信息跳转的intent
            if(shareItem.getPackageName().equals(Constant.MMSPACKAGENAME)){
                mmsIntent = intent;
            }
            appsItemInfos.add(shareItem);
            intentList.add(intent);
        }
    }

    /**
     * 取得gridview
     */
    private void initView() {
        applistbottombtn = fragmentview.findViewById(R.id.applistbottombtn);
        applistbottom = fragmentview.findViewById(R.id.applistbottom);
        applistbottomimg = (ImageView)fragmentview.findViewById(R.id.applistbottomimg);
        layoutPhone = fragmentview.findViewById(R.id.layoutphone);
        layoutMsg = fragmentview.findViewById(R.id.layoutmsg);
        layoutpeople = fragmentview.findViewById(R.id.layoutpeople);
        gridview = (GridView) fragmentview.findViewById(R.id.gridview);
        applistAdapter = new ApplistAdapter(getActivity(), appsItemInfos);
        // 设置gridview的Adapter
        gridview.setAdapter(applistAdapter);
        // 点击应用图标时，做出响应
        gridview.setOnItemClickListener(this);
        fragmentview.findViewById(R.id.bt_clock).setOnClickListener(this);
        tvTime = (TextView)fragmentview.findViewById(R.id.tvtime);
        tvDate = (TextView)fragmentview.findViewById(R.id.tvdate);
        layoutPhone.setOnClickListener(this);
        layoutMsg.setOnClickListener(this);
        layoutpeople.setOnClickListener(this);
        applistbottombtn.setOnClickListener(this);
    }

    @Override

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            ((MainActivity)getBaseActivity()).showMarkList();
        } else {
            if (intentList.get(i) != null) {
                Constant.makeAppName = intentList.get(i).getPackage();
                getActivity().startActivity(intentList.get(i));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layoutmsg:
                if(mmsIntent==null){
                    Intent intent = new Intent();
                    intent.setClassName("com.android.mms","com.android.mms.ui.ConversationList");
                    getActivity().startActivity(intent);
                    Constant.makeAppName = "com.android.mms";
                    Utils.showSystem("pManager","PackageName 信息 null");
                }else {
                    getActivity().startActivity(mmsIntent);
                    Constant.makeAppName = "com.android.mms";
                    Utils.showSystem("pManager","PackageName 信息 nonull");
                }
                break;
            case R.id.layoutpeople:
                Intent intent001 = new Intent();
                intent001.setClassName("com.android.contacts","com.android.contacts.activities.PeopleActivity");
                Constant.makeAppName = "com.android.contacts";
                startActivity(intent001);
                break;
            case R.id.layoutphone:
                Intent intent003 = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"));
                intent003.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent003);
                break;
            case R.id.applistbottombtn:
                if(ishowbottom){
                    ishowbottom = false;
                    applistbottomimg.setImageDrawable(getResources().getDrawable(R.drawable.android_list_idex_left));
                    applistbottom.setVisibility(View.GONE);
                }else {
                    ishowbottom = true;
                    applistbottomimg.setImageDrawable(getResources().getDrawable(R.drawable.android_list_idex));
                    applistbottom.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
    @Override
    public void onResume() {
        initData();
        applistAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    /**
     * 应用列表图
     */
    public static class AppListFragmentTM extends NeedShowAgainModule {

        private AppListFragment appListFragment;
        private int id;

        public AppListFragmentTM(int id){
            this.id = id;
        }

        protected void doInit() {
            this.appListFragment = new AppListFragment();
            this.appListFragment.setArguments(getBundle());
        }

        protected void doShow() {
            addAndCommit(id,this.appListFragment);
        }
    }
}
