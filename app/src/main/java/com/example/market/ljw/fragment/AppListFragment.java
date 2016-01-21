package com.example.market.ljw.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
    private AppData appDataFirst;
    private GridView gridview;
    private PackageManager pManager;
    private ApplistAdapter applistAdapter;

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
            AppData appData = (AppData)msg.getData().getCharSequence("data",null);
            if(appData!=null){
                appDataFirst.setData(appData);
                applistAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected View realCreateViewMethod(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup) {
        fragmentview = paramLayoutInflater.inflate(R.layout.fragment_applist, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fragmentview.setLayoutParams(layoutParams);
        appDataFirst = new AppData();
        initView();
        return fragmentview;
    }

    /**
     * 获取图片、应用名、包名
     */
    private void initData() {
        pManager = getActivity().getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppData appData = new AppData();
                List<ApplicationInfo> applicationInfos = Utils.getInstallAppInfo(getActivity());
                AppsItemInfo ljw = new AppsItemInfo();
                ljw.setlabelName("超级会员");
                appData.getAppsItemInfos().add(ljw);
                appData.getIntentList().add(null);
                for (int i = 0; i < applicationInfos.size(); i++) {
                    ApplicationInfo pinfo = applicationInfos.get(i);

                    Intent intent = getBaseActivity().getPackageManager().getLaunchIntentForPackage(pinfo.packageName);
                    if (intent == null) {
                        continue;
                    }
                    if(Constant.PACKAGENAME.equals(pinfo.packageName)){
                        appData.getAppsItemInfos().get(0).setIcon(pManager.getApplicationIcon(pinfo));
                        continue;
                    }
                    AppsItemInfo shareItem = new AppsItemInfo();
                    // 设置图片
                    shareItem.setIcon(pManager.getApplicationIcon(pinfo));
                    // 设置应用程序名字
                    shareItem.setlabelName(pManager.getApplicationLabel(pinfo).toString());
                    // 设置应用程序的包名
                    shareItem.setPackageName(pinfo.packageName);
                    //获取信息跳转的intent
                    if(shareItem.getPackageName().equals(Constant.MMSPACKAGENAME)){
                        mmsIntent = intent;
                    }
                    appData.getAppsItemInfos().add(shareItem);
                    appData.getIntentList().add(intent);
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("data",appData);
                message.setData(bundle);
                handler.sendMessageDelayed(message,0);
            }
        }).start();
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
        applistAdapter = new ApplistAdapter(getActivity(), appDataFirst.getAppsItemInfos());
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
            if (appDataFirst.intentList.get(i) != null) {
                Constant.makeAppName = appDataFirst.getIntentList().get(i).getPackage();
                getActivity().startActivity(appDataFirst.getIntentList().get(i));
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
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            addAndCommit(id, this.appListFragment);
        }
    }


    /**
     * 数据
     * */
    class AppData implements CharSequence {

        List<Intent> intentList = new ArrayList<Intent>();
        List<AppsItemInfo> appsItemInfos = new ArrayList<AppsItemInfo>();

        void setData(AppData appData){
            intentList.clear();
            appsItemInfos.clear();
            intentList.addAll(appData.intentList);
            appsItemInfos.addAll(appData.appsItemInfos);
        }

        public List<Intent> getIntentList() {
            return intentList;
        }

        public List<AppsItemInfo> getAppsItemInfos() {
            return appsItemInfos;
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int i) {
            return 0;
        }

        @Override
        public CharSequence subSequence(int i, int i2) {
            return null;
        }
    }
}
