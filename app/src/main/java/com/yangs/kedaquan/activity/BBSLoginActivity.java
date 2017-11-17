package com.yangs.kedaquan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangs.kedaquan.R;

/**
 * Created by yangs on 2017/8/17 0017.
 */

public class BBSLoginActivity extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener, View.OnFocusChangeListener {
    private Toolbar toolbar;
    private EditText et_user;
    private EditText et_pwd;
    private EditText et_pwd2;
    private EditText et_email;
    private Button bt_login;
    private TextView tv_regis;
    private Handler handler;
    private ProgressDialog progressDialog;
    private int login_status;
    private String regis_status;
    private TextInputLayout textInputLayout;
    private TextInputLayout textInputLayout2;
    private CheckBox checkBox;
    private ImageView iv_22;
    private ImageView iv_33;
    private LinearLayout ll_2233;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbsloginactivity_layout);
        toolbar = (Toolbar) findViewById(R.id.bbslogin_toolbar);
        toolbar.setTitle("登录到论坛");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        et_user = (EditText) findViewById(R.id.bbslogin_et_user);
        et_pwd = (EditText) findViewById(R.id.bbslogin_et_pwd);
        et_pwd2 = (EditText) findViewById(R.id.bbslogin_et_pwd2);
        et_email = (EditText) findViewById(R.id.bbslogin_et_email);
        checkBox = (CheckBox) findViewById(R.id.bbslogin_cb);
        iv_22 = (ImageView) findViewById(R.id.bbslogin_iv_22);
        iv_33 = (ImageView) findViewById(R.id.bbslogin_iv_33);
        ll_2233 = (LinearLayout) findViewById(R.id.bbslogin_ll_2233);
        checkBox.setChecked(APPAplication.save.getBoolean("bbs_pwd_rem", true));
        if (APPAplication.save.getBoolean("bbs_pwd_rem", true))
            et_pwd.setText(APPAplication.save.getString("bbs_pwd", ""));
        et_user.setText(APPAplication.save.getString("bbs_user", ""));
        bt_login = (Button) findViewById(R.id.bbslogin_bt_login);
        tv_regis = (TextView) findViewById(R.id.bbslogin_tv_register);
        textInputLayout = (TextInputLayout) findViewById(R.id.bbslogin_til);
        textInputLayout2 = (TextInputLayout) findViewById(R.id.bbslogin_til2);
        setHandler();
        bt_login.setOnClickListener(this);
        tv_regis.setOnClickListener(this);
        et_user.setOnFocusChangeListener(this);
        et_pwd.setOnFocusChangeListener(this);
        textInputLayout.setVisibility(View.GONE);
        textInputLayout2.setVisibility(View.GONE);
        if (!APPAplication.save.getBoolean("bbs_login_state", false)) {
            new AlertDialog.Builder(this).setTitle("提示").setCancelable(false)
                    .setMessage("新版论坛上线,与之前的论坛数据不同步，需要重新注册才能发帖哦。")
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            APPAplication.save.edit().putBoolean("bbs_login_state", true).apply();
                        }
                    }).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bbslogin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.cancel();
                        switch (login_status) {
                            case 1:
                                APPAplication.bbs_login_status_check = false;
                                APPAplication.save.edit().putBoolean("bbs_pwd_rem", checkBox.isChecked()).apply();
                                setResult(3);
                                finish();
                                break;
                            case -1:
                                APPAplication.showDialog(BBSLoginActivity.this, "用户名或密码错误");
                                break;
                            case -2:
                                APPAplication.showToast("网络出错..", 0);
                                break;
                        }
                        break;
                    case 2:
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.cancel();
                        if (regis_status.contains("感谢您注册")) {
                            APPAplication.showToast("注册成功,请登录", 0);
                            textInputLayout.setVisibility(View.GONE);
                            textInputLayout2.setVisibility(View.GONE);
                            ll_2233.setVisibility(View.VISIBLE);
                            checkBox.setVisibility(View.VISIBLE);
                            et_pwd.setText(null);
                            tv_regis.setText("还没有注册?");
                            bt_login.setText("登录");
                            toolbar.setTitle("登录到论坛");
                        } else {
                            APPAplication.showDialog2(BBSLoginActivity.this, regis_status, "注册失败");
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bbslogin_bt_login:
                if (textInputLayout.getVisibility() == View.VISIBLE) {
                    final String user = et_user.getText().toString().trim();
                    final String pwd = et_pwd.getText().toString().trim();
                    final String pwd2 = et_pwd2.getText().toString().trim();
                    final String email = et_email.getText().toString().trim();
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
                    if (pwd2.equals("")) {
                        et_pwd2.setError("请再次输入密码");
                        return;
                    } else {
                        et_pwd2.setError(null);
                    }
                    if (!pwd.equals(pwd2)) {
                        et_pwd2.setError("两次输入密码不一致");
                        return;
                    } else {
                        et_pwd2.setError(null);
                    }
                    if (email.equals("")) {
                        et_email.setError("请输入邮箱,便于取回密码");
                        return;
                    } else {
                        et_email.setError(null);
                    }
                    if (progressDialog == null)
                        progressDialog = new ProgressDialog(BBSLoginActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("注册中...");
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            regis_status = APPAplication.bbsSource.register(user, pwd, email);
                            handler.sendEmptyMessage(2);
                        }
                    }).start();
                } else {
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
                    if (progressDialog == null)
                        progressDialog = new ProgressDialog(BBSLoginActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("登录中...");
                    progressDialog.show();
                    APPAplication.bbsSource.setContext(BBSLoginActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            login_status = APPAplication.bbsSource.login(user, pwd, checkBox.isChecked());
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                }
                break;
            case R.id.bbslogin_tv_register:
                if (textInputLayout.getVisibility() == View.VISIBLE) {
                    et_user.setText(APPAplication.save.getString("bbs_user", ""));
                    et_pwd.setText(APPAplication.save.getString("bbs_pwd", ""));
                    ll_2233.setVisibility(View.VISIBLE);
                    textInputLayout.setVisibility(View.GONE);
                    textInputLayout2.setVisibility(View.GONE);
                    checkBox.setVisibility(View.VISIBLE);
                    tv_regis.setText("还没有注册?");
                    bt_login.setText("登录");
                    toolbar.setTitle("登录到论坛");
                } else {
                    et_user.setText(null);
                    et_pwd.setText(null);
                    checkBox.setVisibility(View.GONE);
                    ll_2233.setVisibility(View.GONE);
                    textInputLayout.setVisibility(View.VISIBLE);
                    textInputLayout2.setVisibility(View.VISIBLE);
                    tv_regis.setText("已有账号?去登录");
                    bt_login.setText("注册");
                    toolbar.setTitle("注册论坛账号");
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bbslogin_menu_info:
                APPAplication.showDialog(BBSLoginActivity.this, "论坛采用Discuz架设,目前仅支持在" +
                        "浏览器中打开来设置头像。发图片、表情功能正在开发中。。");
                break;
            case R.id.bbslogin_menu_goto:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.myangs.com:81/forum.php?mod=forumdisplay&fid=46&mobile=1");
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.bbslogin_et_user:
                iv_22.setImageDrawable(ContextCompat.getDrawable(BBSLoginActivity.this, R.drawable.ic_22));
                iv_33.setImageDrawable(ContextCompat.getDrawable(BBSLoginActivity.this, R.drawable.ic_33));
                break;
            case R.id.bbslogin_et_pwd:
                iv_22.setImageDrawable(ContextCompat.getDrawable(BBSLoginActivity.this, R.drawable.ic_22_hide));
                iv_33.setImageDrawable(ContextCompat.getDrawable(BBSLoginActivity.this, R.drawable.ic_33_hide));
                break;
        }
    }
}
