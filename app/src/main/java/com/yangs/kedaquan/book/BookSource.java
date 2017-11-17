package com.yangs.kedaquan.book;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.yangs.kedaquan.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

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
 * Created by yangs on 2017/4/22.
 */

public class BookSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private String cookie;

    public BookSource() {
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
        cookie = APPAplication.save.getString("vpn_cookie", "");
    }

    public Bitmap getCode() {
        Request request = new Request.Builder().url(
                "https://vpn.just.edu.cn/reader/,DanaInfo=lib.just.edu.cn,Port=8080+login.php")
                .headers(requestHeaders).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            cookie = cookie + ";" + response.header("Set-Cookie").split(";")[0];
            Request request2 = new Request.Builder()
                    .url("https://vpn.just.edu.cn/reader/,DanaInfo=lib.just.edu.cn,Port=8080+captcha.php")
                    .headers(requestHeaders).header("Cookie", cookie).build();
            response = mOkHttpClient.newCall(request2).execute();
            return BitmapFactory.decodeStream(response.body().byteStream());
        } catch (IOException e) {
            APPAplication.showToast("连接图书馆服务器失败,请稍后再试", 1);
        }
        return null;
    }

    public ArrayList<Book> getList(String user, String pwd, String code, String type) {
        ArrayList<Book> bookList = new ArrayList<Book>();
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("number", user)
                .add("passwd", pwd).add("captcha", code).add("select", type).add("returnUrl", "");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("https://vpn.just.edu.cn/reader/,DanaInfo=lib.just.edu.cn,Port=8080+redr_verify.php")
                .headers(requestHeaders).header("Cookie", cookie).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Headers headers = response.headers();
            try {
                if (TextUtils.isEmpty(headers.get("Location"))) {
                    Book book = new Book(-2);     //登录错误
                    bookList.add(book);
                    return bookList;
                }
                Request request2 = new Request.Builder()
                        .url("https://vpn.just.edu.cn/reader/,DanaInfo=lib.just.edu.cn,Port=8080+book_lst.php")
                        .headers(requestHeaders).header("cookie", cookie).build();
                String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='book';";
                Cursor cursor = null;
                try {
                    cursor = APPAplication.db.rawQuery(sql, null);
                    if (cursor.moveToNext()) {
                        int count = cursor.getInt(0);
                        if (count > 0) {
                            APPAplication.db.execSQL("drop table book");
                        }
                    }
                } catch (Exception e) {
                    APPAplication.showToast(e.getMessage(), 1);
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
                APPAplication.db.execSQL("create table book(id INTEGER PRIMARY KEY AUTOINCREMENT,书名 TEXT,编号 TEXT,开始 TEXT,结束 TEXT,续借次数 INTEGER);");
                try {
                    response = mOkHttpClient.newCall(request2).execute();
                    Document document = Jsoup.parse(response.body().string());
                    Elements list = document.getElementsByAttributeValue("class", "table_line").select("tr");
                    if (list.size() == 0) {
                        Book book = new Book(-1);   //没有借书
                        bookList.add(book);
                    } else {
                        for (int i = 1; i < list.size(); i++) {
                            Elements tmp = list.get(i).select("td");
                            Book book = new Book(tmp.get(1).text(), tmp.get(0).text(), tmp.get(2).text(), tmp.get(3).text(), tmp.get(4).text());
                            //Book book = new Book(tmp.get(2).text(), tmp.get(1).text(), tmp.get(4).text(), tmp.get(5).text(), 0+"");
                            bookList.add(book);
                            ContentValues cv = new ContentValues();
                            cv.put("书名", book.getBook_name());
                            cv.put("编号", book.getNumber());
                            cv.put("开始", book.getDate_start());
                            cv.put("结束", book.getDate_end());
                            cv.put("续借次数", book.getXj());
                            APPAplication.db.insert("book", null, cv);
                        }
                    }
                    String last = user + " " + " " + android.os.Build.MODEL + " " + android.os.Build.VERSION.SDK + " " + android.os.Build.VERSION.RELEASE;
                    FormBody.Builder formBodyBuilder2 = new FormBody.Builder().add("check", "yangs")
                            .add("user", last);
                    RequestBody requestBody2 = formBodyBuilder2.build();
                    Request request3 = new Request.Builder().url("http://www.myangs.com:8080/book_record.jsp")
                            .headers(requestHeaders).post(requestBody2).build();
                    try {
                        mOkHttpClient.newCall(request3).execute();
                    } catch (Exception e) {

                    }
                    return bookList;
                } catch (IOException ee) {
                }
            } catch (Exception e) {
            }
        } catch (IOException e) {
        }
        Book book = new Book(-3);       //网络错误
        bookList.add(book);
        return bookList;
    }
}
