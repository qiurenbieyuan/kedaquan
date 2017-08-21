package com.yangs.just.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.melnykov.fab.FloatingActionButton;
import com.yangs.just.R;
import com.yangs.just.activity.APPAplication;
import com.yangs.just.activity.BBSLoginActivity;
import com.yangs.just.activity.Browser;
import com.yangs.just.bbs.BBS;
import com.yangs.just.bbs.BBSAdapter;
import com.yangs.just.bbs.BBSDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static com.yangs.just.activity.APPAplication.bbsSource;

/**
 * Created by yangs on 2017/8/6.
 */

public class BBSFragment extends LazyLoadFragment implements OnItemClickListener, OnItemLongClickListener, OnRefreshListener, View.OnClickListener,
        Toolbar.OnMenuItemClickListener, OnLoadMoreListener {
    private View mLay;
    private FloatingActionButton fb;
    public LRecyclerView lRecyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private BBSAdapter bbsAdapter;
    private String url;
    private List<BBS> list;
    private Handler handler;
    private AlertDialog post_dialog;
    private ProgressDialog progressDialog;
    private int login_status;
    private int post_status;
    private Toolbar toolbar;
    private int page;
    private int currentPage = 1;

    @Override
    protected int setContentView() {
        return R.layout.bbs_layout;
    }

    @Override
    protected void lazyLoad() {
        if (isInit) {
            if (!isLoad) {
                initView();
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.bbs_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initView() {
        setHandler();
        mLay = getContentView();
        setHasOptionsMenu(true);
        toolbar = (Toolbar) mLay.findViewById(R.id.bbs_toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        url = "http://www.myangs.com:81/forum.php?mod=forumdisplay&fid=46&mobile=1";
        list = new ArrayList<BBS>();
        lRecyclerView = (LRecyclerView) mLay.findViewById(R.id.articlefragment_lr);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bbsAdapter = new BBSAdapter(list, getActivity());
        lRecyclerViewAdapter = new LRecyclerViewAdapter(bbsAdapter);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewHint("拼命加载中", "只有这么多帖子啦", "网络不给力啊，点击再试一次吧");
        lRecyclerViewAdapter.setOnItemClickListener(this);
        lRecyclerViewAdapter.setOnItemLongClickListener(this);
        lRecyclerView.setLoadMoreEnabled(true);
        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.setOnRefreshListener(this);
        lRecyclerView.setOnLoadMoreListener(this);
        fb = (FloatingActionButton) mLay.findViewById(R.id.articlefragment_fab);
        fb.setOnClickListener(this);
        fb.attachToRecyclerView(lRecyclerView);
        lRecyclerView.refresh();
    }

    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        bbsAdapter.clear();
                        bbsAdapter.addAll(list);
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        APPAplication.showToast("加载内容出错: Network error", 0);
                        break;
                    case 3:
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.cancel();
                        switch (login_status) {
                            case 1:
                                APPAplication.showToast("欢迎您回来," +
                                        APPAplication.save.getString("bbs_user", "null"), 0);
                                break;
                            case -1:
                                APPAplication.showDialog(getActivity(), "用户名或密码错误");
                                break;
                            case -2:
                                APPAplication.showToast("网络出错..", 0);
                                break;
                        }
                        break;
                    case 4:
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        switch (post_status) {
                            case 0:
                                post_dialog.dismiss();
                                APPAplication.showToast("发表成功", 0);
                                lRecyclerView.refresh();
                                break;
                            case -2:
                                APPAplication.showDialog(getActivity(), "网络出错或者论坛服务器崩溃了!");
                                break;
                            case -3:
                                APPAplication.showDialog(getActivity(), "抱歉,未能取得发帖地址!");
                                break;
                        }
                        break;
                    case 5:
                        APPAplication.bbs_login_status_check = true;
                        APPAplication.showToast("欢迎您回来 "
                                + APPAplication.save.getString("bbs_user", "null"), 0);
                        break;
                    case 6:
                        lRecyclerView.setNoMore(true);
                        break;
                    case 7:
                        bbsAdapter.addAll(list);
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 8:
                        APPAplication.showToast("加载出错,请重试", 0);
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("url", bbsAdapter.getList().get(position).getUrl());
        bundle.putString("name", bbsAdapter.getList().get(position).getTitle());
        Intent intent = new Intent(getActivity(), BBSDetailActivity.class);
        intent.putExtras(bundle);
        bbsAdapter.getList().get(position).setColor(Color.GRAY);
        bbsAdapter.notifyDataSetChanged();
        //String a = bbsAdapter.getList().get(position).getUrl().split("tid=")[1].split("&")[0];
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        APPAplication.showDialog2(getActivity(), "标题 : " + bbsAdapter.getList().get(position).getTitle()
                + "\n用户 : " + bbsAdapter.getList().get(position).getUser()
                + "\n回复 : " + bbsAdapter.getList().get(position).getNum()
                + "\nURL : " + bbsAdapter.getList().get(position).getUrl(), "帖子信息");
    }

    @Override
    public void onRefresh() {
        APPAplication.bbsSource.setContext(getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = bbsSource.getList(url);
                page = bbsSource.getBigpage();
                try {
                    if (list.size() > 0)
                        handler.sendEmptyMessage(1);
                    else
                        handler.sendEmptyMessage(2);
                    if (APPAplication.bbs_login_status && !APPAplication.bbs_login_status_check)
                        handler.sendEmptyMessage(5);
                    if (!APPAplication.bbs_login_status &&          //打开时,尝试自动登录
                            !TextUtils.isEmpty(APPAplication.save.getString("bbs_cookie", ""))) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                login_status = APPAplication.bbsSource.login(
                                        APPAplication.save.getString("bbs_user", ""),
                                        APPAplication.save.getString("bbs_pwd", ""), true);
                                handler.sendEmptyMessage(3);
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.articlefragment_fab:
                if (!APPAplication.bbs_login_status) {
                    Snackbar.make(mLay, "登录后发帖才能发帖哦", Snackbar.LENGTH_LONG)
                            .setAction("去登录", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(getActivity(), BBSLoginActivity.class), 3);
                                }
                            }).show();
                    return;
                }
                View view2 = getActivity().getLayoutInflater().inflate(R.layout.bbs_post_dialog, null);
                final EditText et_post_title = (EditText) view2.findViewById(R.id.bbs_post_et_title);
                final EditText et_post_msg = (EditText) view2.findViewById(R.id.bbs_post_et_msg);
                Button bt_post_sub = (Button) view2.findViewById(R.id.bbs_post_bt_sub);
                Button bt_post_cancel = (Button) view2.findViewById(R.id.bbs_post_bt_cancel);
                bt_post_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_dialog.dismiss();
                    }
                });
                bt_post_sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progressDialog == null)
                            progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("发表中...");
                        progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                APPAplication.bbsSource.setContext(getActivity());
                                post_status = APPAplication.bbsSource.postArticle(et_post_title.getText().toString(),
                                        et_post_msg.getText().toString());
                                handler.sendEmptyMessage(4);
                            }
                        }).start();
                    }
                });
                post_dialog = new AlertDialog.Builder(getActivity()).setTitle("发帖")
                        .setView(view2).setCancelable(false)
                        .create();

                post_dialog.show();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bbs_menu_msg:
                if (APPAplication.bbs_login_status) {
                    Intent intent = new Intent(getActivity(), Browser.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", "http://www.myangs.com:81/" + APPAplication.bbsSource.user_url);
                    bundle.putString("cookie", APPAplication.bbsSource.cookie);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    APPAplication.showToast("请先登录", 0);
                    startActivityForResult(new Intent(getActivity(), BBSLoginActivity.class), 3);
                }
                break;
        }
        return true;
    }

    @Override
    public void onLoadMore() {
        if (currentPage >= page) {
            handler.sendEmptyMessage(6);
        } else {
            currentPage++;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        list = bbsSource.getList(url + "&page=" + currentPage);
                        if (list.size() > 0) {
                            handler.sendEmptyMessage(7);
                        } else {
                            handler.sendEmptyMessage(8);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}
