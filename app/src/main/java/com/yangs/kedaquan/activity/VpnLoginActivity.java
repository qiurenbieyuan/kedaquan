package com.yangs.kedaquan.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yangs.kedaquan.R;
import com.yangs.kedaquan.utils.VpnSource;

/**
 * Created by yangs on 2017/11/16 0016.
 */

public class VpnLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private EditText et_user;
    private EditText et_pwd;
    private TextView tv_info;
    private Button bt_login;
    private int vpn_status_code;
    private VpnSource vpnSource;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vpnloginactivity_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        toolbar = (Toolbar) findViewById(R.id.vpnloginactivity_toolbar);
        et_user = (EditText) findViewById(R.id.vpnloginactivity_et_user);
        et_pwd = (EditText) findViewById(R.id.vpnloginactivity_et_pwd);
        tv_info = (TextView) findViewById(R.id.vpnloginactivity_tv_info);
        bt_login = (Button) findViewById(R.id.vpnloginactivity_bt_login);
        bt_login.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        toolbar.setTitle("绑定VPN");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_info.setText("     学校默认为大家开通了VPN账号,通过绑定VPN,你可以使用到内网资源,如:" +
                "导入课表,查询图书馆书籍,选课,进入实验系统等," +
                "VPN系统的密码默认为身份证后6位。");
        et_user.setText(APPAplication.vpn_user);
        et_pwd.setText(APPAplication.vpn_pwd);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    progressDialog.dismiss();
                    switch (vpn_status_code) {
                        case 0:
                            APPAplication.showToast("绑定VPN成功!", 0);
                            finish();
                            break;
                        case -1:
                            APPAplication.showDialog(VpnLoginActivity.this,
                                    "用户名或密码错误");
                            break;
                        case -2:
                            new AlertDialog.Builder(VpnLoginActivity.this)
                                    .setTitle("提示").setMessage("此用户已经在其他地方登录,点击确定后" +
                                    "会自动打开一个浏览器页面,请在页面里点击继续会话,然后点击右上角的" +
                                    "登出,再返回科大圈绑定VPN!")
                                    .setCancelable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            Uri content_url = Uri.parse(vpnSource.getLocation());
                                            intent.setData(content_url);
                                            startActivity(intent);
                                        }
                                    }).create().show();
                            break;
                        case -3:
                            APPAplication.showDialog(VpnLoginActivity.this,
                                    "网络出错");
                            break;
                    }
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vpnloginactivity_bt_login:
                final String user = et_user.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (user.equals("")) {
                    et_user.setError("请输入学号");
                    return;
                } else {
                    et_user.setError(null);
                }
                if (pwd.equals("")) {
                    et_pwd.setError("请输入VPN密码");
                    return;
                } else {
                    et_pwd.setError(null);
                }
                progressDialog.setMessage("正在登录江科大VPN系统...");
                progressDialog.show();
                vpnSource = new VpnSource(user, pwd);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        vpn_status_code = vpnSource.checkVpnUser();
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                break;
        }
    }
}
