package com.yangs.kedaquan.score;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yangs.kedaquan.R;
import com.yangs.kedaquan.activity.APPAplication;
import com.yangs.kedaquan.activity.TitleBuilder;
import com.yangs.kedaquan.utils.getKebiaoSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/8/2.
 */

public class ScoreActivity extends Activity implements OnItemClickListener, OnRefreshListener {
    private ScoreAdapter scoreAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private LRecyclerView lRecyclerView;
    private List<Score> list;
    private getKebiaoSource source;
    private ProgressDialog progressDialog;
    private AlertDialog login_dialog;
    private TitleBuilder titleBuilder;
    private String year;
    private View header_view;
    private TextView header_view_bt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
        titleBuilder = new TitleBuilder(this);
        titleBuilder.setLeftImage(R.drawable.ic_arraw_back_white).setTitleText("成绩绩点", Boolean.FALSE)
                .setTvTitleNoClick().setIvRightNoClick();
        titleBuilder.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lRecyclerView = findViewById(R.id.score_lr);
        list = new ArrayList<>();
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scoreAdapter = new ScoreAdapter(list, getLayoutInflater());
        lRecyclerViewAdapter = new LRecyclerViewAdapter(scoreAdapter);
        lRecyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.gallery_dark_gray, android.R.color.white);
        lRecyclerView.setFooterViewHint("拼命加载中", "", "");
        lRecyclerViewAdapter.setOnItemClickListener(this);
        lRecyclerView.setHasFixedSize(true);
        lRecyclerView.setOnRefreshListener(this);
        final String[] datalist = new String[5];
        datalist[0] = "2015-2016-1";
        datalist[1] = "2015-2016-2";
        datalist[2] = "2016-2017-1";
        datalist[3] = "2016-2017-2";
        datalist[4] = "2017-2018-1";
        header_view = LayoutInflater.from(ScoreActivity.this)
                .inflate(R.layout.score_header_view, (ViewGroup) findViewById(android.R.id.content), false);
        header_view_bt = header_view.findViewById(R.id.score_header_bt);
        header_view_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ScoreActivity.this).setTitle("选择学期")
                        .setSingleChoiceItems(datalist, 4, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                year = datalist[which];
                                lRecyclerView.refresh();
                            }
                        }).show();
            }
        });
        new AlertDialog.Builder(this).setTitle("选择学期").setCancelable(false)
                .setSingleChoiceItems(datalist, 4, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        year = datalist[which];
                        lRecyclerView.refresh();
                    }
                }).show();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    lRecyclerViewAdapter.addHeaderView(header_view);
                    header_view_bt.setText(year);
                    scoreAdapter.clear();
                    scoreAdapter.addAll(list);
                    lRecyclerView.refreshComplete(10);
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    lRecyclerView.refreshComplete(10);
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    APPAplication.showDialog(ScoreActivity.this, "暂时还没有成绩");
                    break;
                case 3:
                    lRecyclerView.refreshComplete(10);
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    APPAplication.showDialog(ScoreActivity.this, "用户名或密码错误");
                    break;
                case 4:
                    lRecyclerView.refreshComplete(10);
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    APPAplication.showDialog(ScoreActivity.this, "网络出错");
                    break;
            }
            return true;
        }
    });

    @Override
    public void onItemClick(View view, int position) {
        Score score = list.get(position);
        APPAplication.showDialog(ScoreActivity.this,
                "课程号: " + score.getCno() + "\n"
                        + "课程名: " + score.getName() + "\n"
                        + "成绩: " + score.getScore() + "\n"
                        + "学分: " + score.getXf() + "\n"
                        + "课时: " + score.getKs() + "\n"
                        + "考核方式: " + score.getKhfx() + "\n"
                        + "课程属性: " + score.getKcsx() + "\n"
                        + "课程性质: " + score.getKcxz() + "\n");
    }

    @Override
    public void onRefresh() {
        lRecyclerViewAdapter.removeHeaderView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                source = new getKebiaoSource(APPAplication.save.getString("xh", "")
                        , APPAplication.save.getString("pwd", ""), ScoreActivity.this);
                switch (source.checkUser(ScoreActivity.this)) {
                    case 0:
                        list = source.getScore(year);
                        if (list.size() > 0)
                            handler.sendEmptyMessage(1);
                        else
                            handler.sendEmptyMessage(2);
                        break;
                    case -1:
                        handler.sendEmptyMessage(3);
                        break;
                    case -2:
                        handler.sendEmptyMessage(4);
                        break;
                }
            }
        }).start();
    }
}
