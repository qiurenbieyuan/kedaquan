package com.yangs.just.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.yangs.just.R;
import com.yangs.just.score.VpnSource;

/**
 * Created by yangs on 2017/10/11 0011.
 */

public class VpnActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private Toolbar toolbar;
    private TextView tv_bu;
    private EditText et_user;
    private EditText et_pwd;
    private Button bt_login;
    private Handler handler;
    private int vpn_status_code;
    private int jw_status_code;
    private int kb_status_code;
    private int current;
    private VpnSource vpnSource;
    private AlertDialog use_dialog;
    private ProgressBar pb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vpnactivity_layout);
        initView();
        initHandler();
        if (use_dialog != null && APPAplication.save.getInt("vpn_use_stat", 0) == 0)
            use_dialog.show();
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        pb.setVisibility(View.GONE);
                        bt_login.setText("登录VPN系统");
                        switch (vpn_status_code) {
                            case 0:
                                APPAplication.showToast("登录vpn系统成功!", 0);
                                APPAplication.save.edit().putString("vpn_user", et_user.getText().toString().trim())
                                        .putString("vpn_pwd", et_pwd.getText().toString().trim()).apply();
                                tv_bu.setText("第 2 / 2 步,请登录教务系统");
                                bt_login.setText("登录教务系统");
                                et_user.setText(null);
                                et_pwd.setText(null);
                                current = 1;
                                break;
                            case -1:
                                APPAplication.showToast("用户名或密码错误", 0);
                                break;
                            case -2:
                                APPAplication.showToast("此用户正在其他地方登录使用,请手动退出!", 0);
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(vpnSource.getLocation());
                                intent.setData(content_url);
                                startActivity(intent);
                                break;
                            case -3:
                                APPAplication.showToast("网络出错", 0);
                                break;
                        }
                        break;
                    case 2:
                        pb.setVisibility(View.GONE);
                        switch (vpn_status_code) {
                            case 0:
                                pb.setVisibility(View.VISIBLE);
                                tv_bu.setText("登录成功,正在导入课表....");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        kb_status_code = vpnSource.getKebiao("2017-2018-1");
                                        handler.sendEmptyMessage(3);
                                    }
                                }).start();
                                bt_login.setText("登录VPN系统");
                                current = 0;
                                break;
                            case -1:
                                APPAplication.showToast("用户名或密码错误", 0);
                                break;
                            case -2:
                                APPAplication.showToast("网络出错", 0);
                                break;
                        }
                        break;
                    case 3:
                        pb.setVisibility(View.GONE);
                        switch (kb_status_code) {
                            case 0:
                                APPAplication.save.edit().putString("xh", et_user.getText().toString().trim())
                                        .putString("pwd", et_pwd.getText().toString().trim()).apply();
                                setResult(4);
                                finish();
                                break;
                            case -1:
                                tv_bu.setText("第 1 / 2 步");
                                APPAplication.showToast("没有公布课表", 0);
                                break;
                            case -2:
                                tv_bu.setText("第 1 / 2 步");
                                APPAplication.showToast("网络出错", 0);
                                break;
                        }
                        break;
                }
            }
        };
    }

    private void initView() {
        current = 0;
        toolbar = (Toolbar) findViewById(R.id.vpnactivity_toolbar);
        pb = (ProgressBar) findViewById(R.id.vpnactivity_pb);
        pb.setVisibility(View.GONE);
        tv_bu = (TextView) findViewById(R.id.vpnactivity_tv);
        et_user = (EditText) findViewById(R.id.vpnactivity_et_user);
        et_pwd = (EditText) findViewById(R.id.vpnactivity_et_pwd);
        bt_login = (Button) findViewById(R.id.vpnactivity_bt_login);
        bt_login.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        toolbar.setTitle("通过VPN导入课表");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        et_user.setText(APPAplication.save.getString("vpn_user", ""));
        et_pwd.setText(APPAplication.save.getString("vpn_pwd", ""));
        use_dialog = new AlertDialog.Builder(this).setTitle("使用说明")
                .setMessage("登录江科大VPN系统: 用户名为学号,密码为登录信息门户所使用的密码," +
                        "默认为身份证后6位。" +
                        "PS: VPN是可以共享的,学校默认为大家开通了VPN账号,如果vpn无法在科大圈登录,请访问" +
                        "https://vpn.just.edu.cn确认下能否登录进vpn。" +
                        "17届的vpn账号貌似还没开通,可以问学长学姐们借下。" +
                        "Tips:经测试,vpn系统偶尔抽风导致失败,请多尝试几次。" +
                        "有问题请点击右上角菜单反馈给我。").setCancelable(false).setPositiveButton("我知道了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                APPAplication.save.edit().putInt("vpn_use_stat", 1).apply();
                                dialog.dismiss();
                            }
                        }).create();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vpn_menu_use:
                if (use_dialog != null)
                    use_dialog.show();
                else
                    APPAplication.showToast("how to use dialog is null ", 0);
                break;
            case R.id.vpn_menu_talk:
                PackageManager packageManager = getPackageManager();
                try {
                    packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                } catch (PackageManager.NameNotFoundException e) {
                    APPAplication.showToast("安装手机QQ后才能反馈哦", 0);
                    break;
                }
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=1125280130";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vpn_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vpnactivity_bt_login:
                final String user = et_user.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (user.equals("")) {
                    et_user.setError("请输入用户名");
                    return;
                } else {
                    et_user.setError(null);
                }
                if (pwd.equals("")) {
                    et_pwd.setError("请输入密码");
                    return;
                } else {
                    et_pwd.setError(null);
                }
                pb.setVisibility(View.VISIBLE);
                if (current == 0) {
                    bt_login.setText("正在登录VPN系统...");
                    vpnSource = new VpnSource(user, pwd);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            vpn_status_code = vpnSource.checkVpnUser();
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                } else if (current == 1) {
                    bt_login.setText("正在登录教务系统...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            jw_status_code = vpnSource.checkJwUser(user, pwd);
                            handler.sendEmptyMessage(2);
                        }
                    }).start();
                }
                break;
        }
    }
}
