package com.example.market.ljw.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.market.ljw.R;
import com.example.market.ljw.core.utils.AppsItemInfo;

import java.util.List;

/**
 * Created by GYH on 2014/10/16.
 */
public class ApplistAdapter extends BaseAdapter {

    private Context context;
    private List<AppsItemInfo> appsItemInfoList;

    public ApplistAdapter(Context context,List<AppsItemInfo> appsItemInfoList){
        this.context = context;
        this.appsItemInfoList = appsItemInfoList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return appsItemInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return appsItemInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;
        if (convertView == null) {
            // 使用View的对象itemView与R.layout.item关联
            convertView = inflater.inflate(R.layout.app_listview_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.apps_image);
            holder.label = (TextView) convertView
                    .findViewById(R.id.apps_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(appsItemInfoList.get(position).getIcon());
        holder.label.setText(appsItemInfoList.get(position).getlabelName().toString());

        return convertView;

    }

    private class ViewHolder{
        ImageView icon;
        TextView label;
    }
}
