package com.yangs.kedaquan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.yangs.kedaquan.score.VpnSource;

/**
 * Created by yangs on 2017/11/16 0016.
 */

public class KebiaoGetActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private EditText et_user;
    private EditText et_pwd;
    private Button bt_login;
    private TextView tv_forget;
    private int kebiao_status_code;
    private VpnSource vpnSource;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kebiaogetactivity_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        toolbar = (Toolbar) findViewById(R.id.kebiaogetactivity_toolbar);
        et_user = (EditText) findViewById(R.id.kebiaogetactivity_et_user);
        et_pwd = (EditText) findViewById(R.id.kebiaogetactivity_et_pwd);
        bt_login = (Button) findViewById(R.id.kebiaogetactivity_bt_login);
        tv_forget = (TextView) findViewById(R.id.kebiaogetactivity_tv_forget);
        bt_login.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_arraw_back_white);
        toolbar.setTitle("导入课表");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (APPAplication.save.getString("vpn_cookie", "").equals("")) {
            APPAplication.showToast("请先绑定VPN", 1);
            finish();
        }
        et_user.setText(APPAplication.save.getString("xh", ""));
        et_pwd.setText(APPAplication.save.getString("pwd", ""));
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    switch (kebiao_status_code) {
                        case 0:
                            progressDialog.setMessage("正在导入课表...");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    kebiao_status_code = vpnSource.getKebiao("2017-2018-1");
                                    handler.sendEmptyMessage(2);
                                }
                            }).start();
                            break;
                        case -1:
                            progressDialog.dismiss();
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "教务密码错误");
                            break;
                        case -2:
                            progressDialog.dismiss();
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "网络出错");
                            break;
                    }
                    break;
                case 2:
                    progressDialog.dismiss();
                    switch (kebiao_status_code) {
                        case 0:
                            APPAplication.save.edit().putString("xh", et_user.getText().toString().trim())
                                    .putString("pwd", et_pwd.getText().toString().trim()).apply();
                            setResult(4);
                            finish();
                            break;
                        case -1:
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "没有公布课表");
                            break;
                        case -2:
                            APPAplication.showDialog(KebiaoGetActivity.this
                                    , "网络出错");
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
            case R.id.kebiaogetactivity_tv_forget:
                Bundle bundle = new Bundle();
                bundle.putString("url", "https://vpn.just.edu.cn/framework/,DanaInfo=jwgl.just.edu.cn,Port=8080+enteraccount.jsp");
                bundle.putString("cookie", APPAplication.save.getString("vpn_cookie", ""));
                Intent intent = new Intent(KebiaoGetActivity.this, Browser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.kebiaogetactivity_bt_login:
                final String user = et_user.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (user.equals("")) {
                    et_user.setError("请输入学号");
                    return;
                } else {
                    et_user.setError(null);
                }
                if (pwd.equals("")) {
                    et_pwd.setError("请输入教务密码");
                    return;
                } else {
                    et_pwd.setError(null);
                }
                progressDialog.setMessage("正在登录教务系统...");
                progressDialog.show();
                vpnSource = new VpnSource("", "");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        kebiao_status_code = vpnSource.checkJwUser(user, pwd);
                        handler.sendEmptyMessage(1);
                    }
                }).start();
                break;
        }
    }
}
