package com.example.market.ljw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.market.ljw.core.common.frame.BaseActivity;
import com.example.market.ljw.core.common.frame.MyAppContext;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.entity.bean.output.MemberOutput;
import com.example.market.ljw.entity.bean.output.UserData;
import com.example.market.ljw.ui.MainActivity;
import com.example.market.ljw.R;
import com.example.market.ljw.entity.bean.Entity;
import com.example.market.ljw.entity.bean.output.AdvertisementOutput;
import com.example.market.ljw.core.common.frame.MyActivity;
import com.example.market.ljw.core.common.frame.taskstack.TaskModule;
import com.example.market.ljw.core.common.http.HttpGroup;
import com.example.market.ljw.core.common.http.HttpResponse;
import com.example.market.ljw.service.InputDataUtils;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DPIUtil;
import com.example.market.ljw.core.utils.ImageAdapter;
import com.example.market.ljw.core.utils.PopUtils;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.core.utils.view.HackyViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GYH on 2014/10/16.
 */
public class CarouselFragment extends Fragment {

    public static final String tag = "com.example.market.ljw.fragment.CarouselFragment";

    //轮播图控件
    private HackyViewPager mViewPager;
    //图片连接集合
    private List<String> imageurls = new ArrayList<>();
    //lock图片
    public final static int IMG_WIDTH = 640;
    public final static int IMG_HEIGHT = 150;
    //适配器
    private ImageAdapter imageAdapter;
    //自动轮询间隔
    private int carouselTime = 60000;
    private View fragmentview;

    //轮询通知操作
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //当前无用户接触时，自动播放轮播图
            if (mViewPager != null && !imageAdapter.isTouched()) {
                int targetIndex = mViewPager.getCurrentItem() + 1;
                if (targetIndex >= imageurls.size()) {
                    targetIndex = 0;
                }
                mViewPager.setCurrentItem(targetIndex);
            }
            sendMessageDelayed(handler.obtainMessage(1), carouselTime);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentview = inflater.inflate(R.layout.fragment_carousel, null);
        initView();
        initPagerPic();
        initData();
        return fragmentview;
    }

    /**
     * 初始化广告图片
     */
    private void initPagerPic() {
        ViewGroup.LayoutParams linearParams = mViewPager.getLayoutParams();
        linearParams.height = (int) ((float) DPIUtil.getWidth() / (float) IMG_WIDTH * IMG_HEIGHT);
        linearParams.width = DPIUtil.getWidth();
        ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
        for (int i = 0; i < imageurls.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(linearParams);
            imageViews.add(imageView);
        }
        imageAdapter = new ImageAdapter(imageViews, imageurls, this, new View.OnClickListener() {

            /**
             * 设置事件，当点击轮播条目时，获取当前促销id，进入促销列表
             * @param v
             */
            @Override
            public void onClick(View v) {
                //当前点击的下标
                int position = mViewPager.getCurrentItem();
                //跳转到相应的页面
//                Toast.makeText(getActivity(),"position="+position,Toast.LENGTH_SHORT).show();

            }
        });
        imageAdapter.setInUsing(true);
        mViewPager.setAdapter(imageAdapter);
        initPointIndex(fragmentview, imageurls, mViewPager);
    }

    /**
     * 初始化数据
     * http://markettest.strosoft.com/API/Service.ashx?service_name=get_advertisement_list&data={AdvertisementTypeCode:%27product%27}
     */
    private void initData() {
        if (Utils.isNetworkConnected(getActivity())) {
            Ion.with(MyAppContext.getInstance())
                    .load(Constant.SERVER_URL)
                    .setBodyParameter(Constant.RequestKeys.SERVICENAME, "get_advertisement_list")
                    .setBodyParameter(Constant.RequestKeys.DATA, new Gson().toJson(InputDataUtils.getPics()))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                return;
                            }
                            AdvertisementOutput advertisementOutput = new AdvertisementOutput();
                            try {
                                advertisementOutput.setContent(result.toString());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                PromptUtil.showMessage(getActivity(), e1.toString());
                            }
                            if (advertisementOutput.isSuccess()) {
                                imageurls = new ArrayList<String>();
                                for (int i = 0; i < advertisementOutput.getAdvertisements().size(); i++) {
                                    imageurls.add(Constant.SERVERBASE_URL + advertisementOutput.getAdvertisements().get(i).getImageUrl());
                                }
                                initPointIndex(fragmentview, imageurls, mViewPager);
                                imageAdapter.notifyDataSetChanged();
                                handler.sendMessageDelayed(handler.obtainMessage(1), carouselTime);
                            } else {
                                fragmentview.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            imageurls = new ArrayList<String>();
            imageurls.clear();
            //判断本地存储是否为空
            if (Constant.imageurls.size() == 0) {
                //如果为空，则隐藏
                fragmentview.setVisibility(View.GONE);
            } else {
                //不为空则显示图片
                imageurls.addAll(Constant.imageurls);
                initPagerPic();
                handler.sendMessageDelayed(handler.obtainMessage(1), carouselTime);
            }
        }
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mViewPager = (HackyViewPager) fragmentview.findViewById(R.id.carousel_viewpage);
    }

    /**
     * 初始化下标，并设置下标变化回调
     *
     * @param view      当前全局view
     * @param imageurls 图片地址连接集合
     * @param viewPager 轮播元素
     */
    private void initPointIndex(View view, List<String> imageurls, ViewPager viewPager) {
        final LinearLayout indexParent = (LinearLayout) view.findViewById(R.id.indexpoint_parent);
        for (int i = 0; i < imageurls.size(); i++) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i != imageurls.size() - 1)
                layoutParams.setMargins(0, 0, (int) getActivity().getResources().getDimension(R.dimen.padding_middle), 0);
            ImageView imageView = new ImageView(getActivity());
            if (i == 0)
                imageView.setImageResource(R.drawable.carousel_dot_indicator_state_select);
            else
                imageView.setImageResource(R.drawable.carousel_dot_indicator_state_normal);
            indexParent.addView(imageView, layoutParams);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //上一个选择的元素
            private ImageView imageView;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (imageView != null)//重置上一个元素，为灰色
                    imageView.setImageResource(R.drawable.carousel_dot_indicator_state_normal);
                if (imageView == null) {//如果刚展示的时候，将第一个元素换成未选择
                    ImageView firtInitImageView = (ImageView) indexParent.getChildAt(0);
                    firtInitImageView.setImageResource(R.drawable.carousel_dot_indicator_state_normal);
                }
                //获取当前选择的元素，然后将其换成已选择
                imageView = (ImageView) indexParent.getChildAt(position);
                imageView.setImageResource(R.drawable.carousel_dot_indicator_state_select);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
    }
}
