package com.yangs.kedaquan.score;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangs.kedaquan.R;
import com.yangs.kedaquan.activity.APPAplication;
import com.yangs.kedaquan.utils.VersionControl;
import com.yangs.kedaquan.utils.getKebiaoSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangs on 2017/8/2.
 */

public class ScoreActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ScoreAdapter.OnItemOnClickListener, Toolbar.OnMenuItemClickListener, ScoreAdapter.OnDeatilItemClickListener {
    private ScoreAdapter scoreAdapter;
    private SwipeRefreshLayout srl;
    private RecyclerView recyclerView;
    private List<Score> list;
    private getKebiaoSource source;
    private ProgressDialog progressDialog;
    private AlertDialog login_dialog;
    private Toolbar toolbar;
    private String year;
    private TextView header_view_jd;
    private TextView header_view_term;
    private TextView tv_empty;
    private LinearLayout header_view_ll;
    private String[] datalist;
    private float jd;
    private double new_jd;
    private AlertDialog term_dialog;
    private String xh;
    private String pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
        toolbar = findViewById(R.id.score_layout_toolbar);
        toolbar.setTitle("成绩绩点");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        srl = findViewById(R.id.score_layout_srl);
        recyclerView = findViewById(R.id.score_layout_rv);
        tv_empty = findViewById(R.id.score_layout_tv);
        list = new ArrayList<>();
        scoreAdapter = new ScoreAdapter(list);
        datalist = new String[7];
        datalist[0] = "2015-2016-1";
        datalist[1] = "2015-2016-2";
        datalist[2] = "2015-2016学年";
        datalist[3] = "2016-2017-1";
        datalist[4] = "2016-2017-2";
        datalist[5] = "2016-2017学年";
        datalist[6] = "2017-2018-1";
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(scoreAdapter);
        scoreAdapter.setOnItemOnClickListener(this);
        scoreAdapter.setOnDeatilItemClickListener(this);
        srl.setColorSchemeColors(Color.CYAN, Color.GREEN, ContextCompat.getColor(this,
                R.color.colorPrimary));
        srl.setOnRefreshListener(this);
        if (term_dialog == null) {
            term_dialog = new AlertDialog.Builder(this).setTitle("选择学期")
                    .setSingleChoiceItems(datalist, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            year = datalist[which];
                            srl.post(new Runnable() {
                                @Override
                                public void run() {
                                    srl.setRefreshing(true);
                                    onRefresh();
                                }
                            });
                        }
                    }).create();
        }
        xh = APPAplication.save.getString("xh", "");
        pwd = APPAplication.save.getString("pwd", "");
        term_dialog.show();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    srl.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_empty.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                    scoreAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    srl.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    tv_empty.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    srl.setRefreshing(false);
                    APPAplication.showDialog(ScoreActivity.this, "用户名或密码错误");
                    break;
                case 4:
                    srl.setRefreshing(false);
                    APPAplication.showDialog(ScoreActivity.this, "网络出错");
                    break;
            }
            return true;
        }
    });

    private void calculateGPA(Boolean isSelf) {
        if (list.size() == 0)
            return;
        float sum = 0f;
        float XFsum = 0f;
        for (int i = 0, j = list.size(); i < j; i++) {
            Score score = list.get(i);
            if (!score.getCheck())
                continue;
            float s1;   //成绩
            float s2;   //学分
            try {
                s2 = Float.parseFloat(score.getXf());
            } catch (NumberFormatException e) {
                continue;   //没有学分,skip it
            }
            try {
                s1 = Float.parseFloat(score.getScore());
            } catch (NumberFormatException e) {
                switch (score.getScore()) {
                    case "优":
                        s1 = 95;
                        break;
                    case "良":
                        s1 = 85;
                        break;
                    case "中":
                    case "通过":
                        s1 = 75;
                        break;
                    case "及格":
                        s1 = 65;
                        break;
                    default:
                        s1 = 0;
                        break;
                }
            }
            sum += (s1 - 50) / 10 * s2;
            XFsum += s2;
        }
        jd = sum / XFsum;
        Score score = new Score();
        BigDecimal b = new BigDecimal(jd);
        new_jd = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (isSelf) {
            list.get(0).setJd(new_jd + "");
        } else {
            score.setJd(new_jd + "");
            score.setTerm(year);
            score.setCheck(false);
            list.add(0, score);
        }
        if (!APPAplication.debug) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VersionControl versionControl = new VersionControl();
                    String content = xh + " " + year + " " + new_jd;
                    versionControl.recordGPA(content);
                }
            }).start();
        }
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                source = new getKebiaoSource(xh, pwd, ScoreActivity.this);
                switch (source.checkUser()) {
                    case 0:
                        if (year.contains("学年")) {
                            String year_1 = year.replace("学年", "") + "-1";
                            final String year_2 = year.replace("学年", "") + "-2";
                            source.getScore(year_1, new getKebiaoSource.OnResponseResult() {
                                @Override
                                public void onResponseResult(int code, String result) {
                                    if (code == -1) {
                                        handler.sendEmptyMessage(4);
                                        return;
                                    }
                                    list.clear();
                                    Document document = Jsoup.parse(result);
                                    Elements score = document.getElementsByAttributeValue("id",
                                            "dataList").select("tr");
                                    for (int j = 1; j < score.size(); j++) {
                                        Score score1 = new Score();
                                        Elements ee = score.get(j).select("td");
                                        score1.setCno(ee.get(2).text());
                                        score1.setName(ee.get(3).text());
                                        score1.setScore(ee.get(4).text());
                                        score1.setXf(ee.get(5).text());
                                        score1.setKs(ee.get(6).text());
                                        score1.setKhfx(ee.get(7).text());
                                        score1.setKcsx(ee.get(8).text());
                                        score1.setKcxz(ee.get(9).text());
                                        if ((score1.getKcsx().equals("必修") || score1.getKcsx().equals("任选"))
                                                && !score1.getName().contains("体育"))
                                            score1.setCheck(true);
                                        else
                                            score1.setCheck(false);
                                        list.add(score1);
                                    }
                                    source.getScore(year_2, new getKebiaoSource.OnResponseResult() {
                                        @Override
                                        public void onResponseResult(int code, String result) {
                                            Document document = Jsoup.parse(result);
                                            Elements score = document.getElementsByAttributeValue("id",
                                                    "dataList").select("tr");
                                            for (int j = 1; j < score.size(); j++) {
                                                Score score1 = new Score();
                                                Elements ee = score.get(j).select("td");
                                                score1.setCno(ee.get(2).text());
                                                score1.setName(ee.get(3).text());
                                                score1.setScore(ee.get(4).text());
                                                score1.setXf(ee.get(5).text());
                                                score1.setKs(ee.get(6).text());
                                                score1.setKhfx(ee.get(7).text());
                                                score1.setKcsx(ee.get(8).text());
                                                score1.setKcxz(ee.get(9).text());
                                                if ((score1.getKcsx().equals("必修") || score1.getKcsx().equals("任选"))
                                                        && !score1.getName().contains("体育"))
                                                    score1.setCheck(true);
                                                else
                                                    score1.setCheck(false);
                                                list.add(score1);
                                            }
                                            calculateGPA(false);
                                            if (list.size() > 0)
                                                handler.sendEmptyMessage(1);
                                            else
                                                handler.sendEmptyMessage(2);
                                        }
                                    });
                                }
                            });
                        } else {
                            source.getScore(year, new getKebiaoSource.OnResponseResult() {
                                @Override
                                public void onResponseResult(int code, String result) {
                                    if (code == -1) {
                                        handler.sendEmptyMessage(4);
                                        return;
                                    }
                                    list.clear();
                                    Document document = Jsoup.parse(result);
                                    Elements score = document.getElementsByAttributeValue("id",
                                            "dataList").select("tr");
                                    for (int j = 1; j < score.size(); j++) {
                                        Score score1 = new Score();
                                        Elements ee = score.get(j).select("td");
                                        score1.setCno(ee.get(2).text());
                                        score1.setName(ee.get(3).text());
                                        score1.setScore(ee.get(4).text());
                                        score1.setXf(ee.get(5).text());
                                        score1.setKs(ee.get(6).text());
                                        score1.setKhfx(ee.get(7).text());
                                        score1.setKcsx(ee.get(8).text());
                                        score1.setKcxz(ee.get(9).text());
                                        if ((score1.getKcsx().equals("必修") || score1.getKcsx().equals("任选"))
                                                && !score1.getName().contains("体育"))
                                            score1.setCheck(true);
                                        else
                                            score1.setCheck(false);
                                        list.add(score1);
                                    }
                                    calculateGPA(false);
                                    if (list.size() > 0)
                                        handler.sendEmptyMessage(1);
                                    else
                                        handler.sendEmptyMessage(2);
                                }
                            });
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.score_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) {
            APPAplication.showToast("绩点: " + jd, 0);
        } else {
            list.get(position).setClick(!list.get(position).getClick());
            scoreAdapter.notifyItemChanged(position, 1);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.score_menu_zx:
                if (item.getTitle().equals("自选")) {
                    for (int i = 1, j = list.size(); i < j; i++) {
                        list.get(i).setCBVisil(true);
                    }
                    scoreAdapter.notifyDataSetChanged();
                    item.setTitle("计算绩点");
                } else if (item.getTitle().equals("计算绩点")) {
                    recyclerView.scrollToPosition(0);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            calculateGPA(true);
                            scoreAdapter.notifyItemChanged(0);
                        }
                    }, 500);
                }
                break;
            case R.id.score_menu_switch:
                term_dialog.show();
                break;
            case R.id.score_menu_switch2:
                if (login_dialog == null) {
                    View v = LayoutInflater.from(this).inflate(R.layout.score_login_dialog, null);
                    login_dialog = new AlertDialog.Builder(this).setTitle("登录教务")
                            .setView(v).create();
                    final EditText et_xh = v.findViewById(R.id.score_login_dialog_et_user);
                    final EditText et_pwd = v.findViewById(R.id.score_login_dialog_et_pwd);
                    et_xh.setText(xh);
                    et_pwd.setText(pwd);
                    Button bt_login = v.findViewById(R.id.score_login_dialog_bt_login);
                    bt_login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String s_xh = et_xh.getText().toString().trim();
                            String s_pwd = et_pwd.getText().toString().trim();
                            if (TextUtils.isEmpty(s_xh)) {
                                et_xh.setError("请输入学号");
                                return;
                            } else {
                                et_xh.setError(null);
                            }
                            if (TextUtils.isEmpty(s_pwd)) {
                                et_pwd.setError("请输入教务密码");
                                return;
                            } else {
                                et_pwd.setError(null);
                            }
                            xh = s_xh;
                            pwd = s_pwd;
                            login_dialog.dismiss();
                            srl.setRefreshing(true);
                            onRefresh();
                        }
                    });
                }
                login_dialog.show();
                break;
            case R.id.score_menu_advie:
                PackageManager packageManager = getPackageManager();
                try {
                    packageManager.getPackageInfo("com.tencent.mobileqq", 0);
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=1125280130";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    APPAplication.showToast("安装手机QQ后才能反馈哦", 0);
                }
                break;
        }
        return true;
    }

    @Override
    public void onDetailItemClickListener(final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.score_xf_dialog, null);
        ((TextView) view.findViewById(R.id.score_xf_dialog_name)).setText(
                list.get(position).getName());
        final EditText et_xf = view.findViewById(R.id.score_xf_dialog_et);
        et_xf.setHint(list.get(position).getXf() + "");
        new AlertDialog.Builder(this).setTitle("自定义学分")
                .setView(view).setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String s = et_xf.getText().toString().trim();
                if (TextUtils.isEmpty(s))
                    APPAplication.showToast("输入的学分为空!", 0);
                else {
                    try {
                        double s2 = Double.parseDouble(s);
                        list.get(position).setXf(s2 + "");
                        scoreAdapter.notifyItemChanged(position, 2);
                    } catch (NumberFormatException e) {
                        APPAplication.showToast("输入的学分格式有误!", 0);
                    }
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }
}
