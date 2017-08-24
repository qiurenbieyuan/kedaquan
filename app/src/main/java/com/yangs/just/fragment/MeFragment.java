package com.yangs.just.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;
import com.yangs.just.R;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.activity.BBSLoginActivity;
import com.yangs.just.activity.Browser;
import com.yangs.just.activity.TitleBuilder;
import com.yangs.just.activity.meAbout;
import com.yangs.just.bbs.BBSSource;
import com.yangs.just.utils.Advice;
import com.yangs.just.utils.AsyncTaskUtil;
import com.yangs.just.utils.FrescoImageLoader;
import com.yangs.just.utils.VersionControl;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

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
    private LinearLayout me_layout_login;
    private LinearLayout me_layout_bbs;
    private LinearLayout me_layout_myinfo;
    private LinearLayout me_layout_advice;
    private LinearLayout me_layout_update;
    private LinearLayout me_layout_about;
    private AsyncTaskUtil mDownloadAsyncTask;
    private OnLoginListener onLoginListener;
    private Handler handler;
    private AlertDialog advice_dialog;
    private Toolbar toolbar;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayMe = inflater.inflate(R.layout.me_layout, container, false);
        setHandler();
        activity = getActivity();
        toolbar = (Toolbar) mLayMe.findViewById(R.id.me_toolbar);
        tv_login = (TextView) mLayMe.findViewById(R.id.me_tv_login);
        tv_bbs = (TextView) mLayMe.findViewById(R.id.me_tv_bbs);
        me_layout_login = (LinearLayout) mLayMe.findViewById(R.id.me_layout_login);
        me_layout_bbs = (LinearLayout) mLayMe.findViewById(R.id.me_layout_bbs);
        me_layout_myinfo = (LinearLayout) mLayMe.findViewById(R.id.me_layout_myinfo);
        me_layout_advice = (LinearLayout) mLayMe.findViewById(R.id.me_layout_advice);
        me_layout_update = (LinearLayout) mLayMe.findViewById(R.id.me_layout_update);
        me_layout_about = (LinearLayout) mLayMe.findViewById(R.id.me_layout_about);
        me_layout_login.setOnClickListener(this);
        me_layout_bbs.setOnClickListener(this);
        me_layout_myinfo.setOnClickListener(this);
        me_layout_advice.setOnClickListener(this);
        me_layout_update.setOnClickListener(this);
        me_layout_about.setOnClickListener(this);
        activity = getActivity();
        onLoginListener = (OnLoginListener) activity;
        tv_login.setText(APPAplication.name);
        if (APPAplication.save.getString("bbs_cookie", "").equals(""))
            tv_bbs.setText("论坛ID : 未登录");
        else
            tv_bbs.setText("论坛ID : " + APPAplication.save.getString("bbs_user", "未登录"));
        save = APPAplication.save;
        return mLayMe;
    }

    public interface OnLoginListener {
        public void onLogin(Intent data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_layout_login:
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.onewanqian.cn/-2/");
                Intent intent = new Intent(activity, Browser.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
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
            case R.id.me_layout_myinfo:
                if (TextUtils.isEmpty(APPAplication.save.getString("xh", ""))) {
                    Toast.makeText(activity, "请先登录!", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(activity).setTitle("个人资料")
                            .setMessage("姓名 : " + save.getString("name", "未登陆") + "\n\n" + "学号 : "
                                    + save.getString("xh", "0") + "\n\n" + "当前学期 : "
                                    + save.getString("term", "0") + "\n\n" + "版本号 : "
                                    + APPAplication.version + "\n")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                break;
            case R.id.me_layout_advice:
                View view = getActivity().getLayoutInflater().inflate(R.layout.me_advice_dialog, null);
                final EditText advice_et_ad = (EditText) view.findViewById(R.id.me_advice_dialog_et_ad);
                final EditText advice_et_qq = (EditText) view.findViewById(R.id.me_advice_dialog_et_qq);
                Button advice_bt_jq = (Button) view.findViewById(R.id.me_advice_dialog_bt_jq);
                Button advice_bt_sub = (Button) view.findViewById(R.id.me_advice_dialog_bt_sub);
                Button advice_bt_cancel = (Button) view.findViewById(R.id.me_advice_dialog_bt_cancel);
                advice_bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        advice_dialog.dismiss();
                    }
                });
                advice_bt_jq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        advice_dialog.dismiss();
                        String url = "mqqwpa://im/chat?chat_type=group&uin=213890400&version=1";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                advice_bt_sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String et_ad = advice_et_ad.getText().toString().trim();
                        final String et_qq = advice_et_qq.getText().toString().trim();
                        if (et_ad.equals("")) {
                            advice_et_ad.setError("请输入建议");
                            return;
                        } else {
                            advice_et_ad.setError(null);
                        }
                        if (et_qq.equals("")) {
                            advice_et_qq.setError("请输入联系方式");
                            return;
                        } else {
                            advice_et_qq.setError(null);
                        }
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("正在火速提交...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        if (!progressDialog.isShowing())
                            progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Advice advice = new Advice();
                                if (advice.post(et_ad, et_qq))
                                    handler.sendEmptyMessage(1);
                                else
                                    handler.sendEmptyMessage(2);
                            }
                        }).start();
                    }
                });
                advice_dialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(false)
                        .setTitle("反馈").create();
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

    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
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
            }
        };
    }
}
