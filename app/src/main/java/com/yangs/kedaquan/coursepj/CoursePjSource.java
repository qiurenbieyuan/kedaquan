package com.yangs.kedaquan.coursepj;

import com.yangs.kedaquan.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangs on 2017/7/30.
 */

public class CoursePjSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private String cookie;
    private String xh;
    private String pwd;

    public CoursePjSource(String xh, String pwd) {
        cookie = APPAplication.save.getString("vpn_cookie", "");
        requestHeaders = new Headers.Builder()
                .add("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                .build();
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        mOkHttpClient = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .followRedirects(false).followSslRedirects(false).build();
        this.xh = xh;
        this.pwd = pwd;
    }

    public int checkUser() {
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("USERNAME", xh).add("PASSWORD",
                pwd);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/jsxsd/xk/,DanaInfo=jwgl.just.edu.cn,Port=8080+LoginToXk")
                .headers(requestHeaders).header("Cookie", cookie).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.header("Location") != null) {
                cookie = cookie + ";" + response.header("Set-Cookie");
                return 0;
            } else {
                return -1;
            }
        } catch (IOException e) {
            return -2;
        }
    }

    public List<String> getPjFind() {
        List<String> urList = new ArrayList<String>();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/jsxsd/xspj/,DanaInfo=jwgl.just.edu.cn,Port=8080+xspj_find.do?Ves632DSdyV=NEW_XSD_JXPJ")
                .headers(requestHeaders).header("Cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Elements elements = document.getElementsByAttributeValue("title", "点击进入评价");
            for (Element element : elements) {
                urList.add("https://vpn.just.edu.cn/jsxsd/" + element.attr("href"));
            }
        } catch (Exception e) {
            APPAplication.showToast(e.toString(), 1);
        }
        return urList;
    }

    public List<CoursePJList> getPjTable(String urList) {
        List<CoursePJList> pjList = new ArrayList<CoursePJList>();
        Request request = new Request.Builder().url(urList).headers(requestHeaders)
                .header("Cookie", cookie).build();
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
                coursePJList.setUrl("https://vpn.just.edu.cn/jsxsd/"
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
                .url("https://vpn.just.edu.cn/jsxsd/xspj/,DanaInfo=jwgl.just.edu.cn,Port=8080+xspj_save.do?issubmit=1&" + post1 + post2 + post3)
                .headers(requestHeaders).header("Cookie", cookie).build();
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
