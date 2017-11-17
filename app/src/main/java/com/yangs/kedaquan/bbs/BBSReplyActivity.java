package com.yangs.kedaquan.bbs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yangs.kedaquan.R;
import com.yangs.kedaquan.activity.APPAplication;
import com.yangs.kedaquan.activity.Browser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/8/29 0029.
 */

public class BBSReplyActivity extends AppCompatActivity implements OnItemClickListener,
        OnRefreshListener, Toolbar.OnMenuItemClickListener {
    private Toolbar toolbar;
    private LRecyclerView lRecyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private BBSReplyAdapter bbsReplyAdapter;
    private List<BBSReply> list;
    private Handler handler;
    private ProgressDialog progressDialog;
    private String url2;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbsreplyactivity_layout);
        setHandler();
        list = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.bbsreply_toolbar);
        toolbar.setTitle("消息中心");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lRecyclerView = (LRecyclerView) findViewById(R.id.bbsreply_layout_lr);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bbsReplyAdapter = new BBSReplyAdapter(list, this);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(bbsReplyAdapter);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewHint("拼命加载中", "只有这么多帖子啦", "网络不给力啊，点击再试一次吧");
        lRecyclerView.setLoadMoreEnabled(true);
        lRecyclerViewAdapter.setOnItemClickListener(this);
        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.setOnRefreshListener(this);
        lRecyclerView.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bbsreply_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        bbsReplyAdapter.clear();
                        bbsReplyAdapter.addAll(list);
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        lRecyclerView.refreshComplete(10);
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        APPAplication.showToast("暂时没有内容", 0);
                        break;
                    case 3:
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        break;
                    case 4:
                        Bundle bundle = new Bundle();
                        bundle.putString("url", url2);
                        bundle.putString("name", title);
                        bundle.putBoolean("isReply", true);
                        Intent intent = new Intent(BBSReplyActivity.this, BBSDetailActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onItemClick(View view, final int position) {
        final String url = list.get(position).getUrl();
        title = list.get(position).getTitle();
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("跳转中..");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                url2 = APPAplication.bbsSource.getRedirectUrl(url);
                handler.sendEmptyMessage(3);
                if (url2 != null) {
                    url2 = "http://www.myangs.com:81/" + url2;
                    handler.sendEmptyMessage(4);
                } else {
                    APPAplication.showToast("error to redirect!", 0);
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = APPAplication.bbsSource.getReplyList(
                        "http://www.myangs.com:81/home.php?mod=space&do=notice&view=mypost&type=post");
                if (list.size() > 0)
                    handler.sendEmptyMessage(1);
                else
                    handler.sendEmptyMessage(2);
            }
        }).start();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.bbsreply_menu_me:
                intent = new Intent(this, Browser.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://www.myangs.com:81/" + APPAplication.bbsSource.user_url);
                bundle.putString("cookie", APPAplication.bbsSource.cookie);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.bbsreply_menu_show:
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.myangs.com:81/home.php?mod=space&do=notice&view=mypost&type=post");
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
        return true;
    }
}
