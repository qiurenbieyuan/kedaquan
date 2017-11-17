package com.yangs.kedaquan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
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
    private LinearLayout ll_zr;
    private LinearLayout ll_open;
    private LinearLayout ll_versioninfo;
    private LinearLayout ll_share;
    private LinearLayout ll_yangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_about_layout);
        toolbar = findViewById(R.id.me_about_toolbar);
        tv_version = findViewById(R.id.me_about_tv_version);
        ll_zr = findViewById(R.id.me_about_zr);
        ll_versioninfo = findViewById(R.id.me_about_versioninfo);
        ll_share = findViewById(R.id.me_about_share);
        ll_open = findViewById(R.id.me_about_open);
        ll_yangs = findViewById(R.id.me_about_yangs);
        ll_zr.setOnClickListener(this);
        ll_versioninfo.setOnClickListener(this);
        ll_open.setOnClickListener(this);
        ll_share.setOnClickListener(this);
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
            case R.id.me_about_zr:
                new AlertDialog.Builder(meAbout.this).setTitle("开发者招聘")
                        .setMessage("本人15大三,想找一个会写android的来把这个项目传递下去," +
                                "会点android入门的就行,以后也可以把这个写进你的" +
                                "简历中哦！")
                        .setPositiveButton("与我联系", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PackageManager packageManager = getPackageManager();
                                try {
                                    packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                                } catch (PackageManager.NameNotFoundException e) {
                                    APPAplication.showToast("安装手机QQ后才能与我联系哦", 0);
                                    return;
                                }
                                String url = "mqqwpa://im/chat?chat_type=wpa&uin=1125280130";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).create().show();
                break;
            case R.id.me_about_share:
                View share_v = LayoutInflater.from(meAbout.this)
                        .inflate(R.layout.me_about_share_layout, null);
                new AlertDialog.Builder(meAbout.this)
                        .setView(share_v).create().show();
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
            case R.id.me_about_open:
                String msg = "1.jsoup<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/jhy/jsoup</a><br>" +
                        "2.okhttp<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/square/okhttp</a><br>" +
                        "3.banner<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/youth5201314/banner</a><br>" +
                        "4.fresco<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/facebook/fresco</a><br>" +
                        "5.GalleryPick<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/YancyYe/GalleryPick</a><br>" +
                        "6.LRecyclerView<br>&nbsp;&nbsp;&nbsp;<a href=''>https://github.com/jdsjlzx/LRecyclerView</a><br>";
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

