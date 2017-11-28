package com.yangs.kedaquan.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import com.yangs.kedaquan.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.*;

import java.security.cert.X509Certificate;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangs on 2017/10/11 0011.
 */

public class VpnSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private String vpn_user;
    private String vpn_pwd;
    private String jw_user;
    private String jw_pwd;
    private String cookie;
    private String location;

    public VpnSource(String vpn_user, String vpn_pwd) {
        this.vpn_user = vpn_user;
        this.vpn_pwd = vpn_pwd;
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
    }

    public String getLocation() {
        return location;
    }

    public void updateUserPwd(String user, String pwd) {
        this.vpn_user = user;
        this.vpn_pwd = pwd;
    }

    /*
    0: valid
    -1: cookie is not valid
    -2: network is error
     */
    public int checkVpnIsValid() {
        int code;
        String url = "https://vpn.just.edu.cn/,DanaInfo=www.just.edu.cn,Port=80+";
        Request request = new Request.Builder().url(url)
                .headers(requestHeaders).addHeader("Cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            String location = response.header("location");
            response.close();
            if (location != null && location.contains("welcome.cgi?p=forced-off")) {
                code = -1;
            } else {
                code = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            code = -2;
        }
        return code;
    }

    /*
    0: 正常
    -1: 密码错误
    -2: 在其他地方登录
    -3: network error
     */
    public int checkVpnUser() {
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("tz_offset", "480")
                .add("username", vpn_user).add("password", vpn_pwd).add("realm", "LDAP-REALM")
                .add("btnSubmit", "登录");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("https://vpn.just.edu.cn/dana-na/auth/url_default/login.cgi")
                .headers(requestHeaders).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            location = response.header("location");
            response.close();
            if (location != null && location.contains("welcome.cgi?p=failed")) {
                return -1;
            }
            if (location != null && location.contains("welcome.cgi?p=user-confirm")) {
                return -2;
            }
            cookie = response.headers("Set-Cookie").get(0) + ";" + response.headers("Set-Cookie").get(1)
                    + ";" + response.headers("Set-Cookie").get(2);
            request = new Request.Builder().url(location)
                    .headers(requestHeaders).header("Cookie", cookie).build();
            try {
                response = mOkHttpClient.newCall(request).execute();
                cookie = cookie + ";" + response.header("Set-Cookie");
                APPAplication.save.edit().putString("vpn_cookie", cookie)
                        .putString("vpn_user", vpn_user)
                        .putString("vpn_pwd", vpn_pwd).apply();
                response.close();
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -3;
    }

    /*
    0: 正常
    -1: 密码错误
    -2: network error
     */
    public int checkJwUser(String jw_user, String jw_pwd) {
        this.jw_user = jw_user;
        this.jw_pwd = jw_pwd;
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("USERNAME", this.jw_user)
                .add("PASSWORD", this.jw_pwd);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/jsxsd/xk/,DanaInfo=jwgl.just.edu.cn,Port=8080+LoginToXk")
                .headers(requestHeaders).post(requestBody).header("Cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            String location = response.header("Location");
            if (location != null) {
                cookie = cookie + ";" + response.header("Set-Cookie");
                response.close();
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    /*
    0:  success
    -1: error
    -2: network error
     */
    public int loginTy(String user, String pwd) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("username", user)
                .add("password", pwd).add("chkuser", "true");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("https://vpn.just.edu.cn/,DanaInfo=202.195.195.147+index1.asp")
                .headers(requestHeaders).post(requestBody).header("Cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            byte[] b = response.body().bytes();
            String info = new String(b, "GB2312");
            if (info.contains("密码或用户名不正确")) {
                return -1;
            } else {
//                cookie = cookie + ";" + response.headers("Set-Cookie").get(0);
//                APPAplication.save.edit().putString("vpn_cookie", cookie).apply();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    /*
    0: success
    -2: network error
     */
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
        } catch (SQLException e) {
            APPAplication.showToast(e.getMessage(), 1);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        APPAplication.save.edit().putString("xh", this.jw_user).putString("pwd", this.jw_pwd).putString("term", year).apply();
        APPAplication.db.execSQL("create table course(id INTEGER PRIMARY KEY AUTOINCREMENT,课程名 TEXT,课程代码 TEXT,教室 TEXT,老师 TEXT,星期 INTEGER,节次 INTEGER,周次 TEXT,颜色代码 INTEGER);");
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("xnxq01id", year);
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("https://vpn.just.edu.cn/jsxsd/xskb/,DanaInfo=jwgl.just.edu.cn,Port=8080+xskb_list.do")
                .headers(requestHeaders)
                .header("Cookie", cookie)
                .post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Document document = Jsoup.parse(response.body().string());
            Element name = document.getElementById("Top1_divLoginName");
            APPAplication.save.edit().putString("name", name.text().split("\\(")[0]).apply();
            APPAplication.name = APPAplication.save.getString("name", "未登录");
            Elements kebiao = document.getElementsByAttributeValue("id", "kbtable").select("tr");
            if (kebiao.size() <= 2) {
                exitVpn();
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
                                    if (color_no_again_control == 13) {
                                        color_no_again_control = 0;     //只支持14种颜色
                                        APPAplication.showToast("客官你的课程数量超过14了,目前还没有足够的颜色区分" +
                                                ",将使用重复的颜色!", 1);
                                    }
                                    color = color_no_again_control++;
                                } finally {
                                    if (cursor != null)
                                        cursor.close();
                                }
                                cv.put("颜色代码", color + "");
                                APPAplication.db.insert("course", null, cv);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            return -2;
        }
        return 0;
    }

    public void exitVpn() {
        Request request = new Request.Builder().url("https://vpn.just.edu.cn/dana-na/auth/logout.cgi")
                .headers(requestHeaders).header("Cookie", cookie).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response != null)
                response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exitVpn2() {
        Headers requestHeaders = new Headers.Builder()
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
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .followRedirects(false).followSslRedirects(false).build();
        Request request = new Request.Builder().url("https://vpn.just.edu.cn/dana-na/auth/logout.cgi")
                .headers(requestHeaders).header("Cookie", APPAplication.save.getString("vpn_cookie", "")).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response != null)
                response.close();
            APPAplication.showToast("退出vpn成功", 0);
        } catch (Exception e) {
            e.printStackTrace();
            APPAplication.showToast("退出vpn失败", 0);
        }
    }
}
