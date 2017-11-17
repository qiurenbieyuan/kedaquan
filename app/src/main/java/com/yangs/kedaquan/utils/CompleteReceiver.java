package com.yangs.kedaquan.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

/**
 * Created by yangs on 2017/3/1.
 */

public class CompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            DownloadManager.Query query = new DownloadManager.Query();
            //在广播中取出下载任务的id
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            query.setFilterById(id);
            Cursor c = manager.query(query);
            Toast.makeText(context, "下载完成", Toast.LENGTH_LONG).show();
            if (c.moveToFirst()) {
                //获取文件下载路径
                String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                //如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
                if (filename != null) {
                    Intent install_intent = new Intent();
                    install_intent.setAction(Intent.ACTION_VIEW);
                    install_intent.setDataAndType(Uri.fromFile(new File(filename)), "application/vnd.android.package-archive");
                    install_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install_intent);
                }
            }
        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
            Toast.makeText(context, "正在下载中", Toast.LENGTH_LONG).show();
        }

    }

}