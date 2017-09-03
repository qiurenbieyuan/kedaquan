package com.yangs.just.bbs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yangs.just.R;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.activity.Browser;
import com.yangs.just.activity.TitleBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/8/3.
 */

public class BBSDetailActivity extends AppCompatActivity implements OnItemClickListener
        , OnRefreshListener, TextWatcher, View.OnClickListener, OnLoadMoreListener,
        Toolbar.OnMenuItemClickListener, OnItemLongClickListener {
    private Toolbar toolbar;
    private LRecyclerView lRecyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private BBSDetailAdapter bbsDetailAdapter;
    private String url;
    private String name;
    private Boolean isReply;
    private List<BBSDetail> list;
    private Handler handler;
    private LinearLayout lay_bottom;
    private EditText bbsdetail_et_text;
    private ImageView bbsdetail_bt_sub;
    private View header_view;
    private int reply_code;
    private int replyOne_code;
    private int pageTotal;
    private int pageCurrent = 1;
    private TextView header_title;
    private SimpleDraweeView header_iv;
    private TextView header_tv_user;
    private TextView header_tv_time;
    private TextView header_tv_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbsdetail_layout);
        setHandler();
        toolbar = (Toolbar) findViewById(R.id.bbsdetail_toolbar);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        name = bundle.getString("name");
        isReply = bundle.getBoolean("isReply");
        list = new ArrayList<>();
        toolbar.setTitle("帖子正文");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        lay_bottom = (LinearLayout) findViewById(R.id.bbsdetail_layout_bottom);
        bbsdetail_et_text = (EditText) lay_bottom.findViewById(R.id.bbsdetail_et_text);
        bbsdetail_bt_sub = (ImageView) lay_bottom.findViewById(R.id.bbsdetail_bt_sub);
        bbsdetail_et_text.addTextChangedListener(this);
        bbsdetail_bt_sub.setOnClickListener(this);
        lRecyclerView = (LRecyclerView) findViewById(R.id.bbsdetail_layout_lr);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bbsDetailAdapter = new BBSDetailAdapter(list, this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(bbsDetailAdapter);
        header_view = LayoutInflater.from(this).inflate(R.layout.bbsdetail_header_view,
                (ViewGroup) findViewById(android.R.id.content), false);
        header_title = (TextView) header_view.findViewById(R.id.bbsdetail_header_tv_title);
        header_iv = (SimpleDraweeView) header_view.findViewById(R.id.bbsdetail_header_iv);
        header_tv_user = (TextView) header_view.findViewById(R.id.bbsdetail_header_tv_user);
        header_tv_time = (TextView) header_view.findViewById(R.id.bbsdetail_header_tv_time);
        header_tv_content = (TextView) header_view.findViewById(R.id.bbsdetail_header_tv_content);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewHint("拼命加载中", "只有这么多帖子啦", "网络不给力啊，点击再试一次吧");
        lRecyclerView.setLoadMoreEnabled(true);
        lRecyclerView.setOnLoadMoreListener(this);
        lRecyclerViewAdapter.setOnItemClickListener(this);
        lRecyclerViewAdapter.setOnItemLongClickListener(this);
        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.setOnRefreshListener(this);
        lRecyclerView.refresh();
    }

    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        bbsDetailAdapter.clear();
                        if (pageCurrent == 1) {
                            lRecyclerViewAdapter.removeHeaderView();
                            lRecyclerViewAdapter.addHeaderView(header_view);
                            header_iv.setImageURI(list.get(0).getAvatar());
                            header_iv.setOnClickListener(BBSDetailActivity.this);
                            header_tv_user.setText(list.get(0).getUser());
                            header_tv_user.setOnClickListener(BBSDetailActivity.this);
                            header_title.setText(name);
                            header_tv_time.setText("发表于: " + list.get(0).getTime());
                            header_tv_content.setText(Html.fromHtml(list.get(0).getContent()));
                            header_tv_content.setOnClickListener(BBSDetailActivity.this);
                            bbsDetailAdapter.addAll(list);
                            bbsDetailAdapter.getList().remove(0);
                        } else {
                            bbsDetailAdapter.addAll(list);
                        }
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        APPAplication.showToast("加载内容失败!", 0);
                        break;
                    case 3:
                        switch (reply_code) {
                            case 0:
                                APPAplication.showToast("回复成功", 0);
                                bbsdetail_et_text.setText(null);
                                InputMethodManager inputMethodManager = (InputMethodManager)
                                        getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(
                                        BBSDetailActivity.this.getCurrentFocus().getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                                lRecyclerView.refresh();
                                break;
                            case -1:
                                APPAplication.showDialog2(BBSDetailActivity.this,
                                        "回复地址不可用！", "回复失败");
                                break;
                            case -2:
                                APPAplication.showDialog2(BBSDetailActivity.this,
                                        "error: 您的请求来路不正确或表单验证串不符", "回复失败");
                                break;
                            case -3:
                                APPAplication.showDialog2(BBSDetailActivity.this,
                                        "网络出错或论坛服务器崩溃了", "回复失败");
                                break;
                            case -4:
                                APPAplication.showDialog2(BBSDetailActivity.this,
                                        "抱歉，您的帖子小于 5 个字符的限制", "回复失败");
                                break;
                            case -5:
                                APPAplication.showDialog2(BBSDetailActivity.this,
                                        "抱歉，您两次发表间隔少于 15 秒，请稍候再发表", "回复失败");
                                break;
                            case -6:
                                APPAplication.showDialog2(BBSDetailActivity.this,
                                        "抱歉，您目前处于见习期间，需要等待 2 分钟后才能进行本操作", "回复失败");
                                break;
                        }
                        break;
                    case 4:
                        lRecyclerView.setNoMore(true);
                        break;
                    case 5:
                        bbsDetailAdapter.addAll(list);
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 6:
                        switch (replyOne_code) {
                            case 1:
                                APPAplication.showToast("回复成功", 0);
                                lRecyclerView.refresh();
                                break;
                            case -1:
                                APPAplication.showToast("回复失败", 0);
                                break;
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        lRecyclerView.refreshComplete(10);
        lRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, final int position) {
        View view_reply = getLayoutInflater().inflate(R.layout.bbsdetail_reply_layout, null);
        final EditText et_reply = (EditText) view_reply.findViewById(R.id.bbsdetail_reply_et);
        new AlertDialog.Builder(BBSDetailActivity.this).setTitle("回复 " + bbsDetailAdapter.getList()
                .get(position).getUser()
                + " ( " + bbsDetailAdapter.getList().get(position).getIndex() + " )")
                .setView(view_reply)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String txt = et_reply.getText().toString().trim();
                        if (txt.equals("")) {
                            APPAplication.showToast("请输入回复内容", 0);
                        } else {
                            dialog.dismiss();
                            APPAplication.bbsSource.setContext(BBSDetailActivity.this);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    replyOne_code = APPAplication.bbsSource.replayOne(
                                            bbsDetailAdapter.getList().get(position).getReplay_me_url(), txt);
                                    handler.sendEmptyMessage(6);
                                }
                            }).start();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = APPAplication.bbsSource.getDetailList(url + "&page=" + pageCurrent);
                pageTotal = APPAplication.bbsSource.getPage();
                pageCurrent = APPAplication.bbsSource.getCurrentPage();
                try {
                    if (list.size() > 0)
                        handler.sendEmptyMessage(1);
                    else
                        handler.sendEmptyMessage(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onLoadMore() {
        if (pageCurrent >= pageTotal) {
            handler.sendEmptyMessage(4);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pageCurrent++;
                    list = APPAplication.bbsSource.getDetailList(url + "&page="
                            + pageCurrent);
                    try {
                        if (list.size() > 0) {
                            handler.sendEmptyMessage(5);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            bbsdetail_bt_sub.setImageResource(R.drawable.ic_send_black_24dp);
            bbsdetail_bt_sub.setClickable(false);
        } else {
            bbsdetail_bt_sub.setImageResource(R.drawable.ic_send_black_24dp_press);
            bbsdetail_bt_sub.setClickable(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bbsdetail_bt_sub:
                if (!APPAplication.bbs_login_status) {
                    APPAplication.showToast("登录后才能回复哦", 0);
                    return;
                }
                final String msg = bbsdetail_et_text.getText().toString().trim();
                if (msg.equals(""))
                    return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        reply_code = APPAplication.bbsSource.replayArticle(msg);
                        handler.sendEmptyMessage(3);
                    }
                }).start();
                break;
            case R.id.bbsdetail_header_tv_user:
            case R.id.bbsdetail_header_iv:
                Intent intent = new Intent(BBSDetailActivity.this, Browser.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.myangs.com:81/" + list.get(0).getUser_url());
                bundle.putString("cookie", APPAplication.bbsSource.cookie);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.bbsdetail_header_tv_content:
                APPAplication.showDialog2(BBSDetailActivity.this, list.get(0)
                                .getContent()
                        , "内容原文(html)");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bbsdetail_menu_refresh:
                bbsDetailAdapter.clear();
                bbsDetailAdapter.notifyDataSetChanged();
                lRecyclerView.refresh();
                break;
            case R.id.bbsdetail_menu_goto:
                View view = getLayoutInflater().inflate(R.layout.bbsdetail_goto_layout, null);
                final EditText et = (EditText) view.findViewById(R.id.bbsdetail_goto_et);
                TextView tv = (TextView) view.findViewById(R.id.bbsdetail_goto_tv);
                et.setText(pageCurrent + "");
                tv.setText("/ " + pageTotal + " 页");
                new AlertDialog.Builder(BBSDetailActivity.this).setTitle("跳转到").setCancelable(false)
                        .setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 0;
                        try {
                            i = Integer.parseInt(et.getText().toString().trim());
                        } catch (Exception e) {
                        }
                        if (i > 0 && i <= pageTotal) {
                            if (url.contains("&page=")) {
                                url = url.replaceAll("&page=\\d+", "").replaceAll("#pid\\d+", "");
                            }
                            dialog.dismiss();
                            pageCurrent = i;
                            bbsDetailAdapter.clear();
                            lRecyclerViewAdapter.notifyDataSetChanged();
                            lRecyclerView.refresh();
                        } else {
                            APPAplication.showToast("页码输入不正确", 0);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            case R.id.bbsdetail_menu_only:
                if (url.contains("&authorid=1"))
                    url = url.replace("&authorid=1", "");
                else
                    url += "&authorid=1";
                lRecyclerView.refresh();
                break;
            case R.id.bbsdetail_menu_show:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url + "&page=" + pageCurrent);
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onItemLongClick(View view, int position) {
        APPAplication.showDialog2(BBSDetailActivity.this, bbsDetailAdapter.getList().get(position)
                        .getContent()
                , "内容原文(html)");
    }
}
