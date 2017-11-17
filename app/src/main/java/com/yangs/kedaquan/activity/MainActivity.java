package com.yangs.kedaquan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yangs.kedaquan.R;
import com.yangs.kedaquan.coursepj.CoursePJ;
import com.yangs.kedaquan.fragment.BBSFragment;
import com.yangs.kedaquan.fragment.FindFragment;
import com.yangs.kedaquan.fragment.KebiaoFragment;
import com.yangs.kedaquan.fragment.MeFragment;
import com.yangs.kedaquan.utils.AsyncTaskUtil;
import com.yangs.kedaquan.utils.getKebiaoSource;
import com.yangs.kedaquan.utils.VersionControl;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
        OnClickListener, MeFragment.OnLoginListener, KebiaoFragment.OnKebiaoRefreshListener {

    private long exitTime = 0;
    private String tmp_version;
    private TextView bottom_tv_kebiao;
    private ImageView bottom_iv_kebiao;
    private TextView bottom_tv_school;
    private ImageView bottom_iv_school;
    private TextView bottom_tv_find;
    private ImageView bottom_iv_find;
    private TextView bottom_tv_me;
    private ImageView bottom_iv_me;
    private LinearLayout mTabKebiao;
    private LinearLayout mTabSchool;
    private LinearLayout mTabFind;
    private LinearLayout mTabMe;
    private KebiaoFragment kebiaoFragment;
    private BBSFragment bbsFragment;
    private FindFragment findFragment;
    private MeFragment meFragment;
    private FragmentManager fm;
    private int currentFragment;
    private ProgressDialog waitingDialog;
    private AsyncTaskUtil mDownloadAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
        mTabKebiao = (LinearLayout) findViewById(R.id.id_tab_kebiao);
        mTabSchool = (LinearLayout) findViewById(R.id.id_tab_school);
        mTabFind = (LinearLayout) findViewById(R.id.id_tab_find);
        mTabMe = (LinearLayout) findViewById(R.id.id_tab_me);
        mTabKebiao.setOnClickListener(this);
        mTabSchool.setOnClickListener(this);
        mTabFind.setOnClickListener(this);
        mTabMe.setOnClickListener(this);
        bottom_tv_kebiao = (TextView) mTabKebiao.findViewById(R.id.id_tab_tv_kebiao);
        bottom_iv_kebiao = (ImageView) mTabKebiao.findViewById(R.id.id_tab_iv_kebiao);
        bottom_tv_school = (TextView) mTabSchool.findViewById(R.id.id_tab_tv_school);
        bottom_iv_school = (ImageView) mTabSchool.findViewById(R.id.id_tab_iv_school);
        bottom_tv_find = (TextView) mTabFind.findViewById(R.id.id_tab_tv_find);
        bottom_iv_find = (ImageView) mTabFind.findViewById(R.id.id_tab_iv_find);
        bottom_tv_me = (TextView) mTabMe.findViewById(R.id.id_tab_tv_me);
        bottom_iv_me = (ImageView) mTabMe.findViewById(R.id.id_tab_iv_me);
        bottom_tv_kebiao.setTextColor(getResources().getColor(R.color.colorGreen));
        bottom_iv_kebiao.setImageResource(R.drawable.bottom_kebiao_iv_press);
        fm = getSupportFragmentManager();
        showFragment(1);
        if (!APPAplication.debug) {
            checkUpdate();
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 3:
                    if (waitingDialog != null)
                        waitingDialog.cancel();
                    try {
                        JSONObject version_tmp = new JSONObject(tmp_version);
                        final String url = version_tmp.getString("url");
                        final String app_name = version_tmp.getString("app_name");
                        String info = "版本号 : " + version_tmp.getString("version") + "\n"
                                + "大小 : " + version_tmp.getString("size") + "\n"
                                + "详细 : \n" + version_tmp.getString("detail");
                        getApplicationContext();
                        new AlertDialog.Builder(MainActivity.this).setTitle("发现新版本")
                                .setCancelable(false)
                                .setMessage(info)
                                .setNegativeButton("下载", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "正在下载中...", Toast.LENGTH_SHORT).show();
                                        mDownloadAsyncTask = new AsyncTaskUtil(MainActivity.this, handler);
                                        mDownloadAsyncTask.execute(url, app_name);
                                    }
                                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "新版本有更好的体验哦，推荐下载!", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    APPAplication.showDialog(MainActivity.this, "学号或密码错误!");
                    waitingDialog.dismiss();
                    break;
                case 6:
                    APPAplication.showToast("抱歉，教务系统正在维护中，请稍后再绑定!", 0);
                    waitingDialog.dismiss();
                    break;
                case 7:
                    waitingDialog.setMessage("导入课表 [" + APPAplication.term + "].....");
                    break;
                case 8:
                    APPAplication.showToast("导入成功!", 0);
                    kebiaoFragment.initKebiao();
                    kebiaoFragment.toolbar_login.setText(APPAplication.save.getString("name", ""));
                    showFragment(1);
                    APPAplication.sendRefreshKebiao(getApplicationContext());
                    break;
                case 9:
                    waitingDialog.cancel();
                    APPAplication.showDialog(MainActivity.this, "账号或密码为空!");
                    break;
                case 10:
                    new AlertDialog.Builder(MainActivity.this).setTitle("导入课表失败")
                            .setMessage("1.教务系统还没有公布 " + APPAplication.term + " 学期的课表,请稍后再试\n" +
                                    "2.当前还没有完成评教，不能查看课表。\n是否需要打开 一键评教?")
                            .setCancelable(false).setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, CoursePJ.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                    break;
                case 12:
                    APPAplication.showDialog(MainActivity.this, "获取课表时正则失败!");
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final VersionControl versionControl = new VersionControl();
                final JSONObject notice;
                tmp_version = versionControl.check("启动 " + APPAplication.save.getString("xh", "首次"));
                JSONObject a;
                try {
                    a = new JSONObject(tmp_version);
                    if (tmp_version.equals("false")) {
                    } else {
                        if (!a.getString("version").equals(APPAplication.version)) {
                            handler.sendEmptyMessage(3);
                        } else {
                        }
                    }
                } catch (Exception e) {
                    APPAplication.showToast(e.toString(), 1);
                }
                try {
                    notice = versionControl.getServerNotice();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (APPAplication.save.getInt("notice_id", 0) != notice.getInt("id")) {
                                    new AlertDialog.Builder(MainActivity.this).setCancelable(false)
                                            .setTitle("通知")
                                            .setMessage(notice.getString("notice"))
                                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    try {
                                                        APPAplication.save.edit().putInt("notice_id", notice.getInt("id"))
                                                                .apply();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).create().show();
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void showFragment(int index) {
        FragmentTransaction transaction = fm.beginTransaction();
        if (kebiaoFragment != null)
            transaction.hide(kebiaoFragment);
        if (bbsFragment != null)
            transaction.hide(bbsFragment);
        if (findFragment != null)
            transaction.hide(findFragment);
        if (meFragment != null)
            transaction.hide(meFragment);
        bottom_tv_kebiao.setTextColor(Color.rgb(152, 152, 152));
        bottom_iv_kebiao.setImageResource(R.drawable.bottom_kebiao_iv);
        bottom_tv_school.setTextColor(Color.rgb(152, 152, 152));
        bottom_iv_school.setImageResource(R.drawable.buttom_iv_school2);
        bottom_tv_find.setTextColor(Color.rgb(152, 152, 152));
        bottom_iv_find.setImageResource(R.drawable.ic_button_iv_find);
        bottom_tv_me.setTextColor(Color.rgb(152, 152, 152));
        bottom_iv_me.setImageResource(R.drawable.buttom_iv_me);
        switch (index) {
            case 1:
                bottom_tv_kebiao.setTextColor(getResources().getColor(R.color.colorGreen));
                bottom_iv_kebiao.setImageResource(R.drawable.bottom_kebiao_iv_press);
                if (kebiaoFragment == null) {
                    kebiaoFragment = new KebiaoFragment();
                    transaction.add(R.id.main_content, kebiaoFragment);
                } else {
                    transaction.show(kebiaoFragment);
                }
                currentFragment = 1;
                break;
            case 2:
                bottom_tv_school.setTextColor(getResources().getColor(R.color.colorGreen));
                bottom_iv_school.setImageResource(R.drawable.ic_buttom_iv_school2_press);
                if (bbsFragment == null) {
                    bbsFragment = new BBSFragment();
                    transaction.add(R.id.main_content, bbsFragment);
                } else {
                    transaction.show(bbsFragment);
                }
                currentFragment = 2;
                break;
            case 3:
                bottom_tv_find.setTextColor(getResources().getColor(R.color.colorGreen));
                bottom_iv_find.setImageResource(R.drawable.ic_button_iv_find_press);
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    transaction.add(R.id.main_content, findFragment);
                } else {
                    transaction.show(findFragment);
                }
                currentFragment = 3;
                break;
            case 4:
                bottom_tv_me.setTextColor(getResources().getColor(R.color.colorGreen));
                bottom_iv_me.setImageResource(R.drawable.buttom_iv_me_press);
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    transaction.add(R.id.main_content, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                currentFragment = 4;
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tab_kebiao:
                if (currentFragment != 1)
                    showFragment(1);
                break;
            case R.id.id_tab_school:
                if (currentFragment != 2)
                    showFragment(2);
                break;
            case R.id.id_tab_find:
                if (currentFragment != 3)
                    showFragment(3);
                break;
            case R.id.id_tab_me:
                if (currentFragment != 4)
                    showFragment(4);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                kebiaoFragment.initKebiao();
                showFragment(1);
                APPAplication.sendRefreshKebiao(getApplicationContext());
                break;
            case 2:         //课表
                login(data);
                break;
            case 3:         //论坛
                showFragment(2);
                if (meFragment != null && meFragment.tv_bbs != null) {
                    meFragment.tv_bbs.setText("论坛ID : " + APPAplication.save.getString("bbs_user",
                            "论坛未登录"));
                }
                if (bbsFragment.lRecyclerView != null)
                    bbsFragment.lRecyclerView.refresh();
                break;
            case 4:
                APPAplication.login_stat = 1;
                APPAplication.save.edit().putInt("week", APPAplication.week)
                        .putInt("login_stat", 1).apply();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VersionControl versionControl = new VersionControl();
                        versionControl.upload(
                                APPAplication.save.getString("name", "") + " "
                                        + APPAplication.save.getString("xh", ""), "");
                    }
                }).start();
                handler.sendEmptyMessage(8);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void login(Intent data) {
        if (waitingDialog == null)
            waitingDialog = new ProgressDialog(MainActivity.this);
        waitingDialog.setMessage("正在登陆...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
        final Bundle bundle = data.getBundleExtra("data");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(bundle.getString("user")) || TextUtils.isEmpty(bundle.getString("pwd"))) {
                    handler.sendEmptyMessage(9);
                } else {
                    getKebiaoSource getKebiaoSource = new getKebiaoSource(bundle.getString("user"), bundle.getString("pwd"), MainActivity.this);
                    switch (getKebiaoSource.checkUser(getApplicationContext())) {
                        case 0:
                            try {
                                APPAplication.term = bundle.getString("term");
                                handler.sendEmptyMessage(7);
                                switch (getKebiaoSource.getKebiao(bundle.getString("term"))) {
                                    case -1:
                                        waitingDialog.cancel();
                                        handler.sendEmptyMessage(10);
                                        break;
                                    case -2:
                                        waitingDialog.cancel();
                                        handler.sendEmptyMessage(12);
                                        break;
                                    default:
                                        APPAplication.week = getKebiaoSource.getWeek(getApplication());
                                        APPAplication.login_stat = 1;
                                        APPAplication.save.edit().putInt("week", APPAplication.week).putInt("login_stat", 1).apply();
                                        VersionControl versionControl = new VersionControl();
                                        versionControl.upload(APPAplication.save.getString("name", "") + " " + APPAplication.save.getString("xh", ""), "");
                                        waitingDialog.cancel();
                                        handler.sendEmptyMessage(8);
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case -1:
                            handler.sendEmptyMessage(5);
                            break;
                        case -2:
                            waitingDialog.cancel();
                            handler.sendEmptyMessage(6);
                            break;
                    }
                }
            }
        }).start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出科大圈", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLogin(Intent data) {
        login(data);
    }

    @Override
    public void onKebiaoRefresh(Intent data) {
        login(data);
    }
}