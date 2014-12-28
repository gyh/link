package com.example.market.ljw.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.market.ljw.utils.Constant;
import com.example.market.ljw.utils.Utils;

import java.io.File;

/**
* Created by GYH on 2014/11/22.
*/
public class DownloadCompleteReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            Toast.makeText(context, "下载完成！", Toast.LENGTH_LONG).show();
            String fileName = "";
            /**
             * The download manager is a system service that handles long-running HTTP downloads.
             */
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);//从下载服务获取下载管理器
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);//设置过滤状态：成功
            Cursor c = downloadManager.query(query);// 查询以前下载过的‘成功文件’
            if (c.moveToFirst()) {// 移动到最新下载的文件
                fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            }
            Utils.showSystem("文件名称",fileName);
            File f = new File(fileName.replace("file://", ""));// 过滤路径
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setAction(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.fromFile(f),"application/vnd.android.package-archive");
            context.startActivity(intent2);
        }
    }
}
