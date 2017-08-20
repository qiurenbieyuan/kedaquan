package com.yangs.just.book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.yangs.just.activity.APPAplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by winutalk on 2017/4/22.
 */

public class BookSource {
    private OkHttpClient mOkHttpClient;
    private Headers requestHeaders;
    private String cookie;

    public BookSource() {
        requestHeaders = new Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/7.0").build();
        mOkHttpClient = new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).build();
        cookie = null;
    }

    public Bitmap getCode() {
        Request request = new Request.Builder().url("http://202.195.195.137:8080/reader/login.php")
                .headers(requestHeaders).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            cookie = response.header("Set-Cookie").split(";")[0];
            if (cookie != null) {
                Request request2 = new Request.Builder().url("http://202.195.195.137:8080/reader/captcha.php")
                        .headers(requestHeaders).header("cookie", cookie).build();
                try {
                    response = mOkHttpClient.newCall(request2).execute();
                    return BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            APPAplication.showToast("连接图书馆服务器失败,请稍后再试", 1);
        }
        return null;
    }

    public ArrayList<Book> getList(String user, String pwd, String code, String type) {
        ArrayList<Book> bookList = new ArrayList<Book>();
        FormBody.Builder formBodyBuilder = new FormBody.Builder().add("number", user)
                .add("passwd", pwd).add("captcha", code).add("select", type).add("returnUrl", "");
        RequestBody requestBody = formBodyBuilder.build();
        Request request = new Request.Builder().url("http://202.195.195.137:8080/reader/redr_verify.php")
                .headers(requestHeaders).header("cookie", cookie).post(requestBody).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Headers headers = response.headers();
            try {
                if (TextUtils.isEmpty(headers.get("Location"))) {
                    Book book = new Book(-2);     //登录错误
                    bookList.add(book);
                    return bookList;
                }
                Request request2 = new Request.Builder().url("http://202.195.195.137:8080/reader/book_lst.php")
                        .headers(requestHeaders).header("cookie", cookie).build();
                String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='book';";
                Cursor cursor = APPAplication.db.rawQuery(sql, null);
                try {
                    if (cursor.moveToNext()) {
                        int count = cursor.getInt(0);
                        if (count > 0) {
                            APPAplication.db.execSQL("drop table book");
                        }
                    }
                } catch (Exception e) {
                    APPAplication.showToast(e.getMessage(), 1);
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
