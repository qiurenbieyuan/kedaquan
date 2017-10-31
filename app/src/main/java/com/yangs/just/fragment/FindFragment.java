package com.yangs.just.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.yangs.just.R;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.activity.Browser;
import com.yangs.just.activity.TitleBuilder;
import com.yangs.just.book.Book_Find;
import com.yangs.just.coursepj.CoursePJ;
import com.yangs.just.find.FindMainAdapter;
import com.yangs.just.find.GlideImageLoader;
import com.yangs.just.score.ScoreActivity;
import com.yangs.just.score.VpnSource;
import com.yangs.just.utils.FindUrl;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangs on 2017/5/6.
 */

public class FindFragment extends LazyLoadFragment implements OnBannerListener, OnItemClickListener, OnRefreshListener, OnItemLongClickListener {
    private Activity activity;
    private View mLayFind;
    private Banner banner;
    private LRecyclerView lRecyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<String> imagesUrl;
    private ProgressDialog progressDialog;
    private Handler handler;
    private int index;
    private List<String> dataList;
    private Map<String, String> url_data;
    private View header_view;
    private Toolbar toolbar;

    @Override
    protected int setContentView() {
        return R.layout.find_layout;
    }

    @Override
    protected void lazyLoad() {
        if (isInit) {
            if (!isLoad) {
                initview();
                setHandler();
            }
        }

    }

    private void initview() {
        activity = getActivity();
        mLayFind = getContentView();
        toolbar = (Toolbar) mLayFind.findViewById(R.id.find_toolbar);
        url_data = new HashMap<>();
        url_data.put("成绩绩点", "http://www.onewanqian.cn/1/");
        url_data.put("校车时刻", "http://jwc.just.edu.cn/list/47.html");
        url_data.put("空教室", "http://www.onewanqian.cn/4/");
        url_data.put("体育成绩", "http://www.onewanqian.cn/6/");
        url_data.put("俱乐部", "http://www.onewanqian.cn/7/");
        url_data.put("早操出勤", "http://www.onewanqian.cn/8/");
        url_data.put("课程计划", "http://www.onewanqian.cn/3/");
        url_data.put("选课结果", "http://www.onewanqian.cn/10/");
        url_data.put("实验系统", "http://202.195.195.198/sy/index.aspx");
        url_data.put("强智教务", "http://jwgl.just.edu.cn:8080/");
        url_data.put("奥蓝系统", "http://202.195.195.238:866/mobile/login.aspx");
        url_data.put("信息门户", "http://my.just.edu.cn/index.portal");
        url_data.put("电话列表", "http://mp.weixin.qq.com/s?__biz=MzA5MDQ1MTU5OQ==&mid=203948380&idx=1&sn=38768267f5611e44b0ad2480559a463c#wechat_redirect");
        url_data.put("四六级", "http://www.yunchafen.com.cn/score/alipay/cet-login");
        url_data.put("实时公交", "http://211.138.195.226/ba_traffic_js/wapbus/zhenjiang/");
        dataList = new ArrayList<>();
        dataList.add("成绩绩点");
        dataList.add("校车时刻");
        dataList.add("空教室");
        dataList.add("体育成绩");
        dataList.add("俱乐部");
        dataList.add("早操出勤");
        dataList.add("课程计划");
        dataList.add("一键评教");
        dataList.add("图书借阅");
        dataList.add("选课结果");
        dataList.add("实验系统");
        dataList.add("强智教务");
        dataList.add("奥蓝系统");
        dataList.add("信息门户");
        dataList.add("电话列表");
        dataList.add("四六级");
        dataList.add("实时公交");
        FindMainAdapter adapter = new FindMainAdapter(dataList);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        header_view = activity.getLayoutInflater().inflate(R.layout.find_header_layout, null);
        banner = (Banner) header_view.findViewById(R.id.find_header_banner);
        GridItemDecoration divider = new GridItemDecoration.Builder(activity)
                .setHorizontal(R.dimen.default_divider_padding)
                .setVertical(R.dimen.default_divider_padding)
                .setColor(Color.rgb(152, 152, 152))
                .build();
        List<String> images = new ArrayList<String>();
        images.add("https://raw.githubusercontent.com/yangs2012/app/master/1.jpg");
        images.add("https://raw.githubusercontent.com/yangs2012/app/master/2.jpg");
        images.add("https://raw.githubusercontent.com/yangs2012/app/master/3.jpg");
        banner.setImages(images).setImageLoader(new GlideImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setOnBannerListener(this).setDelayTime(3000).start();
        lRecyclerViewAdapter.addHeaderView(header_view);
        lRecyclerViewAdapter.setOnItemLongClickListener(this);
        lRecyclerViewAdapter.setOnItemClickListener(this);
        lRecyclerView = (LRecyclerView) mLayFind.findViewById(R.id.aaa);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.addItemDecoration(divider);
        final GridLayoutManager layoutManager = new GridLayoutManager(activity, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0 || position == 1) ? layoutManager.getSpanCount() : 1;
            }
        });
        lRecyclerView.setLayoutManager(layoutManager);
        lRecyclerView.setPullRefreshEnabled(true);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.LineScalePulseOutRapid);
        lRecyclerView.setOnRefreshListener(this);
        lRecyclerView.setHeaderViewColor(R.color.colorGreen, R.color.gallery_black, android.R.color.white);
    }

    @Override
    public void OnBannerClick(int position) {
        index = position;
        if (!APPAplication.isFindUrl) {
            if (progressDialog == null)
                progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("获取链接...");
            progressDialog.setCancelable(false);
            if (!progressDialog.isShowing())
                progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FindUrl findUrl = new FindUrl();
                    List<String> list = findUrl.getUrl();
                    if (list.size() > 0) {
                        APPAplication.FindUrlList = list;
                        APPAplication.isFindUrl = true;
                        handler.sendEmptyMessage(1);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                }
            }).start();
        } else {
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(activity, ScoreActivity.class);  //成绩绩点
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(activity, CoursePJ.class);  //一键评教
                startActivity(intent);
                break;
            case 8:
                intent = new Intent(activity, Book_Find.class); //图书借阅
                startActivity(intent);
                break;
            case 10:
            case 11:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("进入方式");
                String[] type = {"正常进入", "内网进入(需要登录校内VPN)"};
                builder.setItems(type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            dialog.dismiss();
                            Bundle bundle = new Bundle();
                            if (position == 10)
                                bundle.putString("url", "http://202.195.195.198/sy/index.aspx");
                            else
                                bundle.putString("url", "http://jwgl.just.edu.cn:8080/");
                            Intent intent2 = new Intent(activity, Browser.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent2.putExtras(bundle);
                            startActivity(intent2);
                        } else if (which == 1) {
                            dialog.dismiss();
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.vpn_dialog, null);
                            final EditText vpn_et_user = (EditText) view.findViewById(R.id.vpn_dialog_et_user);
                            final EditText vpn_et_pwd = (EditText) view.findViewById(R.id.vpn_dialog_et_pwd);
                            final Button vpn_bt_login = (Button) view.findViewById(R.id.vpn_dialog_bt_login);
                            final ProgressBar vpn_pb = (ProgressBar) view.findViewById(R.id.vpn_dialog_pb);
                            final AlertDialog vpn_dialog = new AlertDialog.Builder(getContext()).setTitle("登录VPN系统")
                                    .setView(view).setCancelable(true).create();
                            vpn_dialog.show();
                            vpn_et_user.setText(APPAplication.save.getString("vpn_user", ""));
                            vpn_et_pwd.setText(APPAplication.save.getString("vpn_pwd", ""));
                            vpn_bt_login.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String user = vpn_et_user.getText().toString().trim();
                                    final String pwd = vpn_et_pwd.getText().toString().trim();
                                    if (user.equals("")) {
                                        vpn_et_user.setError("请输入用户名");
                                        return;
                                    } else {
                                        vpn_et_user.setError(null);
                                    }
                                    if (pwd.equals("")) {
                                        vpn_et_pwd.setError("请输入密码");
                                        return;
                                    } else {
                                        vpn_et_pwd.setError(null);
                                    }
                                    vpn_bt_login.setText("正在登录VPN系统...");
                                    vpn_pb.setVisibility(View.VISIBLE);
                                    final VpnSource vpnSource = new VpnSource(user, pwd);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final int code = vpnSource.checkVpnUser();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    vpn_pb.setVisibility(View.GONE);
                                                    vpn_bt_login.setText("登录VPN系统");
                                                    switch (code) {
                                                        case 0:
                                                            APPAplication.showToast("登录vpn系统成功!", 0);
                                                            APPAplication.save.edit().putString("vpn_user", "")
                                                                    .putString("vpn_pwd", "").apply();
                                                            Bundle bundle = new Bundle();
                                                            if (position == 10)
                                                                bundle.putString("url", "https://vpn.just.edu.cn/sy/,DanaInfo=202.195.195.198+");
                                                            else
                                                                bundle.putString("url", "https://vpn.just.edu.cn/,DanaInfo=jwgl.just.edu.cn,Port=8080+");
                                                            bundle.putString("cookie", APPAplication.save.getString("vpn_cookie", ""));
                                                            Intent intent2 = new Intent(activity, Browser.class);
                                                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent2.putExtras(bundle);
                                                            startActivity(intent2);
                                                            vpn_dialog.dismiss();
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
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            default:
                Bundle bundle = new Bundle();
                bundle.putString("url", url_data.get(dataList.get(position)));
                intent = new Intent(activity, Browser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        APPAplication.showDialog(getContext(), dataList.get(position));
    }

    @Override
    public void onRefresh() {
        lRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                lRecyclerViewAdapter.notifyDataSetChanged();
                lRecyclerView.refreshComplete(10);
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();
                List<String> images = new ArrayList<String>();
                images.add("https://raw.githubusercontent.com/yangs2012/app/master/1.jpg");
                images.add("https://raw.githubusercontent.com/yangs2012/app/master/2.jpg");
                images.add("https://raw.githubusercontent.com/yangs2012/app/master/3.jpg");
                banner.update(images);
            }
        }, 2500);
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
                        Intent intent = new Intent(activity, Browser.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", APPAplication.FindUrlList.get(index));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 2:
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        APPAplication.showDialog(getContext(), "从服务器获取链接失败!");
                        break;
                }
            }
        };
    }

}
