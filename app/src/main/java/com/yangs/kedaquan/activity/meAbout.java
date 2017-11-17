package com.yangs.kedaquan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangs.kedaquan.R;

/**
 * Created by yangs on 2017/2/24.
 */

public class meAbout extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView tv_version;
    private LinearLayout ll_gw;
    private LinearLayout ll_open;
    private LinearLayout ll_versioninfo;
    private LinearLayout ll_source;
    private LinearLayout ll_yangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_about_layout);
        toolbar = (Toolbar) findViewById(R.id.me_about_toolbar);
        tv_version = (TextView) findViewById(R.id.me_about_tv_version);
        ll_versioninfo = (LinearLayout) findViewById(R.id.me_about_versioninfo);
        ll_source = (LinearLayout) findViewById(R.id.me_about_source);
        ll_open = (LinearLayout) findViewById(R.id.me_about_open);
        ll_gw = (LinearLayout) findViewById(R.id.me_about_gw);
        ll_yangs = (LinearLayout) findViewById(R.id.me_about_yangs);
        ll_versioninfo.setOnClickListener(this);
        ll_open.setOnClickListener(this);
        ll_source.setOnClickListener(this);
        ll_gw.setOnClickListener(this);
        ll_yangs.setOnClickListener(this);
        String version;
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (Exception e) {
            version = "(get version error)";
            e.printStackTrace();
        }
        tv_version.setText("版本号 " + version);
        toolbar.setTitle("关于");
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(meAbout.this, Browser.class);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.me_about_gw:
                bundle.putString("url", "http://www.onewanqian.cn:8000/");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.me_about_yangs:
                bundle.putString("url", "http://www.myangs.com");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.me_about_versioninfo:
                bundle.putString("url", "http://www.myangs.com/kedaquan_version.html");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.me_about_source:
                new AlertDialog.Builder(meAbout.this).setTitle("数据来源").setCancelable(false)
                        .setMessage("模拟浏览器登陆教务系统,对页面Jsoup处理得到数据。").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            case R.id.me_about_open:
                String msg = "1.jsoup<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/jhy/jsoup</a><br>" +
                        "2.okhttp<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/square/okhttp</a><br>" +
                        "3.banner<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/youth5201314/banner</a><br>" +
                        "4.fresco<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/facebook/fresco</a><br>" +
                        "5.GalleryPick<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/YancyYe/GalleryPick</a><br>" +
                        "6.LRecyclerView<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/jdsjlzx/LRecyclerView</a><br>" +
                        "7.FloatingActionButton<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/makovkastar/FloatingActionButton</a><br>" +
                        "8.Android Open Source Project<br>&nbsp;&nbsp;&nbsp;<a href=''>https://developer.android.google.cn/license.html</a>";
                new AlertDialog.Builder(meAbout.this).setTitle("开源许可").setCancelable(false)
                        .setMessage(Html.fromHtml(msg)).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
        }
    }
}

