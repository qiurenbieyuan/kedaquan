package com.yangs.just.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yangs.just.activity.APPAplication;
import com.yangs.just.score.Score;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by winutalk on 2017/2/12.
 */

public class getKebiaoSource {
    private String xh;
    private String pwd;
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private String cookie1;
    private String cookie2;
    private Context context;

    public getKebiaoSource(String xh, String pwd, Context context) {
        this.context = context;
        this.xh = xh;
        this.pwd = pwd;
        cookie1 = "";
        cookie2 = "";
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/7.0").build();
    }

    public int checkUser(Context context) {
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                .followSslRedirects(false).build();
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("USERNAME", xh).add("PASSWORD",
                pwd);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://jwgl.just.edu.cn:8080/jsxsd/xk/LoginToXk")
                .headers(requestHeaders).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            cookie1 = response.headers("Set-Cookie").get(0).split(";")[0];
            cookie2 = response.headers("Set-Cookie").get(1).split(";")[0];
            if (response.header("Location") != null) {
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -2;
        }
    }

    public int getWeek(Context context) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("check", "yangs");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://www.myangs.com:8080/getWeek.jsp")
                .post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            JSONObject tmp = new JSONObject(response.body().string());
            return Integer.parseInt(tmp.getString("week"));
        } catch (Exception e) {
            APPAplication.showToast("从服务器获取当前周失败!", 0);
            return 1;
        }
    }

    public int getKebiao(String year) {
        String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='course' ";
        Cursor cursor = APPAplication.db.rawQuery(sql, null);
        try {
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    APPAplication.db.execSQL("drop table course");
                }
            }
        } catch (Exception e) {
            APPAplication.showToast(e.getMessage(), 1);
        }
        APPAplication.save.edit().putString("xh", this.xh).putString("pwd", this.pwd).putString("term", year).apply();
        APPAplication.db.execSQL("create table course(id INTEGER PRIMARY KEY AUTOINCREMENT,课程名 TEXT,课程代码 TEXT,教室 TEXT,老师 TEXT,星期 INTEGER,节次 INTEGER,周次 TEXT,颜色代码 INTEGER);");
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("xnxq01id", year);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://jwgl.just.edu.cn:8080/jsxsd/xskb/xskb_list.do")
                .headers(requestHeaders)
                .header("Cookie", cookie1 + " ; " + cookie2)
                .post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Element name = document.getElementById("Top1_divLoginName");
            APPAplication.save.edit().putString("name", name.text().split("\\(")[0]).apply();
            APPAplication.name = APPAplication.save.getString("name", "未登录");
            Elements kebiao = document.getElementsByAttributeValue("id", "kbtable").select("tr");
            if (kebiao.size() <= 2) {
                return -1;
            }
            int i = 0, color;
            int color_no_again_control = 0;
            for (Element e : kebiao) {
                Elements everyday = e.select("td");
                if (everyday.size() > 0) {
                    i++;
                }
                for (int j = 0; j < everyday.size(); j++) {
                    if (everyday.size() == 1) { // 课表备注
                        APPAplication.save.edit().putString("extra", everyday.get(0).text()).apply();
                    } else {
                        String tmp = "";
                        Element detail = everyday.get(j).select("div.kbcontent").first();
                        if (!detail.html().contains("&nbsp")) {
                            for (String t : detail.html().split("---------------------")) {
                                // 高兼容性 by yangs 2017-2-12
                                Document t2 = Jsoup.parse(t);
                                ContentValues cv = new ContentValues();
                                cv.put("课程代码", t2.text().split(" ")[0]);
                                cv.put("课程名", t2.text().split(" ")[1]);
                                cv.put("教室", t2.getElementsByAttributeValue("title", "教室").text() + " ");
                                cv.put("老师", t2.getElementsByAttributeValue("title", "老师").text());
                                cv.put("周次", t2.getElementsByAttributeValue("title", "周次(节次)").text());
                                cv.put("星期", (j + 1));
                                cv.put("节次", i);
                                sql = "select * from course where 课程名='" + cv.get("课程名") + "';";
                                cursor = APPAplication.db.rawQuery(sql, null);
                                try {
                                    cursor.moveToNext();
                                    color = cursor.getInt(8);
                                } catch (Exception ee) {
                                    color = color_no_again_control++;
                                }
                                cv.put("颜色代码", color + "");
                                APPAplication.db.insert("course", null, cv);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return -2;
        }
        return 0;
    }

    public List<Score> getScore(String year) {
        List<Score> list = new ArrayList<Score>();
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("kksj", year);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://jwgl.just.edu.cn:8080/jsxsd/kscj/cjcx_list")
                .headers(requestHeaders).post(requestBody).header("Cookie", cookie1 + " ; " + cookie2).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Elements score = document.getElementsByAttributeValue("id", "dataList").select("tr");
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
                list.add(score1);
            }
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
        }
        return list;
    }
}
