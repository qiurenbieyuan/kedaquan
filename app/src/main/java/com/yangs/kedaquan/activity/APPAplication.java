package com.yangs.kedaquan.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.tencent.smtt.sdk.QbSdk;
import com.yangs.kedaquan.bbs.BBSSource;
import com.yangs.kedaquan.score.VpnSource;
import com.yangs.kedaquan.utils.CrashHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yangs on 2017/3/18.
 */

public class APPAplication extends Application {
    private static Context context;
    public static Boolean isFindUrl = false;
    public static List<String> FindUrlList;
    public static SharedPreferences save;
    public static int kebiao_show_ct;
    public static String term;
    public static String version;
    public static int login_stat;
    public static String name;
    public static int week;
    public static android.database.sqlite.SQLiteDatabase db;
    public static Boolean debug;
    public static BBSSource bbsSource;
    public static VpnSource vpnSource;
    public static Boolean bbs_login_status;
    public static Boolean bbs_login_status_check;
    public static String vpn_user;
    public static String vpn_pwd;
    public static Boolean isInitWebview;

    @Override
    public void onCreate() {
        super.onCreate();
        debug = false;
        bbs_login_status = false;
        bbs_login_status_check = false;
        context = getApplicationContext();
        save = getSharedPreferences("MainActivity", MODE_PRIVATE);
        name = save.getString("name", "未导入");
        login_stat = save.getInt("login_stat", 0);
        kebiao_show_ct = save.getInt("kebiao_show_ct", 0);
        term = save.getString("term", "2017-2018-1");
        db = getApplicationContext().openOrCreateDatabase("info.db", Context.MODE_PRIVATE, null);
        if (bbsSource == null) {
            bbsSource = new BBSSource();
        }
        isInitWebview = save.getBoolean("isInitWebview", false);
        vpn_user = save.getString("vpn_user", "");
        vpn_pwd = save.getString("vpn_pwd", "");
        vpnSource = new VpnSource(vpn_user, vpn_pwd);
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            week = (int) (1 + (Calendar.getInstance().getTime().getTime() - df.parse("2017-9-4")
                    .getTime()) / (1000 * 3600 * 24 * 7));
            if (week < 1 || week > 20)
                week = 1;
        } catch (ParseException e) {
            week = 1;
            e.printStackTrace();
        }
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName()
                    , 0);
            version = packageInfo.versionName;
        } catch (Exception e) {
            version = "1.0";
            e.printStackTrace();
        }
        Fresco.initialize(this);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
        if (!debug) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                if (b) {
                    Log.i("TAG", "X5内核加载成功");
                } else {
                    Log.i("TAG", "X5内核加载失败");
                }
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    public static void showToast(final String src, final int i) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, src, i).show();
            }
        });
    }

    public static String getPath() {
        return context.getFilesDir().getAbsolutePath();
    }

    public static void showDialog(final Context context1, final String src) {
        new AlertDialog.Builder(context1).setTitle("提示").setMessage(src)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public static void showDialog2(final Context context1, final String src, final String title) {
        new AlertDialog.Builder(context1).setTitle(title).setMessage(src)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public static void sendHandler(Runnable r) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(r);
    }

    public static void sendRefreshKebiao(Context context) {
        Intent appWidgetIntent = appWidgetIntent = new Intent();
        appWidgetIntent.setAction("Kedaquan_Widget_Update");
        context.sendBroadcast(appWidgetIntent);
    }

}