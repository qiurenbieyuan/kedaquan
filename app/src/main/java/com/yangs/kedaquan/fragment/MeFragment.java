package com.yangs.kedaquan.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangs.kedaquan.R;
import com.yangs.kedaquan.activity.APPAplication;
import com.yangs.kedaquan.activity.BBSLoginActivity;
import com.yangs.kedaquan.activity.KebiaoGetActivity;
import com.yangs.kedaquan.activity.NoticeActivity;
import com.yangs.kedaquan.activity.VpnLoginActivity;
import com.yangs.kedaquan.activity.meAbout;
import com.yangs.kedaquan.bbs.BBSSource;
import com.yangs.kedaquan.utils.AsyncTaskUtil;
import com.yangs.kedaquan.utils.VPNUtils;
import com.yangs.kedaquan.utils.VersionControl;

import org.json.JSONObject;

/**
 * Created by yangs on 2017/5/6.
 */

public class MeFragment extends Fragment implements View.OnClickListener {
    private View mLayMe;
    private Activity activity;
    private SharedPreferences save;
    private ProgressDialog progressDialog;
    private String tmp_version;
    public TextView tv_login;
    public TextView tv_bbs;
    public TextView tv_vpn;
    private LinearLayout me_layout_login;
    private LinearLayout me_layout_bbs;
    private LinearLayout me_layout_info;
    private LinearLayout me_layout_advice;
    private LinearLayout me_layout_update;
    private LinearLayout me_layout_about;
    private LinearLayout me_layout_vpn;
    private AsyncTaskUtil mDownloadAsyncTask;
    private OnLoginListener onLoginListener;
    private AlertDialog advice_dialog;
    private Toolbar toolbar;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayMe = inflater.inflate(R.layout.me_layout, container, false);
        activity = getActivity();
        toolbar = mLayMe.findViewById(R.id.me_toolbar);
        tv_login = mLayMe.findViewById(R.id.me_tv_login);
        tv_bbs = mLayMe.findViewById(R.id.me_tv_bbs);
        tv_vpn = mLayMe.findViewById(R.id.me_tv_vpn);
        me_layout_login = mLayMe.findViewById(R.id.me_layout_login);
        me_layout_bbs = mLayMe.findViewById(R.id.me_layout_bbs);
        me_layout_info = mLayMe.findViewById(R.id.me_layout_info);
        me_layout_advice = mLayMe.findViewById(R.id.me_layout_advice);
        me_layout_update = mLayMe.findViewById(R.id.me_layout_update);
        me_layout_about = mLayMe.findViewById(R.id.me_layout_about);
        me_layout_vpn = mLayMe.findViewById(R.id.me_layout_vpn);
        me_layout_login.setOnClickListener(this);
        me_layout_bbs.setOnClickListener(this);
        me_layout_info.setOnClickListener(this);
        me_layout_advice.setOnClickListener(this);
        me_layout_update.setOnClickListener(this);
        me_layout_about.setOnClickListener(this);
        me_layout_vpn.setOnClickListener(this);
        activity = getActivity();
        onLoginListener = (OnLoginListener) activity;
        if (APPAplication.save.getString("bbs_cookie", "").equals(""))
            tv_bbs.setText("论坛ID : 未登录");
        else
            tv_bbs.setText("论坛ID : " + APPAplication.save.getString("bbs_user", "未登录"));
        if (APPAplication.save.getString("vpn_cookie", "").equals(""))
            save = APPAplication.save;
        return mLayMe;
    }

    public interface OnLoginListener {
        public void onLogin(Intent data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_layout_vpn:
                startActivity(new Intent(getContext(), VpnLoginActivity.class));
                break;
            case R.id.me_layout_login:
                if (APPAplication.save.getString("vpn_cookie", "").equals("")) {
                    new AlertDialog.Builder(getContext()).setCancelable(false)
                            .setMessage("请先绑定VPN").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(getContext(), VpnLoginActivity.class));
                        }
                    }).create().show();
                } else {
                    VPNUtils.checkVpnIsValid(getContext(), new VPNUtils.onReultListener() {
                        @Override
                        public void onResult(int code) {
                            switch (code) {
                                case 0:
                                    startActivityForResult(new Intent(getContext(), KebiaoGetActivity.class)
                                            , 1);
                                    break;
                                case -1:
                                    APPAplication.showDialog(getContext(),
                                            "VPN密码错误,请重新绑定!");
                                    break;
                                case -2:
                                    new android.app.AlertDialog.Builder(getContext())
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
                                                    Uri content_url = Uri.parse(APPAplication.vpnSource.getLocation());
                                                    intent.setData(content_url);
                                                    startActivity(intent);
                                                }
                                            }).create().show();
                                    break;
                                case -3:
                                    APPAplication.showDialog(getContext(), "网络错误");
                                    break;
                            }
                        }
                    });
                }
                break;
            case R.id.me_layout_bbs:
                if (APPAplication.save.getString("bbs_cookie", "").equals("")) {
                    startActivityForResult(new Intent(getActivity(), BBSLoginActivity.class), 3);
                } else {
                    new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("当前已登录到" +
                            "论坛,是否退出?")
                            .setCancelable(false).setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    APPAplication.bbsSource.userExit();
                                    handler.sendEmptyMessage(3);
                                }
                            }).start();
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
                break;
            case R.id.me_layout_info:
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
            case R.id.me_layout_advice:
                PackageManager packageManager = getActivity().getPackageManager();
                try {
                    packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                } catch (PackageManager.NameNotFoundException e) {
                    APPAplication.showToast("安装手机QQ后才能反馈哦", 0);
                    return;
                }
                View view = getActivity().getLayoutInflater().inflate(R.layout.me_advice_dialog, null);
                final Button advice_bt_1 = view.findViewById(R.id.me_advice_dialog_bt_1);
                final Button advice_et_2 = view.findViewById(R.id.me_advice_dialog_bt_2);
                advice_bt_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        advice_dialog.dismiss();
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=1125280130";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                advice_et_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        advice_dialog.dismiss();
                        String url = "mqqwpa://im/chat?chat_type=group&uin=213890400&version=1";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                advice_dialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(true)
                        .setTitle("快速反馈").create();
                advice_dialog.show();
                break;
            case R.id.me_layout_update:
                if (progressDialog == null)
                    progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("检查更新中...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                if (!progressDialog.isShowing())
                    progressDialog.show();
                final Message msg = Message.obtain();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VersionControl versionControl = new VersionControl();
                        tmp_version = versionControl.check(APPAplication.save.getString("xh", "首次+手动"));
                        JSONObject a;
                        try {
                            a = new JSONObject(tmp_version);
                            if (tmp_version.equals("false")) {
                                APPAplication.sendHandler(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.cancel();
                                        APPAplication.showToast("网络出错!", 0);
                                    }
                                });
                            } else {
                                if (a.getString("version").equals(APPAplication.version)) {
                                    APPAplication.sendHandler(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.cancel();
                                            APPAplication.showToast("目前还没有新版哦!", 0);
                                        }
                                    });
                                } else {
                                    APPAplication.sendHandler(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (progressDialog != null)
                                                progressDialog.cancel();
                                            try {
                                                JSONObject version_tmp = new JSONObject(tmp_version);
                                                final String url = version_tmp.getString("url");
                                                final String app_name = version_tmp.getString("app_name");
                                                String info = "版本号 : " + version_tmp.getString("version") + "\n"
                                                        + "大小 : " + version_tmp.getString("size") + "\n"
                                                        + "详细 : \n" + version_tmp.getString("detail");
                                                new AlertDialog.Builder(activity).setTitle("发现新版本")
                                                        .setMessage(info)
                                                        .setNegativeButton("下载", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                APPAplication.showToast("正在下载中...", 0);
                                                                mDownloadAsyncTask = new AsyncTaskUtil(activity, new Handler());
                                                                mDownloadAsyncTask.execute(url, app_name);
                                                            }
                                                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        APPAplication.showToast("新版本有更好的体验哦，推荐下载!", 0);
                                                    }
                                                }).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.me_layout_about:
                startActivity(new Intent(activity, meAbout.class));
                break;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    APPAplication.showDialog(getContext(), "提交成功,感谢反馈!");
                    break;
                case 2:
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    APPAplication.showDialog(getContext(), "网络出了点问题,请稍后再提交!");
                    break;
                case 3:
                    APPAplication.showToast("退出成功", 0);
                    APPAplication.bbs_login_status = false;
                    APPAplication.bbs_login_status_check = false;
                    APPAplication.save.edit().putString("bbs_cookie", null).apply();
                    APPAplication.bbsSource = new BBSSource();
                    tv_bbs.setText("论坛ID : 未登录");
                    break;
            }
            return true;
        }
    });
}
