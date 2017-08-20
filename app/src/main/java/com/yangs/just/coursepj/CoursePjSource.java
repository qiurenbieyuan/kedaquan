package com.yangs.just.coursepj;

import android.content.Context;

import com.yangs.just.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by winutalk on 2017/7/30.
 */

public class CoursePjSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private String cookie1;
    private String cookie2;
    private String xh;
    private String pwd;

    public CoursePjSource(String xh, String pwd) {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/7.0").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).build();
        this.xh = xh;
        this.pwd = pwd;
        cookie1 = null;
        cookie2 = null;

    }

    public int checkUser() {
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

    public List<String> getPjFind() {
        List<String> urList = new ArrayList<String>();
        Request request = new Request.Builder()
                .url("http://jwgl.just.edu.cn:8080/jsxsd/xspj/xspj_find.do?Ves632DSdyV=NEW_XSD_JXPJ")
                .headers(requestHeaders).header("Cookie", cookie1 + " ; " + cookie2).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Elements elements = document.getElementsByAttributeValue("title", "点击进入评价");
            for (Element element : elements) {
                urList.add("http://jwgl.just.edu.cn:8080" + element.attr("href"));
            }
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
        }
        return urList;
    }

    public List<CoursePJList> getPjTable(String urList) {
        List<CoursePJList> pjList = new ArrayList<CoursePJList>();
        Request request = new Request.Builder().url(urList).headers(requestHeaders)
                .header("Cookie", cookie1 + " ; " + cookie2).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Elements elements = document.getElementsByAttributeValue("id", "dataList").select("tr");
            for (int i = 1; i < elements.size(); i++) {
                CoursePJList coursePJList = new CoursePJList();
                Elements elements2 = elements.get(i).select("td");
                coursePJList.setName(elements2.get(2).text());
                coursePJList.setTeacher(elements2.get(3).text());
                coursePJList.setScore(elements2.get(4).text());
                coursePJList.setHasPj(elements2.get(6).text().equals("是") ? true : false);
                coursePJList.setUrl("http://jwgl.just.edu.cn:8080"
                        + elements2.get(7).select("a").attr("href").split("javascript:JsMod\\(")[1]
                        .split("'")[1]
                        .split(",")[0]);
                pjList.add(coursePJList);
            }

        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
        }
        return pjList;
    }

    public void doPj(String url, String t) {
        String post1 = url.split("\\?")[1];
        String post2 = "&pj06xh=1&pj0601id_1=9AE00809E3544BC3B449C35937249578&pj0601fz_1_9AE00809E3544BC3B449C35937249578=20"
                + "&pj06xh=2&pj0601id_2=9E39EC0532A046C2AC112288343A3CCF&pj0601fz_2_9E39EC0532A046C2AC112288343A3CCF=12.75"
                + "&pj06xh=5&pj0601id_5=0B305D826FAE4D498DF9E3B6AC725894&pj0601fz_5_0B305D826FAE4D498DF9E3B6AC725894=10"
                + "&pj06xh=4&pj0601id_4=1E4AE0C4D1BC4F3A8E65C12EE84EEA1C&pj0601fz_4_1E4AE0C4D1BC4F3A8E65C12EE84EEA1C=25"
                + "&pj06xh=3&pj0601id_3=FE95FBF31B554D76BFF7F991384A1ED0&pj0601fz_3_FE95FBF31B554D76BFF7F991384A1ED0=30";
        String post3 = "&jynr=" + t;
        Request request = new Request.Builder()
                .url("http://jwgl.just.edu.cn:8080/jsxsd/xspj/xspj_save.do?issubmit=1&" + post1 + post2 + post3)
                .headers(requestHeaders).header("Cookie", cookie1 + " ; " + cookie2).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            System.out.println(response.body().string().split(";")[0].split("alert\\(")[1].replaceAll("'|\\)", ""));
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
        }
    }

    public void Success(String flag) {
        FormBody.Builder formBodyBuilder2 = new FormBody.Builder().add("check", "yangs")
                .add("xh", xh).add("flag", flag);
        RequestBody requestBody2 = formBodyBuilder2.build();
        Request request3 = new Request.Builder().url("http://www.myangs.com:8080/coursepj_record.jsp")
                .headers(requestHeaders).post(requestBody2).build();
        try {
            mOkHttpClient.newCall(request3).execute();
        } catch (Exception e) {
        }
    }
}
