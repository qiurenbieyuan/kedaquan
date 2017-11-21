package com.yangs.kedaquan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangs.kedaquan.activity.APPAplication;
import com.yangs.kedaquan.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yangs on 2017/2/27.
 */

public class Splash extends Activity implements View.OnClickListener {
    private LinearLayout linearLayout;
    private int count = 3;
    private TextView tv_skip;
    private Timer timer;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (APPAplication.debug) {
            startActivity(new Intent(Splash.this, MainActivity.class));
            finish();
        } else {
            setContentView(R.layout.splash_layout);
            linearLayout = findViewById(R.id.splash_layout);
            tv_skip = findViewById(R.id.splash_tv_skip);
            linearLayout.setBackgroundResource(R.drawable.bg_splash);
            linearLayout.setOnClickListener(this);
            tv_skip.setOnClickListener(this);
            task = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            };
            timer = new Timer();
            timer.schedule(task, 100, 1000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null)
            timer.cancel();
        if (task != null)
            task.cancel();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (count == 0) {
                        if (timer != null)
                            timer.cancel();
                        if (task != null)
                            task.cancel();
                        startActivity(new Intent(Splash.this, MainActivity.class));
                        finish();
                    }
                    tv_skip.setVisibility(View.VISIBLE);
                    tv_skip.setText("跳过(" + count + "s)");
                    count--;
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.splash_layout:
                if (timer != null)
                    timer.cancel();
                if (task != null)
                    task.cancel();
                startActivity(new Intent(Splash.this, MainActivity.class));
                PackageManager packageManager = getPackageManager();
                try {
                    packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=985581806";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    APPAplication.showToast("安装QQ后才能抢占名额哦", 0);
                }
                finish();
                break;
            case R.id.splash_tv_skip:
                startActivity(new Intent(Splash.this, MainActivity.class));
                if (timer != null)
                    timer.cancel();
                if (task != null)
                    task.cancel();
                finish();
                break;
        }
    }
}
