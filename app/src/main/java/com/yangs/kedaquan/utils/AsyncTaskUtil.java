package com.yangs.kedaquan.utils;

/**
 * Created by yangs on 2017/3/9 0009.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

public class AsyncTaskUtil extends AsyncTask<String, Double, Boolean> {

    public static final String TAG = "AsyncTaskUtil";

    public static final int NOTIFICATION_PROGRESS_UPDATE = 0x10;//用于更新下载进度的标志
    public static final int NOTIFICATION_PROGRESS_SUCCEED = 0x11;//表示下载成功
    public static final int NOTIFICATION_PROGRESS_FAILED = 0x12;//表示下载失败

    //URL
    private String mUrl;
    //activity
    private Context mContext;
    //任务定时器
    private Timer mTimer;
    //定时任务
    private TimerTask mTask;
    //主线程传递过来的handler
    private Handler mHandler;
    //所要下载的文件大小
    private long mFileSize;
    //已下载的文件大小
    private long mTotalReadSize;
    //app名称
    private String app_name;
    //AsyncTaskRunnable实现了Runnable接口，用于更新下载进度的显示
    private AsyncTaskRunnable mRunnable;

    //构造方法
    public AsyncTaskUtil(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;

        mTimer = new Timer();
        mTask = new TimerTask() {//在run方法中执行定时的任务
            @Override
            public void run() {
                //size表示下载进度的百分比
                float size = (float) mTotalReadSize * 100 / (float) mFileSize;
                //通过AsyncTaskRunnable的setDatas方法下载的进度和状态（更新中、失败、成功）
                mRunnable.setDatas(NOTIFICATION_PROGRESS_UPDATE, size);
                //更新进度
                mHandler.post(mRunnable);
            }
        };
        mRunnable = new AsyncTaskRunnable(mContext);
    }


    // 执行耗时操作,params[0]为url，params[1]为文件名（空则写入null）
    @Override
    protected Boolean doInBackground(String... params) {

        //任务定时器一定要启动
        mTimer.schedule(mTask, 0, 500);

        try {
            mUrl = params[0];
            //建立链接
            URLConnection connection = new URL(mUrl).openConnection();
            //获取文件大小
            mFileSize = connection.getContentLength();

            //获得输入流
            InputStream is = connection.getInputStream();
            //先建立文件夹
            File fold = new File(getFolderPath());
            if (!fold.exists()) {
                fold.mkdirs();
            }

            String fileName = "";
            //判断文件名：用户自定义或由url获得
            if (params[1] != null) {
                app_name = params[1];
                fileName = params[1];
            } else {
                fileName = getFileName(params[0]);
            }
            //文件输出流
            FileOutputStream fos = new FileOutputStream(new File(getFolderPath()
                    + fileName));

            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) != -1) {
                mTotalReadSize += len;
                fos.write(buff, 0, len);
            }
            fos.flush();
            fos.close();

        } catch (Exception e) {
            //异常,下载失败
            mRunnable.setDatas(NOTIFICATION_PROGRESS_FAILED, 0);
            //发送显示下载失败
            mHandler.post(mRunnable);
            if (mTimer != null && mTask != null) {
                mTimer.cancel();
                mTask.cancel();
            }
            e.printStackTrace();
            return false;
        }
        //下载成功
        mRunnable.setDatas(NOTIFICATION_PROGRESS_SUCCEED, 0);
        mHandler.post(mRunnable);
        if (mTimer != null && mTask != null) {
            mTimer.cancel();
            mTask.cancel();
        }
        return true;
    }

    //由url获得文件名
    private String getFileName(String string) {
        return string.substring(string.lastIndexOf("/") + 1);
    }

    //下载文件夹路径
    private String getFolderPath() {
        return Environment.getExternalStorageDirectory().toString() + "/KeDaQuan/";
    }


    // doInBackground方法之前调用，初始化UI
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // 在doInBackground方法之后调用
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            Toast.makeText(mContext, "更新下载完成，请安装! ", Toast.LENGTH_SHORT).show();
            File file = new File(
                    Environment.getExternalStorageDirectory().toString() + "/KeDaQuan/"
                    , app_name);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {
                //兼容安卓7.0以上
                Uri apkUri = FileProvider.getUriForFile(mContext, "com.yangs.just.fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "下载失败,可能是您没有打开存储权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
    }

}