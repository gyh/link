package com.example.market.ljw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.market.ljw.R;
import com.example.market.ljw.common.frame.MyActivity;
import com.example.market.ljw.common.frame.taskstack.ApplicationManager;
import com.example.market.ljw.common.frame.taskstack.NeedShowAgainModule;

/**
 * Created by GYH on 2014/12/6.
 */
public class FragmenTest extends MyActivity{
    static int n;
    @Override
    protected View realCreateViewMethod(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup) {
        View fragmentview = paramLayoutInflater.inflate(R.layout.fragment_test, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        fragmentview.setLayoutParams(layoutParams);
        TextView view = (TextView)fragmentview.findViewById(R.id.testview);
        n = getArguments().getInt("ceshi");
        n++;
        view.setText(n+"");
        View btn = fragmentview.findViewById(R.id.nextbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MarketListFragment.MarketListFragmentTM marketListFragmentTM = new MarketListFragment.MarketListFragmentTM(R.id.fragmenttest);
                ApplicationManager.go(marketListFragmentTM);
            }
        });
        return fragmentview;
    }

    /**
     * 应用列表图
     */
    public static class FragmenTestTM extends NeedShowAgainModule {

        private FragmenTest appListFragment;
        private int id;

        public FragmenTestTM(int id){
            this.id = id;
        }

        protected void doInit() {
            this.appListFragment = new FragmenTest();
            this.appListFragment.setArguments(getBundle());
        }

        protected void doShow() {
            addAndCommit(id,this.appListFragment);
        }
    }
}
