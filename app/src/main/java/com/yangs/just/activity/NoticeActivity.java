package com.yangs.just.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yangs.just.R;
import com.yangs.just.utils.NoticeSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/9/2 0002.
 */

public class NoticeActivity extends AppCompatActivity implements OnItemClickListener, OnRefreshListener {
    private Toolbar toolbar;
    private LRecyclerView lRecyclerView;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<Notice> list;
    private NoticeAdapter noticeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticeactivity_layout);
        toolbar = (Toolbar) findViewById(R.id.notice_toolbar);
        lRecyclerView = (LRecyclerView) findViewById(R.id.notice_layout_lr);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar.setTitle("通知中心");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(list);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(noticeAdapter);
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
    public void onItemClick(View view, final int position) {
        if (list.get(position).getUrl().equals("")) {
            APPAplication.showDialog2(this, list.get(position).getContent(), list.get(position).getTitle());
        } else {
            new AlertDialog.Builder(this).setTitle(list.get(position).getTitle()).setCancelable(true)
                    .setMessage(list.get(position).getContent()).setPositiveButton("打开地址", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(NoticeActivity.this, Browser.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", list.get(position).getUrl());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }).create().show();
        }
    }

    @Override
    public void onRefresh() {
        noticeAdapter.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NoticeSource noticeSource = new NoticeSource();
                JSONObject jsonObject = noticeSource.getNotice();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("notice");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Notice notice = new Notice();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        notice.setTitle(jsonObject1.getString("title"));
                        notice.setContent(jsonObject1.getString("content"));
                        notice.setTime(jsonObject1.getString("time"));
                        notice.setUrl(jsonObject1.getString("url"));
                        notice.setLevel(Integer.parseInt(jsonObject1.getString("level")));
                        list.add(notice);
                    }
                    APPAplication.sendHandler(new Runnable() {
                        @Override
                        public void run() {
                            lRecyclerView.refreshComplete(10);
                            lRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    APPAplication.showToast(e.toString(), 1);
                }
            }
        }).start();
    }
}
