package com.example.market.ljw.fragment;

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

import com.example.market.ljw.R;
import com.example.market.ljw.core.common.frame.MyAppContext;
import com.example.market.ljw.core.utils.Constant;
import com.example.market.ljw.core.utils.DPIUtil;
import com.example.market.ljw.core.utils.ImageAdapter;
import com.example.market.ljw.core.utils.PromptUtil;
import com.example.market.ljw.core.utils.Utils;
import com.example.market.ljw.core.utils.view.HackyViewPager;
import com.example.market.ljw.entity.bean.output.AdvertisementOutput;
import com.example.market.ljw.service.InputDataUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/10/16.
 */
public class MyCarouselFragment extends Fragment {

    public static final String tag = "com.example.market.ljw.fragment.CarouselFragment";

    public final static int IMG_WIDTH = 640;
    public final static int IMG_HEIGHT = 150;
    //自动轮询间隔
    private int carouselTime = 60000;
    private View fragmentview;
//    private

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentview = inflater.inflate(R.layout.fragment_mycarousel, null);

        return fragmentview;
    }

}
